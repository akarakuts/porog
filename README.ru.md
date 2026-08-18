# Порог

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)

English: [README.md](README.md)

**Порог** — экран на 10 секунд перед выходом из дома: погода на ближайшие часы, во сколько выходить на встречу, чеклист и парковка одним тапом. Без аккаунта.

## Возможности

- **Погода** — сейчас и на 2–3 часа (зонт / куртка) через [Open-Meteo](https://open-meteo.com).
- **Выходи в** — следующее событие календаря минус время на дорогу (по умолчанию 30 минут). Опциональное уведомление.
- **Чеклист** — ключи, кошелёк, пропуск, наушники, зарядка, лекарства; свои пункты; галочки сбрасываются каждый день.
- **Парковка** — тап сохраняет точку; «найти машину» открывает системные карты.
- **Виджет** на рабочем столе.
- Экраны **Помощь** и **О программе**.
- **Языки** — русский и английский. При запуске язык = язык **системы**.

## Требования и сборка

Как в [README.md](README.md): JDK 11+, Android SDK (compile 36, min 24), `./gradlew :app:assembleDebug`. Подпись **release** — раздел [Release signing](README.md#release-signing).

## CI (GitHub Actions)

Как в англ. README: [CI](.github/workflows/ci.yml), [Security](.github/workflows/security.yml), [Release](.github/workflows/release.yml) по тегу `v*`. [Dependabot](.github/dependabot.yml).

## Контакты

**Aleksey Karakuts** — [aleksey@karakuts.com](mailto:aleksey@karakuts.com)

## Лицензия

Программа распространяется на условиях **GNU GPLv3** — полный текст в файле [`LICENSE`](LICENSE).

Copyright (C) 2026 Aleksey Karakuts &lt;aleksey@karakuts.com&gt;
