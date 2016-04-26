/*
 * TheMightyTurnMeek.java
 *
 * Created on March 22, 2006, 12:00 PM
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

public class TheMightyTurnMeek extends YourIdolIsFalse {
    public TheMightyTurnMeek(int powerUpCost) {
        super(powerUpCost, "The Mighty Turn Meek");
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && board.getUnitCount(board.getAttackingTerritory().getOwner(),
                GodstormRules.TEMPLE) > 0;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        final Territory attacking = board.getAttackingTerritory();
        Force force = chooseAnAttackingGod(board, gameThread);
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        board.setMoveVerifier(new AbstractMoveVerifier() {
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory territory = (Territory)move;
                verify( territory.getOwner() == attacking.getOwner(),
                        "Choose a territory controlled by "
                        + attacking.getOwner().getName() );
                verify( territory.getForce().getSpecialUnits().contains(GodstormRules.TEMPLE),
                        "Choose a territory with a temple" );
            }
        });
        Territory moveTo = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        board.sendMessage(force.getSpecialUnits().get(0).toString()
        + " flees to a temple in " + moveTo.getName());
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        attacking.getForce().subtract(force);
        moveTo.getForce().add(force);
        attacking.update();
    }
    
    public String getWhenString() {
        return "Play when attacked and only if the attacking player controls "
                + "a temple.";
    }
    
    public String getDescriptionString() {
        return "Move a god from the attacking territory to a territory "
                + "with a temple the attacking player controls.";
    }
    
}
