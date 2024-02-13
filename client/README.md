While the project can be run out-of-the-box with Gradle, running it from within your IDE (Eclipse/IntelliJ) requires setting up OpenJFX.

First download (and unzip) an OpenJFX SDK from https://openjfx.io that matches your Java JDK, then add the following *VM* commands to your run configurations:

    --module-path="/path/to/javafx-sdk/lib" --add-modules=javafx.controls,javafx.fxml

Tip: Make sure you adapt the path to the lib(!) directory (not just the directory that you unzipped).
Tip: Double check that the path is correct. You will receive abstract error messages otherwise.
