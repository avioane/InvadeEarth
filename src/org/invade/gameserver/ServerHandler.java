/*
 * ServerHandler.java
 *
 * Created on April 3, 2007, 11:11 AM
 */

package org.invade.gameserver;

import com.retrogui.dualrpc.common.CallException;
import com.retrogui.dualrpc.server.AbstractServerRpcHandler;
import com.retrogui.dualrpc.server.DualRpcServerDispatcher;
import com.retrogui.messageserver.common.SessionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jonathan Crosmer
 */
public class ServerHandler extends AbstractServerRpcHandler {
    
    private String name = "???";
    
    private static MessageSynchronizer messageSynchronizer = MessageSynchronizer.getInstance();
    private static boolean blockNewGameRequests = false;
    
    public ServerHandler(DualRpcServerDispatcher dispatcher) {
        super(dispatcher);
    }
    
    
    
    
    
    ////////////////////////////////////
    /* Methods called from the client */
    public void sendAsynchronizedMessage(String message) {
        sendEveryone(message);
    }
    
    // The clientLastTimestamp argument indicates what the client's last processed
    // timestamp was.... then if it differs from the server's, we ignore it
    // and send back the correct move.
    public void sendMove(String move, Integer clientLastTimestamp) {
        try {
            sendEveryone(move, messageSynchronizer.addMove(move, clientLastTimestamp));
        } catch (SimultaneousMessageException ex) {
            getDataSince(clientLastTimestamp);
        }
    }
    
    public void sendNewGame(String gameData, Integer clientLastTimestamp) {
        if( isBlockNewGameRequests() ) {
//            getDataSince(clientLastTimestamp);
            // send client a "reset" message so they reload the current game?
        } else {
            try {
                sendEveryone(gameData, messageSynchronizer.newGame(gameData, clientLastTimestamp));
            } catch (SimultaneousMessageException ex) {
                getDataSince(clientLastTimestamp);
            }
        }
    }
    
    /* Request all messages since timestamp, exclusive (client already
      has message with the timestamp argument).  Will call
      receiveMessageList() on client with a messageArray and the
      timestamp of the first message in the array if there are any messages
      to send. */
    public void getDataSince(Integer timestamp) {
        DualRpcServerDispatcher dispatcher = getDispatcher();
        List<String> messages = new ArrayList<String>();
        
        synchronized(messageSynchronizer) {
            int moveIndex = 0;
            if(timestamp < messageSynchronizer.getGameStartTimestamp() ) {
                messages.add(messageSynchronizer.getInitialGameState());
                timestamp = messageSynchronizer.getGameStartTimestamp();
            } else {
                moveIndex = timestamp - messageSynchronizer.getGameStartTimestamp();
                timestamp++;
            }
            messages.addAll(messageSynchronizer.getMoves(moveIndex));
        }
        
        if( ! messages.isEmpty() ) {
            try {
                dispatcher.callAsync(Common.CLIENT_HANDLER_CLASS_NAME,
                        "receiveMessageList", messages, timestamp);
            } catch(CallException ex) {
                ex.printStackTrace();
            }
        }
        
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Map<Long, String> getAllConnected() {
        Hashtable applications = getDispatcher().getDualRpcServer().getMessageServer().getApplications();
        Object [] keys = applications.keySet().toArray();
        Map<Long, String> result = new HashMap<Long, String>();
        for(Object key : keys) {
            DualRpcServerDispatcher dispatcher =
                    (DualRpcServerDispatcher) applications.get((String)key);
            if (dispatcher != null) {
                ServerHandler handler = (ServerHandler)dispatcher
                        .getHandler(Common.SERVER_HANDLER_CLASS_NAME);
                result.put(handler.getConnectionID(), handler.getName());
            }
        }
        return result;
    }
    
    public Long getConnectionID() {
        return getDispatcher().getSession().getSessionId();
    }
    
    public String getName() {
        return name;
    }
    
    /* *End* Methods called from the client */
    //////////////////////////////////////////
    
    
    
    private void sendEveryone(String string) {
        sendEveryone(string, 0);
    }
    
    private void sendEveryone(String string, int timestamp) {
        Hashtable applications = getDispatcher().getDualRpcServer().getMessageServer().getApplications();
        Object [] keys = applications.keySet().toArray();
        for(Object key : keys) {
            try {
                DualRpcServerDispatcher dispatcher =
                        (DualRpcServerDispatcher) applications.get((String)key);
                
                if (dispatcher != null) {
                    dispatcher.callAsync(Common.CLIENT_HANDLER_CLASS_NAME,
                            "receiveMessage", string, timestamp);
                }
            } catch(CallException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
    
    public static synchronized boolean isBlockNewGameRequests() {
        return blockNewGameRequests;
    }
    
    public static synchronized void setBlockNewGameRequests(boolean aBlockNewGameRequests) {
        blockNewGameRequests = aBlockNewGameRequests;
    }
    
}
