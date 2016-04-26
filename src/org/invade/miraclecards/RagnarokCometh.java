/*
 * RagnarokCometh.java
 *
 * Created on March 22, 2006, 3:08 PM
 *
 */

package org.invade.miraclecards;

import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Territory;
import org.invade.rules.GodstormRules;

public class RagnarokCometh extends DeathCard implements AutomaticCard {
    
    public RagnarokCometh(int powerUpCost) {
        super(powerUpCost, "Ragnarok Cometh");
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        for( Territory territory : board.getTerritories() ) {
            territory.getForce().getSpecialUnits().remove(GodstormRules.WAR);
            territory.getForce().getSpecialUnits().remove(GodstormRules.SKY);
            territory.update();
        }
    }
    
    public String getDescriptionString() {
        return "Banish all War gods and Sky gods.";
    }
    
}
