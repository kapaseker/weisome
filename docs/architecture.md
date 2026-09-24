# WeiSome architecture

WeiSome is a JVM Desktop-only Compose Multiplatform application. `jvm-app` owns
the desktop window and calls the public `WeiSomeApp` composable from `shared-ui`.
`shared-ui` owns composable UI, navigation, and UI state. `contracts` defines the
domain models and repository and storage interfaces shared across modules. `data`
implements those interfaces for JVM using Room, DataStore, syntax highlighting,
Markdown rendering, and desktop clipboard integration. `jvm-app` starts Koin and
wires the platform implementations to the shared UI.
