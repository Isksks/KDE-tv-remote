let
  pkgs = import <nixpkgs> {
    config = {
      android_sdk.accept_license = true;
      allowUnfree = true;
    };
  };

  androidPkgs = pkgs.androidenv.composeAndroidPackages {
    platformVersions = [ "37" ];
    buildToolsVersions = [ "36.0.0" ];
    includeNDK = false;
    includeCmake = false;
    includeEmulator = true;
    includeSystemImages = true;
  };

in
pkgs.mkShell {
  ANDROID_SDK_ROOT = "${androidPkgs.androidsdk}/libexec/android-sdk";
  ANDROID_HOME = "${androidPkgs.androidsdk}/libexec/android-sdk";
  JAVA_HOME = pkgs.jdk25.home;

  buildInputs = with pkgs; [
    jdk25
    androidPkgs.androidsdk
  ];

  shellHook = ''
    echo "Java:         $(java -version 2>&1 | head -n 1)"
    echo "Android SDK:  $ANDROID_SDK_ROOT"
    echo
    echo "Build, lint, and run unit tests:"
    echo "  ./gradlew assembleDebug lintDebug testDebugUnitTest"
    echo
    echo "Create and start an API 36 emulator (optional):"
    echo "  avdmanager create avd -n kde-remote -k 'system-images;android-36;google_apis;x86_64' -d pixel"
    echo "  emulator -avd kde-remote"
    echo
    echo "Install on a connected device:"
    echo "  ./gradlew installDebug"
  '';
}
