package rollingdice.model;

import common.util.board.RelativeDirection;

/**
 * Represents the four main directions.
 */
public enum Direction implements RelativeDirection {

    /**
     * Represents the north direction.
     */
    NORTH(-1, 0),

    /**
     * Represents the east direction.
     */
    EAST(0, 1),

    /**
     * Represents the south direction.
     */
    SOUTH(1, 0),

    /**
     * Represents the west direction.
     */
    WEST(0, -1);

    private final int rowChange;
    private final int colChange;

    Direction(int rowChange, int colChange) {
        this.rowChange = rowChange;
        this.colChange = colChange;
    }

    /**
     * {@return the change in the row coordinate when moving to the direction}
     */
    public int getRowChange() {
        return rowChange;
    }

    /**
     * {@return the change in the column coordinate when moving to the
     * direction}
     */
    public int getColChange() {
        return colChange;
    }

    /**
     * {@return the direction that corresponds to the coordinate changes
     * specified}
     *
     * @param rowChange the change in the row coordinate
     * @param colChange the change in the column coordinate
     */
    public static Direction of(int rowChange, int colChange) {
        for (var direction : values()) {
            if (direction.rowChange == rowChange && direction.colChange == colChange) {
                return direction;
            }
        }
        throw new IllegalArgumentException();
    }

}
