/*
 * InvadeEarth.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import org.invade.AllowInvasion;
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
public class InvadeEarth extends AbstractCommandCard implements AllowInvasion, AutomaticCard {
    
    private Territory territory;
    
    public InvadeEarth(SpecialUnit requiredCommander, int powerUpCost) {
        super(requiredCommander, powerUpCost, "Invade Earth");
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board) && board.isBeforeFirstInvasion();
    }
    
    public void doAction(Board board, GameThread gameThread)
    throws EndGameException {
        territory = board.getTerritoryDeck(TerritoryType.LAND).drawNotOwnedBy(
                board.getCurrentPlayer());
        if( territory != null ) {
            board.sendMessage(territory.getName() + " is vulnerable to an " +
                    "attack from space");
            board.getCardsInPlay().add(this);
            checkForAction(board, gameThread);
        }
    }
    
    public void checkForAction(Board board, GameThread gameThread)
    throws EndGameException {
        if( board.getTurnMode().equals(TurnMode.DECLARE_FREE_MOVES) ) {
            board.getTerritoryDeck(TerritoryType.LAND).discard(territory);
            board.getCardsInPlay().remove(this);
        }
    }
    
    public boolean allows(Territory from, Territory to) {
        return from.getType().equals(TerritoryType.MOON) && (to == territory);
    }
    
    public String getName(boolean hidden, boolean inPlay) {
        if( inPlay ) {
            return getName(false, false) + " (" + territory.getName() + ")";
        }
        return super.getName(hidden, inPlay);
    }
    
    public String getDescriptionString() {
        return "Turn over land territory cards until you turn over a " +
                "territory you do not occupy.  During this turn you may " +
                "attack this land territory from any moon territories you " +
                "occupy.";
    }
    
}
