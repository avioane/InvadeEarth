/*
 * Armageddon.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.Card;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Player;
import org.invade.SoundDriver;
import org.invade.SpecialMove;
import org.invade.SpecialUnit;
import org.invade.TurnMode;

/**
 *
 * @author Jonathan Crosmer
 */
public class Armageddon extends NuclearCard implements AutomaticCard {
    
    public Armageddon(SpecialUnit requiredCommander, int powerUpCost) {
        super(requiredCommander, powerUpCost, "Armageddon");
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        SoundDriver.play("nukedan.ogg");
        
        board.sendMessage("Play nuclear cards for free before clicking 'Acknowledge'");
        board.getCardsInPlay().add(this);
        for( Player player : board.getLivingPlayers() ) {
            Object acknowledge = null;
            while( acknowledge != SpecialMove.ACKNOWLEDGE ) {
                board.setTurnMode(TurnMode.ACKNOWLEDGE_EACH_PLAYER);
                board.setCurrentPlayer(player);
                acknowledge = gameThread.take();
                if( acknowledge instanceof Card ) {
                    ((Card)acknowledge).play(board, gameThread);
                }
            }
        }
        board.getCardsInPlay().remove(this);
    }
    
    public String getDescriptionString() {
        return "All players, in turn order, may play any number of nuclear " +
                "command cards without paying the energy cost.";
    }
    
}
