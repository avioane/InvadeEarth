/*
 * Stonehenge.java
 *
 * Created on May 26, 2006, 1:28 PM
 *
 */

package org.invade.miraclecards;

import org.invade.Board;
import org.invade.EconomicCard;

public class TheTabletsOfDestiny extends MagicCard implements EconomicCard {
    public TheTabletsOfDestiny() {
        super("The Tablets of Destiny");
    }
    
    public int getCardPriceChange(Board board) {
        return 0;
    }
    
    public int getCardPowerUpChange(Board board) {
        return 0;
    }
    
    public int getBidChange(Board board) {
        return (board.getCurrentPlayer() == getPlayer()) ? 3 : 0;
    }
    
    public String getDescriptionString() {
        return "When you sacrifice faith tokens for your turn-order bid, "
                + "add +3 to the total.";
    }
}
