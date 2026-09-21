# 计划：将 hydrogen 主题作为文章渲染/导出的默认样式

## 概述

把掘金 hydrogen 主题（`DawnLck/juejin-markdown-theme-hydrogen@b3f86fb` 的 `hydrogen.scss`）完整移植为 weisome 中「微信文章预览 + 导出 HTML」的默认样式。样式文档入库作为工程内容，后续实现直接参考它。

**范围（用户已确认）**：完整对齐样式表 —— 扩展 Markdown 解析器支持 hydrogen 涉及的所有元素（链接、blockquote、hr、图片、表格、删除线、h4-h6、嵌套/任务列表）；装饰效果（h1 的 `#` 前缀、伪元素类引号/logo/链接图标）在导出中改为真实元素输出，在预览中用 Compose 等价实现。em 着重号：导出按样式表内联 `text-emphasis: dot`，预览用斜体近似（已确认）。

**不涉及**：App 自身 UI 样式（DESIGN.md 管辖的是应用外壳；文章内容样式以 `docs/hydrogen.scss` 为规范，这正是本任务的目的）。

## 现状分析

- 解析器 [MarkdownDocumentParser.kt](e:\Workspace\Code\Jvm\weisome\data\src\com\rocybyte\weisome\article\MarkdownDocumentParser.kt) 仅支持 Heading(1-3)/Paragraph/ListBlock(扁平)/CodeBlock + Bold/Italic/Code 内联。
- 模型 [MarkdownDocument.kt](e:\Workspace\Code\Jvm\weisome\contracts\src\com\rocybyte\weisome\article\MarkdownDocument.kt)（contracts 模块，shared-ui 与 data 共同可见）。
- 导出：data 模块 [WechatArticleStyles.kt](e:\Workspace\Code\Jvm\weisome\data\src\com\rocybyte\weisome\article\WechatArticleStyles.kt) 生成内联 CSS，[html/](e:\Workspace\Code\Jvm\weisome\data\src\com\rocybyte\weisome\article\html) 各文件按 block 渲染，产物放剪贴板（[DesktopWechatArticleRepository.kt](e:\Workspace\Code\Jvm\weisome\data\src@jvm\com\rocybyte\weisome\repository\article\DesktopWechatArticleRepository.kt)）。
- 预览：shared-ui [widget/](e:\Workspace\Code\Jvm\weisome\shared-ui\src\com\rocybyte\weisome\page\article\widget) Compose 原生渲染，样式集中在 [Styles.kt](e:\Workspace\Code\Jvm\weisome\shared-ui\src\com\rocybyte\weisome\page\article\widget\Styles.kt)。
- shared-ui 不依赖 data（只依赖 contracts），因此两侧样式值天然双份（现有 `heading()` 已是此模式），保持该模式并注明漂移风险。
- 无图片加载库（无 coil/kamel）。桌面端可用 AWT `ImageIO` + skia 转换免依赖加载网络图片（kotlinx-coroutines-swing 已有）。
- 现有测试断言精确样式串，需同步更新。

## 改动方案

### 0. 样式文档入库（新增 `docs/hydrogen.scss`）

把已下载的 hydrogen.scss 源文件（来自 `raw.githubusercontent.com/DawnLck/juejin-markdown-theme-hydrogen/b3f86fb/hydrogen.scss`，现存于 `%TEMP%\hydrogen.scss`）复制到 `docs/hydrogen.scss`，作为文章样式的唯一规范来源。文件头加注释说明来源仓库与 ref。

### 1. contracts：文档模型扩展（MarkdownDocument.kt）

