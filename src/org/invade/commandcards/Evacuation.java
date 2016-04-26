/*
 * Evacuation.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import org.invade.AbstractMoveVerifier;
import org.invade.Board;
import org.invade.rules.DefaultMoveVerifier;
import org.invade.EndGameException;
import org.invade.Force;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.Player;
import org.invade.SpecialUnit;
import org.invade.Territory;
import org.invade.TurnMode;


/**
 *
 * @author Jonathan Crosmer
 */
public class Evacuation extends AbstractCommandCard {
        
    public Evacuation(SpecialUnit requiredCommander, int powerUpCost) {
        super(requiredCommander, powerUpCost, "Evacuation");
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && board.getTurnMode().equals(TurnMode.ACKNOWLEDGE_INVASION)
        && board.getDefendingTerritory().getOwner().equals(board.getCurrentPlayer())
        && board.getTerritoriesOwned(board.getCurrentPlayer()).size() > 1;
    }
    
    public void doAction(Board board, GameThread gameThread)
    throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        board.setMoveVerifier(new AbstractMoveVerifier(){
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory territory = (Territory)move;
                DefaultMoveVerifier.verifyIsFriendly(territory, board.getCurrentPlayer());
            }
        });
        Territory territory = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        Force force = board.getDefendingTerritory().getForce().getMobileForce();
        territory.getForce().add(force);
        board.getDefendingTerritory().getForce().subtract(force);
        board.getDefendingTerritory().update();
        territory.update();
        board.sendMessage("Units in " + board.getDefendingTerritory().getName()
        + " were evacuated to " + territory.getName());
    }
    
    public String getWhenString() {
        return getOnInvadeYouString(null);
    }
    
    public String getDescriptionString() {
        return "Move all units from the attacked territory to any territory " +
                "you occupy.";
    }
    
}
