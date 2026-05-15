package rollingdice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class RollingDiceStateTest {

    private RollingDiceState state1;
    private RollingDiceState state2;
    private RollingDiceState state3;

    @BeforeEach
    void setUp() {
        state1 = new RollingDiceState(); // initial state
        state2 = new RollingDiceState(new Dice(2, 4, 6), new Position(5, 4)); // goal state (also a dead-end state)
        state3 = new RollingDiceState(new Dice(3, 1, 2), new Position(1, 2)); // intermediate state
    }

    @Test
    void isSolved() {
        assertFalse(state1.isSolved());
        assertTrue(state2.isSolved());
        assertFalse(state3.isSolved());
    }

    @Test
    void isLegalMove_state1() {
        assertFalse(state1.isLegalMove(Direction.NORTH));
        assertTrue(state1.isLegalMove(Direction.EAST));
        assertFalse(state1.isLegalMove(Direction.SOUTH));
        assertFalse(state1.isLegalMove(Direction.WEST));
    }

    @Test
    void isLegalMove_state2() {
        assertFalse(state2.isLegalMove(Direction.NORTH));
        assertFalse(state2.isLegalMove(Direction.EAST));
        assertFalse(state2.isLegalMove(Direction.SOUTH));
        assertFalse(state2.isLegalMove(Direction.WEST));
    }

    @Test
    void isLegalMove_state3() {
        assertFalse(state3.isLegalMove(Direction.NORTH));
        assertTrue(state3.isLegalMove(Direction.EAST));
        assertTrue(state3.isLegalMove(Direction.SOUTH));
        assertFalse(state3.isLegalMove(Direction.WEST));
    }

    @Test
    void makeMove_state1_east() {
        var dice = state1.getDice();
        dice.roll(Direction.EAST);
        var dicePosition = state1.getDicePosition().move(Direction.EAST);
        state1.makeMove(Direction.EAST);
        assertEquals(dice, state1.getDice());
        assertEquals(dicePosition, state1.getDicePosition());
    }

    @Test
    void makeMove_state3_south() {
        var dice = state3.getDice();
        dice.roll(Direction.SOUTH);
        var dicePosition = state3.getDicePosition().move(Direction.SOUTH);
        state3.makeMove(Direction.SOUTH);
        assertEquals(dice, state3.getDice());
        assertEquals(dicePosition, state3.getDicePosition());
    }

    @Test
    void getLegalMoves() {
        assertEquals(EnumSet.of(Direction.EAST), state1.getLegalMoves());
        assertEquals(EnumSet.noneOf(Direction.class), state2.getLegalMoves());
        assertEquals(EnumSet.of(Direction.EAST, Direction.SOUTH), state3.getLegalMoves());
    }

    @Test
    void testEquals() {
        assertTrue(state1.equals(state1));
        assertTrue(state1.equals(new RollingDiceState()));
        assertFalse(state1.equals(null));
        assertFalse(state1.equals("Hello, World!"));
        assertFalse(state1.equals(state2));
        assertFalse(state1.equals(state3));
    }

    @Test
    void testHashCode() {
        assertEquals(state1.hashCode(), state1.hashCode());
        assertEquals(state1.hashCode(), state1.copy().hashCode());
    }

    @Test
    void copy() {
        var copy = state1.copy();
        assertEquals(state1, copy);
        assertNotSame(state1, copy);
    }

    @Test
    void testToString() {
        assertEquals("RollingDiceState[dice=Dice[top=6,north=3,west=2],dicePosition=(0,0)]", state1.toString());
    }

}
