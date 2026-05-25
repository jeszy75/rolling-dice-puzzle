package rollingdice.gui;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import jfxutils.images.ImageStorage;
import jfxutils.images.OrdinalImageStorage;
import org.tinylog.Logger;
import rollingdice.model.Dice;
import rollingdice.model.Direction;
import rollingdice.model.Position;
import rollingdice.model.RollingDiceState;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RollingDiceController {

    @FXML
    private GridPane board;

    @FXML
    private ImageView northSideView;

    @FXML
    private ImageView eastSideView;

    @FXML
    private ImageView southSideView;

    @FXML
    private ImageView westSideView;

    private ImageView topSideView;

    private Map<Dice.Side, ImageView> sideViews;

    @FXML
    private TextField numberOfMovesField;

    private final ImageStorage<Integer> boardImageStorage = new OrdinalImageStorage(Dice.class,
            null,
            "dieWhite_border1.png",
            "dieWhite_border2.png",
            "dieWhite_border3.png",
            "dieWhite_border4.png",
            "dieWhite_border5.png",
            "dieWhite_border6.png"
    );

    private final ImageStorage<Integer> diceImageStorage = new OrdinalImageStorage(Dice.class,
            null,
            "dieRed1.png",
            "dieRed2.png",
            "dieRed3.png",
            "dieRed4.png",
            "dieRed5.png",
            "dieRed6.png"
    );

    private RollingDiceState state;

    @FXML
    private Label numberOfMovesLabel;

    private final IntegerProperty numberOfMoves = new SimpleIntegerProperty();

    private final StringProperty playerName =  new SimpleStringProperty();

    public void setPlayerName(String playerName) {
        this.playerName.set(playerName);
    }

    @FXML
    private void initialize() {
        initializeGrid();
        initializeSideViews();
        initializeNumberOfMoves();
        setupAccelerators();
        resetGame();
    }

    private void initializeGrid() {
        Logger.debug("Initializing board");
        for (var i = 0; i < board.getRowCount(); i++) {
            for (var j = 0; j < board.getColumnCount(); j++) {
                var square = createSquare(i, j);
                board.add(square, j, i);
            }
        }
    }

    private StackPane createSquare(int row, int col) {
        Logger.debug("Create square at ({},{}) with number {}", row, col,
                RollingDiceState.getBoardValue(row, col));
        var square = new StackPane();
        var imageView = new ImageView();
        var image = boardImageStorage.get(RollingDiceState.getBoardValue(row, col)).orElse(null);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setImage(image);
        square.getChildren().add(imageView);
        return square;
    }

    private void initializeSideViews() {
        Logger.debug("Initializing side views");
        createTopSideView();

        sideViews = new EnumMap<>(Dice.Side.class);
        sideViews.put(Dice.Side.NORTH, northSideView);
        sideViews.put(Dice.Side.EAST, eastSideView);
        sideViews.put(Dice.Side.SOUTH, southSideView);
        sideViews.put(Dice.Side.WEST, westSideView);
        sideViews.put(Dice.Side.TOP, topSideView);

        setupClickListeners();
    }

    private void createTopSideView() {
        topSideView = new ImageView();
        topSideView.setFitHeight(100);
        topSideView.setFitWidth(100);
    }

    private void setupClickListeners() {
        Logger.debug("Setting up click listeners");
        for (var sideView : List.of(northSideView, eastSideView, southSideView, westSideView)) {
            sideView.setOnMouseClicked(this::handleMouseClick);
        }
    }

    private void initializeNumberOfMoves() {
        Platform.runLater(() -> Logger.debug("Player name: {}", playerName.get()));
        numberOfMovesLabel.textProperty().bind(Bindings.when(playerName.isNotEmpty())
                .then(playerName.concat("'s moves:"))
                .otherwise("Moves:"));
        numberOfMovesField.textProperty().bind(numberOfMoves.asString());
    }

    private void setupAccelerators() {
        Platform.runLater(() -> {
            Logger.debug("Setting up accelerators");
            var scene = board.getScene();
            if (scene != null) {
                scene.getAccelerators().put(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN), this::resetGame);
                scene.getAccelerators().put(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN), Platform::exit);
                scene.getAccelerators().put(new KeyCodeCombination(KeyCode.UP), () -> processMove(Direction.NORTH));
                scene.getAccelerators().put(new KeyCodeCombination(KeyCode.RIGHT), () -> processMove(Direction.EAST));
                scene.getAccelerators().put(new KeyCodeCombination(KeyCode.DOWN), () -> processMove(Direction.SOUTH));
                scene.getAccelerators().put(new KeyCodeCombination(KeyCode.LEFT), () -> processMove(Direction.WEST));
            }
        });
    }

    private void resetGame() {
        Logger.debug("Resetting game");
        state = new RollingDiceState();
        if (topSideView.getParent() != null) {
            getSquare(state.getDicePosition()).getChildren().remove(topSideView);
        }
        updateSideViews();
        placeDiceOnBoard();
        numberOfMoves.set(0);
    }

    private void updateSideViews() {
        Logger.debug("Updating side views");
        sideViews.forEach((side, imageView) -> {
            var diceValue = state.getDiceValue(side);
            diceImageStorage.get(diceValue)
                    .ifPresent(imageView::setImage);
        });
    }

    private void placeDiceOnBoard() {
        Logger.debug("Placing dice on board");
        getSquare(state.getDicePosition()).getChildren().add(topSideView);
    }

    private void handleMouseClick(MouseEvent mouseEvent) {
        var imageView = (ImageView) mouseEvent.getSource();
        var side = getSideForImageView(imageView);
        var direction = Direction.valueOf(side.name());
        Logger.debug("Click on {} side", direction);
        processMove(direction);
    }

    private Dice.Side getSideForImageView(ImageView imageView) {
        return sideViews.entrySet().stream()
                .filter(entry -> entry.getValue() == imageView)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private void processMove(Direction direction) {
        Logger.info("Rolling dice to {}", direction);
        if (!state.isLegalMove(direction)) {
            Logger.warn("Invalid move");
            return;
        }
        makeMoveAndUpdateUI(direction);
        if (state.isSolved()) {
            Logger.info("Puzzle has been solved");
            showSolvedAndExit();
        }
    }

    private void makeMoveAndUpdateUI(Direction direction) {
        var oldPos = state.getDicePosition();
        state.makeMove(direction);
        moveDice(oldPos, state.getDicePosition());
        updateSideViews();
        numberOfMoves.set(numberOfMoves.get() + 1);
    }

    private void moveDice(Position oldPos, Position newPos) {
        Logger.debug("Moving dice from {} to {}", oldPos, newPos);
        getSquare(oldPos).getChildren().remove(topSideView);
        getSquare(newPos).getChildren().add(topSideView);
    }

    private void showSolvedAndExit() {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Puzzle Solved");
        alert.setHeaderText(null);
        alert.setContentText("Congratulations, you have successfully solved the puzzle!");
        alert.showAndWait();
        Platform.exit();
    }

    private StackPane getSquare(Position position) {
        for (var child : board.getChildren()) {
            if (GridPane.getRowIndex(child) == position.row() && GridPane.getColumnIndex(child) == position.col()) {
                return (StackPane) child;
            }
        }
        throw new IllegalArgumentException("Square is not on the board");
    }

}
