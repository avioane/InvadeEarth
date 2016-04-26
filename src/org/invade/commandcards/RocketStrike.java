/*
 * RocketStrike.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import org.invade.AbstractMoveVerifier;
import org.invade.Board;
import org.invade.CommonBoardEvents;
import org.invade.rules.DefaultMoveVerifier;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.SoundDriver;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.SpecialUnit;
import org.invade.TurnMode;

/**
 *
 * @author Jonathan Crosmer
 */
public class RocketStrike extends NuclearCard {
    
    private TerritoryType territoryType;
    
    public RocketStrike(SpecialUnit requiredCommander, int powerUpCost,
            TerritoryType territoryType) {
        super(requiredCommander, powerUpCost, "Rocket Strike " + territoryType.getNoun());
        this.territoryType = territoryType;
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board) && board.getTerritoriesHostileTo(
                board.getCurrentPlayer(), territoryType).size() > 0;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        board.setMoveVerifier(new AbstractMoveVerifier(){
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory territory = (Territory)move;
                DefaultMoveVerifier.verifyIsEnemy(territory, board.getCurrentPlayer());
                DefaultMoveVerifier.verifyHasUnits(territory);
                DefaultMoveVerifier.verifyNotDevastated(territory);
                DefaultMoveVerifier.verifyType(territory, territoryType);
            }
        });
        Territory territory = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        
        SoundDriver.play("rocketsdan2.ogg");
        
        territory.setNumberToDestroy(Math.min(board.getRandom().nextInt(6) + 1,
                territory.getForce().getMobileIndependentSize()));
        territory.setTerritoryStatus(Territory.Status.DAMAGED);
        board.sendMessage("Nuclear strike is launched on "
                + territory.getName() + " ("
                + territory.getNumberToDestroy() + ")");
        CommonBoardEvents.checkForDestroyed(board, gameThread, territory);        
    }
    
    public String getDescriptionString() {
        return "Choose any opponent's " + territoryType.getNoun().toLowerCase() +
                " territory.  Roll a 6-sided die.  Your opponent must destroy " +
                "units equal to the number rolled in the chosen territory.";
    }
    
}
