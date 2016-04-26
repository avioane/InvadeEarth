/*
 * LavaErupts.java
 *
 * Created on March 22, 2006, 11:17 AM
 *
 */

package org.invade.miraclecards;

import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Territory;
import org.invade.rules.GodstormRules;

public class LavaErupts extends SkyCard implements AutomaticCard {
    private int strength;
    
    public LavaErupts(int powerUpCost, int strength) {
        super(powerUpCost, "Lava Erupts");
        this.strength = strength;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }        
        erupt(board, board.getAttackingTerritory());
        for(Territory territory : board.getAttackingTerritory().getAdjacent() ) {
            erupt(board, territory);
        }
    }
    
    private void erupt(Board board, Territory territory) {
        int effect = Math.min(board.getRandom().nextInt(strength) + 1,
                territory.getForce().getRegularUnits());
        board.sendMessage("Lava erupts in " + territory.getName() + " ("
                + effect + ")");
        territory.getForce().addRegularUnits( - effect );
        Heaven.getHeaven(board, territory.getOwner()).addUnits(effect);
        territory.update();
    }
    
    public String getDescriptionString() {
        return "Roll one of the dice and destroy that many armies in the "
                + "attacking territory.  Repeat this for each adjacent "
                + "territory (including the defending territory).";
    }
}
