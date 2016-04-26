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
import org.invade.PlayableDeck;
import org.invade.Player;

public class DrawEachTurn extends MagicCard {
    
    private PlayableDeck drawType;
    
    public DrawEachTurn(String name, PlayableDeck drawType) {
        super(name);
        this.drawType = drawType;
    }
    
    public void doAtTurnStart(Board board, GameThread gameThread) throws EndGameException {
        if( ! drawType.getCards().isEmpty() ) {
            getPlayer().getCards().add(drawType.draw());
        }
    }
    
    public String getDescriptionString() {
        return "Draw a " + drawType.getName()
        + " card at the start of each of your turns.";
    }
    
}
