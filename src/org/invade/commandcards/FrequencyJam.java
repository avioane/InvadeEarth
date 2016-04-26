/*
 * ScoutForces.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import org.invade.BlockCards;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Player;
import org.invade.SoundDriver;
import org.invade.SpecialUnit;
import org.invade.TurnMode;

/**
 *
 * @author Jonathan Crosmer
 */
public class FrequencyJam extends AbstractCommandCard implements BlockCards {
    
    public FrequencyJam(SpecialUnit requiredCommander, int powerUpCost) {
        super(requiredCommander, powerUpCost, "Frequency Jam (" + requiredCommander + ")");
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board) && board.isBeforeFirstInvasion();
    }
    
    public void doAction(Board board, GameThread gameThread)
    throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_PLAYER);
        setPlayer( (Player)gameThread.take() );
        board.sendMessage(getPlayer().getName() + " experiences a communications failure");
        board.getCardsInPlay().add(this);
        SoundDriver.play("interference.wav");
    }
    
    public void checkForAction(Board board, GameThread gameThread)
    throws EndGameException {
        if( board.getTurnMode().equals(TurnMode.DECLARE_FREE_MOVES) ) {
            board.getCardsInPlay().remove(this);
        }
    }
    
    public String getName(boolean hidden, boolean inPlay) {
        if( inPlay ) {
            return getName(false, false) + " (on " + getPlayer() + ")";
        }
        return super.getName(hidden, inPlay);
    }
    
    public String getDescriptionString() {
        return "Choose a player.  The chosen player cannot play command cards " +
                "during your turn.";
    }
    
}
