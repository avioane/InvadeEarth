/*
 * AliensCatchACold.java
 *
 * Created on March 14, 2006, 11:43 AM
 *
 */

package org.invade.amoeba;

import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Territory;
import org.invade.rules.AmoebaRules;

public class AliensCatchACold extends AbstractAmoebaCard {
    
    public AliensCatchACold() {
        super("Aliens Catch a Cold");
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.sendMessage("All amoebae are destroyed");
        for( Territory territory : board.getTerritoriesOwned(AmoebaRules.ALIENS) ) {
            territory.getForce().clear();
            territory.update();
        }
    }
}
