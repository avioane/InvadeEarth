/*
 * EnemiesConvert.java
 *
 * Created on March 22, 2006, 9:37 AM
 *
 */

package org.invade.miraclecards;

import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.SpecialUnit;
import org.invade.Territory;
import org.invade.rules.GodstormRules;

public class TheSunGrowsCold extends WarCard implements AutomaticCard {
    public TheSunGrowsCold(int powerUpCost) {
        super(powerUpCost, "The Sun Grows Cold");
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        for(Territory territory : board.getTerritories()) {
            for(SpecialUnit special : territory.getForce().getSpecialUnits()) {
                if( GodstormRules.isDeity(special)
                && territory.getForce().getRegularUnits() > 0 ) {
                    territory.getForce().addRegularUnits(-1);
                    Heaven.getHeaven(board, territory.getOwner()).addUnits(1);
                }
            }
            // Call update() here to avoid ConcurrentModificationException
            territory.update();
        }
    }
    
    public String getDescriptionString() {
        return "Destroy 1 army in each territory that contains gods for each "
                + "god in that territory.";
    }
    
}
