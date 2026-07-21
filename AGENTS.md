# Project Rules

- Before changing behavior, read the relevant local code.
- Keep changes scoped; avoid unrelated refactors and formatting-only noise.
- Do not revert or overwrite existing user changes unless explicitly requested.
- Do not create Git commits unless explicitly requested.
- Prefer existing project patterns over new abstractions.
- When editing Chinese or other non-ASCII text, use explicit UTF-8 handling and verify the resulting text.
- Keep tests in the package structure of the code they cover.
- Promote feature-local code to shared code only after it serves multiple features.

## Compose Desktop

- This project supports JVM Desktop only.
- `jvm-app` owns desktop window setup; `shared-ui` owns composable UI.
- Keep `WeiSomeApp` as the shared UI entry point.
- Use Material 3 components and Compose resources already present in the project.
- Keep UI state close to the feature that owns it; do not add dependency injection or navigation until a concrete feature needs it.

## Verification

- After code changes on Windows, run `.\\kotlin.bat check`.
- Add focused tests for new behavior and keep build output out of version control.
