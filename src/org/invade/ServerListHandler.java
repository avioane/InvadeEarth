/*
 * ServerListHandler.java
 *
 * Created on February 10, 2006, 9:12 AM
 *
 */

package org.invade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class ServerListHandler {
    
    public static final URL POST_SERVER_LIST_URL = createURL("http://www.smileygames.net/invade/serverlist/serverlist.php");
    public static final URL RETRIEVE_SERVER_LIST_URL = createURL("http://www.smileygames.net/invade/serverlist/serverlist.php?rss=yes");
    
    // Keys for POST
    public static final String ACTION_KEY = "action";
    public static final String USE_PASSWORD_KEY = "usepass";
    public static final String MAX_PLAYERS_KEY = "maxplayers";
    public static final String RULE_SET_KEY = "ruleset";
    public static final String MAP_KEY = "map";
    public static final String DESCRIPTION_KEY = "desc";
    public static final String ID_KEY = "id";
    public static final String PORT_KEY = "port";
    
    // RSS elements
    public static final String USE_PASSWORD_ELEMENT = "usepass";
    public static final String MAX_PLAYERS_ELEMENT = "maxplayers";
    public static final String RULE_SET_ELEMENT = "ruleset";
    public static final String MAP_ELEMENT = "map";
    public static final String DESCRIPTION_ELEMENT = "description";
    public static final String IP_ELEMENT = "ip";
    public static final String PORT_ELEMENT = "port";
    
    // Values for POST
    public static final String NEW_GAME_VALUE = "new";
    public static final String UPDATE_GAME_VALUE = "update";
    public static final String REMOVE_GAME_VALUE = "remove";
    public static final String UNUSED_VALUE = "0";
    public static final String EMPTY_VALUE = "(empty)";
    
    public static final String START_ID = "[id]";
    public static final String END_ID = "[/id]";
    
    public static final String TEXT_ENCODING = "UTF-8";
    
    public static final String RSS_NAMESPACE = "http://www.smileygames.net/invade/serverlist/";
    public static final String RSS_PREFIX = "ies";
    
    public static final int maxValueLength = 256;
    
    // Non-positive ID returned from POST indicates error
    public static final BigInteger UKNOWN_ERROR = BigInteger.valueOf(0);
    public static final BigInteger WRONG_IP = BigInteger.valueOf(-1);
    public static final BigInteger UNKNOWN_ID = BigInteger.valueOf(-2);
    public static final BigInteger MALFORMED_POST = BigInteger.valueOf(-3);
    public static final BigInteger REJECTED_IP = BigInteger.valueOf(-4);  // Flood control
    
    private ServerListHandler() {}
    
    public static BigInteger post(URL url, String data) {
        BigInteger result = null;
        try {
            // Send data
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Invade Earth/" + InvadeEarth.VERSION);
            connection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(data);
            wr.flush();
            
            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                int startIndex = line.indexOf(START_ID);
                if( startIndex >=0 ) {
                    startIndex += START_ID.length();
                    int endIndex = line.indexOf(END_ID);
                    if( endIndex < 0 ) {
                        endIndex = line.length();
                    }
                    result = new BigInteger(line.substring(startIndex, endIndex));
                }
            }
            wr.close();
            rd.close();
            
            // Any errors caught here are program or server bugs; we return null
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        return result;
    }
    
    public static String encode(String key, String value) {
        if( value == null || value.equals("") ) {
            value = EMPTY_VALUE;
        } else if( value.length() > maxValueLength ) {
            value = value.substring(0, maxValueLength);
        }
        try {
            return URLEncoder.encode(key, TEXT_ENCODING) + "=" + URLEncoder.encode(value, TEXT_ENCODING);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return "";
        }
    }
    
    public static BigInteger postNewGame(int port, String ruleSet, String map,
            String maxPlayers, String description) {
        String data = encode(ACTION_KEY, NEW_GAME_VALUE);
        data += "&" + encode(RULE_SET_KEY, ruleSet);
        data += "&" + encode(MAP_KEY, map);
        data += "&" + encode(MAX_PLAYERS_KEY, maxPlayers);
        data += "&" + encode(DESCRIPTION_KEY, description);
        data += "&" + encode(USE_PASSWORD_KEY, UNUSED_VALUE);
        data += "&" + encode(PORT_KEY, String.valueOf(port));
        return post(POST_SERVER_LIST_URL, data);
    }
    
    public static BigInteger postUpdateGame(BigInteger gameID, int port,
            String ruleSet, String map, String maxPlayers, String description) {
        String data = encode(ACTION_KEY, UPDATE_GAME_VALUE);
        data += "&" + encode(ID_KEY, gameID.toString());
        data += "&" + encode(RULE_SET_KEY, ruleSet);
        data += "&" + encode(MAP_KEY, map);
        data += "&" + encode(MAX_PLAYERS_KEY, maxPlayers);
        data += "&" + encode(DESCRIPTION_KEY, description);
        data += "&" + encode(USE_PASSWORD_KEY, UNUSED_VALUE);
        data += "&" + encode(PORT_KEY, String.valueOf(port));
        return post(POST_SERVER_LIST_URL, data);
    }
    
    public static BigInteger postRemoveGame(BigInteger gameID) {
        String data = encode(ACTION_KEY, REMOVE_GAME_VALUE);
        data += "&" + encode(ID_KEY, gameID.toString());
        return post(POST_SERVER_LIST_URL, data);
    }
    
    public static List<List<String>> getServerList() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(
                RETRIEVE_SERVER_LIST_URL.openStream()));
        StringBuilder rssFeed = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            rssFeed.append(line);
        }
        in.close();
        String string = rssFeed.toString();
        string = string.substring(string.indexOf("<?xml"));
        return parseServerRSS(string);
    }
    
    public static List<List<String>> parseServerRSS(String rss) {
        List<List<String>> result = new ArrayList<List<String>>();
        Document document = XMLHandler.parseDocument(rss);
        Element root = document.getRootElement();
        Namespace namespace = Namespace.getNamespace(RSS_PREFIX, RSS_NAMESPACE);
        
        for( Object child : root.getChild("channel").getChildren("item") ) {
            Element item = (Element)child;
            List<String> row = new ArrayList<String>();
            row.add(item.getChild(IP_ELEMENT, namespace).getText());
            row.add(item.getChild(PORT_ELEMENT, namespace).getText());
            row.add(item.getChild(RULE_SET_ELEMENT, namespace).getText());
            row.add(item.getChild(MAP_ELEMENT, namespace).getText());
            row.add(item.getChild(MAX_PLAYERS_ELEMENT, namespace).getText());
            row.add(item.getChild(DESCRIPTION_ELEMENT).getText());
            if( ! row.get(0).equals("-1") ) {
                result.add(row);
            }
        }
        return result;
    }
    
    public static URL createURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
}
