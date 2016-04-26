/*
 * DefaultMoveVerifier.java
 *
 * Created on August 3, 2005, 1:23 PM
 *
 */

package org.invade.rules;
import java.util.Collections;
import org.invade.*;


public class DefaultMoveVerifier extends AbstractMoveVerifier {
    
    /* Error messages for illegal moves */
    public static final String DEVASTATED = "Select a territory that is not devastated";
    public static final String CLAIM_NEUTRAL = "Only neutral territories may be claimed";
    public static final String CLAIM_LAND = "Only land territories may be claimed";
    public static final String REINFORCE_FRIENDLY = "Reinforcements must be placed in friendly territories";
    public static final String SPACE_STATION_PLACEMENT = "Space Stations must be placed in land territories";
    public static final String UNITS_PER_TERRITORY = "Placement would exceed the limit per territory of a special unit";
    public static final String ATTACK_NOT_PERMITTED = "That attack is impossible";
    public static final String FREE_MOVE_NOT_PERMITTED = "That free move is impossible";
    public static final String NOT_ENOUGH_UNITS = "Not enough units";
    public static final String COMMANDER_NEEDED = "A required commander is not available";
    public static final String CEASE_FIRE = "Invasion is blocked by an active card";
    public static final String NOT_ENOUGH_ENERGY = "Not enough available";
    public static final String BID_MUST_BE_NONNEGATIVE = "Bid must be nonnegative";
    public static final String TURN_ORDER_NOT_AVAILABLE = "Invalid turn order choice";
    public static final String UNIT_LIMIT = "Purchase would exceed a unit limit for that type";
    public static final String NO_ROOM = "No room to place that unit";
    public static final String ILLEGAL_CARD = "That card may not be played at this time";
    public static final String CARD_NOT_IN_HAND = "Only the player holding a card may play it";
    public static final String WRONG_NUMBER_OF_UNITS = "Wrong number of units";
    public static final String NEED_UNITS_FOR_BATTLE = "At least one unit must be sent into battle";
    public static final String LEAVE_ONE_BEHIND = "At least one unit must be left behind";
    public static final String CHOOSE_COMMANDER = "Select one commander";
    public static final String CANNOT_BUY = "Card purchase not allowed";
    public static final String CANNOT_DRAW = "Drawing from that deck is not allowed";
    public static final String DESTROY_IN_FRIENDLY = "Select a friendly territory";
    public static final String IGNORED_FORCE_PLAY_CARD = "A card must be played";
    public static final String WRONG_TYPE = "Wrong territory type";
    public static final String SELECT_FRIENDLY = "Select a friendly territory";
    public static final String SELECT_WITH_COMMANDER = "Select a territory with a commander";
    public static final String SELECT_ENEMY = "Select an enemy territory";
    public static final String DESTROY_MOBILE_UNITS = "Wrong unit type";
    public static final String SELECT_WITH_UNITS = "Select a territory with units";

