/*
 * RagnarokCometh.java
 *
 * Created on March 22, 2006, 3:08 PM
 *
 */

package org.invade.miraclecards;

import org.invade.Board;
import org.invade.EndGameException;
import org.invade.Force;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.rules.DefaultMoveVerifier;
import org.invade.rules.GodstormRules;

public class TheSirenCalls extends WarCard {
    
    public TheSirenCalls(int powerUpCost) {
        super(powerUpCost, "The Siren Calls");
    }
    
    public boolean canPlay(Board board) {
        if( ! super.canPlay(board) ) {
            return false;
        }
        for( Territory territory : board.getTerritories() ) {
            if( territory.getOwner() != board.getCurrentPlayer()
            && board.getTerritoriesOwned(territory.getOwner()).size() > 1 ) {
                return true;
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
                verifyIsEnemy(from, board.getCurrentPlayer());
                verify( board.getTerritoriesOwned(from.getOwner()).size() > 1,
                        "Choose a player with multiple territories" );
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
                verify(to != from, "Choose a different territory");
            }
        });
        Territory to = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        board.sendMessage("Forces in " + from.getName() + " and " + to.getName()
        + " are swapped");
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        Force fromForce = from.getForce().getMobileForce();
        Force toForce = to.getForce().getMobileForce();
        from.getForce().subtract(fromForce);
        from.getForce().add(toForce);
        to.getForce().subtract(toForce);
        to.getForce().add(fromForce);
    }
    
    public String getDescriptionString() {
        return "Swap all of the armies and gods between any 2 territories "
                + "that another player controls.";
    }
    
}
