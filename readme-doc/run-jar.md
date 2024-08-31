## Running `.jar`

To execute the `.jar` file, please be aware of the following dependencies:
```
JavaFX Version: 17.0.11
JavaFX Runtime Version: 17.0.11+3
```

### Windows
1. Set `%PATH_TO_FX%` to `javafx-sdk/lib`, which you can [download from here](https://gluonhq.com/products/javafx/).
2. Run the following command in `cmd`:
	```shell
	java --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml,javafx.media --add-opens javafx.graphics/com.sun.glass.utils=ALL-UNNAMED --add-opens javafx.media/com.sun.media.jfxmedia=ALL-UNNAMED --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED -jar Pokemon.jar
	```
	<i>*I know the command is very long, but bear with me, it works just fine</i>

### Linux
Should be much easier, just run the following command:
```bash
java -jar Pokemon.jar
```
