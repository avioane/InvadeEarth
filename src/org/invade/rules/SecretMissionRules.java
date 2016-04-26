/*
 * SecretMissionRules.java
 *
 * Created on August 1, 2005, 4:43 PM
 *
 */

package org.invade.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.invade.Board;
import org.invade.Card;
import org.invade.Continent;
import org.invade.EndGameException;
import org.invade.GameAlgorithm;
import org.invade.GameThread;
import org.invade.Player;
import org.invade.Territory;
import org.invade.classic.AbstractMissionCard;

public class SecretMissionRules extends ClassicRules {
    
    public String toString() {
        return "Secret Mission";
    }
    
    private int territoriesWithTwoMission = 18;
    private int territoriesMission = 24;
    private int continentsMissions[][] = {{4, 1}, {4, 3}, {0, 3}, {0, 5},
    {2, 1, -1}, {2, 5, -1}};
    private int territoriesWithTwoMissionCount = 1;
    private int territoriesMissionCount = 1;
    private boolean playerEliminationMissions = true;
    private int missionPoints = 10000;
    
    public GameAlgorithm createGameAlgorithm() {
        return new SecretMissionGameAlgorithm();
    }
    
    public List<Card> getMissions(Board board) {
        List<Card> result = new ArrayList<Card>();
        for( int i = 0; i < getContinentsMissions().length; ++i ) {
            result.add(new ContinentsMission(i));
        }
        for( int i = 0; i < getTerritoriesMissionCount(); ++i ) {
            result.add(new TerritoriesMission());
        }
        for( int i = 0; i < getTerritoriesWithTwoMissionCount(); ++i ) {
            result.add(new TerritoriesWithTwoMission());
        }
        if( isPlayerEliminationMissions() || result.size() < board.getPlayers().size() ) {
            for( Player player : board.getPlayers() ) {
                result.add(new KillPlayerMission(player));
            }
        }
        return result;
    }
    
    class SecretMissionGameAlgorithm extends ClassicGameAlgorithm {
        public void claimTerritories() throws EndGameException {
            assignMissions();
            super.claimTerritories();
        }
        public void assignMissions() throws EndGameException {
            List<Card> missions = getMissions(board);
            Collections.shuffle(missions, board.getRandom());
            for( Player player : board.getPlayers() ) {
                Card mission = missions.remove(missions.size() - 1);
                board.setCurrentPlayer(player);
                mission.play(board, gameThread);
            }
        }
    }
    
    
    class TerritoriesWithTwoMission extends AbstractMissionCard {
        public TerritoriesWithTwoMission() {
            super("Occupy " + getTerritoriesMission() + ", two armies each", getMissionPoints());
        }
        public boolean isComplete(Board board) {
            return board.getTerritoriesOwned(getPlayer()).size() >= getTerritoriesMission();
        }
        public String getDescriptionString() {
            return "Occupy " + getTerritoriesMission() + " territories with " +
                    "at least two armies in each territory.";
        }
    }
    
    class TerritoriesMission extends AbstractMissionCard {
        public TerritoriesMission() {
            super("Occupy " + getTerritoriesMission(), getMissionPoints());
        }
        public boolean isComplete(Board board) {
            return board.getTerritoriesOwned(getPlayer()).size() >= getTerritoriesMission();
        }
        public String getDescriptionString() {
            return "Occupy " + getTerritoriesMission() + " territories.";
        }
    }
    
    class KillPlayerMission extends AbstractMissionCard {
        Player target;
        public KillPlayerMission(Player target) {
            super("Eliminate " + target.getName(), getMissionPoints());
            this.target = target;
        }
        public boolean isComplete(Board board) {
            if( getPlayer() == target ) {
                return board.getTerritoriesOwned(getPlayer()).size() >= getTerritoriesMission();
            }
            return ! target.isAlive();
        }
        public String getDescriptionString() {
            return "Destroy all armies belonging to " + target.getName() + ".  " +
                    "If you are " + target.getName() + ", occupy " +
                    getTerritoriesMission() + " territories instead.";
        }
    }
    
    
    
    class ContinentsMission extends AbstractMissionCard {
        private int missionIndex;
        private String descriptionString;
        public ContinentsMission(int missionIndex) {
            super("Conquer Continents", getMissionPoints());
            this.missionIndex = missionIndex;
        }
        public void play(Board board, GameThread gameThread) throws EndGameException {
            super.play(board, gameThread);
            descriptionString = "Conquer the following:\n";
            for( int i = 0; i < getContinentsMissions()[missionIndex].length; ++i ) {
                int continentIndex = getContinentsMissions()[missionIndex][i];
                if( continentIndex >= 0 && continentIndex < board.getContinents().size() ) {
                    Continent continent = board.getContinents().get(
                            getContinentsMissions()[missionIndex][i]);
                    descriptionString += continent.getName() + "\n";
                } else {
                    descriptionString += "Any continent\n";
                }
            }
        }
        public boolean isComplete(Board board) {
            for( int i = 0; i < getContinentsMissions()[missionIndex].length; ++i ) {
                int continentIndex = getContinentsMissions()[missionIndex][i];
                if( continentIndex >= 0 && continentIndex < board.getContinents().size() ) {
                    Continent continent = board.getContinents().get(
                            continentIndex);
                    for( Territory territory : board.getTerritories() ) {
                        if( territory.getContinent() == continent
                                && territory.getOwner() != getPlayer() ) {
                            return false;
                        }
                    }
                }
            }
            return getContinentsOwnedCount(board) >=
                    getContinentsMissions()[missionIndex].length;
        }
        public String getDescriptionString() {
            return descriptionString;
        }
        public int getContinentsOwnedCount(Board board) {
            int result = 0;
            for( Continent continent : board.getContinents() ) {
                boolean own = true;
                for( Territory territory : board.getContinent(continent) ) {
                    if( territory.getOwner() != getPlayer() ) {
                        own = false;
                    }
                }
                if( own ) {
                    result++;
                }
            }
            return result;
        }
    }
    
    
    
    public int getTerritoriesWithTwoMission() {
        return territoriesWithTwoMission;
    }
    
    public void setTerritoriesWithTwoMission(int territoriesWithTwoMission) {
        this.territoriesWithTwoMission = territoriesWithTwoMission;
    }
    
    public int getTerritoriesMission() {
        return territoriesMission;
    }
    
    public void setTerritoriesMission(int territoriesMission) {
        this.territoriesMission = territoriesMission;
    }
    
    public int[][] getContinentsMissions() {
        return continentsMissions;
    }
    
    public void setContinentsMissions(int[][] continentsMissions) {
        this.continentsMissions = continentsMissions;
    }
    
    public int getTerritoriesWithTwoMissionCount() {
        return territoriesWithTwoMissionCount;
    }
    
    public void setTerritoriesWithTwoMissionCount(int territoriesWithTwoMissionCount) {
        this.territoriesWithTwoMissionCount = territoriesWithTwoMissionCount;
    }
    
    public int getTerritoriesMissionCount() {
        return territoriesMissionCount;
    }
    
    public void setTerritoriesMissionCount(int territoriesMissionCount) {
        this.territoriesMissionCount = territoriesMissionCount;
    }
    
    public boolean isPlayerEliminationMissions() {
        return playerEliminationMissions;
    }
    
    public void setPlayerEliminationMissions(boolean playerEliminationMissions) {
        this.playerEliminationMissions = playerEliminationMissions;
    }

    public int getMissionPoints() {
        return missionPoints;
    }

    public void setMissionPoints(int missionPoints) {
        this.missionPoints = missionPoints;
    }
    
}
