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
import org.invade.SpecialMove;
import org.invade.TurnMode;

public class TheBookOfTheDead extends MagicCard {
    
    public TheBookOfTheDead() {
        super("The Book of the Dead");
    }
    
    public void doAtTurnStart(Board board, GameThread gameThread) throws EndGameException {
        board.sendMessage("Use The Book of the Dead (+4 faith, -2 armies)?");
        board.setTurnMode(TurnMode.CHOOSE_YES_NO);
        SpecialMove choice = (SpecialMove)gameThread.take();
        if( choice == SpecialMove.YES ) {
            board.sendMessage(board.getCurrentPlayer() + " activates The Book of the Dead");
            board.getCurrentPlayer().addEnergy(4);
            board.getCurrentPlayer().getReinforcements().addRegularUnits(-2);
        }
    }
    
    public String getDescriptionString() {
        return "Gain 4 faith tokens once each turn if you choose to raise "
                + "2 fewer armies at the start of each of your turns.";
    }
    
}
