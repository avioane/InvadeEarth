/*
 * EnemiesConvert.java
 *
 * Created on March 22, 2006, 9:37 AM
 *
 */

package org.invade.miraclecards;

import org.invade.AbstractMoveVerifier;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.SoundDriver;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.rules.DefaultMoveVerifier;

public class ChampionsArise extends HeroesAreBorn {
    
    public ChampionsArise(int powerUpCost) {
        super(powerUpCost, 0, "Champions Arise");
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        strength = board.getCurrentPlayer().getEnergy() / 2;
        super.doAction(board, gameThread);
    }
    
    public String getDescriptionString() {
        return "Gain 1 army for every 2 faith tokens you have.  (You don't "
                + "have to sacrifice the tokens.)  Place the armies in any "
                + "one territory you control.";
    }
    
}
