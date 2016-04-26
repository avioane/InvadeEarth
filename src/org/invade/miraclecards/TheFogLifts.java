/*
 * TheFogLifts.java
 *
 * Created on March 22, 2006, 12:16 PM
 *
 */

package org.invade.miraclecards;

import org.invade.AbstractMoveVerifier;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.Force;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.rules.GodstormRules;

public class TheFogLifts extends SkyCard {
    private int strength;
    
    public TheFogLifts(int powerUpCost, int strength) {
        super(powerUpCost, "The Fog Lifts");
        this.strength = strength;
    }
    
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && board.getTerritoriesOwned(board.getDefendingTerritory().getOwner(),
                TerritoryType.LAND).size() > 1;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        final Territory defending = board.getDefendingTerritory();
        final Territory attacking = board.getAttackingTerritory();
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        board.setMoveVerifier(new AbstractMoveVerifier() {
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory territory = (Territory)move;
                verify( territory.getOwner() == defending.getOwner()
                && territory != defending,
                        "Choose another one of your territories");
            }
        });
        Territory moveFrom = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        board.setAttackingTerritory(moveFrom);
        board.setTurnMode(TurnMode.COMPLETE_FREE_MOVE);
        Force force = (Force)gameThread.take();
        force.getSpecialUnits().clear();
        if( force.getRegularUnits() > strength ) {
            force.setRegularUnits(strength);
        }
        board.sendMessage("Armies from " + moveFrom.getName() + " are "
                + "revealed (" + force.getRegularUnits() + ")");
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        moveFrom.getForce().subtract(force);
        defending.getForce().add(force);
        board.setAttackingTerritory(attacking);
    }
    
    public String getDescriptionString() {
        return "Move up to " + strength + " of your armies from one of your "
                + "territories to the defending territory.";
    }
    
}
