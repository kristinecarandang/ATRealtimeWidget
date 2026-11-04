# Handoff: what's done and what's next

Last updated: 2026-11-04 (after PR #12, bus stops)

## Done

| PR | Feature |
|---|---|
| #1 | Initial project setup |
| #2 | Connect to the AT API (stop lookup) |
| #3 | Real-time departures for a stop |
| #4 | Live trip updates (delay / cancelled status) |
| #5 | CI: unit tests and lint run on every PR |
| #6 | CI: detekt code quality check |
| #7 | Departure screen (Compose) with loading / no departures / loaded states |
| #8, #9 | `CLAUDE.md` project guide |
| #10 | Hide departures that have already left |
| #11 | Platform info: train rows show "Pl. 1" in a right-hand column |
| #12 | Bus stops: bus rows show the route number after the time (e.g. `08:14   38   ONEHUNGA   on time`). Train rows are unchanged. |

## Next steps (suggested order)

1. **Lint tidy-up** (small, optional). Remove the unused template colours in
   `res/values/colors.xml`, remove the repeated `android:label` on the activity in
   `AndroidManifest.xml`, and move the Retrofit versions from `app/build.gradle.kts`
   into `gradle/libs.versions.toml`. Don't upgrade any versions as part of this.
2. **Task 10: error handling.** Replace the temporary catch-all in
   `DepartureRepository.kt` with specific errors (no internet, AT server error, etc.)
   and add an error state to the screen.
3. **Keep the screen when the phone rotates.** UI state currently lives in
   `remember { mutableStateOf(...) }` in `MainActivity.kt`, so a rotation reloads it.
4. **Stop picker.** Let the user choose the stop instead of the hard-coded
   `DEFAULT_STOP_ID`.
5. **Home screen widget (Glance).** The widget the project is named after.

Version upgrades (Gradle, Compose, Kotlin, Retrofit 3) are flagged by lint but are
optional. Each one would need its own branch.

## Things to remember

- The committed stop is Onehunga (`605-b9605c8e`). To test a bus stop, temporarily change
  `DEFAULT_STOP_ID` to Mangere College (`2419-9c55e172`) and **don't commit it**.
- Live updates for buses haven't been checked against real bus data yet. They're assumed
  to match by `trip_id` like trains do.
- Keep `agp = "9.4.1"` in `gradle/libs.versions.toml`.
- Never print or commit `AT_API_KEY`.
- Before every push, run these one at a time; all must say BUILD SUCCESSFUL:
  `./gradlew detekt`, `./gradlew testDebugUnitTest`, `./gradlew lintDebug`.
- Lint currently shows 16 old warnings (versions, unused colours, etc.) and 0 errors.
  A new warning in a file you changed is worth a look.
