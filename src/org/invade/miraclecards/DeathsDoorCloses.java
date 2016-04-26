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
import org.invade.TerritoryType;
import org.invade.rules.GodstormRules;

public class DeathsDoorCloses extends DeathCard implements AutomaticCard {
    
    public DeathsDoorCloses(int powerUpCost) {
        super(powerUpCost, "Death's Door Closes");
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        for( Territory territory : board.getTerritories() ) {
            if( territory.getType() == TerritoryType.UNDERWORLD
                    && (territory.getForce().getSpecialUnits().contains(GodstormRules.CRYPT)
                    || territory.getForce().getSpecialUnits().contains(GodstormRules.ALTAR)) ) {
                territory.getForce().setRegularUnits((territory.getForce().getRegularUnits()
                + 1) / 2);
                territory.update();
            }
        }
    }
    
    public String getDescriptionString() {
        return "Halve the number of armies in every crypt and altar space in "
                + "the Underworld.";
    }
    
}
