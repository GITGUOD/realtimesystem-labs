package wash.control;


import static wash.control.WashingMessage.Order.ACKNOWLEDGMENT;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;
import wash.io.WashingIO.Spin;

public class SpinController extends ActorThread<WashingMessage> {

    // TODO: add attributes
    private WashingIO io;
    private boolean spiningLeft;

    public SpinController(WashingIO io) {
        this.io = io;
        this.spiningLeft = true;
    }

    @Override
    public void run() {

        // this is to demonstrate how to control the barrel spin:
        // io.setSpinMode(Spin.IDLE);

        try {

            // ... TODO ...

            Order currentOrder = null;

            while (true) {
                // wait for up to a (simulated) minute for a WashingMessage
                WashingMessage m = receiveWithTimeout(60000 / Settings.SPEEDUP);

                // if m is null, it means a minute passed and no message was received
                if (m != null) {
                    currentOrder = m.order(); //Uppdatera order eftersom meddelanden fortfarande finns
                    System.out.println("got " + m);
                    
                }

                //Om det finns order kvar

                if (currentOrder != null) {
                    handleOrder(currentOrder);
                    sendAcknowledgment(m);
                }
                    
                }
                
               
        } catch (InterruptedException unexpected) {
            // we don't expect this thread to be interrupted,
            // so throw an error if it happens anyway
            throw new Error(unexpected);
        }
    }
    
     // Handle the current order
     private void handleOrder(Order order) {
        switch (order) {
            case SPIN_FAST:
                handleSpinFast();
                break;

            case SPIN_SLOW:
                handleSpinSlow();
                break;

            case SPIN_OFF:
                handleSpinOff();
                break;

            default:
                break;
        }
    }

    // Spin fast
    private void handleSpinFast() {
        io.setSpinMode(Spin.FAST);
    }

    // Spin slowly and change direction
    private void handleSpinSlow() {
        if (spiningLeft) {
            io.setSpinMode(Spin.LEFT);
        } else {
            io.setSpinMode(Spin.RIGHT);
        }
        spiningLeft = !spiningLeft;
    }

    // Turn off the spin
    private void handleSpinOff() {
        io.setSpinMode(Spin.IDLE);
    }

    // Send acknowledgment message
    private void sendAcknowledgment(WashingMessage m) {
        if (m != null) {
            m.sender().send(new WashingMessage(this, ACKNOWLEDGMENT));
        }
    }
}
