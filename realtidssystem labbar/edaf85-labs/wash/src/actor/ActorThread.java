package actor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public abstract class ActorThread<M> extends Thread {

    // TODO: one suitable attribute here
    private final LinkedBlockingQueue<M> q = new LinkedBlockingQueue<>(); // En kö 
    //som sparar meddelanden och kan blockera trådar från att lägga element
    //eller ta element om kön är tom eller inte tom.
    // final för att vi vill säkra att det endast finns ett LinkedBlockingQUeue attribut typ

    /** Called by another thread, to send a message to this thread. */
    public void send(M message) {

        try {
            q.put(message); // Lägger meddelandet i kön
        } catch (InterruptedException e) {
           
            throw new Error(e);
        }
        // TODO: implement this method (one or a few lines)
    }
    
    /** Returns the first message in the queue, or blocks if none available. */
    protected M receive() throws InterruptedException {
        // TODO: implement this method (one or a few lines)
        return q.take();
    }
    
    /** Returns the first message in the queue, or blocks up to 'timeout'
        milliseconds if none available. Returns null if no message is obtained
        within 'timeout' milliseconds. */
    protected M receiveWithTimeout(long timeout) throws InterruptedException {
        // TODO: implement this method (one or a few lines)
        return q.poll(timeout, TimeUnit.MILLISECONDS); // Om vi inte får något meddelande inom en viss tid (timeout tiden så returneras null), '
        //TimeUnit.MILLISECONDs är ett sätt att deklarera att värdet timeout står för millisekunder
    }

    
}