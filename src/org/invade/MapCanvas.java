/*
 * MapCanvas.java
 *
 * Created on June 20, 2005, 1:09 PM
 *
 */

package org.invade;

import java.awt.*;
import java.awt.image.*;
import java.awt.BasicStroke;
import java.awt.Color; 
import java.awt.Dimension;
import java.awt.Font; 
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.Timer;
import org.invade.resources.ResourceAnchor;

public class MapCanvas extends JPanel {
    
    private Board board = null;
    private List<Territory> highlighted = new ArrayList<Territory>();
    
    private double zoomFactor = 0.62;
    
    private boolean showShapes = false;
    private boolean showNames = false;
    private boolean showHighlight = true;
    private boolean showCenter = false;
    private boolean showLandingSites = false;
    private boolean showTerritoryTypes = false;
    private boolean showContinents = true;
    private boolean showBorders = true;
    private boolean showBordersOverImage = false;
    private boolean showUnits = true;
    private boolean showSpecialIcons = true;
    private boolean showOwnerCircle = true;
    private boolean includeSpecialUnitsInCount = false;
    private boolean scrollToHighlighted = true;
    private static boolean iconsEnabled = true;
    private EnumSet<EdgeType> showEdgeTypes = EnumSet.noneOf(EdgeType.class);
    private Color textColor = Color.WHITE;
    private Color borderColor = Color.WHITE;
    private Color highlightColor = Color.YELLOW;
    private Color centerColor = Color.WHITE;
    private Color backgroundColor = Color.BLACK;
    private Color plagueColor = new Color(0, 96, 0);
    private int ownerCircleSize = 32;
    private int ownerSelectedCircleSize = 48;
    private int centerSize = 12;
    private int landingSiteSize = centerSize + 7;
    private Stroke borderStroke = new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private Stroke highlightStroke = new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private Stroke normalStroke = new BasicStroke(1.0f);
    private Font nameFont = new Font("Arial", Font.PLAIN, 12);
    private Font unitsFont = new Font("Arial", Font.BOLD, 24 );
    private Image backgroundImage = null;
    private Timer timer; 
    private int blink = 0;
    
    public MapCanvas() {
        super();
        ToolTipManager.sharedInstance().registerComponent(this);
        
        //make the gui refresh itself every half second
        /*
        timer = new Timer(500, new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  if( blink == 0 ) {
                      blink = 1;
                  } else {
                      blink = 0;
                  }
                  repaint();
              }
           });
        timer.start();
         */
    }
    
    public void paintComponent(Graphics graphics) {
        if( board == null ) {
            updateSize();
            return;
        }
        
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, getWidth(), getHeight());
        
