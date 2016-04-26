/*
 * StealthMODs.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import java.util.Collections;
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.GameThread;
import org.invade.SpecialUnit;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.rules.DefaultRules;
import org.invade.Territory;
import org.invade.SoundDriver;

/**
 *
 * @author Jonathan Crosmer
 */
public class StealthStation extends AbstractCommandCard implements AutomaticCard {
    
    private TerritoryType territoryType;
    
    public StealthStation(SpecialUnit requiredCommander, int powerUpCost, TerritoryType territoryType) {
        super(requiredCommander, powerUpCost, "Stealth Station");
        this.territoryType = territoryType;
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && board.getTurnMode().equals(TurnMode.ACKNOWLEDGE_INVASION)
        && board.getDefendingTerritory().getOwner().equals(board.getCurrentPlayer())
        && board.getDefendingTerritory().getType().equals(TerritoryType.LAND)
        && (DefaultRules.SPACE_STATION.getMaxTotal() < 0
                || board.getUnitCount(null, DefaultRules.SPACE_STATION) < DefaultRules.SPACE_STATION.getMaxTotal())
                && (DefaultRules.SPACE_STATION.getMaxOwnable() < 0
                || board.getUnitCount( board.getDefendingTerritory().getOwner(),
                DefaultRules.SPACE_STATION ) < DefaultRules.SPACE_STATION.getMaxOwnable())
                && (DefaultRules.SPACE_STATION.getMaxPerTerritory() < 0
                || Collections.frequency(board.getDefendingTerritory().getForce().getSpecialUnits(),
                DefaultRules.SPACE_STATION) < DefaultRules.SPACE_STATION.getMaxPerTerritory());
    }
    
    public void doAction(Board board, GameThread gameThread) {
        board.getDefendingTerritory().getForce().getSpecialUnits().add(DefaultRules.SPACE_STATION);
        board.getDefendingTerritory().setTerritoryStatus(Territory.Status.REINFORCED);
        SoundDriver.play("warning_horndan.wav");
    }
    
    public String getWhenString() {
        return getOnInvadeYouString(territoryType);
    }
    
    public String getDescriptionString() {
        return "Place a space station in the defending " +
                territoryType.getNoun().toLowerCase() + " territory.";
    }
    
}
