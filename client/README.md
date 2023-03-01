While the project can be run out-of-the-box with Gradle, running it from within your IDE (Eclipse/IntelliJ) requires setting up OpenJFX.

First download (and unzip) an OpenJFX SDK from https://openjfx.io that matches your Java JDK, then add the following *VM* commands to your run configurations:

    --module-path="/Users/seb/Downloads/javafx-sdk-19.0.2.1/lib" --add-modules=javafx.controls,javafx.fxml

Make sure you adapt the path to the lib directory.