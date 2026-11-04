# CLAUDE.md

Android app (Kotlin + Jetpack Compose) that shows the next 3 departures for a single
Auckland Transport (AT) stop, with live delay/cancellation status. Works for train
stations (shows the platform) and bus stops (shows the route number). No database.

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
| Domain logic (pure) | `ServiceQuery.kt` (4am service-day rollover), `DepartureFilter.kt` (next N pickups, skips departures that have already left), `LiveStatus.kt` (delay/cancelled text), `DepartureRow.kt` (row mapping, `departureText()`, `platformLabel()`, wraps times past 24:00 back to the normal clock), `PlatformLookup.kt` (`platformsByStop()`), `RouteLookup.kt` (`routeLabel()`, `routeLabelsByRoute()`) |
| Network | `AtApiClient.kt` (singleton Retrofit, `https://api.at.govt.nz/`), `AtApiService.kt` (endpoints: stop, stoptrips, tripupdates, route; plus Gson data classes) |
| Persistence | None |

## Data flow

1. `MainActivity` starts in `Loading`. A `LaunchedEffect` calls
   `loadDepartureBoard(DEFAULT_STOP_ID)` on `Dispatchers.IO`.
2. `loadDepartureBoard` makes these calls in sequence:
   - `getStop` gets the stop name.
   - `getStopTrips` uses the date and start hour from `serviceQueryFor(now)`.
     `nextDepartures()` then keeps the next 3 pickups that haven't left yet.
   - `getTripUpdates` fetches live updates for those trip IDs, and `liveStatusByTrip()`
     turns them into a map by trip.
   - `platformsByStop()` calls `getStop` once per distinct departure `stop_id` to get
     its `platform_code` (null for bus stops).
   - `routeLabelsByRoute()` calls `getRoute` once per distinct `route_id`. The label is
     `route_short_name` only when `route_type` is 3 (bus), otherwise null.
   - A failed platform or route lookup just means no label; the board still loads.
3. Each trip goes through `toDepartureRow(trip, live, platform, route)` to build the
   `DepartureBoard`. The screen shows `departureText(row)` on the left (time, route if
   any, destination, status) and `platformLabel()` in a fixed-width column on the right.
4. Any other exception is logged and returns `null`.
5. `toUiState(board)` produces `Loaded` or `NoDepartures`, and `DepartureScreen` recomposes.

## Known limitations / planned work

- The stop ID is hard-coded (`DEFAULT_STOP_ID` in `MainActivity.kt`). The committed
  default is Onehunga train station (`605-b9605c8e`). To test a bus stop, temporarily
  change it to Mangere College (`2419-9c55e172`) and don't commit that change.
- Live updates for buses are assumed to match by `trip_id` like trains (not yet confirmed
  with real bus data). If they don't match, the row shows no live status.
- UI state lives in `remember { mutableStateOf(...) }`, so it's lost when the screen rotates.
- Error handling is a temporary catch-all. "Task 10" will add specific error handling
  and an error UI state.

## Tests

- Unit tests in `app/src/test/` cover each pure function: `ServiceQueryTest`,
  `DepartureFilterTest`, `LiveStatusTest`, `DepartureRowTest`, `DepartureUiStateTest`,
  `AtApiServiceTest`, `PlatformLookupTest`, `RouteLookupTest`.
- Tests use fixed sample JSON copied from real AT responses, never live network calls.
- Instrumented tests (`app/src/androidTest/`) contain only the template.
- When adding logic, keep it in pure top-level functions with a matching unit test.

## Handoff notes

`claude/handoff-next-steps.md` lists what's done and what's next. Update it after each
feature is merged.

## How to work with me
- I'm not an app developer. Explain things simply, in plain English.
- Always show me the code and wait for my "yes" before committing anything.
- One new branch per feature. A feature's unit tests go on the same branch
  as a separate commit.
- Read a file before suggesting changes to it. Only open the files needed.
- Don't run the Gradle commands. I run them myself and paste in only the
  error if one fails.
