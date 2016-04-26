/*
 * EnemiesConvert.java
 *
 * Created on March 22, 2006, 9:37 AM
 *
 */

package org.invade.miraclecards;

import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.Territory;
import org.invade.TurnMode;
import org.invade.rules.DefaultMoveVerifier;
import org.invade.rules.GodstormRules;

public class PrayersAreAnswered extends WarCard {
    
    private int strength;
    
    public PrayersAreAnswered(int powerUpCost, int strength) {
        super(powerUpCost, "Prayers are Answered");
        this.strength = strength;
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && board.getUnitCount(board.getCurrentPlayer(), GodstormRules.TEMPLE) > 0;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        board.setMoveVerifier(new DefaultMoveVerifier() {
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory territory = (Territory)move;
                verifyIsFriendly(territory, board.getCurrentPlayer());
                verify( territory.getForce().getSpecialUnits().contains(
                        GodstormRules.TEMPLE), "Choose a territory with a temple");
            }
        });
        Territory territory = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        int effect = Math.min(board.getRandom().nextInt(strength) + 1,
                territory.getForce().getRegularUnits());
        board.sendMessage(territory.getName() + " receives reinforcements "
                + "(" + effect + ")");
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        territory.getForce().addRegularUnits(effect);
    }
    
    public String getDescriptionString() {
        return "Choose a territory with a temple you control.  Roll one of "
                + "the dice and gain that many armies in that territory.";
    }
    
}
