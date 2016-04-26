/*
 * BackgroundEventThread.java
 *
 * Created on August 12, 2005, 1:43 PM
 *
 */

package org.invade;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BackgroundEventThread extends Thread {
    private BlockingQueue<Runnable> events = new LinkedBlockingQueue<Runnable>();
    public static final Runnable END_THREAD = new Runnable(){ public void run(){} };
    
    public BackgroundEventThread() {
        super("BackgroundEventThread");
    }
    
    public void run() {
        while(true) {
            try {
                Runnable event = events.take();
                if( event == END_THREAD ) {
                    return;
                } else {
                    event.run();
                }
            } catch (InterruptedException ex) {
            }
        }
    }
    
    public void invokeLater(Runnable runnable) {
        try {
            events.put(runnable);
        } catch (InterruptedException ex) {
        }
    }
}