    public void verifyWithAssumptions(Board board, Object move)
    throws IllegalMoveException {
        if( move instanceof Card ) {
            verify( board.getCurrentPlayer().getCards().contains((Card)move),
                    CARD_NOT_IN_HAND );
            verify( ((Card)move).canPlay(board), ILLEGAL_CARD );
            
        } else if(board.getTurnMode().equals(TurnMode.FORCE_PLAY_CARD)) {
            /* This will not be executed unless an agent ignored a
             * TurnMode.FORCE_PLAY_CARD directive. */
            verify(false, IGNORED_FORCE_PLAY_CARD);
            
        } else if(board.getTurnMode().equals(TurnMode.CLAIM_TERRITORIES)) {
            verify( ((Territory)move).getOwner() == Player.NEUTRAL,
                    CLAIM_NEUTRAL);
            verify( ((Territory)move).getType().equals(TerritoryType.LAND),
                    CLAIM_LAND );
            verifyNotDevastated((Territory)move);
            
        } else if(board.getTurnMode().equals(TurnMode.EVEN_REINFORCEMENTS)
        || board.getTurnMode().equals(TurnMode.REINFORCEMENTS)) {
            ForcePlacement duple = (ForcePlacement)move;
            Territory territory = duple.getTerritory();
            Force force = duple.getForce();
            verify( territory.getOwner() == board.getCurrentPlayer(),
                    REINFORCE_FRIENDLY);
            verify( ! force.getSpecialUnits().contains(DefaultRules.SPACE_STATION)
            || territory.getType().equals(TerritoryType.LAND), SPACE_STATION_PLACEMENT );
            /* Verify that this move will not cause the selected territory
             * to have too many units of a particular type */
            {
                Force temp = new Force();
                temp.add(force);
                temp.add(territory.getForce());
                for(SpecialUnit special : temp.getSpecialUnits()) {
                    verify( special.getMaxPerTerritory() < 0
                            || Collections.frequency(temp.getSpecialUnits(), special)
                            <= special.getMaxPerTerritory(), UNITS_PER_TERRITORY );
                }
            }
            if( board.getTurnMode().equals(TurnMode.EVEN_REINFORCEMENTS) ) {
                verify( force.getSize() <= board.getNumberToPlace(),
                        WRONG_NUMBER_OF_UNITS );
            }
            
        } else if( board.getTurnMode().equals(TurnMode.DECLARE_INVASIONS) ) {
            if( ! checkForSentinel(SpecialMove.END_MOVE, move) ) {
                TerritoryDuple duple = (TerritoryDuple)move;
                verifyInvasion(board, duple.getFirst(), duple.getSecond());
            }
            
        } else if( board.getTurnMode().equals(TurnMode.DECLARE_FREE_MOVES) ) {
            if( ! checkForSentinel(SpecialMove.END_MOVE, move) ) {
                TerritoryDuple duple = (TerritoryDuple)move;
                verifyIsFriendly(duple.getFirst(), board.getCurrentPlayer());
                verifyIsFriendly(duple.getSecond(), board.getCurrentPlayer());
                verify(duple.getFirst().getForce().getMobileForce().getSize() > 1,
                        NOT_ENOUGH_UNITS);
                verify(board.canFreeMove(duple.getFirst(), duple.getSecond()),
                        FREE_MOVE_NOT_PERMITTED);
            }
            
        } else if( board.getTurnMode().equals(TurnMode.BID) ) {
            verify( (Integer)move <= board.getCurrentPlayer().getEnergy(),
                    NOT_ENOUGH_ENERGY );
            verify( (Integer)move >= 0, BID_MUST_BE_NONNEGATIVE );
            
        } else if( board.getTurnMode().equals(TurnMode.CHOOSE_ORDER) ) {
            verify( board.getTurnChoices().contains((Integer)move),
                    TURN_ORDER_NOT_AVAILABLE );
            
        } else if( board.getTurnMode().equals(TurnMode.BUY_UNITS) ) {
            if( ! checkForSentinel(SpecialMove.END_MOVE, move) ) {
                SpecialUnit special = (SpecialUnit)move;
                Player player = board.getCurrentPlayer();
                verify( player.getEnergy() >= special.getPrice(), NOT_ENOUGH_ENERGY );
                verify( special.getMaxOwnable() < 0 
                        || board.getUnitCount(player, special) < special.getMaxOwnable(),
                        UNIT_LIMIT);
                verify( special.getMaxTotal() < 0 
                        || board.getUnitCount(null, special) < special.getMaxTotal(),
                        UNIT_LIMIT); 
                
                if( special.equals(DefaultRules.SPACE_STATION) ) {
                    verify( CommonBoardMethods.hasRoomFor(board, special,
                        board.getTerritoriesOwned(player, TerritoryType.LAND),
                            Collections.frequency(player.getReinforcements().getSpecialUnits(), special) + 1),
                            NO_ROOM);
                } else {
                    verify( CommonBoardMethods.hasRoomFor(board, special,
                        board.getTerritoriesOwned(player), 
                            Collections.frequency(player.getReinforcements().getSpecialUnits(), special) + 1), NO_ROOM );
                }
            }
            
        } else if( board.getTurnMode().equals(TurnMode.CHOOSE_DESTROYED) ) {            
            verify( ((Force)move).getMobileIndependentSize()
            == board.getDamagedTerritory().getNumberToDestroy(),
                    WRONG_NUMBER_OF_UNITS);
            verify( ((Force)move).getMobileIndependentSize() == ((Force)move).getSize(),
                    DESTROY_MOBILE_UNITS);
            
        } else if( board.getTurnMode().equals(TurnMode.DECLARE_ATTACK_FORCE)
        || board.getTurnMode().equals(TurnMode.COMPLETE_FREE_MOVE)
        || board.getTurnMode().equals(TurnMode.DECLARE_DEFENSE_FORCE) ) {
            if( ! board.getTurnMode().equals(TurnMode.DECLARE_DEFENSE_FORCE) ) {
                verify( ((Force)move).getMobileIndependentSize() <
                        board.getAttackingTerritory().getForce().getMobileIndependentSize(),
                        LEAVE_ONE_BEHIND );
            }
            if( ! board.getTurnMode().equals(TurnMode.COMPLETE_FREE_MOVE) ) {
                verify( ((Force)move).getMobileIndependentSize() > 0, NEED_UNITS_FOR_BATTLE );
            }
            
        } else if( board.getTurnMode().equals(TurnMode.BATTLE_RESULTS)
        || board.getTurnMode().equals(TurnMode.ACKNOWLEDGE_INVASION)
        || board.getTurnMode().equals(TurnMode.ACKNOWLEDGE_GAME_OVER)
        || board.getTurnMode().equals(TurnMode.ACKNOWLEDGE_EACH_PLAYER) ) {
            verifySentinel(SpecialMove.ACKNOWLEDGE, move);
        
        } else if( board.getTurnMode().equals(TurnMode.CHOOSE_YES_NO) ) {
            verify( move instanceof SpecialMove, WRONG_TYPE );
            verify( move == SpecialMove.YES || move == SpecialMove.NO, 
                    "Wrong sentinel type (need yes/no): " + move );
            
        } else if( board.getTurnMode().equals(TurnMode.DRAW_CARD) ) {
            if( ! checkForSentinel(SpecialMove.END_MOVE, move) ) {
                verify( ((PlayableDeck)move).canDraw(board), CANNOT_DRAW );
            }
            
        } else if( board.getTurnMode().equals(TurnMode.BUY_CARDS) ) {
            if( ! checkForSentinel(SpecialMove.END_MOVE, move) ) {
                verify( ((PlayableDeck)move).canBuy(board), CANNOT_BUY );
            }
            
        } else if( board.getTurnMode().equals(TurnMode.DESTROY_A_REGULAR_UNIT) ) {
            Territory territory = (Territory)move;
            verify( territory.getOwner() == board.getCurrentPlayer(),
                    DESTROY_IN_FRIENDLY );
            verify( territory.getForce().getRegularUnits() > 0,
                    NOT_ENOUGH_UNITS );
            
        } else if( board.getTurnMode().equals(TurnMode.CHOOSE_COMMANDER) ) {
            verify( ((Force)move).getSize() == 1
                    && ((Force)move).getRegularUnits() == 0
                    && ((Force)move).getSpecialUnits().get(0).isMobile(),
                    CHOOSE_COMMANDER );
            
        } else if( board.getTurnMode().equals(TurnMode.CHOOSE_PLAYER) ) {
            Player castCheck = (Player)move;
            
        } else if( board.getTurnMode().equals(TurnMode.CHOOSE_TERRITORY) ) {
            Territory castCheck = (Territory)move;
            
        }
    }
    
