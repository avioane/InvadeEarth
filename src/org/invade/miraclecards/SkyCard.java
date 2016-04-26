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

public abstract class SkyCard extends AbstractMiracleCard {
    public SkyCard(int powerUpCost, String name) {
        super(GodstormRules.SKY, powerUpCost, name);
    }
        
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && board.getTurnMode().equals(TurnMode.ACKNOWLEDGE_INVASION)
        && board.getDefendingTerritory().getOwner().equals(board.getCurrentPlayer())
        && board.getDefendingTerritory().getType().equals(TerritoryType.LAND);
    }
    
    public String getWhenString() {
        return "Play when attacked.";
    }    
}
