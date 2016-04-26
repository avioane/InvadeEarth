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

public class TheFaithfulGather extends WarCard implements AutomaticCard {
    
    private int strength;
    
    public TheFaithfulGather(int powerUpCost, int strength) {
        super(powerUpCost, "The Faithful Gather");
        this.strength = strength;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        for(Territory territory : board.getTerritories()) {
            if(territory.getOwner() == board.getCurrentPlayer()
            && territory.getForce().getSpecialUnits().contains(GodstormRules.TEMPLE)) {
                territory.getForce().addRegularUnits(strength);
            }
        }
    }
    
    public String getDescriptionString() {
        return "Gain " + strength + " armies in each territory where you "
                + "control a temple.";
    }
    
}
