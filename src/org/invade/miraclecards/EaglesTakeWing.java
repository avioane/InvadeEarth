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

public class EaglesTakeWing extends WarCard {
    
    public EaglesTakeWing(int powerUpCost) {
        super(powerUpCost, "Eagles Take Wing");
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && board.getTerritoriesOwned(board.getCurrentPlayer(),
                TerritoryType.LAND).size() > 1;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        board.setMoveVerifier(new DefaultMoveVerifier() {
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory from = (Territory)move;
                verifyIsFriendly(from, board.getCurrentPlayer());
                verifyType(from, TerritoryType.LAND);
            }
        });
        final Territory from = (Territory)gameThread.take();
        board.setMoveVerifier(new DefaultMoveVerifier() {
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory to = (Territory)move;
                verifyIsFriendly(to, board.getCurrentPlayer());
                verifyType(to, TerritoryType.LAND);
                verify(from != to, "Choose a different territory");
            }
        });
        Territory to = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        
        board.setAttackingTerritory(from);
        board.setDefendingTerritory(to);
        board.setTurnMode(TurnMode.COMPLETE_FREE_MOVE);
        Force force = (Force)gameThread.take();
        board.sendMessage("Units from " + from.getName() + " move to "
                + to.getName());
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        from.getForce().subtract(force);
        to.getForce().add(force);
    }
    
    public String getDescriptionString() {
        return "Move any number of armies and gods from one territory you "
                + "control to another territory you control.";
    }
    
}
