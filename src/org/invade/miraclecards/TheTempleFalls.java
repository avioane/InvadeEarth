/*
 * RagnarokCometh.java
 *
 * Created on March 22, 2006, 3:08 PM
 *
 */

package org.invade.miraclecards;

import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.rules.DefaultMoveVerifier;
import org.invade.rules.GodstormRules;

public class TheTempleFalls extends WarCard {
    
    public TheTempleFalls(int powerUpCost) {
        super(powerUpCost, "The Temple Falls");
    }
    
    public boolean canPlay(Board board) {
        if( ! super.canPlay(board) ) {
            return false;
        }
        for( Territory territory : board.getTerritories() ) {
            if( territory.getForce().getSpecialUnits().contains(GodstormRules.TEMPLE) ) {
                for( Territory owned : board.getTerritoriesOwned(territory.getOwner()) ) {
                    if( !owned.getForce().getSpecialUnits().contains(GodstormRules.TEMPLE) ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        board.setMoveVerifier(new DefaultMoveVerifier() {
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory from = (Territory)move;
                verifyType(from, TerritoryType.LAND);
                verify(from.getForce().getSpecialUnits().contains(GodstormRules.TEMPLE),
                        "Choose a territory with a temple");
            }
        });
        final Territory from = (Territory)gameThread.take();
        board.setMoveVerifier(new DefaultMoveVerifier() {
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory to = (Territory)move;
                verify(to.getOwner() == from.getOwner(),
                        "Choose a territory owned by " + from.getOwner().getName());
                verifyType(to, TerritoryType.LAND);
                verify(! to.getForce().getSpecialUnits().contains(GodstormRules.TEMPLE),
                        "Choose a territory with no temple");
            }
        });
        Territory to = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        from.getForce().getSpecialUnits().remove(GodstormRules.TEMPLE);
        to.getForce().getSpecialUnits().add(GodstormRules.TEMPLE);
    }
    
    public String getDescriptionString() {
        return "Move a temple any player controls to a different territory "
                + "that player controls.";
    }
    
}
