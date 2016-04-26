/*
 * ClientHandler.java
 *
 * Created on April 3, 2007, 12:53 PM
 */

package org.invade.gameserver;

import com.retrogui.dualrpc.client.AbstractClientRpcHandler;
import com.retrogui.dualrpc.client.DualRpcClient;
import com.retrogui.dualrpc.common.CallException;
import java.util.List;
import org.invade.NetworkMessageListener;

/**
 *
 * @author Jonathan Crosmer
 */
public class ClientHandler extends AbstractClientRpcHandler {
    
    
    private NetworkMessageListener listener;
    
    // Keep track of our last processed timestamp in a synchronized variable.
    private int lastTimestamp = 0;
    private DualRpcClient client;
    
    public ClientHandler(DualRpcClient client) {
        this.client = client;
    }
    
    
    public NetworkMessageListener getListener() {
        return listener;
    }
    
    public void setListener(NetworkMessageListener listener) {
        this.listener = listener;
    }
    
    
    public synchronized int getLastTimestamp() {
        return lastTimestamp;
    }
    private synchronized void incrementLastTimestamp() {
        lastTimestamp++;
    }
    private synchronized void setLastTimeStamp(int lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }
    
    /* Called from the server to indicate that a game message (move or
     * new game) with timestamp has been sent.  A timestamp of 0 means
     * that message order is unimportant (for example, chat messages) while
     * a positive timestamp means we should check to make sure
     * we have all the previous messages before applying this one.  If we don't,
     * we can call getDataSince(our last timestamp) on the server.
     * If the message list timestamp is <= our last timestamp, we can
     * ignore the message.
     */
    public synchronized void receiveMessage(String string, Integer ts) {
        int timestamp = ts;  //unbox
        if( timestamp == 0 ) {
            process(string);
        } else if( timestamp == getLastTimestamp() + 1 ) {
            incrementLastTimestamp();
            process(string);
        } else if( timestamp > getLastTimestamp() + 1 ) {
            update();
        } else {
            // else ignore, we've already processed it
        }
    }
    
    /* Called from the server to update this client with a list of old messages.
     * The timestamp argument is the timestamp associated with string[0] from
     * the server.
     * If the timestamp is > our last
     * one, we should process all of the messages and update our timestamp---
     * this may include a new game message.
     */
    public synchronized void receiveMessageList(List<String> strings, Integer ts) {
        int timestamp = ts;  //unbox
        if( timestamp > getLastTimestamp() ) {
            setLastTimeStamp(timestamp - 1);
        }
        for(int i = 0; i < strings.size(); ++i) {
            String string = strings.get(i);
            receiveMessage(string, timestamp + i );
        }
    }
    
    public synchronized void update() {
        try {
            client.callAsync(Common.SERVER_HANDLER_CLASS_NAME, "getDataSince", getLastTimestamp());
        } catch (CallException ex) {
            ex.printStackTrace();
        }
    }
    
    private void process(String string) {
        if( listener != null ) {
            listener.receiveNetworkMessage(string);
        }
    }
    
}
