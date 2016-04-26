/*
 * Die.java
 *
 * Created on July 6, 2005, 4:37 PM
 *
 */

package org.invade;

import java.util.Random;

public class Die implements Comparable<Die> {
    private int value;
    private DiceType diceType;

    public Die(Random random, DiceType diceType) {
        this(0, diceType);
        roll(random);
    }
    
    public Die(int value, DiceType diceType) {
        this.value = value;
        this.diceType = diceType;
    }
    
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public DiceType getDiceType() {
        return diceType;
    }

    public void setDiceType(DiceType diceType) {
        this.diceType = diceType;
    }
    
    public int compareTo(Die other) {
        return value - other.value;
    }
    
    public void roll(Random random) {
        if( diceType.equals(DiceType.SIX_SIDED) ) {
            value = random.nextInt(6) + 1;
        } else if( diceType.equals(DiceType.EIGHT_SIDED) ) {
            value = random.nextInt(8) + 1;
        }
    }
    
}
