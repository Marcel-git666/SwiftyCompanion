# Swifty Companion

Native Android app for browsing 42 student profiles through the [42 API v2](https://api.intra.42.fr/apidoc).
Built with Kotlin and Jetpack Compose.

## Features

| Subject requirement | Implementation |
|---|---|
| At least 2 views | Search screen → Profile screen |
| Handle all errors | Not found (404), no connection, rate limit (429), invalid credentials (401), server error (5xx), invalid login input, unexpected response |
| Login details | Photo, name, email, location, wallet, evaluation points, level |
| Skills with level and percentage | Skills of the main cursus, sorted by level, with a progress bar |
| Completed projects incl. failed | Grouped by cursus, Passed / Failed with final mark |
| Navigate back | Top bar arrow and system back gesture |
| Flexible layout | Adapts to width (phone, landscape, foldable, tablet) at a 600 dp breakpoint |
| No token per query | One token held in memory and reused for all requests |
| **Bonus:** token refresh | A new token is requested automatically when the API returns 401 and the failed request is retried |

## Setup

1. Register an application on the intra: **Settings → API → Register a new app**.
   The redirect URI is not used by the Client Credentials flow, so any value works.
2. Copy `.env.example` to `.env` in the project root and fill in your keys:
   ```
   FORTY_TWO_UID=your_uid
   FORTY_TWO_SECRET=your_secret
   ```
   `.env` is git-ignored. Gradle reads it at build time and exposes the values as `BuildConfig` fields.
3. Open the project in Android Studio, sync Gradle, and run the `app` configuration.

Requirements: Android Studio with AGP 9.4+, Android 11 (API 30) or newer.

## Architecture

The layer structure follows feature-based clean architecture:

```
UI (Compose) → ViewModel → Repository → Remote source → Ktor → 42 API
     ui/       presentation/   domain/ + data/      device/
```

| Layer | Package | Responsibility |
|---|---|---|
| `device` | `features/*/device` | Talks to the outside world (Ktor). DTOs mirror the JSON exactly. Exceptions are converted to typed errors here. |
| `data` | `features/*/data` | Repository implementation, in-memory cache, DTO → domain mapping |
| `domain` | `features/*/domain` | Repository interfaces and domain models, independent of the API and the UI |
| `presentation` | `presentation/*` | ViewModels exposing `StateFlow` state and accepting `Intent`s |
| `ui` | `ui/*` | Stateless Compose screens |
| `di` | `di/AppContainer.kt` | Manual dependency injection, created once in `Application` |

Errors are values, not exceptions: data sources return `Either<Data, Error>` and `UserError` is a sealed interface.
The compiler enforces that every error case is handled in the UI.

## Token management

- **Client Credentials Flow** (`grant_type=client_credentials`): the app authenticates itself, no user login.
- The Ktor `Auth` plugin with a bearer provider manages the token:
  - `loadTokens` fetches the token once before the first request and keeps it in memory.
  - `refreshTokens` runs when the API returns **401**, fetches a new token, and retries the original request transparently.
- The HTTP clients live in an `AppContainer` owned by `Application`, so the token survives screen rotation and other configuration changes.

### Demonstrating the bonus

Debug builds show a **"Debug: simulate token expiry"** button on the search screen.
It replaces the cached token with an invalid one. The next search (of a login that is not cached yet) gets a real 401 from the API and recovers automatically.

Filter Logcat with `tag:Auth tag:HTTP tag:UserRepository`:

```
Auth  DEBUG: using a simulated expired token
HTTP  REQUEST: https://api.intra.42.fr/v2/users/login  → RESPONSE: 401
Auth  Server returned 401 (token expired/invalid) → fetching a new one
HTTP  REQUEST: https://api.intra.42.fr/oauth/token     → RESPONSE: 200
HTTP  REQUEST: https://api.intra.42.fr/v2/users/login  → RESPONSE: 200
```

HTTP logging uses `LogLevel.INFO` and is enabled in debug builds only, so tokens and secrets never appear in the log.

## Libraries

| Library | Why |
|---|---|
| **Jetpack Compose + Material 3** | Modern declarative UI toolkit recommended by Google, with built-in light and dark theme and dynamic color |
| **Ktor Client** (OkHttp engine) | Kotlin-first and coroutine-based. The `Auth` plugin provides token caching and refresh on 401 out of the box. Multiplatform-ready. |
| **kotlinx.serialization** | Compile-time generated serializers that respect Kotlin null safety (unlike reflection-based Gson) |
| **kotlinx-datetime** / `kotlin.time` | Multiplatform date and time handling: parsing API timestamps, converting to local time |
| **Coil 3** | Async image loading for Compose with memory and disk cache |
| **Navigation Compose** | Type-safe routes (`@Serializable`). The back stack survives rotation and process death. |
| **Lifecycle ViewModel** | State survives configuration changes |

No DI framework is used. The dependency graph is small, so a manual `AppContainer` keeps it explicit.

## Project structure

```
app/src/main/java/com/example/swiftycompanion/
├── MainActivity.kt
├── SwiftyCompanionApplication.kt
├── common/
│   ├── device/          HttpClient.kt, ExpiredTokenSimulation.kt
│   └── utils/           Either.kt
├── di/                  AppContainer.kt
├── features/
│   ├── auth/            OAuth token (data, device, models)
│   └── users/           User lookup (data, device, domain, errors)
├── presentation/
│   ├── search/          SearchViewModel, SearchState
│   └── profile/         ProfileViewModel, ProfileState
└── ui/
    ├── navigation/      AppNavHost (routes)
    ├── search/          SearchScreen
    ├── profile/         ProfileScreen, ProfileHeader, ProfileList
    ├── common/          shared UI helpers
    └── theme/
```

## Test scenarios

| Scenario | Expected result |
|---|---|
| Existing login, also `  LOGIN  ` with spaces and capitals | Profile opens |
| Non-existent login | "Student not found." |
| `.`, `..`, `a b` | "Invalid login…", no request sent |
| Airplane mode | "No internet connection." |
| Many searches in quick succession | Button disabled while loading. 429 shows "Too many requests…" |
| Rotate, toggle dark mode, unfold the device on the profile | Same screen and state, no new token |
| Process death (background the app, `adb shell am kill com.example.swiftycompanion`, return) | Profile restored and reloaded, Retry on error |
| Double tap back or Search | Single navigation, never an empty screen |
| Debug: simulate token expiry, then search a new login | 401 → new token → profile opens |