```kotlin
sealed interface MarkdownBlock {
    // 现有 Heading/Paragraph/ListBlock/CodeBlock 保留
    data class BlockQuote(val blocks: List<MarkdownBlock>) : MarkdownBlock
    data object HorizontalRule : MarkdownBlock
    data class Table(
        val header: List<List<MarkdownInline>>,
        val rows: List<List<List<MarkdownInline>>>,
    ) : MarkdownBlock
}

data class ListItem(
    val content: List<MarkdownInline>,
    val task: Boolean? = null,      // null=非任务项；true=[x]；false=[ ]
    val child: ListBlock? = null,   // 嵌套子列表
)

sealed interface MarkdownInline {
    // 现有 Text/Bold/Italic/Code 保留
    data class Link(val text: String, val url: String) : MarkdownInline
    data class Strikethrough(val value: String) : MarkdownInline
    data class Image(val alt: String, val url: String) : MarkdownInline
}
```

`ListBlock.items` 类型由 `List<List<MarkdownInline>>` 改为 `List<ListItem>`。

### 2. data：解析器扩展（MarkdownDocumentParser.kt）

- heading 正则 `#{1,3}` → `#{1,6}`。
- hr：`^([-*_])\1{2,}\s*$`。
- blockquote：收集 `>` 开头的连续行，剥离一级 `>` 前缀后递归 `parse()`（天然支持嵌套 blockquote）。
- 表格：当前行含 `|` 且下一行是分隔行（`---`/`:--`/`--:` 模式）时进入表格模式；按 `|` 切分单元格，忽略对齐标记（hydrogen 未定义对齐样式）。
- 嵌套列表：缩进 ≥2 空格的列表行归入上一项的 `child`；任务项识别 `[ ] `/`[x] ` 前缀。
- 内联扩展（在现有 code-span 占位与 emphasis 流程上叠加，注意先匹配 `![alt](url)` 再 `[text](url)`）：`~~删除线~~`、`[text](url)`、`![alt](url)`。
- 链接文本只支持纯文本（不支持链接内嵌粗体等），注明上限。

### 3. data：HTML 导出（WechatArticleStyles.kt + html/）

`WechatArticleStyles.kt` 全部换成 hydrogen 值（见下方映射表），新增：

- `h1PrefixCss`（蓝色 `#` 前缀 span）、`linkIconSpan`（以 hydrogen 内联 SVG data URI 为背景的 18×18 span）、`hrLogo`（掘金 logo PNG data URI + 渐变线的双 div 结构）、blockquote 引号 span（绝对定位）、表格偶数行背景（导出到每个偶数 `<tr>` 的 td 上，因内联样式无法表达 `nth-child`）。
- h2 边框内联：`border-left: 5px solid #454545; padding-left: 10px;`。
- `capitalizeFirstLetter(text)` 帮助函数：实现 h2/h3/p 的 `::first-letter` 大写（首字符 uppercase；若首个内联是 Code/Image 则跳过，注明简化）。
- em：`<em style="font-style: italic; text-emphasis: dot; text-emphasis-position: under;">`（补 font-style: italic 以脱离对 UA 默认的依赖）。
- 链接：`<a href="..." style="color: #027fff; text-decoration: none; border-bottom: 2px solid transparent; margin: 0 4px; padding-bottom: 4px;">文本</a>` + 图标 span；hover 态静态介质不适用。
- 图片：`<img src="..." style="display: block; margin: 0 auto; max-width: 100%; border-radius: 2px; box-shadow: ...;" alt="...">`。

html/ 新增 `BlockQuote.kt`、`HorizontalRule.kt`、`Table.kt` 渲染器；`MarkdownToWechatHtml.kt` 分发新 block 类型；`ListBlock.kt` 支持嵌套与任务项（任务项 `list-style: none` + `☐`/`☑` 文本前缀）；`Inline.kt` 支持 Link/Strikethrough/Image 与新 code/em 样式。

### 4. shared-ui：Compose 预览（widget/）

