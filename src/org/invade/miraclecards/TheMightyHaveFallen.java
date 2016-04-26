/*
 * RagnarokCometh.java
 *
 * Created on March 22, 2006, 3:08 PM
 *
 */

package org.invade.miraclecards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Territory;
import org.invade.rules.GodstormRules;

public class TheMightyHaveFallen extends DeathCard implements AutomaticCard {
    
    private int strength;
    
    public TheMightyHaveFallen(int powerUpCost, int strength) {
        super(powerUpCost, "The Mighty Have Fallen");
        this.strength = strength;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        
        List<Territory> territories = new ArrayList<Territory>(board.getTerritories());
        Collections.shuffle(territories, board.getRandom()); // break ties randomly
        Collections.sort(territories, new Comparator<Territory>() {
            public int compare(Territory first, Territory second) {
                return second.getForce().getRegularUnits() - first.getForce().getRegularUnits();
            }
        });
        for( int i = 0; i < strength; ++i ) {
            Territory territory = territories.get(i);
            int destroy = territory.getForce().getRegularUnits() / 2;
            territory.getForce().addRegularUnits(-destroy);
            Heaven.getHeaven(board, territory.getOwner()).addUnits(destroy);
            territory.update();
        }
    }
    
    
    public String getDescriptionString() {
        return "Halve the number of armies in the " + strength + " largest "
                + "armies on the Ancient Earth gameboard.";
    }
    
}
