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

public class TheDeadWalk extends DeathCard {
    
    private int strength;
    
    public TheDeadWalk(int powerUpCost, int strength) {
        super(powerUpCost, "The Dead Walk");
        this.strength = strength;
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && board.getUnitCount(board.getCurrentPlayer(), GodstormRules.TEMPLE) > 0
                && board.getTerritoriesOwned(board.getCurrentPlayer(),
                TerritoryType.UNDERWORLD).size() > 0;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        board.setMoveVerifier(new DefaultMoveVerifier() {
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory from = (Territory)move;
                verifyIsFriendly(from, board.getCurrentPlayer());
                verifyType(from, TerritoryType.UNDERWORLD);
            }
        });
        final Territory from = (Territory)gameThread.take();
        board.setMoveVerifier(new DefaultMoveVerifier() {
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory to = (Territory)move;
                verifyIsFriendly(to, board.getCurrentPlayer());
                verify( to.getForce().getSpecialUnits().contains(
                        GodstormRules.TEMPLE), "Choose a territory with a temple");
            }
        });
        Territory to = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        
        board.setAttackingTerritory(from);
        board.setDefendingTerritory(to);
        board.setTurnMode(TurnMode.COMPLETE_FREE_MOVE);
        Force force = (Force)gameThread.take();
        force.getSpecialUnits().clear();
        if( force.getRegularUnits() > strength ) {
            force.setRegularUnits(strength);
        }
        board.sendMessage("The dead of " + from.getName() + " awaken in "
                + to.getName() + " (" + force.getRegularUnits() + ")" );
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        from.getForce().subtract(force);
        to.getForce().add(force);
    }
    
    public String getDescriptionString() {
        return "Move up to " + strength + " of your armies from any Underworld "
                + "space to a temple you control.";
    }
    
}
