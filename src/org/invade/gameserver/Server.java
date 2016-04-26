/*
 * Server.java
 *
 * Created on April 3, 2007, 10:56 AM
 */

package org.invade.gameserver;

import com.retrogui.dualrpc.server.DualRpcServer;
import com.retrogui.dualrpc.server.ServerNotRunningException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Jonathan Crosmer
 */
public class Server {
    
    public static void main(String args[]) {
        
        
        final Server server = new Server();
        new Thread("ServerThread") {
            public void run() {
                try {
                    server.startServer("localhost", 2210);
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
        System.out.println("*** Service started ***");
        System.out.println("Commands:  exit");
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String command = "";
        while(true) {
            try {
                command = input.readLine().toLowerCase();
            } catch(IOException e) {
                e.printStackTrace();
            }
            if(command.equals("exit")) {
                try {
                    server.dualRpcServer.shutdown(2);
                } catch(ServerNotRunningException e) {
                    e.printStackTrace();
                } finally {
                    System.exit(0);
                }
            }
        }
        
    }
    
    private String host;
    private int port;
    private DualRpcServer dualRpcServer;
    
    protected static final Logger logger = Logger.getLogger(Server.class.getName());
    
    public static final int SHUTDOWN_TIME = 0;
    
    public void startServer(String host, int port) throws
            ClassNotFoundException, IOException {
        
        this.host = host;
        this.port = port;
        
        configureLog4j();
        
        dualRpcServer = new DualRpcServer(host, port, new Object());
        dualRpcServer.registerServerSideHandlerClassname(
                Common.SERVER_HANDLER_CLASS_NAME);
        dualRpcServer.listen();
        
    }
    
    public void shutdown() throws ServerNotRunningException {
        dualRpcServer.shutdown(SHUTDOWN_TIME);
    }
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
    
    private void configureLog4j() {
        // configure log4j
        Properties properties = new Properties();
        
        // define which level and appenders to use
        properties.setProperty("log4j.rootLogger", "DEBUG, stdout");
        
        // stdout appender, with ISO 8601 date format
        properties.setProperty("log4j.appender.stdout",
                "org.apache.log4j.ConsoleAppender");
        properties.setProperty("log4j.appender.stdout.layout",
                "org.apache.log4j.PatternLayout");
        properties.setProperty("log4j.appender.stdout.layout.ConversionPattern",
                "%d [%t] %-5p %c - %m%n");
        
        PropertyConfigurator.configure(properties);
    }
    
    

    public boolean isRunning() {
        return dualRpcServer != null && dualRpcServer.getMessageServer() != null;
    }
    
    public int getCurrentConnectionCount() {        
        return isRunning() ? dualRpcServer.currentConnectionCount() : 0;
    }
    
}
