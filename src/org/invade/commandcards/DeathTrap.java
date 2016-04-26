/*
 * DeathTrap.java
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
import org.invade.SoundDriver;
import org.invade.SpecialUnit;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.Territory;

/**
 *
 * @author Jonathan Crosmer
 */
public class DeathTrap extends AbstractCommandCard implements AutomaticCard {
    
    private TerritoryType territoryType;
    
    public DeathTrap(SpecialUnit requiredCommander, int powerUpCost, TerritoryType territoryType, String name) {
        super(requiredCommander, powerUpCost, name);
        this.territoryType = territoryType;
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && board.getTurnMode().equals(TurnMode.ACKNOWLEDGE_INVASION)
        && board.getDefendingTerritory().getOwner().equals(board.getCurrentPlayer())
        && board.getDefendingTerritory().getType().equals(territoryType);
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        SoundDriver.play("deathtrap.ogg");
            
        board.getAttackingTerritory().setNumberToDestroy(
                (board.getAttackingTerritory().getForce().getMobileIndependentSize()+1) / 2 );
        board.getAttackingTerritory().setTerritoryStatus(Territory.Status.DAMAGED);
        CommonBoardEvents.checkForDestroyed(board, gameThread, board.getAttackingTerritory());
    }
    
    public String getWhenString() {
        return getOnInvadeYouString(territoryType);
    }
    
    public String getDescriptionString() {
        return "Your opponent must destroy half the units in the invading " +
                "territory.  Round up.";
    }
    
}
