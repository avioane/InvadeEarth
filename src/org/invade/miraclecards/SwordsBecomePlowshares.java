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

public class SwordsBecomePlowshares extends SkyCard implements AutomaticCard {
    private int strength;
    
    public SwordsBecomePlowshares(int powerUpCost, int strength) {
        super(powerUpCost, "Swords Become Plowshares");
        this.strength = strength;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        int effect = Math.min(board.getRandom().nextInt(strength) + 1,
                board.getAttackingTerritory().getForce().getRegularUnits());
        board.sendMessage("Attacking armies throw down their weapons "
                + board.getDefendingTerritory().getOwner().getName()
                + " (" + effect + ")");
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        board.getAttackingTerritory().getForce().addRegularUnits( - effect );
        board.getAttackingTerritory().update();
    }
    
    public String getDescriptionString() {
        return "Roll one of the dice and destroy that many armies in the "
                + "attacking territory.  The armies don't go to the Underworld.";
    }
    
}
