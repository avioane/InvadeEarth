/*
 * RagnarokCometh.java
 *
 * Created on March 22, 2006, 3:08 PM
 *
 */

package org.invade.miraclecards;

import org.invade.AbstractMoveVerifier;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.Territory;
import org.invade.TurnMode;
import org.invade.rules.GodstormRules;

public class TheLandIsPurged extends WarCard {
    
    public TheLandIsPurged(int powerUpCost) {
        super(powerUpCost, "The Land Is Purged");
    }
    
    public boolean canPlay(Board board) {
        if( ! super.canPlay(board) ) {
            return false;
        }
        for( Territory territory : board.getTerritories() ) {
            if( territory.isPlague() ) {
                return true;
            }
        }
        return false;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        board.setMoveVerifier(new AbstractMoveVerifier() {
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory territory = (Territory)move;
                verify( territory.isPlague(),
                        "Choose a territory afflicted by the plague");
            }
        });
        Territory territory = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        board.sendMessage(territory.getName() + " is purged");
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        territory.setPlague(false);
        territory.getForce().setRegularUnits(0);
        territory.update();
    }
    
    public String getDescriptionString() {
        return "Choose a territory with a plague marker.  Destroy that "
                + "plague marker and any armies in its territory.";
    }
    
}
