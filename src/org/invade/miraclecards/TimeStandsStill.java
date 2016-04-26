/*
 * EnemiesConvert.java
 *
 * Created on March 22, 2006, 9:37 AM
 *
 */

package org.invade.miraclecards;

import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.rules.GodstormRules;

public class TimeStandsStill extends WarCard implements AutomaticCard {
    
    public TimeStandsStill(int powerUpCost, int strength) {
        super(powerUpCost, "Time Stands Still");
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board) && board.getYear() > 1;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        board.setYear(board.getYear() - 1);
    }
    
    public String getDescriptionString() {
        return "Move the epoch marker back 1 space on the epoch chart.";
    }
    
}
