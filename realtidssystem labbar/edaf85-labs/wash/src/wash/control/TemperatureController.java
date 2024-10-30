package wash.control;

import static wash.control.WashingMessage.Order.ACKNOWLEDGMENT;
import static wash.control.WashingMessage.Order.TEMP_SET_40;
import static wash.control.WashingMessage.Order.TEMP_SET_60;


import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {
 // TODO: add attributes

    private WashingIO io;
    private double upperMargin = 0.478; //Enligt beräkningar: 10* (2000/(10*4.184*10^3))
    private double lowerMargin = 0.0952; //Enligt beräkningar: 10*2.38*10^(-4)(60-40)
    private boolean ackSent;
    private Order currentOrder;
    private ActorThread<WashingMessage> sender;


    public TemperatureController(WashingIO io) {
        // TODO
        this.io = io;
        upperMargin += 0.2;
        lowerMargin += 0.2;
    }

    @Override
 
        public void run() {
            
            try {
                
                while(true) {
                    WashingMessage m = receiveWithTimeout(10000 / Settings.SPEEDUP);	
                    
                    if (m != null) {
                        currentOrder = m.order();
                        sender = m.sender();
                        System.out.println("TemperatureController got " + m);
                        ackSent = false;
                    
     
                           handleOrder(m.order());
                            

                    
                    }
                    
                    if(currentOrder == TEMP_SET_40) {
                        
                        adjustTemperature(40);
                    }
                    
                    if(currentOrder == TEMP_SET_60) {
                        
                        adjustTemperature(60);
                        
                    }
                    
                    
                    
                    
                }
        } catch(InterruptedException e) {
            throw new Error(e);
        }
    }

    
    private void sendAck() {
        if (!ackSent && sender != null) {
            System.out.println("TemperatureController sending ACK");
            sender.send(new WashingMessage(this, ACKNOWLEDGMENT));
            ackSent = true;
        }
    }

    private void handleOrder(Order order) {

        
        switch(order) {
            case TEMP_SET_40:
                io.heat(true);
                break;
                
            case TEMP_SET_60:
                io.heat(true);
                break;
                
            case TEMP_IDLE:
                io.heat(false);
                System.out.println("TemperatureController sending ACK");
                sender.send(new WashingMessage(this, ACKNOWLEDGMENT));
                ackSent = true;
                break;
                
            default:
                break;

        }
    }
    private void adjustTemperature(double targetTemperature) {
        double minTemperature = targetTemperature - 2 + lowerMargin;
        double maxTemperature = targetTemperature -  upperMargin;
    
        if (io.getTemperature() > minTemperature) {
            sendAck();
        }
    
        if (io.getTemperature() <= minTemperature) {
            io.heat(true);
        }
    
        if (io.getTemperature() >= maxTemperature) {
            io.heat(false);
        }
    }
    

}
