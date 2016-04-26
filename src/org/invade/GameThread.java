/*
 * GameThread.java
 *
 * Created on July 8, 2005, 1:31 PM
 *
 */

package org.invade;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.JOptionPane;


public class GameThread extends Thread {
    
    private GameAlgorithm gameAlgorithm;
    private Board board;
    private List<Object> log;
    private BlockingQueue<Object> queue;
    private boolean processingEvents;
    
    public static final Object END_THREAD = new Object();
    
    private static Component errorMessageParent = null;
    private static boolean showWarnings = true;
    private static boolean warned = false;
    
    public GameThread() {
        super("Game Thread");
        log = new ArrayList<Object>();
        queue = new LinkedBlockingQueue<Object>();
        processingEvents = false;
        board = null;
        gameAlgorithm = null;
    }
    
    public void run() {
        warned = false;
        if( board == null ) {
            return;
        }
        try {
            board.getRandom().setSeed((Long)take());
            gameAlgorithm.startGame(board, this);
        } catch(EndGameException e) {  // Might happen if user quits game
            board.setTurnMode(TurnMode.GAME_OVER);
        } catch(ClassCastException e) {  // Some illegal move data might cause this
            e.printStackTrace();
        }
        synchronized(this) {
            processingEvents = false;
            notifyAll();
        }
    }
    
    /* Enqueues the response object and waits for the thread to finish
     * processing the response.  If the thread is not alive, either before or
     * after the response is enqueued, this method will return. */
    public synchronized void put(Object response) {
        if( isAlive() ) {
            try {
                processingEvents = true;
                
                queue.put(response);
                
                while( processingEvents && isAlive() ) {
                    wait();
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                Thread.currentThread().interrupt();
                
            }
        }
    }
    
    public Object take() throws EndGameException {
        synchronized(this) {
            if( queue.isEmpty() ) {
                processingEvents = false;
                notifyAll();
            }
        }
        try {
            Object result = queue.take();
            if( result.equals(END_THREAD)) {
                throw new EndGameException();
            }
            board.getMoveVerifier().verify(board, result);
            
            log.add(result);
            
            return result;
        } catch (InterruptedException ex) {            
            ex.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (final IllegalMoveException ex) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
//                    StringBuilder errorMessage = new StringBuilder(ex.getMessage() + "\n");
//                    for( StackTraceElement element : ex.getStackTrace() ) {
//                        errorMessage.append(element).append("\n");
//                    }
                    if(isShowWarnings() && !warned) {
                        warned = true;
                        JOptionPane.showMessageDialog(getErrorMessageParent(),
                                "Ignoring illegal move\n" +
                                "This indicates a possible program bug, but the game should continue normally\n" +
                                "Warnings can be suppressed from the Options menu\n\n"
                                + ex.getMessage(), "Warning",
                                JOptionPane.WARNING_MESSAGE, null);
                    }
                    System.out.println(ex.getMessage());
                }
            });
        }
        return take();
    }
    
    public List<Object> getLog() {
        return log;
    }
    
    public int getLogSize() {
        return log.size();
    }
    
    public GameThread start(Board board) {
        this.board = board;
        gameAlgorithm = board.getRules().createGameAlgorithm();
        start();
        return this;
    }
    
    public GameAlgorithm getGameAlgorithm() {
        return gameAlgorithm;
    }
    
    
    
    public static boolean isShowWarnings() {
        return showWarnings;
    }
    
    public static void setShowWarnings(boolean showWarnings) {
        GameThread.showWarnings = showWarnings;
    }
    
    public static Component getErrorMessageParent() {
        return errorMessageParent;
    }
    
    public static void setErrorMessageParent(Component errorMessageParent) {
        GameThread.errorMessageParent = errorMessageParent;
    }
    
}
