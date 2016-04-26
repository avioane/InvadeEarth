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

public class TheGroundShakes extends DeathCard implements AutomaticCard {
    
    public TheGroundShakes(int powerUpCost) {
        super(powerUpCost, "The Ground Shakes");
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        Continent target = board.getContinents().get(
                board.getRandom().nextInt(board.getContinents().size()));
        board.sendMessage("A great earthquake strikes " + target.getName());
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        for( Territory territory : board.getContinent(target) ) {
            int destroy = territory.getForce().getRegularUnits() / 2;
            territory.getForce().addRegularUnits(- destroy);
            Heaven.getHeaven(board, territory.getOwner()).addUnits(destroy);
            territory.update();
        }
    }
    
    public String getDescriptionString() {
        return "Choose a continent randomly.  Halve the number of armies in each "
                + "territory on that continent.";
    }
    
}
