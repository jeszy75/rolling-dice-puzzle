package rollingdice.model;

import common.util.Copyable;

import java.util.Objects;

/**
 * Represents a traditional six-sided dice.
 */
public class Dice implements Copyable<Dice> {

    /**
     * Represents the sides of a dice.
     */
    public enum Side {

        /**
         * Represents the top side of the dice.
         */
        TOP,

        /**
         * Represents the north side of the dice.
         */
        NORTH,

        /**
         * Represents the east side of the dice.
         */
        EAST,

        /**
         * Represents the west side of the dice.
         */
        WEST,

        /**
         * Represents the south side of the dice.
         */
        SOUTH,

        /**
         * Represents the bottom side of the dice.
         */
        BOTTOM
    }

    private int top;
    private int north;
    private int west;

    /**
     * Creates a {@code Dice} object with 6, 3, and 2 on the top, north, and
     * west sides, respectively.
     */
    public Dice() {
        this(6, 3, 2);
    }

    /**
     * Creates a {@code Dice} object with the numbers given on the top, north,
     * and west sides, respectively.
     *
     * @param top the number on the top side
     * @param north the number on the north side
     * @param west  the number on the west side
     */
    public Dice(int top, int north, int west) {
        checkSides(top, north, west);
        this.top = top;
        this.north = north;
        this.west = west;
    }

    private void checkSides(int top, int north, int west) {
        if (!isDiceValue(top) || !isDiceValue(north) || !isDiceValue(west)
                || top == north || top == west || north == west
                || top + north == 7 || top + west == 7 || north + west == 7) {
            throw new IllegalArgumentException("Invalid dice configuration");
        }
    }

    /**
     * {@return the number on the specified side of the dice}
     *
     * @param side a side of the dice
     */
    public int getValue(Side side) {
        return switch (side) {
            case TOP -> top;
            case NORTH -> north;
            case EAST -> 7 - west;
            case SOUTH -> 7 - north;
            case WEST -> west;
            case BOTTOM -> 7 - top;
        };
    }

    /**
     * Rolls the dice in the direction specified.
     *
     * @param direction the direction in which the dice is rolled
     */
    public void roll(Direction direction) {
        switch (direction) {
            case NORTH -> rollNorth();
            case EAST -> rollEast();
            case SOUTH -> rollSouth();
            case WEST -> rollWest();
        }
    }

    private void rollNorth() {
        var newNorth = top;
        top = getValue(Side.SOUTH);
        north = newNorth;
    }

    private void rollEast() {
        var newTop = west;
        west = getValue(Side.BOTTOM);
        top = newTop;
    }

    private void rollSouth() {
        var newTop = north;
        north = getValue(Side.BOTTOM);
        top = newTop;
    }

    private void rollWest() {
        var newTop = getValue(Side.EAST);
        west = top;
        top = newTop;
    }

    /**
     * {@return whether the given value is a valid dice face number}
     *
     * @param value the value to be tested
     */
    public static boolean isDiceValue(int value) {
        return 1 <= value && value <= 6;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return (o instanceof Dice that) && top == that.top && north == that.north && west == that.west;
    }

    @Override
    public int hashCode() {
        return Objects.hash(top, north, west);
    }

    @Override
    public Dice copy () {
        return new Dice(top, north, west);
    }

    @Override
    public String toString() {
        return String.format("Dice[top=%d,north=%d,west=%d]", top, north, west);
    }

    static void main(String[] args) {
        var dice = new Dice();
        System.out.println(dice);
        dice.roll(Direction.NORTH);
        System.out.println(dice);
        dice.roll(Direction.EAST);
        System.out.println(dice);
        dice.roll(Direction.SOUTH);
        System.out.println(dice);
        dice.roll(Direction.WEST);
        System.out.println(dice);
    }

}
