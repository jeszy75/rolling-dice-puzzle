package rollingdice.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    private final ImageStorage<Integer> boardImageStorage = new OrdinalImageStorage(Dice.class,
            null,
            "dieWhite1.png",
            "dieWhite2.png",
            "dieWhite3.png",
            "dieWhite4.png",
            "dieWhite5.png",
            "dieWhite6.png"
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
    private void initialize() {
        initializeGrid();
        initializeSideViews();
        setupAccelerators();
        resetGame();
    }

    private void initializeGrid() {
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
        for (var sideView : List.of(northSideView, eastSideView, southSideView, westSideView)) {
            sideView.setOnMouseClicked(this::handleMouseClick);
        }
    }

    private void setupAccelerators() {
        Platform.runLater(() -> {
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
        state = new RollingDiceState();
        if (topSideView.getParent() != null) {
            getSquare(state.getDicePosition()).getChildren().remove(topSideView);
        }
        updateSideViews();
        placeDiceOnBoard();
    }

    private void updateSideViews() {
        sideViews.forEach((side, imageView) -> {
            var diceValue = state.getDiceValue(side);
            diceImageStorage.get(diceValue)
                    .ifPresent(imageView::setImage);
        });
    }

    private void placeDiceOnBoard() {
        getSquare(state.getDicePosition()).getChildren().add(topSideView);
    }

    private void handleMouseClick(MouseEvent mouseEvent) {
        var imageView = (ImageView) mouseEvent.getSource();
        var side = getSideForImageView(imageView);
        var direction = Direction.valueOf(side.name());
        Logger.debug("Direction chosen: {}", direction);
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
        if (state.isLegalMove(direction)) {
            var oldPos = state.getDicePosition();
            state.makeMove(direction);
            var newPos = state.getDicePosition();
            moveDice(oldPos, newPos);
            updateSideViews();
            if (state.isSolved()) {
                showSolvedAndExit();
            }
        } else {
            Logger.debug("Invalid move");
        }
    }

    private void moveDice(Position oldPos, Position newPos) {
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
