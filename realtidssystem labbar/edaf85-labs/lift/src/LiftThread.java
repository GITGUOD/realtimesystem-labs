import lift.LiftView;

public class LiftThread extends Thread {

    private Monitor monitor;
    private boolean directionUp;
    private LiftView lift;
    


    public LiftThread(Monitor monitor, LiftView lift) {
        this.monitor = monitor;
        this.directionUp = true;
        this.lift = lift;

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            while(true) {

                monitor.beforeLifting();
                monitor.liftControl();
                moveLift();
                
                
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        
    }

    private void moveLift() {

        if(directionUp) {
            
            int nextFloor = monitor.getCurrentFloor() + 1;
            lift.moveLift(monitor.getCurrentFloor(), nextFloor);
            monitor.setCurrentFloor(monitor.getCurrentFloor() + 1);

            if(monitor.getCurrentFloor() == monitor.getNbrOfFloors() - 1) {
                directionUp = !directionUp;
            }
            } else {
                int nextFloor = monitor.getCurrentFloor() - 1;
                lift.moveLift(monitor.getCurrentFloor(), nextFloor);
                monitor.setCurrentFloor(monitor.getCurrentFloor() - 1);

                    if(monitor.getCurrentFloor() == 0) {
                        directionUp = !directionUp;
                    }
            }
    
    }

    

    
}



