# highlights（vendored）

Vendored from [SnipMeDev/Highlights](https://github.com/SnipMeDev/Highlights) at commit
`4c0caa8105a766b03870aa3b16bf3d61d7c9a2bd` (upstream version 1.1.0, includes the post-release
multiline-comment fix). Licensed under Apache 2.0 — see [LICENSE](LICENSE.md).

维护约定：
- 原样迁移，不裁剪功能；修改保持可 cherry-pick 上游补丁的最小 diff。
- 包名 `dev.snipme.highlights` 不变，业务代码直接依赖。
- AGENTS.md 的"新函数必须加 KDoc"规则对本 vendored 模块整体豁免，仅对人为修改过的函数生效。

本地偏离记录：
- `HighlightsCancellationTest.returns immediately result from second invocation`：上游用墙钟时间断言
  （`time1 < time2`），依赖"第一次分析在取消前尚未开始"的调度时序，在快 CPU 机器上确定性失败
  （协作式取消只在 `ensureActive()` 检查点生效）。改为行为断言：第一次请求被取消、第二次请求成功。

