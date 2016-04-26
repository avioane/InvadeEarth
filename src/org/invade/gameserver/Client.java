/*
 * Client.java
 *
 * Created on April 3, 2007, 5:25 PM
 */

package org.invade.gameserver;

import com.retrogui.dualrpc.client.DualRpcClient;
import com.retrogui.dualrpc.client.NotConnectedException;
import com.retrogui.dualrpc.common.CallException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
import org.invade.NetworkMessageListener;

/**
 *
 * @author Jonathan Crosmer
 */
public class Client {
    
    public static void main(String args[]) {
        Client c = new Client("localhost", 2210);
        try {
            c.connect();
            c.setName("Harvy");
            
            // update us on stuff that happened before we joined
            c.client.call(Common.SERVER_HANDLER_CLASS_NAME, "getDataSince", Integer.valueOf(0));
            Thread.sleep(1000);
            
            c.client.callAsync(Common.SERVER_HANDLER_CLASS_NAME,
                    "sendNewGame", "*NEW GAME*", c.clientHandler.getLastTimestamp());
            Thread.sleep(1000);
            c.client.callAsync(Common.SERVER_HANDLER_CLASS_NAME,
                    "sendMove", "*A*", c.clientHandler.getLastTimestamp());
            
            c.client.callAsync(Common.SERVER_HANDLER_CLASS_NAME,
                    "sendMove", "*B*",  c.clientHandler.getLastTimestamp());
            
            c.client.callAsync(Common.SERVER_HANDLER_CLASS_NAME,
                    "sendMove", "*C*",  c.clientHandler.getLastTimestamp());
            Thread.sleep(5000);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private String host;
    private int port;
    private DualRpcClient client = null;
    private ClientHandler clientHandler;
    
    private String name = "???";
    private long connectionID = 0L;
    
    
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public void connect()
    throws UnknownHostException, GeneralSecurityException, IOException, CallException {
        
        configureLog4j();
        
        // Create a DualRpcClient client object
        client = new DualRpcClient(host, port);
        clientHandler = new ClientHandler(client);
        client.registerClientSideHandler(clientHandler);
        
        client.connect();
        
        connectionID = (Long)client.call(Common.SERVER_HANDLER_CLASS_NAME,
                "getConnectionID");
        
        clientHandler.update();
                
    }
    
    public void configureLog4j() {
        // configure log4j. In production you should use a configuration file
        Properties props = new Properties();
        
        // define which level and appenders to use
        props.setProperty("log4j.rootLogger", "ERROR, stdout");
        
        // stdout appender, with ISO 8601 date format
        props.setProperty("log4j.appender.stdout",
                "org.apache.log4j.ConsoleAppender");
        props.setProperty("log4j.appender.stdout.layout",
                "org.apache.log4j.PatternLayout");
        props.setProperty("log4j.appender.stdout.layout.ConversionPattern",
                "%d [%t] %-5p %c - %m%n");
        
        PropertyConfigurator.configure(props);
    }
    
    public void disconnect() throws NotConnectedException {
        client.disconnect();
    }
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setName(String name) throws CallException {
        this.name = name;
        client.callAsync(Common.SERVER_HANDLER_CLASS_NAME,
                "setName", name);
    }
    
    public String getName() {
        return name;
    }
    
    public void sendAsynchronizedMessage(String message) throws CallException {
        client.callAsync(Common.SERVER_HANDLER_CLASS_NAME,
                "sendAsynchronizedMessage", message);
    }
    
    public void sendMove(String message) throws CallException {
        client.callAsync(Common.SERVER_HANDLER_CLASS_NAME,
                "sendMove", message, clientHandler.getLastTimestamp());
    }
    
    public void sendNewGame(String message) throws CallException {
        client.callAsync(Common.SERVER_HANDLER_CLASS_NAME,
                "sendNewGame", message, clientHandler.getLastTimestamp());
    }
    
    public Map<Long, String> getAllConnected() throws CallException {
        return Collections.checkedMap((Map)client.call(Common.SERVER_HANDLER_CLASS_NAME,
                "getAllConnected"), Long.class, String.class);
    }
    
    public long getConnectionID() {
        return connectionID;
    }
    
    public void setMessageListener(NetworkMessageListener listener) {
        clientHandler.setListener(listener);
    }
    
    public boolean isConnected() {
        return client != null;
    }
    
}
