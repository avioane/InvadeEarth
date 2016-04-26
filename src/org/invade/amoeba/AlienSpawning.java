/*
 * AlienSpawning.java
 *
 * Created on March 14, 2006, 11:41 AM
 *
 */

package org.invade.amoeba;

import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Territory;
import org.invade.rules.AmoebaRules;

public class AlienSpawning extends AbstractAmoebaCard {
    
    public AlienSpawning() {
        super("Alien Spawning");
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.sendMessage("Amoebae undergo mitosis");
        for( Territory territory : board.getTerritoriesOwned(AmoebaRules.ALIENS) ) {
            territory.getForce().setRegularUnits(territory.getForce().getRegularUnits() * 2);
        }
    }
    
}
