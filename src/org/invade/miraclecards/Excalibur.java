/*
 * Excalibur.java
 *
 * Created on May 26, 2006, 1:32 PM
 *
 */

package org.invade.miraclecards;

import org.invade.Board;
import org.invade.Continent;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Territory;

public class Excalibur extends MagicCard {
    
    private int strength;
    
    public Excalibur(int strength) {
        super("Excalibur");
        this.strength = strength;
    }
    
    public void doAtTurnStart(Board board, GameThread gameThread) throws EndGameException {
        for( Continent continent : board.getContinents() ) {
            boolean own = true;
            for( Territory territory : board.getContinent(continent) ) {
                if( territory.getOwner() != board.getCurrentPlayer() ) {
                    own = false;
                } 
            }
            if( own ) {
                board.getCurrentPlayer().getReinforcements().addRegularUnits(strength);
            }
        }        
    }
    
    public String getDescriptionString() {
        return "Gain " + strength + " additional armies for each continent "
                + "you control at the start of each of your turns.";
    }
    
}
