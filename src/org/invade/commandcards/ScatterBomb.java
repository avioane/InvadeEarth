/*
 * ScatterBomb.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.CommonBoardEvents;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Player;
import org.invade.SoundDriver;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.SpecialUnit;

/**
 *
 * @author Jonathan Crosmer
 */
public class ScatterBomb extends NuclearCard implements AutomaticCard {
    
    private TerritoryType territoryType;
    private int hits;
    
    public ScatterBomb(SpecialUnit requiredCommander, int powerUpCost,
            TerritoryType territoryType, int hits) {
        super(requiredCommander, powerUpCost, "Scatter Bomb " + territoryType.getNoun());
        this.territoryType = territoryType;
        this.hits = hits;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        Player current = board.getCurrentPlayer();
        
        SoundDriver.play("aexp4dan.ogg");
        
        for( int i = 0; i < hits; ++i ) {
            Territory territory = board.getTerritoryDeck(territoryType).draw();
            if( territory != null ) {
                if( territory.getOwner() != board.getCurrentPlayer() ) {
                    board.sendMessage("Scatter bomb hits " + territory.getName());
                    territory.setNumberToDestroy(
                            (territory.getForce().getMobileIndependentSize()+1) / 2 );
                    territory.setTerritoryStatus(Territory.Status.DAMAGED);
                    CommonBoardEvents.checkForDestroyed(board, gameThread, territory);
                }
                board.getTerritoryDeck(territoryType).discard(territory);
            }
            board.setCurrentPlayer(current);
        }
    }
    
    public String getDescriptionString() {
        return "Turn over " + hits + " " + territoryType.getNoun().toLowerCase() +
                " territory cards.  Destroy half the opponents' units on the " +
                territoryType.getNoun().toLowerCase() + " territories drawn.  " +
                "Round up.";
    }
    
    public TerritoryType getTerritoryType() {
        return territoryType;
    }
    
}
