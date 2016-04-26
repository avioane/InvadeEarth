/*
 * TheWineOfEternalLife.java
 *
 * Created on May 26, 2006, 1:32 PM
 *
 */

package org.invade.miraclecards;

import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;

public class TheWineOfEternalLife extends MagicCard {
    
    private int strength;
    
    public TheWineOfEternalLife(int strength) {
        super("The Wine of Eternal Life");
        this.strength = strength;
    }
    
    public void doAtTurnStart(Board board, GameThread gameThread) throws EndGameException {
        board.getCurrentPlayer().getReinforcements().addRegularUnits(strength);
    }
    
    public String getDescriptionString() {
        return "Gain " + strength + " additional army at"
                + " the start of each of your turns.";
    }
    
}
