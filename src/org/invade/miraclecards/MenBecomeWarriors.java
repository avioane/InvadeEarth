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

public class MenBecomeWarriors extends SkyCard implements AutomaticCard {
    private int strength;
    
    public MenBecomeWarriors(int powerUpCost, int strength) {
        super(powerUpCost, "Men Become Warriors");
        this.strength = strength;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        board.getDefendingTerritory().getForce().addRegularUnits(strength);
    }
    
    public String getDescriptionString() {
        return "Gain " + strength + " armies in the defending territory.";
    }
    
}
