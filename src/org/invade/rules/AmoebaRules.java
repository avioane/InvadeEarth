/*
 * AmoebaRules.java
 *
 * Created on March 13, 2006, 3:40 PM
 *
 */

package org.invade.rules;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import org.invade.Board;
import org.invade.Card;
import org.invade.EndGameException;
import org.invade.GameAlgorithm;
import org.invade.PlayableDeck;
import org.invade.Player;
import org.invade.SpecialUnit;
import org.invade.TerritoryType;
import org.invade.amoeba.AlienDevastation;
import org.invade.amoeba.AlienSpawning;
import org.invade.amoeba.AlienWarning;
import org.invade.amoeba.AliensCatchACold;
import org.invade.amoeba.AmoebaInvasion;
import org.invade.amoeba.CommunicationsIntercepted;
import org.invade.amoeba.HeroicResistanceLeader;
import org.invade.amoeba.MothershipSyphon;
import org.invade.amoeba.Terraforming;
import org.invade.amoeba.WartimeInflation;

import org.invade.MapIcon;

import java.awt.*;
import java.awt.image.*;
import java.net.URL;
import org.invade.resources.ResourceAnchor;
import javax.imageio.ImageIO;
import java.io.IOException;

public class AmoebaRules extends DefaultRules {
    
    public String toString() {
        return "Amoeba Invasion";
    }
    
    public GameAlgorithm createGameAlgorithm() {
        return new AmoebaGameAlgorithm();
    }
    
    public static Player ALIENS = new Player("Giant Amoebae", new Color(20, 195, 75)) {
        public BufferedImage getPlayerImage() {
            URL url = ResourceAnchor.class.getResource("icons/amoeba.png");
            Image amoeba = null;
            if( url != null ) {
                try {
                    amoeba = ImageIO.read(url);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            
            //now, load the image into a buffered image, and then perform a filter on it to "tint" the image into the players color
            int iw = MapIcon.AMOEBA.getIconWidth();
            int ih = MapIcon.AMOEBA.getIconHeight();
        
            BufferedImage bi = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB);
            Graphics2D big = bi.createGraphics();
            big.drawImage(amoeba, 0, 0, null);
            
            return bi;
        }
    };
    
    public static SpecialUnit ALIEN_LEADER =  new SpecialUnit("Amoeba Leader", 0,
            new Color(96, 196, 48), true, false, true, 0, -1, -1,
            "land.png");
    
    private PlayableDeck amoebaDeck;
    
    
    private int amoebaInvasionStrength = 3;
    private int amoebaWarningStrength = 5;
    
    public List<PlayableDeck> getStartingDecks(Board board) {
        amoebaDeck = new PlayableDeck();
        amoebaDeck.setName("Amoeba Invasion");
        amoebaDeck.add(
                new MothershipSyphon(),
                new MothershipSyphon(),
                new CommunicationsIntercepted(),
                new CommunicationsIntercepted(),
                new AlienDevastation(TerritoryType.LAND),
                new AlienDevastation(TerritoryType.LAND),
                new AlienDevastation(TerritoryType.WATER),
                new AlienDevastation(TerritoryType.MOON),
                new Terraforming(),
                new Terraforming(),
                new AlienWarning(getAmoebaWarningStrength()),
                new AmoebaInvasion(TerritoryType.LAND, getAmoebaInvasionStrength()),
                new AmoebaInvasion(TerritoryType.LAND, getAmoebaInvasionStrength()),
                new AmoebaInvasion(TerritoryType.LAND, getAmoebaInvasionStrength()),
                new AmoebaInvasion(TerritoryType.LAND, getAmoebaInvasionStrength()),
                new AmoebaInvasion(TerritoryType.LAND, getAmoebaInvasionStrength()),
                new AmoebaInvasion(TerritoryType.LAND, getAmoebaInvasionStrength()),
                new AmoebaInvasion(TerritoryType.WATER, getAmoebaInvasionStrength()),
                new AmoebaInvasion(TerritoryType.WATER, getAmoebaInvasionStrength()),
                new AmoebaInvasion(TerritoryType.WATER, getAmoebaInvasionStrength()),
                new AmoebaInvasion(TerritoryType.WATER, getAmoebaInvasionStrength()),
                new AmoebaInvasion(TerritoryType.MOON, getAmoebaInvasionStrength()),
                new AmoebaInvasion(TerritoryType.MOON, getAmoebaInvasionStrength()),
                new AmoebaInvasion(TerritoryType.MOON, getAmoebaInvasionStrength()),
                new AlienSpawning(),
                new AlienSpawning(),
                new AlienSpawning(),
                new AliensCatchACold(),
                new HeroicResistanceLeader(2),
                new HeroicResistanceLeader(2)
                );
        
        Collections.shuffle(amoebaDeck.getCards(), board.getRandom());
        
        List<PlayableDeck> result = super.getStartingDecks(board);
        for(PlayableDeck deck : result) {
            if( deck.getRequiredUnit() == DIPLOMAT ) {
                deck.add(
                        new WartimeInflation(1),
                        new WartimeInflation(1),
                        new WartimeInflation(1)
                        );
            }
        }
        
        return result;
    }
    
    
    class AmoebaGameAlgorithm extends DefaultGameAlgorithm {
        public void receiveSupplies() throws EndGameException {
            Card card = amoebaDeck.draw();
            card.play(board, gameThread);
            amoebaDeck.discard(card);
            super.receiveSupplies();
        }
    }
    
    public int getAmoebaInvasionStrength() {
        return amoebaInvasionStrength;
    }
    
    public void setAmoebaInvasionStrength(int amoebaInvasionStrength) {
        this.amoebaInvasionStrength = amoebaInvasionStrength;
    }
    
    public int getAmoebaWarningStrength() {
        return amoebaWarningStrength;
    }
    
    public void setAmoebaWarningStrength(int amoebaWarningStrength) {
        this.amoebaWarningStrength = amoebaWarningStrength;
    }
    
}
