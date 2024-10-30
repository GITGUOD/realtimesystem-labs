import lift.LiftView;

public class ManyPersonRidesLift {


    public static void main(String[] args) {

        final int NBR_FLOORS = 7, MAX_PASSENGERS = 4;

        LiftView  view = new LiftView(NBR_FLOORS, MAX_PASSENGERS);
        Monitor monitor = new Monitor(view, MAX_PASSENGERS, NBR_FLOORS);

        //Starta en tråd
        LiftThread liftThread = new LiftThread(monitor, view);

        for(int i = 0; i < 20; i++) {
            PassengerThread passengerThread = new PassengerThread(monitor, view);
            Thread threadForPassenger = new Thread(passengerThread);
            threadForPassenger.start();
        }
        Thread thread = new Thread(liftThread);
        thread.start();


       
    }

}
