/*
 * DrawEachTurn.java
 *
 * Created on March 28, 2006, 2:53 PM
 *
 */

package org.invade.miraclecards;

import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Player;
import org.invade.SpecialMove;
import org.invade.TurnMode;

public class AegisShieldOfAthena extends MagicCard implements BlockMiracle {
    
    public AegisShieldOfAthena() {
        super("Aegis, Shield of Athena");
    }
    
    public boolean checkForBlock(Board board, GameThread gameThread) throws EndGameException {
        Player current = board.getCurrentPlayer();
        board.setCurrentPlayer(getPlayer());
        board.sendMessage("Use Aegis, Shield of Athena (block miracle effects)?");
        board.setTurnMode(TurnMode.CHOOSE_YES_NO);
        SpecialMove choice = (SpecialMove)gameThread.take();
        board.setCurrentPlayer(current);
        if( choice == SpecialMove.YES ) {
            board.sendMessage(getPlayer() + " activates Aegis, Shield of Athena");
            board.getCardsInPlay().remove(this);
            getDeck().discard(this);
            return true;
        }
        return false;
    }
    
    public String getDescriptionString() {
        return "Discard Aegis, Shield of Athena to cancel the effects of any "
                + "miracle card just played.  Relics can't be cancelled.";
    }
    
}
