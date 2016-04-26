/*
 * YourIdolIsFalse.java
 *
 * Created on March 22, 2006, 11:36 AM
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
import org.invade.TurnMode;
import org.invade.rules.GodstormRules;

public class YourIdolIsFalse extends SkyCard {
    public YourIdolIsFalse(int powerUpCost) {
        super(powerUpCost, "Your Idol is False");
    }
    
    public YourIdolIsFalse(int powerUpCost, String name) {
        super(powerUpCost, name);
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && GodstormRules.hasDeity(board.getAttackingTerritory().getForce());
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        Territory attacking = board.getAttackingTerritory();
        Force force = chooseAnAttackingGod(board, gameThread);
        board.sendMessage(force.getSpecialUnits().get(0).toString()
        + " is banished from " + attacking.getName());
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        attacking.getForce().subtract(force);
        attacking.update();
        
    }
    
    protected Force chooseAnAttackingGod(Board board, GameThread gameThread)
    throws EndGameException {
        Territory defending = board.getDefendingTerritory();
        Territory attacking = board.getAttackingTerritory();
        board.setDefendingTerritory(attacking);
        board.setTurnMode(TurnMode.CHOOSE_COMMANDER);
        board.setMoveVerifier(new AbstractMoveVerifier() {
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Force force = (Force)move;
                verify( force.getMobileForce().getSize() == 1
                        && GodstormRules.hasDeity(force),
                        "Choose a god");
            }
        });
        Force force = (Force)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        board.setDefendingTerritory(defending);
        return force;
    }
    
    public String getDescriptionString() {
        return "Banish a god of your choice from the attacking territory.";
    }
    
}
