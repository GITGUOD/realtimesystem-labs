package wash.control;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import static wash.control.WashingMessage.Order.*;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {

	private WashingIO io;
	private Order order;
	private ActorThread<WashingMessage> sender;
	private boolean ackSent;

    public WaterController(WashingIO io) {
    	this.io = io;
    }

    @Override
    public void run() {
    	
    		try {
    			while(true) {
    				
    				WashingMessage m = receiveWithTimeout(1000 / Settings.SPEEDUP);
    				
    				if (m != null) {
                    	
                    	order = m.order();
                    	sender = m.sender();
                    	
                        System.out.println("WaterController got " + m);
                        
                        ackSent = false;
                    
    					handleOrder(m.order());
    				}
    				
					checkingWaterLevels();
    					
    			}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
    					
            case WATER_FILL:
                io.drain(false);
                io.fill(true);
                break;
                
            case WATER_DRAIN:
                io.fill(false);	
                io.drain(true);
                break;
            
            case WATER_IDLE:
                io.fill(false);
                io.drain(false);
                sender.send(new WashingMessage(this, ACKNOWLEDGMENT));
                break;
            default:
                break;
                
        }
    }

    private void checkingWaterLevels() {

        if(order == WATER_FILL) {
            if(io.getWaterLevel() > 10) {
                io.fill(false);
                
                sendAck();
            }
        }
            
        if(order == WATER_DRAIN) {
            if(io.getWaterLevel() == 0) {
                sendAck();
            }
        }
    }
}