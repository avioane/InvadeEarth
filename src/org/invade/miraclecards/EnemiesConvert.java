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

public class EnemiesConvert extends SkyCard implements AutomaticCard {
    private int strength;
    
    public EnemiesConvert(int powerUpCost, int strength) {
        super(powerUpCost, "Enemies Convert");
        this.strength = strength;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        int convert = Math.min(strength, board.getAttackingTerritory().getForce().getRegularUnits());
        board.sendMessage("Armies rebel and join "
                + board.getDefendingTerritory().getOwner().getName()
                + " (" + convert + ")");
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        board.getAttackingTerritory().getForce().addRegularUnits( - convert );
        board.getDefendingTerritory().getForce().addRegularUnits( convert );
        board.getAttackingTerritory().update();
    }
    
    public String getDescriptionString() {
        return "Destroy " + strength + " armies in the attacking territory "
                + "and gain " + strength + " of your armies in the defending "
                + "territory.  The destroyed armies don't go to the Underworld.";
    }
    
}
