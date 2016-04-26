/*
 * XMLHandler.java
 *
 * Created on June 23, 2005, 1:21 PM
 *
 */

package org.invade;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.invade.agents.RemoteAgent;
import org.invade.resources.ResourceAnchor;



public class XMLHandler {
    
    public static final String VERSION = "version";
    public static final String INDEX = "index";
    public static final String GAME = "game";
    public static final String BOARD = "board";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String PLAYER_COUNT = "players";
    public static final String NAME = "name";
    public static final String CONTINENT = "continent";
    public static final String CONTINENT_BONUS = "bonus";
    public static final String TERRITORY = "territory";
    public static final String LANDING_SITE = "landingSite";
    public static final String DEVASTATED = "devastated";
    public static final String PLAGUE = "plague";
    public static final String POINT = "point";
    public static final String POLYGON = "polygon";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String TERRITORY_TYPE = "territoryType";
    public static final String EDGE = "edge";
    public static final String EDGE_TYPE = "edgeType";
    public static final String MOVE_LIST = "moveList";
    public static final String LONG = "long";
    public static final String INT = "int";
    public static final String SPECIAL_MOVE = "specialMove";
    public static final String FORCE = "force";
    public static final String SPECIAL_UNIT = "specialUnit";
    public static final String REGULAR_UNITS = "regularUnits";
    public static final String CARD = "card";
    public static final String DECK = "deck";
    public static final String PLAYER = "player";
    public static final String RULES = "rules";
    public static final String TERRITORY_DUPLE = "territoryDuple";
    public static final String FORCE_PLACEMENT = "forcePlacement";
    public static final String IMAGE = "image";
    
    public static final String TEXT = "text";
    public static final String PLAYER_LIST = "playerList";
    public static final String SESSION_ID = "sessionID";
    public static final String AGENT = "agent";
    public static final String COLOR = "color";
    public static final String NETWORK = "network";
    public static final String WAIT = "wait";
    public static final String CONTINUE = "continue";
    
    public static final String LOAD_INCLUDED_MAP = "loadIncludedMap";
    
    public static final long IGNORE_SESSION_ID = -1;
    
    // Supress default constructor, insuring non-instantiability
    private XMLHandler() {}
    
    public static String toString(Document document) {
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        return outputter.outputString(document);
    }
    
    public static Document parseDocument(String string) {
        try {
            SAXBuilder parser = new SAXBuilder();
            return parser.build(new StringReader(string));
        } catch(IOException e) {
            return null;
        } catch(JDOMException e) {
            return null;
        }
    }
    
