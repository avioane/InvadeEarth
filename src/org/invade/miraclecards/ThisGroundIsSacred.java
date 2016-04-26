/*
 * ThisGroundIsSacred.java
 *
 * Created on March 22, 2006, 10:59 AM
 *
 */

package org.invade.miraclecards;

import org.invade.AutomaticCard;
import org.invade.BlockInvasion;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Territory;
import org.invade.TurnMode;
import org.invade.rules.GodstormRules;

public class ThisGroundIsSacred extends SkyCard implements BlockInvasion, AutomaticCard {
    
    private Territory sacred;
    
    public ThisGroundIsSacred(int powerUpCost) {
        super(powerUpCost, "This Ground is Sacred");
    }
    
    public void doAction(Board board, GameThread gameThread)
    throws EndGameException {
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        sacred = board.getDefendingTerritory();
        board.getCardsInPlay().add(this);
    }
    
    public void checkForAction(Board board, GameThread gameThread)
    throws EndGameException {
        if( board.getTurnMode().equals(TurnMode.DECLARE_FREE_MOVES) ) {
            board.getCardsInPlay().remove(this);
        }
    }
    
    public String getName(boolean hidden, boolean inPlay) {
        if( inPlay ) {
            return getName(false, false) + " (" + sacred.getName() + ")";
        }
        return super.getName(hidden, inPlay);
    }
    
    public boolean blocks(Territory from, Territory to) {
        return to == sacred;
    }
    
    public String getDescriptionString() {
        return "Stop this invasion.  The defending territory can't be "
                + "attacked this turn.";
    }
    
}
