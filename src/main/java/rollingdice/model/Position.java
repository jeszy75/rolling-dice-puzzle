package rollingdice.model;

/**
 * Represents a 2D position.
 *
 * @param row the row coordinate
 * @param col the column coordinate
 */
public record Position(int row, int col) {

    /**
     * {@return the position whose vertical and horizontal distances from this
     * position are equal to the coordinate changes of the direction given}
     *
     * @param direction the direction to move towards
     */
    public Position move(Direction direction) {
        return new Position(row + direction.getRowChange(), col + direction.getColChange());
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", row, col);
    }

}
