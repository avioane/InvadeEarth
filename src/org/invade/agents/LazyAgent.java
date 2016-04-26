/*
 * LazyAgent.java
 *
 * Created on August 4, 2005, 1:13 PM
 *
 */

package org.invade.agents;

import org.invade.Agent;
import org.invade.Board;
import org.invade.SpecialMove;
import org.invade.TurnMode;


/* LazyAgent plays an END_MOVE or ACKNOWLEDGE whenever possible and declares or 
 * moves the default Force in an invasion or free move.  Turn modes that 
 * LazyAgent ignores (and should be handled in subclasses) are:
 * CLAIM_TERRITORIES, REINFORCEMENTS, EVEN_REINFORCEMENTS, BID, CHOOSE_ORDER,
 * DRAW_CARD, CHOOSE_COMMANDER, FORCE_PLAY_CARD, DESTROY_A_MOD, CHOOSE_TERRITORY
 */
public abstract class LazyAgent extends Agent {
    
    public Object getMove(Board board) {
        if(board.getTurnMode().equals(TurnMode.ACKNOWLEDGE_GAME_OVER)
        || board.getTurnMode().equals(TurnMode.ACKNOWLEDGE_EACH_PLAYER)
        || board.getTurnMode().equals(TurnMode.ACKNOWLEDGE_INVASION)
        || board.getTurnMode().equals(TurnMode.BATTLE_RESULTS) ) {
            return SpecialMove.ACKNOWLEDGE;
        } else if(board.getTurnMode().equals(TurnMode.BUY_UNITS)
        || board.getTurnMode().equals(TurnMode.BUY_CARDS)
        || board.getTurnMode().equals(TurnMode.DECLARE_INVASIONS)
        || board.getTurnMode().equals(TurnMode.DECLARE_FREE_MOVES)) {
            return SpecialMove.END_MOVE;
        } else if(board.getTurnMode().equals(TurnMode.DECLARE_ATTACK_FORCE) ) {
            return board.getAttackingTerritory().getForce().getDefaultAttack(board.getRules());
        } else if(board.getTurnMode().equals(TurnMode.DECLARE_DEFENSE_FORCE) ) {
            return board.getDefendingTerritory().getForce().getDefaultDefense(board.getRules());
        } else if(board.getTurnMode().equals(TurnMode.CHOOSE_DESTROYED) ) {
            return board.getDamagedTerritory().getForce().getDefaultDestroyed(
                        board.getDamagedTerritory().getNumberToDestroy());
        } else if(board.getTurnMode().equals(TurnMode.COMPLETE_FREE_MOVE) ) {
            return board.getAttackingTerritory().getForce().getDefaultFreeMove();
        }
        return super.getMove(board);
    }
    
}
