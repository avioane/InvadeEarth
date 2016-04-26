/*
 * SkyCard.java
 *
 * Created on March 22, 2006, 9:34 AM
 *
 */

package org.invade.miraclecards;

import org.invade.Board;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.rules.GodstormRules;

public abstract class WarCard extends AbstractMiracleCard {
    public WarCard(int powerUpCost, String name) {
        super(GodstormRules.WAR, powerUpCost, name);
    }
        
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && (board.getTurnMode() == TurnMode.ACKNOWLEDGE_EACH_PLAYER);
    }
    
    public String getWhenString() {
        return "Play on your turn.";
    }    
}
