/*
 * Player.java
 *
 * Created on June 20, 2005, 12:40 PM
 *
 */

package org.invade;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.invade.agents.LocalHumanAgent;

import java.awt.*;
import java.awt.image.*;
import java.net.URL;
import org.invade.resources.ResourceAnchor;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Player {
    
    private Force reinforcements;
    private Color color;
    private String name;
    private int energy;
    private Agent agent;
    private List<Card> cards; 
    private int bonusPoints;
    private int freeMoves;
    private long sessionID;
    private boolean alive;
    
    private BufferedImage playerImage;

    public static final Player NEUTRAL = new Player("Neutral", Color.DARK_GRAY);        
    private static Color[] colors = {Color.RED.darker(), Color.BLUE.darker(), Color.BLACK, Color.MAGENTA.darker(), 
        Color.ORANGE.darker(), Color.GREEN.darker(), Color.CYAN.darker(), Color.YELLOW.darker(), Color.PINK.darker()};

    public Player(String name, Color color) {
        setReinforcements(new Force());
        this.name = name;
        this.color = color;
        energy = 0;
        agent = new LocalHumanAgent();
        cards = new ArrayList<Card>();
        bonusPoints = 0;
        freeMoves = 0;
        sessionID = 0;
        alive = true;
    }
    
    public Force getReinforcements() {
        return reinforcements;
    }

    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }    
    
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }
    
    public void addEnergy(int energy) {
        this.energy += energy;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public void setReinforcements(Force reinforcements) {
        this.reinforcements = reinforcements;
    }

    public List<Card> getCards() {
        return cards;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(int bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public int getFreeMoves() {
        return freeMoves;
    }

    public void setFreeMoves(int freeMoves) {
        this.freeMoves = freeMoves;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    
    public static Color getDefaultColor(int index) {
        return colors[index % colors.length];
    }
    
    public BufferedImage getPlayerImage() {
        if( playerImage == null ) {
            createPlayerImage();
        }
        
        return playerImage;
    }
    
    public void createPlayerImage() {
        URL url = ResourceAnchor.class.getResource("icons/darkgray.png");
        Image darkgray = null;
        if( url != null ) {
            try {
                darkgray = ImageIO.read(url);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } 

        //now, load the image into a buffered image, and then perform a filter on it to "tint" the image into the players color
        int iw = MapIcon.DARK_GRAY.getIconWidth();
        int ih = MapIcon.DARK_GRAY.getIconHeight();

        BufferedImage bi = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB);
        playerImage = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB);
        Graphics2D big = bi.createGraphics();
        big.drawImage(darkgray, 0, 0, null);

        byte[] identity = new byte[256];
        byte[] red_lookup = new byte[256];
        byte[] green_lookup = new byte[256];
        byte[] blue_lookup = new byte[256];

        //first, set everything to the identity
        for (int i = 0; i < 256; i++) {
            identity[i] = (byte) i; red_lookup[i] = (byte) i; blue_lookup[i] = (byte) i; green_lookup[i] = (byte) i; 
        } 

        //now, change the lookups to be the players color, but dont change black and white
        for ( int j=100;j<150 ;j++ ) {
            red_lookup[j]=(byte)(getColor().getRed());
            green_lookup[j]=(byte)(getColor().getGreen()); 
            blue_lookup[j]=(byte)(getColor().getBlue());   
        }
        for ( int j=151;j<256 ;j++ ) {
            red_lookup[j]=(byte)(255);
            green_lookup[j]=(byte)(255);
            blue_lookup[j]=(byte)(255);   
        }
        byte[][] color_threshold = {red_lookup, green_lookup, blue_lookup, identity};
        ByteLookupTable blut=new ByteLookupTable(0,color_threshold); 
        LookupOp lop = new LookupOp(blut, null); 
        lop.filter(bi,playerImage); 

        //playerImage should now be a created and filtered BufferedImage
    }
}