    public static boolean writeDocument(File file, Document document) {
        OutputStream out;
        try {
            out = new FileOutputStream(file);
        } catch(FileNotFoundException e) {
            return false;
        }
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        try {
            outputter.output(document, out);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    
    public static Document readDocument(File file) {
        InputStream in;
        try {
            in = new FileInputStream(file);
        } catch(FileNotFoundException e) {
            return null;
        }
        
        return readDocument(in);
    }
    
    public static Document readDocument(InputStream in) {
        Document document;
        try {
            SAXBuilder parser = new SAXBuilder();
            document = parser.build(in);
        } catch(IOException e) {
            return null;
        } catch(JDOMException e) {
            return null;
        }
        return document;
    }
    
    public static boolean load(File file, Board board) {
        return load(readDocument(file), board, null);
    }
    
    public static boolean load(Document document, Board board,
            HasGameThread gameThreadHolder) {
        if( document == null ) {
            return false;
        }
        try {
            parseGameDocument(document, board, gameThreadHolder);
        } catch(InvalidXMLException e) {
            return false;
        }
        return true;
    }
    
    public static boolean save(File file, Board board) {
        return writeDocument(file, createGameDocument(board));
    }
    
    public static Document createGameDocument(Board board) {
        return createGameDocument(null, board, null);
    }
    
    public static Document createGameDocument(Document originalBoardState, Board board,
            List<Object> log) {
        return createGameDocument(originalBoardState, board, log, false, null);
    }
    
    public static Document createGameDocument(Document originalBoardState, Board board,
            List<Object> log, boolean addPlayerData, String loadedDefaultMapPath) {
        
        Element root = new Element(GAME);
        root.setAttribute(VERSION, InvadeEarth.VERSION);
        int playerCount = board.getPlayers().size();
        if( playerCount > 0 ) {
            root.setAttribute(PLAYER_COUNT, Integer.toString(playerCount));
        }
        Document result = new Document(root);
        if( addPlayerData && (originalBoardState != null) ) {
            try {
                root.addContent(extractPlayerListElement(originalBoardState));
            } catch(InvalidXMLException ex) {
                ex.printStackTrace(); // Should not happen
            }
        }
        root.addContent(createElement(board.getRules()));
        if( originalBoardState != null ) {
            try {
                if( loadedDefaultMapPath != null ) {
                    // include only an instruction to load the board
                    // from the included maps
                    root.addContent(new Element(BOARD).addContent(new Element(LOAD_INCLUDED_MAP).addContent(loadedDefaultMapPath)));
                } else {
                    // include complete board XML info
                    root.addContent(extractBoardElement(originalBoardState));
                }
            } catch(InvalidXMLException ex) {
                ex.printStackTrace(); // Should not happen
            }
        } else {
            root.addContent(createElement(board));
        }
        if( log != null ) {
            root.addContent(createElement(board, log));
        }
        return result;
    }
    
    public static Element createElement(String name, int value) {
        return new Element(name).addContent(new Text(Integer.toString(value)));
    }
    
    public static Element createElement(String name, long value) {
        return new Element(name).addContent(new Text(Long.toString(value)));
    }
    
    public static Element createElement(String name, Enum value) {
        return new Element(name).addContent(new Text(value.name()));
    }
    
    public static Element createElement(String name, Object value) {
        return new Element(name).addContent(new Text(value.toString()));
    }
    
    public static void addFlag(Element parent, String name, boolean value) {
        if( value ) {
            parent.addContent(new Element(name));
        }
    }
    
    public static Element createIndexReference(String name, int index) {
        return new Element(name).setAttribute(INDEX, Integer.toString(index));
    }
    
    public static <E> void addIndexReference(Element parent, String name,
            E object, List<E> list) {
        int index = list.indexOf(object);
        if( index >= 0 ) {
            parent.addContent(createIndexReference(name, index));
        }
    }
    
    public static Element createElement(Board board) {
        Element result = new Element(BOARD);
        result.addContent(createElement(WIDTH, board.getSize().width));
        result.addContent(createElement(HEIGHT, board.getSize().height));
        if( board.getMapImage() != null ) {
            result.addContent(createElement(IMAGE, board.getMapImage()));
        }
        for( Continent continent : board.getContinents() ) {
            result.addContent( createElement(continent, board) );
        }
        for( Territory territory : board.getTerritories() ) {
            result.addContent( createElement(territory, board) );
        }
        return result;
    }
    
    public static Element createElement(Point point) {
        Element result = new Element(POINT);
        result.addContent(createElement(X, point.x));
        result.addContent(createElement(Y, point.y));
        return result;
    }
    
    public static Element createElement(Continent continent, Board board) {
        Element result = new Element(CONTINENT);
        result.addContent(createElement(NAME, continent.getName()));
        result.addContent(createElement(CONTINENT_BONUS, continent.getBonus()));
        if( continent.getColor() != null ) {
            result.addContent(createElement(COLOR, continent.getColor().getRGB()));
        }
        return result;
    }
    
    public static Element createElement(Territory territory, Board board) {
        Element result = new Element(TERRITORY);
        result.addContent(createElement(NAME, territory.getName()));
        result.addContent(createElement(TERRITORY_TYPE, territory.getType()));
        addFlag(result, LANDING_SITE, territory.isLandingSite());
        addFlag(result, DEVASTATED, territory.isDevastated());
        addFlag(result, PLAGUE, territory.isPlague());
        addIndexReference(result, CONTINENT, territory.getContinent(), board.getContinents());
        if( ! territory.getForce().isEmpty() ) {
            result.addContent(createElement(territory.getForce()));
        }
        result.addContent(createElement(territory.getCenter()));
        result.addContent(createElement(territory.getShape()));
        for( Territory to : territory.getAdjacent(true) ) {
            result.addContent( createElement(board.getTerritories().indexOf(to),
                    territory.getEdgeType(to, true)) );
        }
        return result;
    }
    
    public static Element createElement(int territoryIndex, EdgeType edgeType) {
        Element result = new Element(EDGE);
        result.addContent(createIndexReference(TERRITORY, territoryIndex));
        result.addContent(createElement(EDGE_TYPE, edgeType));
        return result;
    }
    
    public static Element createElement(Polygon polygon) {
        Element result = new Element(POLYGON);
        for( int i = 0; i < polygon.npoints; ++i ) {
            result.addContent( createElement(new Point(polygon.xpoints[i], polygon.ypoints[i]  )) );
        }
        return result;
    }
    
    public static Element createElement(Rules rules) {
        return createJavaElement(RULES, rules);
    }
    
    public static Element createJavaElement(String name, Object object) {
        OutputStream out = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(out);
        encoder.writeObject(object);
        encoder.close();
        Document document = parseDocument(out.toString());
        Element result = document.getRootElement();
        document.removeContent(result);
        return new Element(name).addContent(result); 
    }
    
    
    public static void parseGameDocument(Document document, Board board,
            HasGameThread gameThreadHolder) throws InvalidXMLException {
        parseGameDocument(document, board, gameThreadHolder, 0);
    }
    
    public static void parseGameDocument(Document document, Board board,
            HasGameThread gameThreadHolder, long mySessionID) throws InvalidXMLException {
        if( document.getRootElement().getName().equals(GAME) ) {
            parsePlayerCount(document.getRootElement(), board);
            // Since execution order is important, we do not use a for-loop
            Element child = null;
            if( (child = document.getRootElement().getChild(RULES)) != null ) {
                parseRules(child, board);
            }
            if( (child = document.getRootElement().getChild(BOARD)) != null ) {
                parseBoard(child, board);
                if( gameThreadHolder != null ) {
                    gameThreadHolder.setGameThread(new GameThread().start(board));
                }
            }
            if( (child = document.getRootElement().getChild(PLAYER_LIST)) != null
                    && mySessionID != 0 ) {
                parsePlayerList(child, board, mySessionID);
            }
            if( (child = document.getRootElement().getChild(MOVE_LIST)) != null
                    && gameThreadHolder != null ) {
                parseMoveList(child, board, gameThreadHolder.getGameThread());
            }
        } else {
            throw new InvalidXMLException("Missing <game> root element");
        }
    }
    
    public static Element extractBoardElement(Document document)
    throws InvalidXMLException {
        if( document.getRootElement().getName().equals(GAME) ) {
            Element element = document.getRootElement().getChild(BOARD);
            if( element == null ) {
                throw new InvalidXMLException("Missing <board> element");
            }
            return (Element)element.clone();
        } else {
            throw new InvalidXMLException("Missing <game> root element");
        }
    }
    
    public static Element extractPlayerListElement(Document document)
    throws InvalidXMLException {
        if( document.getRootElement().getName().equals(GAME) ) {
            Element element = document.getRootElement().getChild(PLAYER_LIST);
            if( element == null ) {
                throw new InvalidXMLException("Missing <board> element");
            }
            return (Element)element.clone();
        } else {
            throw new InvalidXMLException("Missing <game> root element");
        }
    }
    
    public static int parseInt(Element element) throws InvalidXMLException {
        try {
            return Integer.parseInt(element.getText());
        } catch(NumberFormatException e) {
            throw new InvalidXMLException("Number format exception in " + element
                    + " with text: " + element.getText());
        }
    }
    
    public static long parseLong(Element element) throws InvalidXMLException {
        try {
            return Long.parseLong(element.getText());
        } catch(NumberFormatException e) {
            throw new InvalidXMLException("Number format exception in " + element
                    + " with text: " + element.getText());
        }
    }
    
    public static <E> E parseIndexReference(Element element, List<E> list)
    throws InvalidXMLException {
        try {
            return list.get(Integer.parseInt(element.getAttributeValue(INDEX)));
        } catch(NumberFormatException e) {
        } catch(NullPointerException e) {
        } catch(IndexOutOfBoundsException e) {
        }
        throw new InvalidXMLException("Could not resolve index in " + element);
    }
    
    public static <T extends Enum<T>> T parseEnum(Element element,
            Class<T> enumClass) throws InvalidXMLException {
        try {
            return Enum.valueOf(enumClass, element.getText());
        } catch(IllegalArgumentException e) {
        }
        throw new InvalidXMLException("Could not resolve enum type in "
                + element + " with value: " + element.getText());
    }
    
    public static void parsePlayerCount(Element element, Board board)
    throws InvalidXMLException {
        try {
            String playerCountString =
                    element.getAttributeValue(PLAYER_COUNT);
            if( playerCountString != null ) {
                int playerCount = Integer.parseInt( playerCountString );
                if( playerCount > 0 && playerCount != board.getPlayers().size() ) {
                    board.getPlayers().clear();
                    for( int i = 0; i < playerCount; ++i ) {
                        board.getPlayers().add(new Player("Player " + (i+1),
                                Player.getDefaultColor(i)));
                    }
                }
            }
        } catch(NumberFormatException e) {}
    }
    
//    public static List getMove(Document document, int actionId) 
//    throws InvalidXMLException {
//        Element movelist;
//        if( (movelist = document.getRootElement().getChild(MOVE_LIST)) != null ) {
//            return (List)(movelist.getChildren().get(actionId - 1)); //-1 b/c the list is 0 indexed, but action (movelist) ids are 
//                                                                     //1 based (b/c .size() of game log used to create actionids) 
//        }
//        return null;
//    }
    
    public static void parseBoard(Element element, Board board)
    throws InvalidXMLException {
        // Load an included map instead of looking for board XML info here
        // if the LOAD_INCLUDED_MAP element is present
        Element loadIncludedMap = element.getChild(LOAD_INCLUDED_MAP);
        if( loadIncludedMap != null ) {
            Document includedMapDocument = XMLHandler.readDocument(
                    ResourceAnchor.class.getResourceAsStream(loadIncludedMap.getText()));
            parseBoard(includedMapDocument.getRootElement().getChild(BOARD), board);
            return;
        }       
        
        board.getTerritories().clear();
        for( Object object : element.getChildren(TERRITORY) ) {
            board.getTerritories().add(new Territory());
        }
        board.getContinents().clear();
        for( Object object : element.getChildren(CONTINENT) ) {
            board.getContinents().add(new Continent());
        }
        int territoryIndex = 0;
        int continentIndex = 0;
        for( Object object : element.getChildren() ) {
            Element child = (Element)object;
            if( child.getName().equals(WIDTH) ) {
                board.getSize().width = parseInt(child);
            } else if( child.getName().equals(HEIGHT) ) {
                board.getSize().height = parseInt(child);
            } else if( child.getName().equals(IMAGE) ) {
                board.setMapImage(child.getText());
            } else if( child.getName().equals(TERRITORY) ) {
                Territory territory = board.getTerritories().get(territoryIndex);
                parseTerritory(child, territory, board);
                territoryIndex++;
            } else if( child.getName().equals(CONTINENT) ) {
                Continent continent = board.getContinents().get(continentIndex);
                parseContinent(child, continent);
                continentIndex++;
            }
        }
    }
    
    public static void parseContinent(Element element, Continent continent)
    throws InvalidXMLException {
        for( Object object : element.getChildren() ) {
            Element child = (Element)object;
            if( child.getName().equals(NAME) ) {
                continent.setName(child.getText());
            } else if( child.getName().equals(CONTINENT_BONUS) ) {
                continent.setBonus(parseInt(child));
            } else if( child.getName().equals(COLOR) ) {
                continent.setColor(new Color(parseInt(child)));
            }
        }
    }
    
    public static void parseTerritory(Element element, Territory territory,
            Board board) throws InvalidXMLException {
        for( Object object : element.getChildren() ) {
            Element child = (Element)object;
            if( child.getName().equals(NAME) ) {
                territory.setName(child.getText());
            } else if( child.getName().equals(LANDING_SITE) ) {
                territory.setLandingSite(true);
            } else if( child.getName().equals(DEVASTATED) ) {
                territory.setDevastated(true, board);
            } else if( child.getName().equals(PLAGUE) ) {
                territory.setPlague(true);
            } else if( child.getName().equals(CONTINENT) ) {
                territory.setContinent(
                        parseIndexReference(child, board.getContinents()));
            } else if( child.getName().equals(TERRITORY_TYPE) ) {
                territory.setType(parseEnum(child, TerritoryType.class));
            } else if( child.getName().equals(POINT) ) {
                parsePoint(child, territory.getCenter());
            } else if( child.getName().equals(POLYGON) ) {
                parsePolygon(child, territory.getShape());
            } else if( child.getName().equals(EDGE) ) {
                parseEdge(child, territory, board);
            } else if( child.getName().equals(FORCE) ) {
                parseForce(child, board, territory.getForce());
            }
        }
    }
    
    public static void parseEdge(Element element, Territory from,
            Board board)  throws InvalidXMLException {
        if( element.getChild(TERRITORY) == null
                || element.getChild(EDGE_TYPE) == null ) {
            throw new InvalidXMLException("Edge is missing data");
        }
        Territory to = parseIndexReference(element.getChild(TERRITORY),
                board.getTerritories());
        EdgeType edgeType = parseEnum(element.getChild(EDGE_TYPE), EdgeType.class);
        from.setDirectedEdge( to, edgeType, true );
    }
    
    public static void parsePoint(Element element, Point point) throws InvalidXMLException {
        for( Object object : element.getChildren() ) {
            Element child = (Element)object;
            if( child.getName().equals(X) ) {
                point.x = parseInt(child);
            } if( child.getName().equals(Y) ) {
                point.y = parseInt(child);
            }
        }
    }
    
    public static void parsePolygon(Element element, Polygon polygon)
    throws InvalidXMLException {
        polygon.reset();
        Point point = new Point();
        for( Object o : element.getChildren(POINT) ) {
            if( o instanceof Element ) {
                Element pointElement = (Element)o;
                parsePoint(pointElement, point);
                polygon.addPoint(point.x, point.y);
            }
        }
    }
    
    public static void parseRules(Element element, Board board)
    throws InvalidXMLException {
        try {
            Object object = parseJavaObject(element);
            if( object == null ) {
                throw new InvalidXMLException("Rules tag has invalid data");
            }
            board.setRules((Rules)object);
        } catch(ClassCastException e) {
            throw new InvalidXMLException("Rules tag has invalid data");
        }
    }
    
    /* The parent parameter is an element that was generated by
     * a call to createJavaElement(...). */
    public static Object parseJavaObject(Element parent)
    throws InvalidXMLException {
        Element element = (Element)parent.getChildren().get(0);
        element.detach();
        Document document = new Document(element);
        InputStream in = new ByteArrayInputStream(toString(document).getBytes());
        XMLDecoder decoder = new XMLDecoder(in);
        try {
            Object result = decoder.readObject();
            element.detach();
            parent.addContent(element);
            return result;
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new InvalidXMLException("Missing data in Java Bean XML");
        }
    }
    
    
    /* These methods are for persisting the state of the game after moves have
     * been made.
     */
    
    public static boolean save(File file, Document originalBoardState, Board board, GameThread gameThread) {
        List<Object> log = gameThread.getLog();
        Document document = createGameDocument(originalBoardState, board, log, true, null);
        boolean result = writeDocument(file, document);
        return result;
    }
    
    public static Element createElement(Board board, List<Object> log) {
        Element result = new Element(MOVE_LIST);
        for(Object object : log) {
            if( object instanceof Long ) {
                result.addContent(createElement(LONG, object));
            } else if( object instanceof Integer ) {
                result.addContent(createElement(INT, object));
            } else if( object instanceof Territory ) {
                result.addContent(createIndexReference(TERRITORY,
                        board.getTerritories().indexOf(object)));
            } else if( object instanceof SpecialMove ) {
                result.addContent(createElement(SPECIAL_MOVE, object));
            } else if( object instanceof Force ) {
                result.addContent(createElement((Force)object));
            } else if( object instanceof Card ) {
                result.addContent(createIndexReference(CARD,
                        board.getAllCards().indexOf(object)));
            } else if( object instanceof PlayableDeck ) {
                result.addContent(createIndexReference(DECK,
                        board.getDecks().indexOf(object)));
            } else if( object instanceof Player ) {
                result.addContent(createIndexReference(PLAYER,
                        board.getPlayers().indexOf(object)));
            } else if( object instanceof SpecialUnit ) {
                result.addContent(createElement(SPECIAL_UNIT,
                        (SpecialUnit)object));
            } else if( object instanceof TerritoryDuple ) {
                TerritoryDuple duple = (TerritoryDuple)object;
                result.addContent(new Element(TERRITORY_DUPLE)
                .addContent(createIndexReference(TERRITORY,
                        board.getTerritories().indexOf(duple.getFirst())))
                        .addContent(createIndexReference(TERRITORY,
                        board.getTerritories().indexOf(duple.getSecond()))));
            } else if( object instanceof ForcePlacement ) {
                ForcePlacement duple = (ForcePlacement)object;
                result.addContent(new Element(FORCE_PLACEMENT)
                .addContent(createIndexReference(TERRITORY,
                        board.getTerritories().indexOf(duple.getTerritory())))
                        .addContent(createElement(duple.getForce())));
            }
        }
        return result;
    }
    
    public static Element createElement(Force force) {
        Element result = new Element(FORCE);
        result.addContent(createElement(REGULAR_UNITS, force.getRegularUnits()));
        for( SpecialUnit special : force.getSpecialUnits() ) {
            result.addContent(createElement(SPECIAL_UNIT, special));
        }
        return result;
    }
    
    // Apply the move list to the board
    // Disable sound and animations during this operation if the move list
    // has multiple elements
    public static void parseMoveList(Element element, Board board,
            GameThread gameThread) throws InvalidXMLException {
        boolean sound = SoundDriver.isSoundEnabled();
        if( element.getChildren().size() > 1 ) {
            SoundDriver.setSoundEnabled(false);
        }
        for( Object current : element.getChildren() ) {
            Object move = parseMove((Element)current, board);
            gameThread.put(move);
        }
        
        if( element.getChildren().size() > 1 ) {
            for( Territory territory : board.getTerritories() ) {
                territory.setTerritoryStatus(Territory.Status.NONE);
            }
        }
        
        SoundDriver.setSoundEnabled(sound);
    }
    
    public static Object parseMove(Element element, Board board)
    throws InvalidXMLException {
        if( element.getName().equals(LONG) ) {
            return parseLong(element);
        } else if( element.getName().equals(INT) ) {
            return parseInt(element);
        } else if( element.getName().equals(TERRITORY) ) {
            return parseIndexReference(element, board.getTerritories());
        } else if( element.getName().equals(SPECIAL_MOVE) ) {
            return parseEnum(element, SpecialMove.class);
        } else if( element.getName().equals(FORCE) ) {
            Force force = new Force();
            parseForce(element, board, force);
            return force;
        } else if( element.getName().equals(CARD) ) {
            return parseIndexReference(element, board.getAllCards());
        } else if( element.getName().equals(DECK) ) {
            return parseIndexReference(element, board.getDecks());
        } else if( element.getName().equals(PLAYER) ) {
            return parseIndexReference(element, board.getPlayers());
        } else if( element.getName().equals(SPECIAL_UNIT) ) {
            return board.getRules().getUnitWithName(element.getText());
        } else if( element.getName().equals(TERRITORY_DUPLE) ) {
            List children = element.getChildren();
            if( children.size() < 2 ) {
                throw new InvalidXMLException("Need two territories");
            }
            return new TerritoryDuple(
                    parseIndexReference((Element)children.get(0), board.getTerritories()),
                    parseIndexReference((Element)children.get(1), board.getTerritories()) );
        } else if( element.getName().equals(FORCE_PLACEMENT) ) {
            List children = element.getChildren();
            if( children.size() < 2 ) {
                throw new InvalidXMLException("Need territory and force");
            }
            Force force = new Force();
            parseForce( (Element)children.get(1), board, force );
            return new ForcePlacement(
                    parseIndexReference((Element)children.get(0), board.getTerritories()),
                    force );
        }
        return null;
    }
    
    public static void parseForce(Element element, Board board, Force force) throws InvalidXMLException {
        for( Object object : element.getChildren() ) {
            Element child = (Element)object;
            if( child.getName().equals(REGULAR_UNITS) ) {
                force.addRegularUnits(parseInt(child));
            } else if( child.getName().equals(SPECIAL_UNIT) ) {
                force.getSpecialUnits().add(board.getRules().getUnitWithName(child.getText()));
            }
        }
    }
    
    public static List getMoveList(Document document) {
        Element child;
        if( (child = document.getRootElement().getChild(MOVE_LIST)) != null ) {
            return ((Element)child.clone()).getChildren();
        }
        return null;
    }
  
    
    /* The code below is used for traffic across a network. */
    
    public static Document createMessageDocument(String contents) {
        return new Document(createElement(TEXT, contents));
    }
    
    public static String parseMessage(Document document) {
        return document.getRootElement().getText();
    }
    
    public static String createNetworkDirective(String contents) {
        return toString(createNetworkDirectiveDocument(contents));
    }
    
    public static Document createNetworkDirectiveDocument(String contents) {
        return new Document(createElement(NETWORK, contents));
    }
    
    public static String parseNetworkDirectiveDocument(Document document) {
        return document.getRootElement().getText();
    }
    
    public static Element createElement(List<Player> players) {
        return createElement(players, false);
    }
    
    public static Element createElement(List<Player> players, boolean ignoreSessionID) {
        Element result = new Element(PLAYER_LIST);
        for( Player player : players ) {
            Element element = new Element(PLAYER);
            element.addContent(createElement(NAME, player.getName()));
            element.addContent(createJavaElement(AGENT, player.getAgent()));
            if( !ignoreSessionID ) {
                element.addContent(createElement(SESSION_ID, player.getSessionID()));
            }
            element.addContent(createElement(COLOR, player.getColor().getRGB()));
            result.addContent(element);
        }
        return result;
    }
    
    public static void parsePlayerList(Element element, Board board,
            long mySessionID) throws InvalidXMLException {
        board.getPlayers().clear();
        for( Object object : element.getChildren(PLAYER) ) {
            Element child = (Element)object;
            Player player = new Player("??? (Missing Name)", Color.BLACK);
            parsePlayer(child, player, mySessionID);
            board.getPlayers().add(player);
        }
    }
    
    public static void parsePlayer(Element element, Player player,
            long mySessionID) throws InvalidXMLException {
        player.setSessionID(0);  // In case there is no element
        for( Object object : element.getChildren() ) {
            Element child = (Element)object;
            if( child.getName().equals(NAME) ) {
                player.setName(child.getText());
            } if( child.getName().equals(COLOR) ) {
                player.setColor(new Color(parseInt(child)));
            } else if( child.getName().equals(SESSION_ID) ) {
                long sessionID = parseInt(child);
                player.setSessionID(sessionID);
            }
        }
        if( player.getSessionID() == mySessionID || player.getSessionID() == 0 ) {
            Object object = parseJavaObject(element.getChild(AGENT));
            if( object == null ) {
                throw new InvalidXMLException("Agent tag has invalid data");
            }
            player.setAgent((Agent)object);
        } else {
            player.setAgent(new RemoteAgent());
        }
    }
    
    public static void parseOnlyPlayerList(Document document, Board board)
    throws InvalidXMLException{
        if( document.getRootElement().getChild(PLAYER_LIST) != null ) {
            parsePlayerList(document.getRootElement().getChild(PLAYER_LIST),
                    board, 0);
        }
    }
    
    public static boolean isMessage(Document document) {
        return document.getRootElement().getName().equals(TEXT);
    }
    
    public static boolean isNetworkDirective(Document document) {
        return document.getRootElement().getName().equals(NETWORK);
    }
    
    public static boolean isGame(Document document) {
        return document.getRootElement().getName().equals(GAME);
    }
    
    public static boolean isMoveList(Document document) {
        return document.getRootElement().getName().equals(MOVE_LIST);
    }
    
    public static Document createMoveListDocument(Board board, List<Object> log) {
        return new Document(createElement(board, log));
    }
    
    public static Document createPlayerListDocument(List<Player> players) {
        return new Document(new Element(GAME).addContent(createElement(players, true)));
    } 


}