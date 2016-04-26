/*
 * AssassinBomb.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import org.invade.AbstractMoveVerifier;
import org.invade.Board;
import org.invade.CommonBoardMethods;
import org.invade.rules.DefaultMoveVerifier;
import org.invade.EndGameException;
import org.invade.Force;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.SoundDriver;
import org.invade.Territory;
import org.invade.TurnMode;
import org.invade.SpecialUnit;

/**
 *
 * @author Jonathan Crosmer
 */
public class AssassinBomb extends NuclearCard {
    
    public AssassinBomb(SpecialUnit requiredCommander, int powerUpCost) {
        super(requiredCommander, powerUpCost, "Assassin Bomb");
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && CommonBoardMethods.areEnemyCommanders(board, board.getCurrentPlayer());
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        board.setMoveVerifier(new AbstractMoveVerifier(){
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory territory = (Territory)move;
                DefaultMoveVerifier.verifyIsEnemy(territory, board.getCurrentPlayer());
                DefaultMoveVerifier.verifyHasCommander(territory);
                DefaultMoveVerifier.verifyNotDevastated(territory);
            }
        });
        Territory territory = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        
        board.setDefendingTerritory(territory);
        board.setTurnMode(TurnMode.CHOOSE_COMMANDER);
        Force force = (Force)gameThread.take();
        SpecialUnit unit = force.getSpecialUnits().get(0);
        if( board.getRandom().nextInt(4) != 0 ) {
            territory.getForce().subtract(force);
            territory.update();
            territory.setTerritoryStatus(Territory.Status.DAMAGED);
            board.sendMessage(unit.toString() + " was assassinated in " + territory.getName());
            
            SoundDriver.play("bombtiqdan.ogg");
        } else {
            board.sendMessage("An assassination attempt against "
                    + unit.toString() + " in " + territory.getName() + " failed");
        }
    }
    
    public String getDescriptionString() {
        return "Choose an opponent's commander.  Roll an 8-sided die.  If " +
                "you roll a 3 or higher destroy the chosen commander.";
    }
    
}
