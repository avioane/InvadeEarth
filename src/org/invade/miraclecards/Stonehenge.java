/*
 * Stonehenge.java
 *
 * Created on May 26, 2006, 1:28 PM
 *
 */

package org.invade.miraclecards;

import org.invade.Board;
import org.invade.EconomicCard;

public class Stonehenge extends MagicCard implements EconomicCard {
    public Stonehenge() {
        super("Stonehenge");
    }
    
    public int getCardPriceChange(Board board) {
        return (board.getCurrentPlayer() == getPlayer()) ? -1 : 0;
    }
    
    public int getCardPowerUpChange(Board board) {
        return 0;
    }
    
    public int getBidChange(Board board) {
        return 0;
    }
    
    public String getDescriptionString() {
        return "Sacrifice only 1 faith token, instead of 2, to obtain miracle"
                + " cards on your turn.";
    }
}
