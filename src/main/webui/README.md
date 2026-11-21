# c0-text-renderer

Angular 22 application configured with multi-language support (**English**, **Catalan**, and **Spanish**).

## Internationalization (i18n) Workflow

- **Source locale (`en-DEV`):** Raw text written in HTML templates (`i18n`) and TypeScript (`$localize`). Used only as a developer baseline and never shipped to production.
- **Production locales (`en`, `ca`, `es`):** Reviewed and translated files stored in `src/locale/`:
  - `src/locale/messages.en.xlf` — Reviewed production English
  - `src/locale/messages.ca.xlf` — Catalan
  - `src/locale/messages.es.xlf` — Spanish

## Available Commands

### Development Server

Run the local server at `http://localhost:4200/` in your preferred language:

| Command | Description |
| :--- | :--- |
| `npm start` | Starts the app using the reviewed **English (`en`)** translations (default). |
| `npm run start:ca` | Starts the app using **Catalan (`ca`)** translations. |
| `npm run start:es` | Starts the app using **Spanish (`es`)** translations. |
| `npm run start:en` | Starts the app using **English (`en`)** translations. |
| `npm run start:dev` | Starts the app using raw **`en-DEV`** source strings without loading translation files. |

### Translations

| Command | Description |
| :--- | :--- |
| `npm run extract-i18n` | Extracts all `i18n` and `$localize` strings into `src/locale/messages.xlf`. |

> **Workflow tip:** After running `npm run extract-i18n`, update `messages.en.xlf`, `messages.ca.xlf`, and `messages.es.xlf` with any new `<trans-unit>` entries before building for production.

### Production Build & Testing

| Command | Description |
| :--- | :--- |
| `npm run build` | Compiles production bundles for all three languages into `dist/c0-text-renderer/browser/` (`/en/`, `/ca/`, and `/es/`). |
| `npm run watch` | Rebuilds the development bundle automatically whenever source files change. |
| `npm test` | Runs unit tests using Vitest. |
