# WeiSome 主题 Token 体系设计与实现计划

## Summary

依据 `DESIGN.md`（Vivid Precision / Minimalist-Glassmorphism）为 weisome 建立自研主题 token 体系，**彻底移除 Material3 依赖**（用户确认），**字体先用系统字体**（用户确认，token 预留 `fontFamily` 字段）。参考 Cook 项目的做法（`CookTokens.kt` 纯 object token + `CookTheme.kt` 主题入口），但以 CompositionLocal + foundation 组件自实现替代 M3 `MaterialTheme`。

## Current State Analysis

- [WeiSomeTheme.kt](file:///e:/Workspace/Code/Jvm/weisome/shared-ui/src/com/rocybyte/weisome/ui/WeiSomeTheme.kt)：M3 `MaterialTheme` + `lightColorScheme` 薄封装，将被重写
- [WeiSomeDesignResources.kt](file:///e:/Workspace/Code/Jvm/weisome/shared-ui/src/com/rocybyte/weisome/ui/WeiSomeDesignResources.kt)：7 个 teal 系硬编码颜色（与 DESIGN.md 蓝/橙体系不符）+ `WeiSomeDimensions`（PagePadding 24dp / ContentSpacing 16dp / EditorMinLines），将被删除合并
- [WeiSomeApp.kt](file:///e:/Workspace/Code/Jvm/weisome/shared-ui/src/com/rocybyte/weisome/ui/WeiSomeApp.kt)：使用 M3 `Surface` + `MaterialTheme.colorScheme.background`
- [WechatArticleEditorScreen.kt](file:///e:/Workspace/Code/Jvm/weisome/shared-ui/src/com/rocybyte/weisome/page/article/screen/WechatArticleEditorScreen.kt)：使用 M3 `Button`/`Icon`/`OutlinedTextField`/`SegmentedButton`/`Text`/`MaterialTheme.typography`
- article widget 的 [Inline.kt](file:///e:/Workspace/Code/Jvm/weisome/shared-ui/src/com/rocybyte/weisome/page/article/widget/Inline.kt)、[CodeBlock.kt](file:///e:/Workspace/Code/Jvm/weisome/shared-ui/src/com/rocybyte/weisome/page/article/widget/CodeBlock.kt)、[ListBlock.kt](file:///e:/Workspace/Code/Jvm/weisome/shared-ui/src/com/rocybyte/weisome/page/article/widget/ListBlock.kt) 使用 M3 `Text` / `LocalTextStyle`
- M3 依赖仅在 `shared-ui/module.yaml`（`$compose.material3: exported`）；jvm-app 不依赖 M3
- 无字体资源，`composeResources/` 只有 drawable + strings

DESIGN.md 关键规格：
- 颜色：`#FFFFFF` 基底 + electric blue (`#0058bc`) / sunset orange (`#b42907`) / lavender tertiary，全套 M3 命名 token（surface、primary、error、fixed 变体等）
- 字体：Plus Jakarta Sans → 本次用 `FontFamily.SansSerif`；紧凑字距（headline 负 letter-spacing，em 单位）
- 形状：Rounded Level 2（sm 4dp / DEFAULT 8dp / md 12dp / lg 16dp / xl 24dp，1rem=16px 换算）
- 间距：4/8px 基础单位，gutter 24 / margin 32 / stack-xs 4 / sm 12 / md 24 / lg 48 / xl 80 / container-max 1280
- 组件规格：Primary 按钮 = 高饱和渐变填充 + 白字、无阴影或 hover 极淡同色光晕；Secondary = 白底 + 1px 高对比边框 + primary 文字；输入框 = `#F9F9FB` 底、focus 变白 + 2px primary 边框；分段控件 = Apple 风格滑动白 pill + 浅灰轨道

## Assumptions & Decisions

1. **彻底移除 M3**（用户确认）：所有 M3 组件用 foundation 自实现替换，删除 `$compose.material3` 依赖。AGENTS.md 中 "Use Material 3 components" 一条与用户本次指令冲突，以用户指令为准（AGENTS.md 文档本身不在本次修改范围）。
2. **系统字体**（用户确认）：`WeiSomeTypography` 每个 style 携带 `fontFamily = FontFamily.SansSerif`，后续补 Plus Jakarta Sans 时只需改一处构造。
3. **亮色单主题**：DESIGN.md 仅提供 light token，颜色/形状/间距做成纯 `internal object` 直接访问（Cook 模式），不用 CompositionLocal 包一层。仅 `LocalWeiSomeTextStyle` 需要 ambient（`BasicText` 样式继承链），用 `staticCompositionLocalOf`。
4. **`WeiSomeText` 统一文本组件**：foundation 的 `BasicText` 不读取任何 ambient text style，自实现一个 `WeiSomeText`（BasicText 包装，默认 `LocalWeiSomeTextStyle.current` + `WeiSomeColors.OnSurface` 色）替代 M3 `Text`。
5. **输入框 label 静态置顶**：不自实现 M3 浮动 label（复杂度不值），label 作为静态小标题放输入框上方，符合极简风格。
6. **按钮渐变**：Primary 用 Electric（`primary → primaryContainer` 线性渐变）；编辑器两个复制按钮一个 Primary（复制 HTML）一个 Secondary（复制掘金）以体现两级层次。
7. **页面边距按 DESIGN.md 更新**：`PagePadding` 24dp → `Spacing.margin` 32dp；`ContentSpacing` 16dp → 按语境映射 token（stack-sm 12 / stack-md 24）。
8. **`EditorMinLines = 12`** 是编辑器内容配置而非设计 token，移为 `WechatArticleEditorScreen.kt` 内 `private const`。
9. 数值换算：1rem = 16px；DESIGN.md px → Compose `dp`/`sp`；letterSpacing 用 `em` TextUnit（`(-0.04).em`），lineHeight 按比例换算为 `sp` 字面量（64×1.1=70.4 等）。
10. article widget 的 `WechatArticlePreviewStyles` 是微信文章内容样式（独立于 app 主题），保持不动，只迁移其 M3 `Text` 引用。

## Proposed Changes

### 一、主题 token 层（`shared-ui/src/com/rocybyte/weisome/ui/`）

**1. 新增 `ui/WeiSomeColors.kt`** — 全量颜色 token，`internal object WeiSomeColors`，逐条映射 DESIGN.md frontmatter（surface 系、on-surface 系、primary/secondary/tertiary 及 container、error、fixed 变体、outline、background）。命名转 camelCase（`surfaceContainerLow`、`primaryFixedDim`…）。

**2. 新增 `ui/WeiSomeTypography.kt`** — `internal object WeiSomeTypography`：

```kotlin
val display = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 64.sp,
    fontWeight = FontWeight.ExtraBold, lineHeight = 70.4.sp, letterSpacing = (-0.04).em)
val h1 = TextStyle(…48/700/57.6sp/-0.03em…)
val h2 = TextStyle(…32/700/38.4sp/-0.02em…)
val h3 = TextStyle(…24/600/31.2sp/-0.02em…)
val bodyLg = TextStyle(…18/400/28.8sp/-0.01em…)
val bodyMd = TextStyle(…16/400/25.6sp/-0.01em…)
val labelSm = TextStyle(…13/600/15.6sp/0.02em…)
```

同文件定义 `val LocalWeiSomeTextStyle = staticCompositionLocalOf { WeiSomeTypography.bodyMd }`。

**3. 新增 `ui/WeiSomeTokens.kt`** — 对应 Cook 的 `CookTokens.kt`：

```kotlin
internal object WeiSomeSpacing {
    val unit = 8.dp; val gutter = 24.dp; val margin = 32.dp
    val stackXs = 4.dp; val stackSm = 12.dp; val stackMd = 24.dp; val stackLg = 48.dp; val stackXl = 80.dp
    val containerMax = 1280.dp
}
internal object WeiSomeShapes {
    val sm = RoundedCornerShape(4.dp); val default = RoundedCornerShape(8.dp)
    val md = RoundedCornerShape(12.dp); val lg = RoundedCornerShape(16.dp)
    val xl = RoundedCornerShape(24.dp); val full = RoundedCornerShape(50)  // pill，仅 chips/tags 用
}
internal object WeiSomeBorders { val thin = 1.dp }
```

**4. 重写 `ui/WeiSomeTheme.kt`** — 删除全部 M3 引用：

```kotlin
/** Provides WeiSome ambient text style defaults to [content]. */
@Composable
internal fun WeiSomeTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalWeiSomeTextStyle provides WeiSomeTypography.bodyMd) { content() }
}
```

### 二、共享组件层（`shared-ui/src/com/rocybyte/weisome/widget/`，全部 `internal`）

**5. 新增 `widget/WeiSomeText.kt`** — M3 `Text` 替代：`BasicText` 包装，`style` 默认 `LocalWeiSomeTextStyle.current`，`color` 参数默认 `WeiSomeColors.onSurface`（通过 `style.copy(color=…)` 应用），保留 `modifier`/`text` 基本参数面。

**6. 新增 `widget/WeiSomeButton.kt`** — 两级按钮：
- `WeiSomePrimaryButton(text, onClick, enabled, modifier)`：`Brush.linearGradient(primary, primaryContainer)` 填充、白字 `labelSm`、`WeiSomeShapes.default` 圆角、内边距（水平 24 / 垂直 12）；hover 时 `Modifier.shadow(8.dp, shape, spotColor = primary.copy(alpha = 0.2f))` 极淡同色光晕；disabled 半透明 + 去交互
- `WeiSomeSecondaryButton(text, onClick, enabled, modifier)`：白底 + 1px `outlineVariant` 边框 + `primary` 文字
- 均用 foundation（`Box + clickable/pointerHoverState`），无 M3

**7. 新增 `widget/WeiSomeTextField.kt`** — DESIGN.md 输入框规格：`Box` 装饰（`surfaceContainerLow`(`#F9F9FB`→映射 `#f3f3f4`，实际用 DESIGN.md 指定的 `#F9F9FB` 需增补 token，见决策 11）+ focus 变白；默认 1px `outlineVariant` 边框，focus 2px `primary` 边框；`BasicTextField` 内核 + `cursorBrush = SolidColor(primary)`；参数面：`value`/`onValueChange`/`label`(静态置顶)/`placeholder`/`minLines`/`modifier`。focus 态用 `InteractionSource`/`focusable` 状态驱动。

**8. 新增 `widget/WeiSomeSegmentedControl.kt`** — Apple 风格分段控件：浅灰轨道（`surfaceContainerHigh` 圆角 `full`）+ `animateDpAsState` 滑动白色 pill + foundation `Icon`；API：`WeiSomeSegmentedControl(options: List<WeiSomeSegmentedOption>, selectedIndex, onSelected, modifier)`，`WeiSomeSegmentedOption(painter, contentDescription)`。

> 补充决策 11：DESIGN.md 组件规格中输入框底色 `#F9F9FB` 与 frontmatter 的 `surface-container-low #f3f3f4` 不一致，frontmatter 是 token 源，输入框用 `surfaceContainerLow`；同时为规格中的卡片边框色 `#F2F2F7` 增补 `WeiSomeColors.cardBorder` 常量（供边框使用，最小增补）。

### 三、迁移改造

**9. 修改 `ui/WeiSomeApp.kt`** — `Surface(color = MaterialTheme.colorScheme.background)` → `Box(Modifier.fillMaxSize().background(WeiSomeColors.background))`，删除 M3 import。

**10. 修改 `page/article/screen/WechatArticleEditorScreen.kt`**
- `Button` → `WeiSomeSecondaryButton`（复制掘金）/ `WeiSomePrimaryButton`（复制 HTML）
- `OutlinedTextField` → `WeiSomeTextField`
- `SingleChoiceSegmentedButtonRow`/`SegmentedButton`/M3 `Icon` → `WeiSomeSegmentedControl`（内部用 foundation Icon）
- M3 `Text` → `WeiSomeText`；`MaterialTheme.typography.headlineMedium` → `WeiSomeTypography.h2`；`bodyMedium` → `WeiSomeTypography.bodyMd`
- `WeiSomeDimensions.PagePadding` → `WeiSomeSpacing.margin`；`ContentSpacing` → 按语境 `WeiSomeSpacing.stackSm/stackMd`；`EditorMinLines` → 文件内 `private const val EDITOR_MIN_LINES = 12`
- 删除 `@OptIn(ExperimentalMaterial3Api::class)` 与全部 M3 import

**11. 修改 article widget 三文件**（`Inline.kt`、`CodeBlock.kt`、`ListBlock.kt`）— M3 `Text` → `WeiSomeText`；M3 `LocalTextStyle` → `LocalWeiSomeTextStyle`（Inline.kt）。其余逻辑不动。

**12. 修改 `shared-ui/module.yaml`** — 删除 `- $compose.material3: exported`。

**13. 删除 `ui/WeiSomeDesignResources.kt`**（token 全部并入新文件，`EditorMinLines` 已迁移）。

## 执行顺序

1. 新增 token 层四文件（Colors → Typography → Tokens → Theme 重写）
2. 新增 widget 层四文件（Text → Button → TextField → SegmentedControl）
3. 迁移 WeiSomeApp + EditorScreen + article widgets
4. 删除 WeiSomeDesignResources.kt、清理 module.yaml
5. 验证

## Verification

1. `.\kotlin.bat check` — 编译 + 现有 52 个测试全绿（token/UI 为声明式常量与无状态组件，不新增测试；现有测试保证无回归）
2. 人工/桌面运行验证：编辑器界面（标题 h2 字重、渐变主按钮、白底描边次按钮、分段控件滑动 pill、输入框 focus 白底 + 2px 蓝边框、页面 32dp 边距）与 DESIGN.md 风格一致
3. 确认仓库内无残留 `androidx.compose.material3` import（grep 验证）
