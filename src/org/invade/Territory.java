/*
 * Territory.java
 *
 * Created on June 20, 2005, 12:32 PM
 *
 */

package org.invade;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Icon;
import java.awt.*;
import java.awt.Image;
import java.net.URL;

import org.invade.resources.ResourceAnchor;

public class Territory {
    
    private String name = "";
    private Player owner = Player.NEUTRAL;
    private Force force = new Force();
    private TerritoryType type = TerritoryType.LAND;
    private Polygon shape = new Polygon();
    private Point center = new Point();
    private Map<Territory, EdgeType> edges = new HashMap<Territory, EdgeType>();
    private Set<Territory> adjacentDevastated = new HashSet<Territory>();
    private Continent continent = null;
    private boolean landingSite = false;
    private boolean devastated = false;
    private boolean plague = false;
    private Status territoryStatus = Status.NONE;
    private long iconPlacedTime = 0;
    
    // Only temporary; should not need persistence
    private int numberToDestroy = 0;
    
    private static final int DEFAULT_HALF_HEIGHT = 38;
    private static final int DEFAULT_SIDE_LENGTH = 44;
    
    
    public String toString() {
        return name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Player getOwner() {
        return owner;
    }
    
    public void setOwner(Player owner) {
        this.owner = owner;
    }
    
    public Force getForce() {
        return force;
    }
    
    public TerritoryType getType() {
        return type;
    }
    
    public void setType(TerritoryType type) {
        this.type = type;
    }
    
    public Polygon getShape() {
        return shape;
    }
    
    public Point getCenter() {
        return center;
    }
    
    public void setDefaultShape() {
        shape.reset();
        int x = getCenter().x;
        int y = getCenter().y;
        shape.addPoint( x - DEFAULT_SIDE_LENGTH / 2, y - DEFAULT_HALF_HEIGHT );
        shape.addPoint( x + DEFAULT_SIDE_LENGTH / 2, y - DEFAULT_HALF_HEIGHT );
        shape.addPoint( x + DEFAULT_SIDE_LENGTH, y );
        shape.addPoint( x + DEFAULT_SIDE_LENGTH / 2, y + DEFAULT_HALF_HEIGHT );
        shape.addPoint( x - DEFAULT_SIDE_LENGTH / 2, y + DEFAULT_HALF_HEIGHT );
        shape.addPoint( x - DEFAULT_SIDE_LENGTH, y );
    }
    
    
    public void setDirectedEdge(Territory to, EdgeType edgeType) {
        setDirectedEdge(to, edgeType, false);
    }
    
    /* Sets a directed edge from this territory to "to" of type edgeType.
     * If edgeType is null, any existing edge is removed. */
    public void setDirectedEdge(Territory to, EdgeType edgeType, boolean overrideDevastation) {
        if( overrideDevastation || !( isDevastated() || to.isDevastated() ) ) {
            if( to != this ) {
                if( edgeType == null ) {
                    edges.remove(to);
                } else {
                    edges.put(to, edgeType);
                }
            }
        }
    }
    
    public void setUndirectedEdge(Territory to, EdgeType edgeType) {
        setUndirectedEdge(to, edgeType, false);
    }
    
    /* Sets an "undirected edge" by adding two complementary directed edges */
    public void setUndirectedEdge(Territory to, EdgeType edgeType, boolean overrideDevastation) {
        setDirectedEdge(to, edgeType, overrideDevastation);
        to.setDirectedEdge(this, edgeType, overrideDevastation);
    }
    
    public Set<Territory> getAdjacent() { return getAdjacent(false); }
    
    public Set<Territory> getAdjacent(boolean overrideDevastation) {
        if( ! overrideDevastation ) {
            Set<Territory> result = new HashSet<Territory>(edges.keySet());
            result.removeAll(adjacentDevastated);
            return result;
        } else {
            Set<Territory> result = Collections.unmodifiableSet(edges.keySet());
            return result;
        }
    }
    
    public EdgeType getEdgeType(Territory to) { return getEdgeType(to, false); }
    
    public EdgeType getEdgeType(Territory to, boolean overrideDevastation) {
        if(!overrideDevastation && (isDevastated() || to.isDevastated()) ) {
            return null;
        }
        return edges.get(to);
    }
    
    public void removeDirectedEdge(Territory to) {
        removeDirectedEdge(to, false);
    }
    
    public void removeDirectedEdge(Territory to, boolean overrideDevastation) {
        if( (overrideDevastation || !( isDevastated() || to.isDevastated() ))
        && (to != this) ) {
            edges.remove(to);
        }
    }
    
    public void removeUndirectedEdge(Territory to) {
        removeUndirectedEdge(to, false);
    }
    
    public void removeUndirectedEdge(Territory to, boolean overrideDevastation) {
        removeDirectedEdge(to, overrideDevastation);
        to.removeDirectedEdge(this, overrideDevastation);
    }
    
    public boolean hasEdgeTo(Territory to) { return hasEdgeTo(to, false); }
    
    public boolean hasEdgeTo(Territory to, boolean overrideDevastation) {
        if( (!overrideDevastation) && (isDevastated() || to.isDevastated()) ) {
            return false;
        }
        return getEdgeType(to) != null && getEdgeType(to).isPassable();
    }
    
    public Continent getContinent() {
        return continent;
    }
    
    public void setContinent(Continent continent) {
        this.continent = continent;
    }
    
    public boolean isLandingSite() {
        return landingSite;
    }
    
    public void setLandingSite(boolean landingSite) {
        this.landingSite = landingSite;
    }
    
    public boolean isDevastated() {
        return devastated;
    }
    
    public void setDevastated(boolean devastated, Board board) {
        this.devastated = devastated;
        for( Territory from : board.getTerritories() ) {
            from.adjacentDevastated.clear();
            for( Territory to : from.getAdjacent(true) ) {
                if( to.isDevastated() ) {
                    from.adjacentDevastated.add(to);
                }
            }
        }
        TerritoryDeck deck = board.getTerritoryDeck(getType());
        if( deck != null ) {
            if( devastated ) {
                deck.getCards().remove(this);
            } else {
                deck.getCards().add(0, this);
            }
        }
    }
    
    public long getIconPlacedTime() {
        return this.iconPlacedTime;
    }
    
    public Status getTerritoryStatus() {
        return this.territoryStatus;
    }
    
    public void setTerritoryStatus(Status status) {
        this.territoryStatus = status;
        this.iconPlacedTime = System.currentTimeMillis();
    }
    
    public int getNumberToDestroy() {
        return numberToDestroy;
    }
    
    public void setNumberToDestroy(int numberToDestroy) {
        this.numberToDestroy = numberToDestroy;
    }
    
    public void addNumberToDestroy(int addend) {
        this.numberToDestroy += addend;
    }
    
    public void destroyUnits(Force force) {
        getForce().subtract(force);
        addNumberToDestroy( - force.getMobileIndependentSize());
        update();
    }
    
    /* Sets the owner to neutral and destroys all dependent units if no more
     * aligned, independent units are in the territory.  Should be called after destroying
     * units. */
    public void update() {
        if( force.getRegularUnits() <= 0 ) {
            boolean neutral = true;
            for( SpecialUnit special : force.getSpecialUnits() ) {
                neutral &= special.isNeutral() || (! special.isIndependentUnit());
            }
            if( neutral ) {
                List<SpecialUnit> independent = new ArrayList<SpecialUnit>();
                for( SpecialUnit special : force.getSpecialUnits() ) {
                    if( special.isIndependentUnit() ) {
                        independent.add(special);
                    }
                }
                force.getSpecialUnits().retainAll(independent);
                setOwner(Player.NEUTRAL);
            }
        }
    }
    
    public boolean isPlague() {
        return plague;
    }
    
    public void setPlague(boolean plague) {
        this.plague = plague;
    }
    
    
    
    public enum Status {
        NONE,
        ATTACKING,
        DEFENDING,
        DAMAGED("icons/explosion.gif", -35, -10, 7000),
        REINFORCED("icons/reinforced.gif", -40, -25, 5000);
        
        private Image image = null;
        private int offsetX = 0;
        private int offsetY = 0;
        private long displayTime = 0; // ms
        
        private Status() {}
        
        private Status(String location, int offsetX, int offsetY, long displayTime) {
            if( this.image == null ) {
                URL url = ResourceAnchor.class.getResource(location);
                if( url != null ) {
                    this.image = Toolkit.getDefaultToolkit().getImage(url);
                }
            }
            
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.displayTime = displayTime;
        }
        
        public Image getImage() {
            return image;
        }
        
        public int getOffsetX() {
            return offsetX;
        }
        
        public int getOffsetY() {
            return offsetY;
        }
        
        public long getDisplayTime() {
            return displayTime;
        }
    }
    
}