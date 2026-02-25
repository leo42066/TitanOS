# TitanOS Android Scaffold

TitanOS is a modular Android/Linux-oriented mobile OS shell designed for high-performance phones (target: **B160V Android device**). This repository now contains a working Android app scaffold implementing:

- **TitanLauncher** with app drawer + quick mode toggles
- **SkyView Flight Radar** lock-screen activity with OpenSky data integration + orientation-aware AR radar positioning
- **TitanGameMode** auto trigger hooks for Steam Link/game packages + performance profile hooks
- **Overlay system** for FPS/CPU/GPU/battery + mini radar count
- **System monitoring** service with thermal/battery drain alerts
- **Persistent mode state** across reboot via DataStore

## Project Structure

- `app/src/main/java/com/titanos/feature/launcher`: launcher UI + app discovery
- `app/src/main/java/com/titanos/feature/skyview`: radar lock screen + aircraft feed
- `app/src/main/java/com/titanos/feature/gamemode`: game mode state and activation use case
- `app/src/main/java/com/titanos/feature/overlay`: overlay coordinator
- `app/src/main/java/com/titanos/feature/monitoring`: live system telemetry state + alerts
- `app/src/main/java/com/titanos/service`: background services for monitoring and game detection
- `app/src/main/java/com/titanos/data`: repositories for modes, aircraft APIs, and system telemetry
- `app/src/main/java/com/titanos/domain`: clean interfaces and use cases

## Notes

- APIs that require ROM-level privilege (CPU/GPU governor tuning, aggressive process killing, global overlay injection) are provided as integration hooks in repository methods.
- OpenSky feed refreshes every 5 seconds.
- `MainActivity` is registered as both launcher and `HOME` category for testing a custom launcher flow.

## Build

Use Android Studio (Giraffe+/AGP 8.4+) and run app module on Android 10+ device:

1. Open project
2. Sync Gradle
3. Build and install `app`
4. Grant overlay/location/accessibility permissions to test all modules
