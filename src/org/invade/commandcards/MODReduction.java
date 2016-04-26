/*
 * MODReduction.java
 *
 * Created on July 10, 2005, 5:27 PM
 *
 */

package org.invade.commandcards;
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.CommonBoardEvents;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Player;
import org.invade.SpecialUnit;


/**
 *
 * @author Jonathan Crosmer
 */
public class MODReduction extends AbstractCommandCard implements AutomaticCard {
    
    public MODReduction(SpecialUnit requiredCommander, int powerUpCost) {
        super(requiredCommander, powerUpCost, "MOD Reduction");
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board) && board.isBeforeFirstInvasion();
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.sendMessage("At an international summit, all leaders agree to " +
                "decommission MOD units");
        board.sendMessage("Click territories to remove units one at a time");
        Player current = board.getCurrentPlayer();
        for( Player player : board.getLivingPlayers() ) {
            if( player != current ) {
                CommonBoardEvents.removeMODs(board, gameThread, player, 4);
            }
        }
        CommonBoardEvents.removeMODs(board, gameThread, current, 2);
    }
    
    public String getDescriptionString() {
        return "All of your opponents must remove 4 MODs in turn order.  " +
                "Then you remove 2 MODs.";
    }
    
}
