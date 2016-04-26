/*
 * AbstractMiracleCard.java
 *
 * Created on February 24, 2006, 2:02 PM
 *
 */

package org.invade.miraclecards;

import org.invade.Board;
import org.invade.Player;
import org.invade.SpecialUnit;
import org.invade.commandcards.AbstractCommandCard;

public abstract class AbstractMiracleCard extends AbstractCommandCard {
    
    public AbstractMiracleCard(SpecialUnit requiredUnit, int powerUpCost, String name) {
        super(requiredUnit, powerUpCost, name);
    }
    
    public String getPlayMessage(String playerName, String name) {
        return playerName + ", by the " + getRequiredCommander()
        + ", invokes " + name;
    }
    
    public String getWhenString() {
        return BEFORE_FIRST_INVASION;
    }
    
}
