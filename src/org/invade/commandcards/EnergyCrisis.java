/*
 * EnergyCrisis.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Player;
import org.invade.SpecialUnit;

/**
 *
 * @author Jonathan Crosmer
 */
public class EnergyCrisis extends AbstractCommandCard implements AutomaticCard {
    
    public EnergyCrisis(SpecialUnit requiredCommander, int powerUpCost) {
        super(requiredCommander, powerUpCost, "Energy Crisis");
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board) && board.isBeforeFirstInvasion();
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        for( Player player : board.getLivingPlayers() ) {
            if( player != board.getCurrentPlayer() && player.getEnergy() > 0 ) {
                player.addEnergy(-1);
                board.getCurrentPlayer().addEnergy(1);
            }
        }
    }
    
    public String getDescriptionString() {
        return "Collect one energy from each opponent.";
    }
    
}
