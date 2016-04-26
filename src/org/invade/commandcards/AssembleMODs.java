/*
 * AssembleMODs.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import org.invade.AbstractMoveVerifier;
import org.invade.Board;
import org.invade.rules.DefaultMoveVerifier;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.SpecialUnit;
import org.invade.SoundDriver;

/**
 *
 * @author Jonathan Crosmer
 */
public class AssembleMODs extends AbstractCommandCard {
    
    private TerritoryType territoryType;
    
    /** Creates a new instance of AssembleMODs */
    public AssembleMODs(SpecialUnit requiredCommander, int powerUpCost, TerritoryType territoryType) {
        super(requiredCommander, powerUpCost, "Assemble MODs (" + territoryType + ")");
        this.territoryType = territoryType;
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && board.isBeforeFirstInvasion()
        && ! board.getTerritoriesOwned(board.getCurrentPlayer(), territoryType).isEmpty();
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        board.setMoveVerifier(new AbstractMoveVerifier(){
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory territory = (Territory)move;
                DefaultMoveVerifier.verifyIsFriendly(territory, board.getCurrentPlayer());
                DefaultMoveVerifier.verifyType(territory, territoryType);
            }
        });
        Territory territory = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        territory.getForce().addRegularUnits(3);
        territory.setTerritoryStatus(Territory.Status.REINFORCED);
        SoundDriver.play("armymarch.ogg");
        board.sendMessage(territory.getName() + " received reinforcements");
    }
    
    public String getDescriptionString() {
        return "Place 3 MODs on any one "
                + territoryType.getNoun().toLowerCase() + " territory you " +
                "occupy.";
    }
    
}