        if( backgroundImage != null ) {
            graphics.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(),
                    backgroundColor, this);
        }
        
        Graphics2D page = (Graphics2D) graphics;
        AffineTransform old = page.getTransform();
        page.transform(AffineTransform.getScaleInstance(zoomFactor, zoomFactor));
        
        //this bufferedimageop is used to display the playerImage BufferedImages...(the player Territory icons)
        BufferedImageOp biop = null;
        AffineTransform at = new AffineTransform();
        at.scale(1, 1);
        biop = new AffineTransformOp(at,
                AffineTransformOp.TYPE_BILINEAR);
        
        if( getBoard() != null ) {
            page.setStroke(borderStroke); 
            
            for( Territory territory : getBoard().getTerritories() ) {
                if( isShowShapes() ) {
                    if( territory.getShape().npoints > 0 ) {
                        page.setColor(territory.getOwner().getColor());
                        if( isShowTerritoryTypes() ) {
                            page.setColor(territory.getType().getColor());
                        }
                        if( isShowContinents()
                        && (territory.getContinent() != null)
                        && (territory.getContinent().getColor() != null) ) {
                            page.setColor(territory.getContinent().getColor());
                        }
                        page.fillPolygon(territory.getShape());
                    }
                }
            }
            if( backgroundImage == null || isShowBordersOverImage() ) {
                for( Territory territory : getBoard().getTerritories() ) {
                    if( territory.getShape().npoints > 0 ) {
                        if( isShowBorders() ) {
                            page.setColor(getBorderColor());
                            page.drawPolygon(territory.getShape());
                        }
                    }
                }
            }
            for( Territory territory : getBoard().getTerritories() ) {
                boolean needCenter = isShowCenter()
                || (territory.isLandingSite() && isShowLandingSites());
                if( needCenter ) {
                    page.setColor(getCenterColor());
                    page.fillOval(territory.getCenter().x - centerSize / 2,
                            territory.getCenter().y - centerSize / 2,
                            centerSize, centerSize );
                    if( territory.isLandingSite() && isShowLandingSites() ) {
                        page.setStroke(normalStroke);
                        page.drawOval(territory.getCenter().x - 1 - landingSiteSize / 2,
                                territory.getCenter().y - 1 - landingSiteSize / 2,
                                landingSiteSize, landingSiteSize );
                    }
                }
                if( territory.isDevastated() ) {
                    Icon devastationIcon = board.getRules().getDevastatedIcon();
                    devastationIcon.paintIcon(this, page,
                            territory.getCenter().x - devastationIcon.getIconWidth() / 2,
                            territory.getCenter().y - devastationIcon.getIconHeight() / 2);
                }
                if( isShowUnits() && !territory.isDevastated() ) {
                    if( isShowOwnerCircle() ) {
                        
                        page.setColor(territory.getOwner().getColor());
                        page.setStroke(borderStroke);
                        
                        //first, draw any icons for hte territory
                        if( board.getTurnMode().equals(TurnMode.CLAIM_TERRITORIES) ) {
                            //just highlight the attacking territory
                            if( territory == board.getAttackingTerritory() ) {
                                MapIcon.FLAG.paintIcon(this, page, 
                                    territory.getCenter().x + 7, territory.getCenter().y - 19);
                            }
                        } else {
                            if( getHighlighted().contains(territory)                        
                            && ( territory.getShape().npoints > 0 ) ) {
                                /*
                                int alpha;
                                if( blink == 0 ) {
                                    alpha = 60;
                                } else {
                                    alpha = 25;
                                }
                                page.setColor(new Color(255, 255, 255, alpha));
                                page.fillPolygon(territory.getShape());
                                 */

                                if( territory == board.getDefendingTerritory() ) {
                                MapIcon.DEFENSE.paintIcon(this, page, 
                                    territory.getCenter().x + 15, territory.getCenter().y - 12);
                                } else {
                                    MapIcon.ATTACK.paintIcon(this, page, 
                                            territory.getCenter().x + 5, territory.getCenter().y - 23);
                                }

                            }
                        }
                        
                        if( territory.isPlague() ) {
                            MapIcon.PLAGUE.paintIcon(this, page, 
                                  territory.getCenter().x - 37, territory.getCenter().y - 42);

                        }
                        
                        //then, draw the player territory icon and determine text color
                        //set the text color depending on the brightness of the player's color
                        //Brightness = ((Red value X 299) + (Green value X 587) + (Blue value X 114)) / 1000
                        Color playercolor = territory.getOwner().getColor();
                        double brightness = ((playercolor.getRed() * 299) + (playercolor.getGreen() * 587) + (playercolor.getBlue() * 114)) / 1000;
                        if( brightness > 125 ) {
                            setTextColor(Color.black);
                        } else {
                            setTextColor(Color.white);
                        }
                        
                        if( territory.getOwner() != Player.NEUTRAL ) {
                            page.drawImage(territory.getOwner().getPlayerImage(),biop,
                                    territory.getCenter().x - territory.getOwner().getPlayerImage().getWidth() / 2,
                                    territory.getCenter().y - territory.getOwner().getPlayerImage().getHeight() / 2);
                            
                        } else {
                            MapIcon.DARK_GRAY.paintIcon(this, page,
                                    territory.getCenter().x - 2 - MapIcon.DARK_GRAY.getIconWidth() / 2,
                                    territory.getCenter().y + 2 - MapIcon.DARK_GRAY.getIconHeight() / 2);
                        }
                    }
                    page.setColor(getTextColor());

                    page.setFont(unitsFont);
                    String string = Integer.toString( isIncludeSpecialUnitsInCount()
                    ? territory.getForce().getMobileIndependentSize() : territory.getForce().getRegularUnits() );
                    if( ! territory.getForce().getSpecialUnits().isEmpty() ) {
                        if( isShowSpecialIcons() ) {
                            page.setStroke(normalStroke);
                            drawUnitIcons(page, territory.getCenter().x, territory.getCenter().y - 32 - (needCenter ? 16 : 0), territory.getForce());
                        }
                    }
                    drawCenteredString(string, page, territory.getCenter(),
                            (needCenter ? -0.4 : 0.3) );
                }
                if( isShowNames() ) {
                    page.setColor(getTextColor());
                    page.setFont(nameFont);
                    drawCenteredString(territory.getName(), page, territory.getCenter(), 1.5 );
                }
                if( backgroundImage == null || isShowBordersOverImage() ) {
                    if( isShowHighlight() ) {
                        if( getHighlighted().contains(territory) ) {
                            if( territory.getShape().npoints > 0 ) {
                                page.setStroke(highlightStroke);
                                page.setColor(getHighlightColor());
                                page.drawPolygon(territory.getShape());
                            }
                        }
                    }
                }
                for( Territory adjacent : territory.getAdjacent() ) {
                    EdgeType type = territory.getEdgeType(adjacent);
                    if( type != null ) {
                        if( getShowEdgeTypes().contains( type ) ) {
                            page.setColor( type.getColor() );
                            page.setStroke(type.getStroke());
                            int fromX = territory.getCenter().x;
                            int fromY = territory.getCenter().y;
                            int toX = adjacent.getCenter().x;
                            int toY = adjacent.getCenter().y;
                            if( type.isWrapHorizontal() ) {
                                if( toX > fromX ) {
                                    toX -= getBoard().getSize().width;
                                } else if( toX < fromX ) {
                                    toX += getBoard().getSize().width;
                                }
                                page.drawLine(fromX, fromY, toX, toY);
                            } else if( fromX < toX || (fromX == toX && fromY < toY) ) {
                                page.drawLine(fromX, fromY, toX, toY);
                            }
                        }
                        
                        if( type == EdgeType.MAELSTROM ) {
                            //draw a sunk icon 
                            int xloc = territory.getCenter().x - (territory.getCenter().x - adjacent.getCenter().x) / 2 - (MapIcon.SUNK_MARKER.getIconWidth() / 2);
                            int yloc = territory.getCenter().y - (territory.getCenter().y - adjacent.getCenter().y) / 2 - (MapIcon.SUNK_MARKER.getIconHeight() / 2);
                            MapIcon.SUNK_MARKER.paintIcon(this, page,
                                   xloc, yloc);
                        }
                    }
                }
                if( isIconsEnabled() ) {
                    Territory.Status status = territory.getTerritoryStatus();
                    if( status != Territory.Status.NONE ) {
                        if( System.currentTimeMillis() - territory.getIconPlacedTime() >
                                status.getDisplayTime() ) {
                            territory.setTerritoryStatus(Territory.Status.NONE);
                            
                            //you have to flush the image in order to get the application to stop repainting!
                            status.getImage().flush();
                        } else if(status.getImage() != null) {
                            page.drawImage(status.getImage(), 
                                    territory.getCenter().x + status.getOffsetX(),
                                    territory.getCenter().y + status.getOffsetY(), this);  
                        }
                    }
                }
            }
        }
        page.setTransform(old);
    }
    
    /* Draws string on page at (point.x, point.y + h * heightFactor),
     * where h is the height of string, and the string is anchored at
     * approximately the center of the baseline. */
    public void drawCenteredString(String string, Graphics page, Point point,
            double heightFactor) {
        Rectangle2D bounds = page.getFontMetrics().getStringBounds(
                string, page );
        page.drawString( string,
                (int)(point.x - bounds.getWidth() / 2)+1,
                (int)(point.y + bounds.getHeight() * heightFactor) );
    }
    
    /* Draw the unit icons at height y, centering them horizontally around x */
    public void drawUnitIcons(Graphics page, int x, int y, Force force) {
        int width = 0;
        for(SpecialUnit unit : force.getSpecialUnits()) {
            Icon icon = zoomFactor < 1.0 ? unit.getPlainIcon() : unit.getIcon();
            if( icon != null ) {
                width += icon.getIconWidth();
            }
        }
        int currentX = x - width / 2;
        for(SpecialUnit unit : force.getSpecialUnits()) {
            Icon icon = zoomFactor < 1.0 ? unit.getPlainIcon() : unit.getIcon();
            if( icon != null ) {
                icon.paintIcon(this, page, currentX, y);
                currentX += icon.getIconWidth();
            }
        }
    }
    
    public Board getBoard() {
        return board;
    }
    
    public void setBoard(Board board) {
        this.board = board;
        updateImage();
        updateSize();
    }
    
    public List<Territory> getHighlighted() {
        return highlighted;
    }
    
    public void setHighlighted(Territory single) {
        highlighted.clear();
        addHighlighted(single);
    }
    
    public void setHighlighted(Territory first, Territory second) {
        highlighted.clear();
        addHighlighted(first);
        addHighlighted(second);
    }
    
    private void addHighlighted(Territory territory) {
        if( territory != null ) {
            highlighted.add(territory);
        }
    }
    
    public Color getTextColor() {
        return textColor;
    }
    
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }
    
    public Color getHighlightColor() {
        return highlightColor;
    }
    
    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    }
    
    public Color getCenterColor() {
        return centerColor;
    }
    
    public void setCenterColor(Color centerColor) {
        this.centerColor = centerColor;
    }
    
    public Ellipse2D getCenterEllipse(Territory territory) {
        return new Ellipse2D.Double(territory.getCenter().x - centerSize / 2,
                territory.getCenter().y - centerSize / 2,
                centerSize, centerSize);
    }
    public boolean isShowShapes() {
        return showShapes;
    }
    
    public void setShowShapes(boolean showShapes) {
        this.showShapes = showShapes;
    }
    
    public boolean isShowNames() {
        return showNames;
    }
    
    public void setShowNames(boolean showNames) {
        this.showNames = showNames;
    }
    
    public boolean isShowHighlight() {
        return showHighlight;
    }
    
    public void setShowHighlight(boolean showHighlight) {
        this.showHighlight = showHighlight;
    }
    
    public boolean isShowCenter() {
        return showCenter;
    }
    
    public void setShowCenter(boolean showCenter) {
        this.showCenter = showCenter;
    }
    
    public boolean isShowTerritoryTypes() {
        return showTerritoryTypes;
    }
    
    public void setShowTerritoryTypes(boolean showTerritoryTypes) {
        this.showTerritoryTypes = showTerritoryTypes;
    }
    
    public boolean isShowContinents() {
        return showContinents;
    }
    
    public void setShowContinents(boolean showContinents) {
        this.showContinents = showContinents;
    }
    
    public boolean isShowBorders() {
        return showBorders;
    }
    
    public void setShowBorders(boolean showBorders) {
        this.showBorders = showBorders;
    }
    
    public Color getBorderColor() {
        return borderColor;
    }
    
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }
    
    public EnumSet<EdgeType> getShowEdgeTypes() {
        return showEdgeTypes;
    }
    
    public void setShowEdgeTypes(EnumSet<EdgeType> showEdgeTypes) {
        this.showEdgeTypes = showEdgeTypes;
    }
    
    public double getZoomFactor() {
        return zoomFactor;
    }
    
    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
        updateSize();
    }
    
    public Point resolveZoom(Point point) {
        return new Point((int)(point.x / zoomFactor), (int)(point.y / zoomFactor));
    }
    
    public void updateImage() {
        if( board == null || board.getMapImage() == null ) {
            setBackgroundImage(null);
        } else {
            URL url = ResourceAnchor.class.getResource("maps/images/" + board.getMapImage());
            if( url != null ) {
                try {
                    setBackgroundImage(ImageIO.read(url));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                setBackgroundImage(null);
            }
        }
        repaint();
    }
    
    public void updateSize() {
        if( getBoard() != null ) {
            setPreferredSize(new Dimension(
                    (int)(getBoard().getSize().width * getZoomFactor()),
                    (int)(getBoard().getSize().height * getZoomFactor())) );
            revalidate();
            repaint();
        } else {
            setPreferredSize(new Dimension(0, 0));
            revalidate();
        }
    }
    
    /* Tries getBoard().getTerritoryAt(point), but also checks
     * "centers" of territories */
    public Territory getTerritoryAt(Point point) {
        Territory result = getBoard().getTerritoryAt(point);
        if( result == null ) {
            for( Territory territory : getBoard().getTerritories() ) {
                if( getCenterEllipse(territory).contains(point) ) {
                    result = territory;
                }
            }
        }
        return result;
    }
    
    public String getToolTipText(MouseEvent event) {
        Territory territory = getTerritoryAt(resolveZoom(event.getPoint()));
        if( territory != null ) {
            StringBuilder toolTip = new StringBuilder("<html><b>");
            toolTip.append( territory.getName() ).append("</b><br>");
            if( territory.getContinent() != null ) {
                if( territory.getContinent().getColor() != null ) {
                    toolTip.append("<font color=#");
                    toolTip.append(colorToHex(territory.getContinent().getColor()));
                    toolTip.append(">");
                }
                toolTip.append(" (").append(territory.getContinent().getName());
                toolTip.append(", Bonus: "+territory.getContinent().getBonus());
                toolTip.append(")<br>");
                if( territory.getContinent().getColor() != null ) {
                    toolTip.append("</font>");
                }
            }
            toolTip.append("<font color=#");
            toolTip.append(colorToHex(territory.getOwner().getColor()));
            toolTip.append(">").append(territory.getOwner().getName());
            toolTip.append("</font>").append("<br>").append("Units:  ");
            toolTip.append(territory.getForce().getRegularUnits());
            for( SpecialUnit special : territory.getForce().getSpecialUnits() ) {
                toolTip.append("<br><font color=#");
                toolTip.append(colorToHex(special.getColor())).append(">");
                toolTip.append( special.toString() );
                toolTip.append("</font>");
            }
            toolTip.append("</html>");
            return toolTip.toString();
        }
        return null;
    }
    
    public static String colorToHex(Color color) {
        String red = Integer.toString(color.getRed(), 16);
        String green = Integer.toString(color.getGreen(), 16);
        String blue = Integer.toString(color.getBlue(), 16);
        return (red.length() == 1 ? "0" + red : red) +
                (green.length() == 1 ? "0" + green : green) +
                (blue.length() == 1 ? "0" + blue : blue);
    }
    
    public boolean isShowUnits() {
        return showUnits;
    }
    
    public void setShowUnits(boolean showUnits) {
        this.showUnits = showUnits;
    }
    
    public boolean isShowSpecialIcons() {
        return showSpecialIcons;
    }
    
    public void setShowSpecialIcons(boolean showSpecialIcons) {
        this.showSpecialIcons = showSpecialIcons;
    }
    
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    
    public Image getBackgroundImage() {
        return backgroundImage;
    }
    
    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }
    
    public boolean isShowLandingSites() {
        return showLandingSites;
    }
    
    public void setShowLandingSites(boolean showLandingSites) {
        this.showLandingSites = showLandingSites;
    }
    
    public boolean isIncludeSpecialUnitsInCount() {
        return includeSpecialUnitsInCount;
    }
    
    public void setIncludeSpecialUnitsInCount(boolean includeSpecialUnitsInCount) {
        this.includeSpecialUnitsInCount = includeSpecialUnitsInCount;
    }
    
    public Color getPlagueColor() {
        return plagueColor;
    }
    
    public void setPlagueColor(Color plagueColor) {
        this.plagueColor = plagueColor;
    }
    
    public boolean isShowOwnerCircle() {
        return showOwnerCircle;
    }
    
    public void setShowOwnerCircle(boolean showOwnerCircle) {
        this.showOwnerCircle = showOwnerCircle;
    }
    
    public boolean isShowBordersOverImage() {
        return showBordersOverImage;
    }
    
    public void setShowBordersOverImage(boolean showBordersOverImage) {
        this.showBordersOverImage = showBordersOverImage;
    }
    
    public static synchronized void setIconsEnabled(boolean enable) {
        iconsEnabled = enable;
    }
    
    public static synchronized boolean isIconsEnabled() {
        return iconsEnabled;
    }
    
    public boolean isScrollToHighlighted() {
        return scrollToHighlighted;
    }
    
    public void setScrollToHighlighted(boolean scrollToHighlighted) {
        this.scrollToHighlighted = scrollToHighlighted;
    }
    
}
