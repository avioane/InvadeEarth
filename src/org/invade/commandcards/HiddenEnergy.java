/*
 * HiddenEnergy.java
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
public class HiddenEnergy extends AbstractCommandCard implements AutomaticCard {
    
    private TerritoryType territoryType;
    
    private Territory territory;
    
    public HiddenEnergy(SpecialUnit requiredCommander, int powerUpCost, TerritoryType territoryType) {
        super(requiredCommander, powerUpCost, "Hidden Energy");
        this.territoryType = territoryType;
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board) && board.isBeforeFirstInvasion();
    }
    
    public void doAction(Board board, GameThread gameThread)
    throws EndGameException {
        setPlayer( board.getCurrentPlayer() );
        territory = board.getTerritoryDeck(territoryType).draw();
        if( territory != null ) {
            board.sendMessage(territory.getName() + " contains hidden energy");
            board.getCardsInPlay().add(this);
            checkForAction(board, gameThread);
        }
    }
    
    public void checkForAction(Board board, GameThread gameThread)
    throws EndGameException {
        board.getTerritoryDeck(territoryType).discard(territory);
        if( territory.getOwner() == getPlayer() ) {
            getPlayer().addEnergy(4);
            board.sendMessage("Hidden energy sources were harvested");
            board.getCardsInPlay().remove(this);
        } else if( board.getTurnMode().equals(TurnMode.DECLARE_FREE_MOVES) ) {
            board.getCardsInPlay().remove(this);
        }
    }
    
    public String getName(boolean hidden, boolean inPlay) {
        if( inPlay ) {
            return getName(false, false) + " (" + territory.getName() + ")";
        }
        return super.getName(hidden, inPlay);
    }
    
    public String getDescriptionString() {
        return "Draw a " + territoryType.getNoun().toLowerCase() + " card.  " +
                "If you occupy this water territory at the end of your turn, " +
                "collect 4 energy.  Discard the territory card at the end of " +
                "this turn.";
    }
    
}
