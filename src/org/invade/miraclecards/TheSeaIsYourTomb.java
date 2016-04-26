/*
 * RagnarokCometh.java
 *
 * Created on March 22, 2006, 3:08 PM
 *
 */

package org.invade.miraclecards;

import java.util.ArrayList;
import java.util.List;
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.Continent;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Player;
import org.invade.Territory;
import org.invade.rules.GodstormRules;

public class TheSeaIsYourTomb extends DeathCard implements AutomaticCard {
    
    private String continentName;
    
    public TheSeaIsYourTomb(int powerUpCost, String continentName) {
        super(powerUpCost, "The Sea is Your Tomb");
        this.continentName = continentName;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        
        List<Continent> remove = new ArrayList<Continent>();
        for( Continent continent : board.getContinents() ) {
            if( continent.getName().equals(continentName) ) {
                for( Territory territory : board.getContinent(continent) ) {
                    Heaven.getHeaven(board, territory.getOwner()).addUnits(
                            territory.getForce().getRegularUnits());
                    territory.getForce().clear();
                    territory.setOwner(Player.NEUTRAL);
                    territory.setDevastated(true, board);
                }
                remove.add(continent);
            }
        }
        board.getContinents().removeAll(remove);
    }
    
    public String getDescriptionString() {
        return "Sink " + continentName + ".";
    }
    
}
