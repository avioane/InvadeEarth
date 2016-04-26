/*
 * Terraforming.java
 *
 * Created on March 14, 2006, 10:14 AM
 *
 */

package org.invade.amoeba;

import org.invade.AbstractMoveVerifier;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.Territory;
import org.invade.TurnMode;
import org.invade.rules.DefaultMoveVerifier;

public class Terraforming extends AbstractAmoebaCard {
    
    public Terraforming() {
        super("Terraforming");
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        boolean exists = false;
        for( Territory territory : board.getTerritories() ) {
            if( territory.isDevastated() ) {
                exists = true;
            }
        }
        if( ! exists ) {
            return;
        }
        board.sendMessage("Select a devastated territory");        
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        board.setMoveVerifier(new AbstractMoveVerifier(){
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory territory = (Territory)move;
                DefaultMoveVerifier.verify(territory.isDevastated(), "Select a devastated territory");
            }
        });
        Territory territory = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        board.sendMessage(territory.getName() + " is restored");
        territory.setDevastated(false, board);
    }
    
}
