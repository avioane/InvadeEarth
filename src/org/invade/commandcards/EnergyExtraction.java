/*
 * EnergyExtraction.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.SpecialUnit;
import org.invade.TurnMode;

/**
 *
 * @author Jonathan Crosmer
 */
public class EnergyExtraction extends AbstractCommandCard implements AutomaticCard {
    
    private TerritoryType territoryType;
    
    public EnergyExtraction(SpecialUnit requiredCommander, int powerUpCost, TerritoryType territoryType) {
        super(requiredCommander, powerUpCost, "Energy Extraction");
        this.territoryType = territoryType;
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board) && board.isBeforeFirstInvasion();
    }
    
    public void doAction(Board board, GameThread gameThread)
    throws EndGameException {
        board.getCardsInPlay().add(this);
    }
    
    public void checkForAction(Board board, GameThread gameThread)
    throws EndGameException {
        if( board.getTurnMode().equals(TurnMode.DECLARE_FREE_MOVES) ) {
            boolean receiveEnergy = true;
            for( Territory territory : board.getTerritories() ) {
                if( territory.getType().equals(territoryType)
                && territory.getOwner() != board.getCurrentPlayer() ) {
                    receiveEnergy = false;
                }                    
            }
            if( receiveEnergy ) {
                board.sendMessage("The energy extraction operation was successful");
                board.getCurrentPlayer().addEnergy(7);
            }
            board.getCardsInPlay().remove(this);
        }
    }
    
    public String getDescriptionString() {
        return "If you occupy all the " + territoryType.getNoun().toLowerCase()
        + " territories at the end of this turn, collect 7 energy.";
    }
    
}
