/*
 * ThisGroundIsSacred.java
 *
 * Created on March 22, 2006, 10:59 AM
 *
 */

package org.invade.miraclecards;

import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Player;
import org.invade.TurnMode;
import org.invade.rules.GodstormRules;

public class FireSearsTheSky extends SkyCard implements GodswarBonus, AutomaticCard {
    
    private int strength;
    private Player player;
    
    public FireSearsTheSky(int powerUpCost, int strength) {
        super(powerUpCost, "Fire Sears the Sky");
        this.strength = strength;
    }
    
    public void doAction(Board board, GameThread gameThread)
    throws EndGameException {
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        player = board.getCurrentPlayer();
        board.getCardsInPlay().add(this);
    }
    
    public void checkForAction(Board board, GameThread gameThread)
    throws EndGameException {
        if( board.getTurnMode().equals(TurnMode.DECLARE_FREE_MOVES) ) {
            board.getCardsInPlay().remove(this);
        }
    }
    
    public int getGodswarBonusValue(Player player) {
        if(player != this.player) {
            return 0;
        }
        return strength;
    }
    
    public String getDescriptionString() {
        return "Add +" + strength + " to the result of any godswar battles "
                + "you fight until the end of this turn.";
    }
    
}
