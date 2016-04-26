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
import org.invade.CommonBoardEvents;
import org.invade.CommonBoardMethods;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.SpecialUnit;
import org.invade.TerritoryType;
import org.invade.rules.GodstormRules;

public class AnEmpireIsBorn extends WarCard implements AutomaticCard {
    private static final SpecialUnit temple = GodstormRules.TEMPLE;
    
    public AnEmpireIsBorn(int powerUpCost) {
        super(powerUpCost, "An Empire is Born");
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        board.getCurrentPlayer().getReinforcements().getSpecialUnits().add(temple);
        CommonBoardEvents.placeReinforcements(board, gameThread);
    }
    
    public String getDescriptionString() {
        return "Place a temple in any territory you control.";
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && (board.getUnitCount(board.getCurrentPlayer(), GodstormRules.TEMPLE)
        < GodstormRules.TEMPLE.getMaxOwnable()
        || GodstormRules.TEMPLE.getMaxOwnable() < 0)
        && CommonBoardMethods.hasRoomFor(board, GodstormRules.TEMPLE,
                board.getTerritoriesOwned(board.getCurrentPlayer(), TerritoryType.LAND), 1 );
    }
    
}
