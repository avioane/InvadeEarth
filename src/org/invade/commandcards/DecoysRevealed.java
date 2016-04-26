/*
 * DecoysRevealed.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.CommonBoardEvents;
import org.invade.EndGameException;
import org.invade.Force;
import org.invade.GameThread;
import org.invade.Player;
import org.invade.SpecialUnit;
import org.invade.Territory;



/**
 *
 * @author Jonathan Crosmer
 */
public class DecoysRevealed extends AbstractCommandCard implements AutomaticCard {
        
    public DecoysRevealed(SpecialUnit requiredCommander, int powerUpCost) {
        super(requiredCommander, powerUpCost, "Decoys Revealed");
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board) && board.isBeforeFirstInvasion();
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        Force commanders = new Force();
        for( Territory territory : board.getTerritories() ) {
            if( territory.getOwner() == board.getCurrentPlayer() ) {
                commanders.getSpecialUnits().addAll( 
                        territory.getForce().getMobileForce().getSpecialUnits() );
                territory.getForce().subtract(commanders);
            }
        }
        board.getCurrentPlayer().getReinforcements().add(commanders);
        CommonBoardEvents.placeReinforcements(board, gameThread);
        /* If a player empties a territory that previously held only commanders
         * and does not replace them immediately, he loses control of that
         * territory. */
        for( Territory territory : board.getTerritories() ) {
            territory.update();
        }
    }
    
    public String getDescriptionString() {
        return "Move any number of your commanders to any number of " +
                "territories you occupy.";
    }
    
}
