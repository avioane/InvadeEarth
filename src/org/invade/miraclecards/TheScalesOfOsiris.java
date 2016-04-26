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
public class TheScalesOfOsiris extends MagicCard {
    public TheScalesOfOsiris() {
        super("The Scales of Osiris");
    }
    
    public String getDescriptionString() {
        return "Revive twice as many armies from crypts.";
    }
}
