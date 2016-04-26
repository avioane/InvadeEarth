/*
 * Stonehenge.java
 *
 * Created on May 26, 2006, 1:28 PM
 *
 */

package org.invade.miraclecards;

import org.invade.Board;
import org.invade.EconomicCard;
import org.invade.Player;

public class MjolnirHammerOfThor extends MagicCard implements GodswarBonus {
    
    private int strength;
    
    public MjolnirHammerOfThor(int strength) {
        super("Mjolnir, Hammer of Thor");
        this.strength = strength;
    }
    
    public String getDescriptionString() {
        return "Add +" + strength + " to the result of any godswar battles you fight.";
    }

    public int getGodswarBonusValue(Player player) {
        return (player == getPlayer() ? strength : 0);
    }
}
