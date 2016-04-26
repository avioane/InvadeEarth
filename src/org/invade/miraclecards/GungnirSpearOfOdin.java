/*
 * Stonehenge.java
 *
 * Created on May 26, 2006, 1:28 PM
 *
 */

package org.invade.miraclecards;

import org.invade.Board;
import org.invade.EconomicCard;

/* Hack warning:
 * Card effects are implemented directly in GodstormRules.
 */
public class GungnirSpearOfOdin extends MagicCard {
    public GungnirSpearOfOdin() {
        super("Gungnir, Spear of Odin");
    }
    
    public String getDescriptionString() {
        return "Reroll all 1's when you fight godswar battles.";
    }
}
