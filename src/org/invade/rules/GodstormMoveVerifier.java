/*
 * GodstormMoveVerifier.java
 *
 * Created on August 26, 2005, 11:44 AM
 *
 */

package org.invade.rules;

import org.invade.Board;
import org.invade.Card;
import org.invade.TerritoryDuple;
import org.invade.Force;
import org.invade.ForcePlacement;
import org.invade.IllegalMoveException;
import org.invade.SpecialMove;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.TurnMode;

public class GodstormMoveVerifier extends DefaultMoveVerifier {
    
    public static final String TEMPLE_PLACEMENT = "Temples must be placed in land territories";
    public static final String NO_UNDERWORLD_REINFORCEMENTS = "Place reinforcements in the land of the living";
    public static final String NO_CARDS = "You may not play cards during this phase";
    public static final String NO_UNDERWORLD_INVASION = "You may not invade the Underworld during this phase";
    
    public void verifyWithAssumptions(Board board, Object move)
    throws IllegalMoveException {
        if(board.getTurnMode().equals(TurnMode.EVEN_REINFORCEMENTS)
        || board.getTurnMode().equals(TurnMode.REINFORCEMENTS)) {
            ForcePlacement duple = (ForcePlacement)move;
            Territory territory = duple.getTerritory();
            Force force = duple.getForce();
            verify( ! force.getSpecialUnits().contains(GodstormRules.TEMPLE)
            || territory.getType().equals(TerritoryType.LAND), TEMPLE_PLACEMENT );
            verify( territory.getType() != TerritoryType.HEAVEN
                    && territory.getType() != TerritoryType.UNDERWORLD,
                    NO_UNDERWORLD_REINFORCEMENTS);
        } else if(board.getTurnMode().equals(TurnMode.DECLARE_INVASIONS)) {
            if( ! checkForSentinel(SpecialMove.END_MOVE, move) ) {
                TerritoryDuple duple = (TerritoryDuple)move;
                Territory from = duple.getFirst();
                Territory to = duple.getSecond();
                verify(to.getType() != TerritoryType.UNDERWORLD, NO_UNDERWORLD_INVASION);
            }
        }
        
        super.verifyWithAssumptions(board, move);
    }
    
}

class ResurrectionMoveVerifier extends GodstormMoveVerifier {
    public static final String RESURRECT_TO_TEMPLES = "Resurrection must occur in temples";
    public void verifyWithAssumptions(Board board, Object move)
    throws IllegalMoveException {
        verify( ! (move instanceof Card), GodstormMoveVerifier.NO_CARDS );
        if(board.getTurnMode().equals(TurnMode.EVEN_REINFORCEMENTS)
        || board.getTurnMode().equals(TurnMode.REINFORCEMENTS)) {
            ForcePlacement duple = (ForcePlacement)move;
            Territory territory = duple.getTerritory();
            verify(territory.getForce().getSpecialUnits().contains(GodstormRules.TEMPLE), RESURRECT_TO_TEMPLES);
        }
        super.verifyWithAssumptions(board, move);
    }
}

class EmbarkFromHeavenMoveVerifier extends DefaultMoveVerifier {
    public static final String EMBARK_INCORRECT = "Place units in underworld or heaven";
    public void verifyWithAssumptions(Board board, Object move)
    throws IllegalMoveException {
        verify( ! (move instanceof Card), GodstormMoveVerifier.NO_CARDS );
        if(board.getTurnMode().equals(TurnMode.EVEN_REINFORCEMENTS)
        || board.getTurnMode().equals(TurnMode.REINFORCEMENTS)) {
            ForcePlacement duple = (ForcePlacement)move;
            Territory territory = duple.getTerritory();
            verify(territory.getType() == TerritoryType.HEAVEN
                    || territory.getType() == TerritoryType.UNDERWORLD, EMBARK_INCORRECT);
        }
        super.verifyWithAssumptions(board, move);
    }
}

class InvadeTheUnderworldMoveVerifier extends DefaultMoveVerifier {
    public static final String INVADE_FROM = "Invade from heaven or the underworld";
    public static final String INVADE_THROUGH_GATE = "Invade from heaven through a gate";
    public static final String INVADE_TO = "Invade the underworld";
    public void verifyWithAssumptions(Board board, Object move)
    throws IllegalMoveException {
        verify( ! (move instanceof Card), GodstormMoveVerifier.NO_CARDS );
        if(board.getTurnMode().equals(TurnMode.DECLARE_INVASIONS)) {
            if( ! checkForSentinel(SpecialMove.END_MOVE, move) ) {
                TerritoryDuple duple = (TerritoryDuple)move;
                Territory from = duple.getFirst();
                Territory to = duple.getSecond();
                verify(from.getType() == TerritoryType.HEAVEN
                        || from.getType() == TerritoryType.UNDERWORLD, INVADE_FROM);
                verify(from.getType() == TerritoryType.UNDERWORLD
                        || to.getForce().getSpecialUnits().contains(GodstormRules.GATE),
                        INVADE_THROUGH_GATE);
                verify(to.getType() == TerritoryType.UNDERWORLD, INVADE_TO);
            }
        }
        super.verifyWithAssumptions(board, move);
    }
}
