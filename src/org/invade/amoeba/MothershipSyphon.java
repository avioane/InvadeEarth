/*
 * MothershipSyphon.java
 *
 * Created on March 13, 2006, 4:00 PM
 *
 */

package org.invade.amoeba;

import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Player;

public class MothershipSyphon extends AbstractAmoebaCard {

    public MothershipSyphon() {
        super("Mothership Syphon");
    }

    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.sendMessage("Each player loses one energy");
        for(Player player : board.getLivingPlayers()) {
            if(player.getEnergy() > 0) {
                player.addEnergy(-1);
            }
        }
    }
    
}
