/*
 * ContinentalBomb.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import java.util.ArrayList;
import java.util.List;
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.CommonBoardEvents;
import org.invade.Continent;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.SpecialUnit;
import org.invade.SoundDriver;

/**
 *
 * @author Jonathan Crosmer
 */
public class ContinentalBomb extends NuclearCard implements AutomaticCard {
    
    private TerritoryType territoryType;
    
    public ContinentalBomb(SpecialUnit requiredCommander, int powerUpCost,
            TerritoryType territoryType, String name) {
        super(requiredCommander, powerUpCost, name);
        this.territoryType = territoryType;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        List<Continent> possibleTargets = new ArrayList<Continent>();
        for( Territory territory : board.getTerritories() ) {
            if( territory.getType().equals(territoryType)
            && ! possibleTargets.contains(territory.getContinent()) ) {
                possibleTargets.add(territory.getContinent());
            }
        }
        if( !possibleTargets.isEmpty() ) {
            Continent target = possibleTargets.get(
                    board.getRandom().nextInt(possibleTargets.size()));
            board.sendMessage(getName(false, true) + " strikes " + target.getName());
            SoundDriver.play("nukedan.ogg");
            for( Territory territory : board.getContinent(target) ) {
                territory.addNumberToDestroy(1);
                territory.setTerritoryStatus(Territory.Status.DAMAGED);
                CommonBoardEvents.checkForDestroyed(board, gameThread, territory);
            }
        }
    }
    
    public String getDescriptionString() {
        return "Roll a 6-sided die.  Destroy one unit in each territory in " +
                "the " + this.territoryType.toString() + " zone rolled.";
    }
    
    public TerritoryType getTerritoryType() {
        return territoryType;
    }
    
}
