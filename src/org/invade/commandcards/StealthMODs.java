/*
 * StealthMODs.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.GameThread;
import org.invade.Player;
import org.invade.SoundDriver;
import org.invade.SpecialUnit;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.Territory;

/**
 *
 * @author Jonathan Crosmer
 */
public class StealthMODs extends AbstractCommandCard implements AutomaticCard {
    
    private TerritoryType territoryType;
    
    public StealthMODs(SpecialUnit requiredCommander, int powerUpCost, TerritoryType territoryType) {
        super(requiredCommander, powerUpCost, "Stealth MODs (" + territoryType + ")");
        this.territoryType = territoryType;
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && board.getTurnMode().equals(TurnMode.ACKNOWLEDGE_INVASION)
        && !board.getDefendingTerritory().getOwner().equals(Player.NEUTRAL)
        && board.getDefendingTerritory().getType().equals(territoryType);
    }
    
    public void doAction(Board board, GameThread gameThread) {
        board.getDefendingTerritory().getForce().addRegularUnits(3); 
        board.getDefendingTerritory().setTerritoryStatus(Territory.Status.REINFORCED);
        SoundDriver.play("warning_horndan.wav");
    }
    
    public String getWhenString() {
        return getOnInvasionString(territoryType);
    }
    
    public String getDescriptionString() {
        return "Place 3 additional defending MODs in the defending "
                + territoryType.getNoun().toLowerCase() + " territory.";
    }
    
}
