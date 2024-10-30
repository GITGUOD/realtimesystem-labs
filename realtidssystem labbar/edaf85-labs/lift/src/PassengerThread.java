import lift.LiftView;
import lift.Passenger;

public class PassengerThread extends Thread{

    private LiftView lift;
    private Monitor monitor;

    public PassengerThread(Monitor monitor, LiftView lift) {
        this.lift = lift;
        this.monitor = monitor;

    }

    @Override
    public void run() {
 
            try {

            while(true) {
                
                Passenger pass = lift.createPassenger();
                pass.begin();
                monitor.passengerToLift(pass);
                pass.enterLift();
                monitor.enteringLift(pass.getStartFloor(), pass.getDestinationFloor());
                monitor.passengersBoarding(pass, pass.getDestinationFloor(), pass.getDestinationFloor());
                pass.exitLift();
                monitor.exitingLift(pass.getDestinationFloor());
                monitor.finishingLine(pass, pass.getStartFloor(), pass.getDestinationFloor());
                pass.end();
            }

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        
    }