- `Styles.kt`（`WechatArticlePreviewStyles`）：镜像 hydrogen 值 —— 正文字色 `0xDE2E2424`（rgba(46,36,36,0.87)）、主题蓝 `0x1976D2`、链接蓝 `0x027FFF`、各级标题字号/字重/边距（h1 30/w500/上35下5、h2 28/w400/上20 + 左边框、h3 24/w400/上15、h4 20/w500、h5 16、h6 16（样式表未定义 h6 字号，取正文基准 16px，注明）、行高 1.5×字号）。
- `Heading.kt`：h1 前置蓝色 `#`（AnnotatedString 拼接）；h2 用 `Row`（5dp 色条 `#454545` + 10dp 间距 + 文本，`IntrinsicSize.Min` 撑高）+ `hoverable` + `animateColorAsState` 300ms hover 变 `#1976d2`；支持 level 1-6。
- `Paragraph.kt`：字色 + 首字母大写。
- `Inline.kt`：code 芯片改红字粉底（`#c0341d` / `#fbe5e1`、圆角 2dp、加粗 w900、字号 0.87×）；Link（蓝字 + 尾随链接图标 + hover 下划线，图标用 `PathParser` 解析 hydrogen SVG 的 path data 后 Canvas 描边 `#027FFF`）；Strikethrough（`TextDecoration.LineThrough` + `rgba(0,0,0,0.6)`）；Image（`InlineTextContent` 占位 + 协程 AWT `ImageIO.read(URL)` → `toComposeImageBitmap` 异步加载，圆角 2dp + 阴影）。
- `ListBlock.kt`：缩进 28dp、嵌套子列表（顶部 3dp 间距）、ol 项前缀 `n.`、任务项无符号 + `☐`/`☑` 前缀。
- `CodeBlock.kt`：底 `#F8F8F8`、字色 `#333333`、字号 12sp、行高 1.75、内边距 15/12、圆角 `RoundedCornerShape(0.dp, 4.dp, 0.dp, 4.dp)`；语法高亮 span 颜色沿用 `WeiSomeLightCodeTheme`。
- 新增 `BlockQuote.kt`（左边框 4dp `#CBCBCB` hover 变蓝、底色 `rgba(200,200,200,0.12)`、灰字 `#666`、内边距 5/23/1、上下 22dp、左上/右下引号字符 `“”`（24sp/w800/#CBCBCB/60% 透明度，`offset` 定位）、内部 p 上下 10dp、嵌套 blockquote 上下 10dp）、`HorizontalRule.kt`（98% 宽 1dp 高 `Brush.linearGradient(#DDDDDD,#999999,#DDDDDD)` + 居中 60×20 掘金 logo：Base64 常量解码 PNG）、`Table.kt`（外框 2dp `#C6C6C6`、表头底 `#F6F6F6`、偶数行 `#FCFCFC`、单元格内边距 12/7、行高 24dp、td 最小宽 120dp、12sp）。
- `WechatArticlePreview.kt`：分发新 block 类型。
- 新增 `widget/HydrogenAssets.kt`：掘金 logo PNG 的 Base64 常量与解码函数、链接图标 SVG path data 常量（值均抄自 docs/hydrogen.scss，注释注明来源与漂移风险）。

### 5. 测试

- 更新：`MarkdownDocumentParserTest`、`html/HeadingTest|ParagraphTest|ListBlockTest|CodeBlockTest|InlineTest`、`DesktopWechatArticleRepositoryTest`、shared-ui `CodeBlockTest`（样式串断言全部换成 hydrogen 值）。
- 新增（放对应包）：parser 的 hr/blockquote/嵌套 blockquote/表格/嵌套列表/任务项/link/del/image/h4-h6 用例；html 的新 block 渲染器用例；`capitalizeFirstLetter` 用例。
- 每个新函数按 AGENTS.md 加 KDoc。

## 样式值映射（源：docs/hydrogen.scss，实现以此文件为准）

