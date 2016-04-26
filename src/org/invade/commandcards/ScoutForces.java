/*
 * ScoutForces.java
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
import org.invade.SoundDriver;

/**
 *
 * @author Jonathan Crosmer
 */
public class ScoutForces extends AbstractCommandCard implements AutomaticCard {
    
    private TerritoryType territoryType;
    
    private Territory territory;
    
    public ScoutForces(SpecialUnit requiredCommander, int powerUpCost, TerritoryType territoryType) {
        super(requiredCommander, powerUpCost, "Scout Forces");
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
            board.getCardsInPlay().add(this);
            checkForAction(board, gameThread);
        }
    }
    
    public void checkForAction(Board board, GameThread gameThread)
    throws EndGameException {
        if( territory.getOwner() == getPlayer() ) {
            board.sendMessage(getPlayer().getName() + " recovers scouts in " + territory.getName());
            territory.setTerritoryStatus(Territory.Status.REINFORCED);
            SoundDriver.play("armymarch.ogg");
            territory.getForce().addRegularUnits(5);
            board.getCardsInPlay().remove(this);
            board.getTerritoryDeck(territoryType).discard(territory);
        }
    }
    
    public String getName(boolean hidden, boolean inPlay) {
        if( inPlay && ! hidden ) {
            return getName(false, false) + " (" + territory.getName() + ")";
        } else if( inPlay ) {
            return getName(false, false) + " (" + getPlayer().getName() + ")";
        }
        return super.getName(hidden, inPlay);
    }
    
    public String getDescriptionString() {
        return "Draw a " + territoryType.getNoun().toLowerCase() +
                " territory card and secretly place it facedown in " +
                "front of you.  Place 5 MODs on the territory card.  When " +
                "you occupy this territory, immediately place the MODs.  " +
                "Discard the territory card.";
    }
    
}
