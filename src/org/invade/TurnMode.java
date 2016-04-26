/*
 * TurnMode.java
 *
 * Created on June 25, 2005, 12:14 PM
 *
 */

package org.invade;

/**
 *
 * @author Jonathan Crosmer
 */
public enum TurnMode {
    
    // Non-interactive modes
    NONE, GAME_OVER, TURN_START,
    
    /* Simple interactive modes: These require input that any agent should
     * be able to provide. */
    EVEN_REINFORCEMENTS, BID, CHOOSE_ORDER,
    REINFORCEMENTS, CLAIM_TERRITORIES,
    BUY_UNITS, BUY_CARDS, DECLARE_INVASIONS, ACKNOWLEDGE_INVASION,
    DECLARE_ATTACK_FORCE, DECLARE_DEFENSE_FORCE, BATTLE_RESULTS, CHOOSE_DESTROYED,
    DECLARE_FREE_MOVES, COMPLETE_FREE_MOVE,
    DRAW_CARD, ACKNOWLEDGE_GAME_OVER, ACKNOWLEDGE_EACH_PLAYER, 
    FORCE_PLAY_CARD, DESTROY_A_REGULAR_UNIT,    
    CHOOSE_YES_NO,
            
    /* Complex modes:  These require input that may have additional semantics or 
     * constraints.  For example, a turn mode of CHOOSE_TERRITORY does not 
     * indicate anything about why an agent should choose a territory or what
     * will happen to that territory.
     * The DefaultMoveVerifier may not verify input in these modes.  Card
     * objects that use these must not implement AutomaticCard and should 
     * define a more strict MoveVerifier when necessary.
     */
    CHOOSE_TERRITORY, CHOOSE_PLAYER, CHOOSE_COMMANDER

}