| 元素 | 关键值 |
|---|---|
| 正文 | 16px / 行高 1.75 / rgba(46,36,36,0.87) / p 上下边距 22px |
| h1 | 30px w500 上35 下5，前缀 `#`（#1976d2，右距10） |
| h2 | 28px w400 上20，左边框 5px #454545（hover→#1976d2），左内距 10 |
| h3 | 24px w400 上15 下10（padding-bottom 0） |
| h4/h5/h6 | 20px w500 / 16px / 16px（h6 字号为补全值） |
| h2/h3/p | 首字母大写（::first-letter capitalize 的等价实现） |
| em | 着重号 dot（导出内联 text-emphasis；预览斜体近似） |
| 行内 code | w900、0.87em、圆角2、内距 .065em .4em、#c0341d on #fbe5e1、等宽字体 |
| 代码块 | #333 on #f8f8f8、12px、行高1.75、内距 15/12、圆角 0 4px |
| a | #027fff、无下划线、底部 2px 透明边框（hover 显色）、尾随 18×18 链接图标、hover 字色 #275b8c |
| del | rgba(0,0,0,0.6) + 删除线 |
| blockquote | 左边框 4px #cbcbcb（hover→主题蓝）、底 rgba(200,200,200,0.12)、字色 #666、内距 5/23/1、上下 22px、角标引号 24px w800 #cbcbcb 60% |
| hr | 98% 宽渐变线 #ddd→#999→#ddd、上下 32px、居中掘金 logo（60×20 白底 PNG） |
| 表格 | 外框 2px #c6c6c6、表头 #f6f6f6 黑字左对齐、偶数行 #fcfcfc、单元格内距 12/7、行高 24px、td 最小宽 120px、12px |
| 列表 | 缩进 28px、ol 项左内距 6px、项间距 0、嵌套列表上距 3px、任务项无符号 |
| 图片 | 块级居中、max-width 100%、圆角 2px、Material 阴影 |

## 决策与已知上限

1. **语法高亮配色沿用 `WeiSomeLightCodeTheme`**：hydrogen 未定义高亮配色（themes.js 无 highlight 字段），只定义了代码块容器样式；容器样式换成 hydrogen 值，语法 span 颜色不动。
2. **预览 em = 斜体近似**（用户已确认），代码注明与样式表的差异。
3. **h6 字号 16px**：样式表未定义，取正文基准（浏览器默认值在导出场景不可控）。
4. **p 边距按声明值 `margin: 22px 0`**：浏览器会折叠相邻边距（视觉 22px），微信内联样式不折叠（视觉 44px）。按「样式定义的怎么样就怎么样」取声明值；如粘贴后过松再调。
5. **WeChat 平台上限**（ponytail: 微信编辑器粘贴过滤是黑盒，无法客户端修复）：
   - `position:absolute` 的 blockquote 引号、hr logo 可能被过滤；
   - 外链 `<img src>` 可能被微信替换/清除（微信要求图片入素材库）；
   - `text-emphasis` 在旧 WebView 可能不渲染（退化为普通文本）。
6. **预览图片加载无缓存**（ponytail: 每次解码远端图片，量大时可引入 coil；升级路径明确）。
7. **链接文本仅纯文本**、表格对齐标记解析但忽略、`details`/脚注非 Markdown 子集元素不实现。
8. **跳过**：`@media 720px`（桌面/微信不适用）；导出中的 hover/transition（静态介质）；预览 hover 动画用 `animateColorAsState` 简化实现。
9. **双侧样式值各自维护**（shared-ui / data 不互相依赖，沿用现有 heading() 双份模式），docs/hydrogen.scss 是对照基准。

## 实施顺序

1. `docs/hydrogen.scss` 入库。
2. contracts 模型扩展 → parser 扩展 + parser 测试（先让文档结构绿）。
3. data 导出样式与渲染器 + html 测试。
4. shared-ui 预览样式与组件（含新 widget）。
5. 全量更新/新增测试。

## 验证

1. `.\kotlin.bat check`（编译 + 全部测试通过）。
2. 运行桌面应用人工核对预览：标题层级/边框 hover、代码块、链接图标、blockquote 引号与 hover、hr logo、表格斑马纹、嵌套/任务列表、图片加载。
3. 「复制」后在浏览器空白页粘贴导出 HTML，核对内联样式（blockquote 引号、hr logo、链接图标、text-emphasis）是否保留；微信编辑器的过滤行为属平台上限，仅记录不阻塞。
