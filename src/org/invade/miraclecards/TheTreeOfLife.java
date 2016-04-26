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

public class TheTreeOfLife extends MagicCard {
    
    public TheTreeOfLife() {
        super("The Tree of Life");
    }
    
    public void checkForAction(Board board, GameThread gameThread) throws EndGameException {
        while( board.getTurnMode() == TurnMode.BATTLE_RESULTS
                && getPlayer() == board.getDefendingTerritory().getOwner()
                && board.getDefendingTerritory().getNumberToDestroy() > 0
                && getPlayer().getEnergy() > 0 ) {
            Player current = board.getCurrentPlayer();
            board.setCurrentPlayer(getPlayer());
            board.sendMessage("Use The Tree of Life (-1 faith instead of losing a defending army)?");
            board.setTurnMode(TurnMode.CHOOSE_YES_NO);
            SpecialMove choice = (SpecialMove)gameThread.take();
            board.setCurrentPlayer(current);
            board.setTurnMode(TurnMode.BATTLE_RESULTS);
            if( choice == SpecialMove.YES ) {
                board.sendMessage(getPlayer() + " activates The Tree of Life");
                board.getDefendingTerritory().setNumberToDestroy(
                        board.getDefendingTerritory().getNumberToDestroy() - 1);
                getPlayer().addEnergy(-1);
            } else {
                break;
            }
        }
    }
    
    public String getDescriptionString() {
        return "Sacrifice 1 faith token instead of losing a defending army.";
    }
    
}
