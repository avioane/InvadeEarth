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
import org.invade.rules.GodstormRules;

public class OurFaithIsOurStrength extends WarCard implements AutomaticCard {
    
    private int strength;
    
    public OurFaithIsOurStrength(int powerUpCost, int strength) {
        super(powerUpCost, "Our Faith Is Our Strength");
        this.strength = strength;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        board.getCurrentPlayer().addEnergy(strength * board.getUnitCount(
                board.getCurrentPlayer(), GodstormRules.TEMPLE));
    }
    
    public String getDescriptionString() {
        return "Gain " + strength + " faith tokens for each temple you control.";
    }
    
}
