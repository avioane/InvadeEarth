/*
 * FreyasTearsOfGold.java
 *
 * Created on May 26, 2006, 1:32 PM
 *
 */

package org.invade.miraclecards;

import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;

public class FreyasTearsOfGold extends MagicCard {
    
    private int strength;
    
    public FreyasTearsOfGold(int strength) {
        super("Freya's Tears of Gold");
        this.strength = strength;
    }
    
    public void doAtTurnStart(Board board, GameThread gameThread) throws EndGameException {
        board.getCurrentPlayer().addEnergy(strength);
    }
    
    public String getDescriptionString() {
        return "Gain " + strength + " additional faith token at"
                + " the start of each of your turns.";
    }
    
}
