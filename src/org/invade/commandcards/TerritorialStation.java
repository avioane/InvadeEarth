/*
 * TerritorialStation.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.CommonBoardEvents;
import org.invade.CommonBoardMethods;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.SpecialUnit;
import org.invade.TerritoryType;
import org.invade.rules.DefaultRules;


/**
 *
 * @author Jonathan Crosmer
 */
public class TerritorialStation extends AbstractCommandCard implements AutomaticCard {
        
    public TerritorialStation(SpecialUnit requiredCommander, int powerUpCost) {
        super(requiredCommander, powerUpCost, "Territorial Station");
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && board.isBeforeFirstInvasion()
        && (board.getUnitCount(board.getCurrentPlayer(), DefaultRules.SPACE_STATION)
        < DefaultRules.SPACE_STATION.getMaxOwnable()
        || DefaultRules.SPACE_STATION.getMaxOwnable() < 0)
        && CommonBoardMethods.hasRoomFor(board, DefaultRules.SPACE_STATION, 
                board.getTerritoriesOwned(board.getCurrentPlayer(), TerritoryType.LAND), 1 );
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.getCurrentPlayer().getReinforcements().getSpecialUnits().add(DefaultRules.SPACE_STATION);
        CommonBoardEvents.placeReinforcements(board, gameThread);
    }

    public String getDescriptionString() {
        return "Place a space station on any land territory you occupy.";
    }
    
}
