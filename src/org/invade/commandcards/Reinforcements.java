/*
 * Reinforcements.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import java.util.ArrayList;
import java.util.List;
import org.invade.AbstractMoveVerifier;
import org.invade.Board;
import org.invade.rules.DefaultMoveVerifier;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.SoundDriver;
import org.invade.SpecialUnit;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.TurnMode;

/**
 *
 * @author Jonathan Crosmer
 */
public class Reinforcements extends AbstractCommandCard {
    
    private TerritoryType territoryType;
    
    public Reinforcements(SpecialUnit requiredCommander, int powerUpCost, TerritoryType territoryType) {
        super(requiredCommander, powerUpCost, "Reinforcements (" + territoryType + ")");
        this.territoryType = territoryType;
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && board.isBeforeFirstInvasion()
        && ! board.getTerritoriesOwned(board.getCurrentPlayer(), territoryType).isEmpty();
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        int max = Math.min(3, board.getTerritoriesOwned(board.getCurrentPlayer(), territoryType).size());
        final List<Territory> chosen = new ArrayList<Territory>();
        
        for( int i = 0; i < max; ++i ) {
            board.setMoveVerifier(new AbstractMoveVerifier(){
                public void verifyWithAssumptions(Board board, Object move)
                throws IllegalMoveException {
                    Territory territory = (Territory)move;
                    DefaultMoveVerifier.verifyIsFriendly(territory, board.getCurrentPlayer());
                    DefaultMoveVerifier.verifyType(territory, territoryType);
                    verify( ! chosen.contains(territory), "The units must be placed in different territories");
                }
            });
            
            Territory territory = (Territory)gameThread.take();
            territory.setTerritoryStatus(Territory.Status.REINFORCED);
            board.sendMessage(territory.getName() + " received reinforcements");
            
            SoundDriver.play("armymarch.ogg");
        
            territory.getForce().addRegularUnits(1);
            chosen.add(territory);
        }
        board.setMoveVerifier(board.getRules().getMoveVerifier());
    }
    
    public String getDescriptionString() {
        return "Place 3 MODs, one each on three different "
                + territoryType.getNoun().toLowerCase() + " territories you " +
                "occupy.";
    }
    
}
