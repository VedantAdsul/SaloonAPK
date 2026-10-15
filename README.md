# Salon Mirror 💈

**An Android app that analyzes facial features and suggests hairstyles and beard styles.**

Salon Mirror is a Java-based Android project that uses the device's front camera and Google ML Kit Face Detection to estimate a face shape, then presents grooming suggestions based on the selected service and gender.

## Features

- **Camera-based face analysis** — uses the front camera to capture frames for face detection.
- **Face-shape estimation** — analyzes detected face contours and calculates approximate proportions to classify face shape.
- **Hair and beard suggestions** — displays suggestions based on the estimated face shape and the selected service.
- **Service selection** — choose Haircut, Beard, or Both before starting the analysis.
- **Gender selection** — passes the selected option into the suggestion flow.
- **Analysis feedback** — displays progress while face features are sampled.
- **Try again flow** — return to the start screen to run another analysis.

> **Important:** Face-shape classification is an approximate, rule-based estimate based on detected facial contours. It is intended for grooming inspiration, not medical, biometric, or identity-related use. Results can vary with lighting, camera angle, and face positioning.

## Tech stack

- **Language:** Java
- **Platform:** Android
- **Face detection:** Google ML Kit Face Detection
- **Camera:** AndroidX CameraX
- **UI:** XML layouts, View Binding, AndroidX, Material Components
- **Build system:** Gradle (Kotlin DSL)

## Getting started

### Prerequisites

- Android Studio
- JDK 17 for the Android Gradle Plugin build
- Android SDK for **API 35**
- An Android device or emulator running **Android 7.0 (API 24)** or later
- A working front-facing camera for face analysis

### Setup

1. Clone this repository:

   ```bash
   git clone https://github.com/VedantAdsul/SaloonAPK.git
   ```

2. Open the cloned project folder in Android Studio.
3. Sync the project with Gradle and allow the required dependencies to download.
4. Run the `app` configuration on an Android device or emulator.
5. Grant camera permission when prompted, then choose the desired service and start the analysis.

You can also build a debug APK from the repository root using the Gradle wrapper.

**Windows**
```powershell
.\gradlew.bat assembleDebug
```

**macOS / Linux**
```bash
./gradlew assembleDebug
```

The debug APK is typically generated at `app/build/outputs/apk/debug/app-debug.apk`.

## How it works

1. Select the service and gender option on the start screen.
2. Open the camera preview and position your face in view.
3. Start analysis; CameraX supplies frames to ML Kit Face Detection.
4. The app samples facial contour points, calculates approximate proportions, and applies heuristic rules to estimate a face shape.
5. A results screen displays the estimated shape and relevant hair or beard suggestions.

## Permissions and privacy

- **Camera:** required for real-time face detection and the analysis flow.

Face analysis uses camera frames. Review and test the app's implementation before using it in environments where privacy is important. Avoid capturing or sharing images of other people without their consent.

## Contributing

Suggestions, bug reports, and improvements are welcome. When reporting an issue, include your Android version, device model, steps to reproduce, and relevant logs. Do not attach private images or other sensitive personal data.

## License

No license has been specified yet. Unless a license is added to this repository, the source code is not explicitly offered for reuse, modification, or redistribution.
