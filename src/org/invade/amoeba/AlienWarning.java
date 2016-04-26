/*
 * AlienWarning.java
 *
 * Created on March 14, 2006, 10:27 AM
 *
 */

package org.invade.amoeba;

import org.invade.Board;
import org.invade.CommonBoardEvents;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Player;

public class AlienWarning extends AbstractAmoebaCard {
    
    private int units;
    
    public AlienWarning(int units) {
        super("Alien Warning");
        this.units = units;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.sendMessage("All players lose " + units + " units");
        for( Player player : board.getLivingPlayers() ) {            
            CommonBoardEvents.removeMODs(board, gameThread, player, units);
        }
    }
    
}
