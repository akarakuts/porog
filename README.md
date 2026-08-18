# Porog

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)

Russian / Русский: [README.ru.md](README.ru.md)

**Porog** is a 10-second Android screen for the moment you leave home: weather for the next few hours, when to leave for the next calendar event, a leave-home checklist, and one-tap parking. No account.

## Features

- **Weather** — current temperature and the next 2–3 hours (umbrella / jacket hint) via [Open-Meteo](https://open-meteo.com) (no API key).
- **Leave by** — next calendar event minus your commute minutes (default 30). Optional “time to leave” notification.
- **Checklist** — keys, wallet, pass, headphones, charger, medicine; custom items; ticks reset each day.
- **Parking** — one tap saves a GPS pin; “find my car” opens system maps.
- **Home-screen widget** — temperature, leave-by time, next event.
- **Help** and **About** screens.
- **Locales** — English and Russian (`values` / `values-ru`). Language follows the **system** locale at launch (no in-app override).

## Android stack

| Area | Choice |
|------|--------|
| UI | Compose Material 3, Material icons |
| Navigation | Navigation Compose |
| State | ViewModel |
| Async | Kotlin Coroutines |
| Settings / checklist / parking | DataStore Preferences |
| Widget | Glance |
| Location | Play Services Location, with `LocationManager` fallback |

See `app/build.gradle.kts` for versions.

## Requirements

- **JDK 11+** (CI uses 21)
- **Android SDK** compile SDK **36** (minor 1); **minSdk 24**, **targetSdk 36**
- Gradle via `./gradlew`

## CI & automation

| Workflow | Trigger | Purpose |
|----------|---------|---------|
| [CI](.github/workflows/ci.yml) | push / PR to `main`, manual | `:app:check` (unit tests, Lint, compile) |
| [Security](.github/workflows/security.yml) | push / PR to `main`, weekly | OSV dependency scan, CodeQL |
| [Release](.github/workflows/release.yml) | tag `v*` | Upload-keystore–signed **APK + AAB** + GitHub Release (requires secrets) |

[Dependabot](.github/dependabot.yml) opens weekly PRs for Gradle and GitHub Actions dependencies.

## Build & run

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

## Release signing

`app/build.gradle.kts` loads **`keystore.properties`** from the repo root; if it exists, **`signingConfigs.upload`** is applied to **`release`**; otherwise **`release`** uses the **debug** keystore so fresh clones and CI still build installable APKs.

### 1. Create an upload keystore (once)

Prefer the RuStore helper scripts under `../rustore/porog/` (see that folder’s README). Or:

```bash
keytool -genkeypair -v \
  -keystore upload-keystore.jks \
  -alias upload \
  -keyalg RSA -keysize 2048 -validity 10000
```

Keep **`upload-keystore.jks`** and passwords in a password manager; **back up** the file — without it you cannot ship compatible updates.

### 2. Local signed `release` builds

1. Copy [`keystore.properties.example`](keystore.properties.example) to **`keystore.properties`** in the **repository root** (gitignored).
2. Set `storeFile` (often `../rustore/porog/upload-keystore.jks`), passwords, and `keyAlias=upload`.
3. Run:

```bash
./gradlew :app:assembleRelease :app:bundleRelease
```

or `./scripts/build_release.sh` after setting `store-upload.dir`.

If **`keystore.properties` is missing**, `release` still signs with the **debug** keystore — **do not** publish that build to an app store.

### 3. GitHub Actions tag releases (`v*`)

Configure these **repository secrets**:

| Secret | Value |
|--------|-------|
| `RELEASE_KEYSTORE_BASE64` | Base64 of `upload-keystore.jks` |
| `RELEASE_STORE_PASSWORD` | Keystore password |
| `RELEASE_KEY_ALIAS` | Key alias (e.g. `upload`) |
| `RELEASE_KEY_PASSWORD` | Key password |

## Testing

```bash
./gradlew :app:check
./scripts/check_strings_parity.sh
```

## Scripts

| Script | Purpose |
|--------|---------|
| `scripts/build_release.sh` | Signed APK/AAB → path from `store-upload.dir` |
| `scripts/check_strings_parity.sh` | Verify `values` / `values-ru` string key parity |

## Contact

**Aleksey Karakuts** — [aleksey@karakuts.com](mailto:aleksey@karakuts.com)

## License

This program is free software: you can redistribute it and/or modify it under the terms of the **GNU General Public License** as published by the Free Software Foundation, either **version 3** of the License, or (at your option) any later version.

See the [`LICENSE`](LICENSE) file for the full GPLv3 text.

Copyright (C) 2026 Aleksey Karakuts &lt;aleksey@karakuts.com&gt;
