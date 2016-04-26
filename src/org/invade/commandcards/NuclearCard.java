/*
 * NuclearCard.java
 *
 * Created on July 22, 2005, 11:14 PM
 *
 */

package org.invade.commandcards;

import org.invade.Board;
import org.invade.CommonBoardMethods;
import org.invade.SpecialUnit;
import org.invade.TurnMode;

/**
 *
 * @author Jonathan Crosmer
 */
public abstract class NuclearCard extends AbstractCommandCard {
    
    public NuclearCard(SpecialUnit requiredCommander, int powerUpCost, String name) {
        super(requiredCommander, powerUpCost, name);
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board) &&
                (board.isBeforeFirstInvasion()
                ||                
                (board.getLastCardUsed() instanceof Armageddon));
    }
    
    public int getPowerUpCost(Board board) {
        if( CommonBoardMethods.isAnyInPlay(board, Armageddon.class) ) {
            return 0;
        }
        return super.getPowerUpCost(board);
    }
    
}
