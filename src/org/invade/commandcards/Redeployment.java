/*
 * Redeployment.java
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
public class Redeployment extends AbstractCommandCard implements AutomaticCard {
    
    public Redeployment(SpecialUnit requiredCommander, int powerUpCost) {
        super(requiredCommander, powerUpCost, "Redeployment");
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && board.getTurnMode().equals(TurnMode.DECLARE_FREE_MOVES);
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.getCurrentPlayer().setFreeMoves( board.getCurrentPlayer().getFreeMoves() + 1 );
    }
    
     public String getWhenString() {
        return END_OF_TURN;
    }
     
     public String getDescriptionString() {
         return "Take an extra free move this turn.  You may only take this " +
                 "free move after you have finished attacking.";
     }
    
}
