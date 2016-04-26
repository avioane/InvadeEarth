/*
 * RagnarokCometh.java
 *
 * Created on March 22, 2006, 3:08 PM
 *
 */

package org.invade.miraclecards;

import org.invade.AbstractMoveVerifier;
import org.invade.Board;
import org.invade.CommonBoardEvents;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.Territory;
import org.invade.TurnMode;
import org.invade.rules.GodstormRules;

public class TheGodsForsakeUs extends DeathCard {
    
    public TheGodsForsakeUs(int powerUpCost) {
        super(powerUpCost, "The Gods Forsake Us");
    }
    
    public boolean canPlay(Board board) {
        if( ! super.canPlay(board) ) {
            return false;
        }
        for( Territory territory : board.getTerritories() ) {
            if( (!territory.getForce().getSpecialUnits().contains(GodstormRules.TEMPLE))
            && !territory.isPlague() ) {
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
                verify( ! territory.getForce().getSpecialUnits().contains(GodstormRules.TEMPLE),
                        "Choose a territory without a temple");
                verify( ! territory.isPlague(),
                        "Choose a territory free of the plague");
            }
        });
        Territory territory = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        board.sendMessage("Plague strikes " + territory.getName());
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        territory.setPlague(true);
        CommonBoardEvents.sufferPlague(board, territory);
    }
    
    public String getDescriptionString() {
        return "Choose a territory that doesn't contain a temple or a plague "
                + "marker.  Place the extra plague marker in that territory.  "
                + "(Halve the number of armies, rounded down, and banish all "
                + "gods in that territory immediately.)";
    }
    
}