    /* Checks to see whether a territory can invade another territory
     * (either directly, through a space station, or through a card),
     * if that territory has a sufficient force (at least two mobile units),
     * if the invading player has the necessary commander(s),
     * if the defending player does not have a cease fire agreement,
     * and if the defending territory is not devastated.
     * If any condition is violated, an IllegalMoveException is thrown.
     */
    public static void verifyInvasion(Board board, Territory from, Territory to)
    throws IllegalMoveException {
        verifyIsFriendly(from, board.getCurrentPlayer());
        verifyIsEnemy(to, board.getCurrentPlayer());
        verify( (from.hasEdgeTo(to)
        || (to.isLandingSite()
        && from.getForce().getSpecialUnits().contains(DefaultRules.SPACE_STATION) )
        || CommonBoardMethods.isInvasionAllowedByCard(board, from, to)
        ), ATTACK_NOT_PERMITTED );
        verify(from.getForce().getMobileIndependentSize() > 1, NOT_ENOUGH_UNITS);
        if( from.getType().equals(TerritoryType.WATER)
        || to.getType().equals(TerritoryType.WATER) ) {
            verify( board.getUnitCount(from.getOwner(), DefaultRules.NAVAL ) > 0, COMMANDER_NEEDED );
        } else if( from.getType().equals(TerritoryType.MOON)
        || to.getType().equals(TerritoryType.MOON) ) {
            verify( board.getUnitCount(from.getOwner(), DefaultRules.SPACE ) > 0, COMMANDER_NEEDED );
        }        
        verify( !CommonBoardMethods.isInvasionBlockedByCard(board, from, to),
                CEASE_FIRE);
    }
    
    public static void verifyNotDevastated(Territory territory)
    throws IllegalMoveException {
        verify( ! territory.isDevastated(), DEVASTATED );
    }
    
    public static void verifyType(Territory territory, TerritoryType type)
    throws IllegalMoveException {
        verify( territory.getType().equals(type), WRONG_TYPE );
    }
    
    public static void verifyIsFriendly(Territory territory, Player owner)
    throws IllegalMoveException {
        verify( territory.getOwner() == owner, SELECT_FRIENDLY );
    }
    
    public static void verifyIsEnemy(Territory territory, Player notOwner)
    throws IllegalMoveException {
        verify( territory.getOwner() != notOwner, SELECT_ENEMY );
    }
    
    public static void verifyHasCommander(Territory territory)
    throws IllegalMoveException {
        verify( territory.getForce().getMobileForce().getSpecialUnits().size() > 0,
                SELECT_WITH_COMMANDER );
    }
    
    public static void verifyHasUnits(Territory territory)
    throws IllegalMoveException {
        verify( ! territory.getForce().getMobileForce().isEmpty(), SELECT_WITH_UNITS );
    }
    
}
