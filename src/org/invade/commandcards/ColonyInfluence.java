/*
 * ColonyInfluence.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.SpecialUnit;
import org.invade.TurnMode;

/**
 *
 * @author Jonathan Crosmer
 */
public class ColonyInfluence extends AbstractCommandCard implements AutomaticCard {
    
    public ColonyInfluence(SpecialUnit requiredCommander, int powerUpCost) {
        super(requiredCommander, powerUpCost, "Colony Influence (" + requiredCommander + ")");
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && board.getTurnMode().equals(TurnMode.ACKNOWLEDGE_GAME_OVER);
    }
    
    public void doAction(Board board, GameThread gameThread)
    throws EndGameException {
        board.getCurrentPlayer().setBonusPoints(
                board.getCurrentPlayer().getBonusPoints() + 3 );
    }
    
    public String getWhenString() {
        return END_OF_GAME;
    }
    
    public String getDescriptionString() {
        return "If your " + getRequiredCommander() + " is still alive, move " +
                "your score marker ahead 3 spaces.";
    }
    
}
