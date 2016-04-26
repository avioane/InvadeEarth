/*
 * RagnarokCometh.java
 *
 * Created on March 22, 2006, 3:08 PM
 *
 */

package org.invade.miraclecards;

import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.Continent;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Territory;
import org.invade.rules.GodstormRules;

public class BloodCoatsTheLand extends DeathCard implements AutomaticCard {
    
    public BloodCoatsTheLand(int powerUpCost) {
        super(powerUpCost, "Blood Coats the Land");
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        Continent target = board.getContinents().get(
                board.getRandom().nextInt(board.getContinents().size()));
        board.sendMessage("Blood coats " + target.getName());
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        for( Territory territory : board.getContinent(target) ) {
            if( territory.getForce().getRegularUnits() > 0 ) {
                territory.getForce().addRegularUnits(-1);
                Heaven.getHeaven(board, territory.getOwner()).addUnits(1);
                territory.update();
            }
        }
    }
    
    public String getDescriptionString() {
        return "Choose a continent randomly.  Destroy 1 army in each "
                + "territory on that continent.";
    }
    
}
