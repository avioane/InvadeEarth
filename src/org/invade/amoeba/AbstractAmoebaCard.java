/*
 * AbstractAmoebaCard.java
 *
 * Created on March 13, 2006, 4:04 PM
 *
 */

package org.invade.amoeba;

import org.invade.Board;
import org.invade.SpecialUnit;
import org.invade.commandcards.AbstractCommandCard;

public abstract class AbstractAmoebaCard extends AbstractCommandCard {
    
    public AbstractAmoebaCard(String name) {
        super(null, 0, name);
    }
    
    public boolean canPlay(Board board) {
        return true;
    }


    public String getPlayMessage(String playerName, String name) {
        return playerName + " draws " + name;
    }

    
    public int getPowerUpCost(Board board) {
        return 0;
    }


    public SpecialUnit getRequiredCommander() {
        return null;
    }


    public String getWhenString() {
        return "Play immediately.";
    }
    
}
