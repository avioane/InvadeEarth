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

public class TheYoungGrowOld extends DeathCard implements AutomaticCard {
    
    public TheYoungGrowOld(int powerUpCost) {
        super(powerUpCost, "The Young Grow Old");
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        for( Territory territory : board.getTerritories() ) {
            if( territory.getType() == TerritoryType.LAND
                    && territory.getForce().getRegularUnits() > 1 ) {
                territory.getForce().addRegularUnits(-1);
                Heaven.getHeaven(board, territory.getOwner()).addUnits(1);
                territory.update();
            }
        }
    }
    
    public String getDescriptionString() {
        return "Destroy 1 army in each territory that has more than 1 army.";
    }
    
}
