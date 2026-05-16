package rollingdice.model;

import puzzle.State;
import puzzle.solver.BreadthFirstSearch;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the states of the rolling dice puzzle.
 */
public class RollingDiceState implements State<Direction, RollingDiceState> {

    // Empty squares are represented by 0s.
    private static final int[][] BOARD = {
            {4, 6, 2, 5, 1},
            {5, 2, 1, 0, 4},
            {6, 2, 3, 1, 5},
            {6, 4, 6, 3, 6},
            {5, 3, 4, 5, 1},
            {3, 6, 0, 3, 3}
    };

    /**
     * The number of rows in the game board.
     */
    public static final int ROWS = BOARD.length;

    /**
     * The number of columns in the game board.
     */
    public static final int COLS = BOARD[0].length;

    private final Dice dice;
    private Position dicePosition;

    /**
     * Creates a {@code RollingDiceState} object representing the original
     * initial state.
     */
    public RollingDiceState() {
        this(new Dice(), new Position(0, 0));
    }

    /**
     * Creates a {@code RollingDiceState} object with the specified dice
     * and position.
     *
     * @param dice the orientation of the dice
     * @param dicePosition the position of the dice on the game board
     */
    public RollingDiceState(Dice dice, Position dicePosition) {
        Objects.requireNonNull(dice, "dice must not be null");
        Objects.requireNonNull(dicePosition, "dicePosition must not be null");
        if (!isOnBoard(dicePosition)) {
            throw new IllegalArgumentException("dicePosition is not on the board");
        }
        this.dice = dice.copy();
        this.dicePosition = dicePosition;
    }

    /**
     * {@return the number on the specified side of the dice}
     *
     * @param side a side of the dice
     */
    public int getDiceValue(Dice.Side side) {
        return dice.getValue(side);
    }

    /**
     * {@return a copy of the dice to preserve encapsulation}
     */
    public Dice getDice() {
        return dice.copy();
    }

    /**
     * {@return the current position of the dice}
     */
    public Position getDicePosition() {
        return dicePosition;
    }

    /**
     * {@return whether the puzzle is solved}
     */
    @Override
    public boolean isSolved() {
        return dicePosition.row() == ROWS - 1 && dicePosition.col() == COLS - 1;
    }

    /**
     * {@return whether is it possible to roll the dice in the direction
     * specified}
     *
     * @param direction the direction in which the dice is rolled
     */
    @Override
    public boolean isLegalMove(Direction direction) {
        var newDicePosition = dicePosition.movedTowards(direction);
        return isOnBoard(newDicePosition)
                && (getBoardValue(newDicePosition) == dice.getValue(Dice.Side.TOP) || getBoardValue(newDicePosition) == 0);
    }

    /**
     * Rolls the dice in the direction specified.
     *
     * @param direction the direction in which the dice is rolled
     */
    @Override
    public void makeMove(Direction direction) {
        dice.roll(direction);
        dicePosition = dicePosition.movedTowards(direction);
    }

    /**
     * {@return the set of all moves that can be applied to the state}
     */
    @Override
    public Set<Direction> getLegalMoves() {
        return Arrays.stream(Direction.values())
                .filter(this::isLegalMove)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Direction.class)));
    }

    private boolean isOnBoard(Position position) {
        return 0 <= position.row() && position.row() < ROWS
                && 0 <= position.col() && position.col() < COLS;
    }

    /**
     * {@return the value of the square on the game board at the specified
     * coordinates}
     *
     * @param row the row coordinate of the square
     * @param col the column coordinate of the square
     */
    public static int getBoardValue(int row, int col) {
        return BOARD[row][col];
    }

    /**
     * {@return the value of the square on the game board at the specified
     * position}
     *
     * @param position the position of the square
     */
    public static int getBoardValue(Position position) {
        return BOARD[position.row()][position.col()];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return (o instanceof RollingDiceState that)
                && (dice.equals(that.dice))
                && Objects.equals(dicePosition, that.dicePosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dice, dicePosition);
    }

    /**
     * {@return a deep copy of this object}
     */
    @Override
    public RollingDiceState copy() {
        return new RollingDiceState(dice, dicePosition);
    }

    @Override
    public String toString() {
        return String.format("RollingDiceState[dice=%s,dicePosition=%s]", dice, dicePosition);
    }

    static void main() {
        var rollingDiceState = new RollingDiceState();
        new BreadthFirstSearch<Direction, RollingDiceState>()
                .solveAndPrintSolution(rollingDiceState);
    }

}
