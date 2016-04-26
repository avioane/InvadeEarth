/*
 * EnemiesConvert.java
 *
 * Created on March 22, 2006, 9:37 AM
 *
 */

package org.invade.miraclecards;

import java.util.Collections;
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.SpecialUnit;
import org.invade.rules.GodstormRules;

public class ThousandWorkersAmass extends SkyCard implements AutomaticCard {
    private static final SpecialUnit temple = GodstormRules.TEMPLE;
    
    public ThousandWorkersAmass(int powerUpCost) {
        super(powerUpCost, "1000 Workers Amass");
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        board.getDefendingTerritory().getForce().getSpecialUnits().add(temple);
    }
    
    public String getDescriptionString() {
        return "Place a temple in the defending territory.";
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && (temple.getMaxTotal() < 0
                || board.getUnitCount(null, temple) < temple.getMaxTotal())
                && (temple.getMaxOwnable() < 0
                || board.getUnitCount( board.getDefendingTerritory().getOwner(),
                temple ) < temple.getMaxOwnable())
                && (temple.getMaxPerTerritory() < 0
                || Collections.frequency(board.getDefendingTerritory().getForce().getSpecialUnits(),
                temple) < temple.getMaxPerTerritory());
    }
    
}
