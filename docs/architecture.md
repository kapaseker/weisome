# WeiSome architecture

WeiSome is a JVM Desktop-only Compose Multiplatform application. `jvm-app` owns
the desktop window and calls the public `WeiSomeApp` composable from `shared-ui`.
The `contracts` and `data` modules are intentionally empty placeholders for future
desktop features.

