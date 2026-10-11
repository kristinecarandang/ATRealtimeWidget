# CLAUDE.md

Android app (Kotlin + Jetpack Compose) that shows the next 3 departures for a single
Auckland Transport (AT) stop, with live delay/cancellation status. No database.

## Commands

These are the checks CI runs on every PR to `main` (`.github/workflows/pr-checks.yml`):

```sh
./gradlew testDebugUnitTest   # unit tests
./gradlew lintDebug           # Android lint
./gradlew detekt              # code quality (config/detekt/detekt.yml)
```

## Setup

- `AT_API_KEY` must be set in `local.properties` (not committed). It is exposed as
  `BuildConfig.AT_API_KEY` via `app/build.gradle.kts`.

## Project map

- **Modules:** a single `:app` module, package `com.example.atrealtimewidget`.
- **Stack:** Compose + Material 3, Retrofit 2.11 + Gson, coroutines. minSdk 24, targetSdk 37.
- **Entry points:** no custom `Application` class. `MainActivity` is the only Activity
  and the launcher. No app widget exists yet, despite the project name.
- **Architecture:** a simplified layered design with no ViewModel. Most logic is in pure
  top-level functions, which makes it easy to unit test.

| Layer | Files |
|---|---|
| UI (Compose) | `MainActivity.kt`, `DepartureScreen.kt`, `ui/theme/*` |
| UI state | `DepartureUiState.kt`: sealed `Loading` / `NoDepartures` / `Loaded`, plus `toUiState()` |
| Repository | `DepartureRepository.kt`: `loadDepartureBoard()` returns a `DepartureBoard` |
| Domain logic (pure) | `ServiceQuery.kt` (4am service-day rollover), `DepartureFilter.kt` (next N pickups), `LiveStatus.kt` (delay/cancelled text), `DepartureRow.kt` (row mapping, wraps times past 24:00 back to the normal clock) |
| Network | `AtApiClient.kt` (singleton Retrofit, `https://api.at.govt.nz/`), `AtApiService.kt` (endpoints + Gson data classes) |
| Persistence | None |

## Data flow

1. `MainActivity` starts in `Loading`. A `LaunchedEffect` calls
   `loadDepartureBoard(DEFAULT_STOP_ID)` on `Dispatchers.IO`.
2. `loadDepartureBoard` makes three calls in sequence:
   - `getStop` gets the stop name.
   - `getStopTrips` uses the date and start hour from `serviceQueryFor(now)`.
     `nextDepartures()` then keeps the next 3 pickups.
   - `getTripUpdates` fetches live updates for those trip IDs, and `liveStatusByTrip()`
     turns them into a map by trip.
3. Each trip goes through `toDepartureRow(trip, live)` to build the `DepartureBoard`.
4. Any exception is logged and returns `null`.
5. `toUiState(board)` produces `Loaded` or `NoDepartures`, and `DepartureScreen` recomposes.

## Known limitations / planned work

- The stop ID is hard-coded (`DEFAULT_STOP_ID` in `MainActivity.kt`).
- UI state lives in `remember { mutableStateOf(...) }`, so it's lost when the screen rotates.
- Error handling is a temporary catch-all. "Task 10" will add specific error handling
  and an error UI state.

## Tests

- Unit tests in `app/src/test/` cover each pure function: `ServiceQueryTest`,
  `DepartureFilterTest`, `LiveStatusTest`, `DepartureRowTest`, `DepartureUiStateTest`,
  `AtApiServiceTest`.
- Instrumented tests (`app/src/androidTest/`) contain only the template.
- When adding logic, keep it in pure top-level functions with a matching unit test.
