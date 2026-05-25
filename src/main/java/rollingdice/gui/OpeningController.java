package rollingdice.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jfxutils.JFXUtils;
import org.tinylog.Logger;

import java.io.IOException;

public class OpeningController {

    @FXML
    private TextField playerNameField;

    @FXML
    private void handleStart(ActionEvent event) throws IOException {
        Logger.debug("Click on Start button");
        Logger.info("Player name: {}", playerNameField.getText());
        Stage stage = JFXUtils.getWindow(playerNameField);
        JFXUtils.loadFXML(stage, OpeningController.class, "/game.fxml",
                (RollingDiceController controller) -> {
                    controller.setPlayerName(playerNameField.getText());
                });
    }
    
}
