/*
 * CeaseFire.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import org.invade.AutomaticCard;
import org.invade.BlockInvasion;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Player;
import org.invade.SpecialUnit;
import org.invade.Territory;
import org.invade.TurnMode;


/**
 *
 * @author Jonathan Crosmer
 */
public class CeaseFire extends AbstractCommandCard implements BlockInvasion, AutomaticCard {
    
    private Player attacker;
    
    public CeaseFire(SpecialUnit requiredCommander, int powerUpCost) {
        super(requiredCommander, powerUpCost, "Cease Fire");
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && board.getTurnMode().equals(TurnMode.ACKNOWLEDGE_INVASION)
        && board.getDefendingTerritory().getOwner().equals(board.getCurrentPlayer());
    }
    
    public void doAction(Board board, GameThread gameThread)
    throws EndGameException {
        setPlayer(board.getCurrentPlayer());
        attacker = board.getAttackingTerritory().getOwner();
        board.getCardsInPlay().add(this);
    }
    
    public void checkForAction(Board board, GameThread gameThread)
    throws EndGameException {
        if( board.getTurnMode().equals(TurnMode.DECLARE_FREE_MOVES) ) {
            board.getCardsInPlay().remove(this);
        }
    }
    
    public String getName(boolean hidden, boolean inPlay) {
        if( inPlay ) {
            return getName(false, false) + " (" + attacker + " and " + getPlayer() + ")";
        }
        return super.getName(hidden, inPlay);
    }
    
    public String getWhenString() {
        return getOnInvadeYouString(null);
    }
    
    public String getDescriptionString() {
        return "Prevent the invasion.  The attacking player cannot attack any " +
                "of your territories for the rest of his/her turn.";
    }

    public boolean blocks(Territory from, Territory to) {
        return from.getOwner() == attacker && to.getOwner() == getPlayer();
    }
    
}
