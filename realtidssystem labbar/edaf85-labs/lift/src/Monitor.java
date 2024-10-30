import lift.LiftView;
import lift.Passenger;

public class Monitor {

        private int currentFloor;
        private int passengerInLift;
        private int nbrFloors;
        private int toEnter[];
        private int toExit[];
        private boolean moving;
        private int MAX_PASSENGERS;
        private LiftView lift;
        //private boolean directionUp;
        private int passagersIn;   
        private int passagersOut;   

        public Monitor(LiftView lift, int max_passengers, int nbrFloors) {
            this.lift = lift;
            this.MAX_PASSENGERS = max_passengers;
            this.toEnter = new int[nbrFloors]; //Antal passagerare som väntar på en våning
            this.toExit = new int[nbrFloors]; //Antal passagerare som väntar på att komma till den våningen
            //T.ex toEnter[3] == 2, detta betyder 2 passagerare på våning 3 som väntar på hissen.

                for(int i = 0; i < nbrFloors; i++) {
                    toEnter[i] = 0;
                    toExit[i] = 0;
                }

                    this.passagersIn = 0;
                    this.passagersOut = 0;
                    this.currentFloor = 0;
                    this.passengerInLift = 0;
                    this.moving = true;
                    this.nbrFloors = nbrFloors;
                    //this.directionUp = true;

                    
        }

        public synchronized void enteringLift(int fromFloor, int toFloor) {

            //Personer i våningen minskar
            toEnter[fromFloor]--;
            //Folk som ska ut sin destination ökar
            toExit[toFloor]++;
        }

        public synchronized void exitingLift(int toFloor) {

            //Folk kliver av
            passengerInLift--;
            toExit[toFloor]--;
            //Färdig ombord, de är nu ute ur hissen

        }

        //Generera en passagerare
        public synchronized void passengerToLift(Passenger pass) throws InterruptedException{
        
                //Hämtar startdestinationer
                int fromFloor = pass.getStartFloor();

                //Ökar antalet patienter som kommer in
                toEnter[fromFloor]++;
                lift.showDebugInfo(toEnter, toExit);
                
                //Notifierar att det kommer passagerare
                notifyAll();
        
                    //passenger should wait for the lift to arrive 
                    //or wait if passenger in lift is greater than max passengers or the lift is moving
                    //Om hissen inte är på passagerares våning eller om hissen är full eller om hissen rör sig, väntar passagerarna.
                    while((currentFloor != fromFloor) || (passengerInLift >= MAX_PASSENGERS) || moving){
                        wait();
                    }

                    //Under är boarding

                    passagersIn++;
                    passengerInLift++;
                    

        }

        public synchronized void passengersBoarding(Passenger pass, int toFloor, int fromFloor) throws InterruptedException {
            
            //Börjar gå in
            //Har nu klivit ombord
            finishedEntering();
            lift.showDebugInfo(toEnter, toExit);
            
            //Meddelar att något har hänt
            notifyAll();
    
                //passagerarna väntar om de ej har kommit till sin destination eller om hissen rör sig.
                while((currentFloor != toFloor) || moving){
                    wait();
                }
                    //Tillstånden har ändrats

                    //Börjar gå ut från hissen

                    //Här måste vi ändra på metoder
                    passagersOut++;
                    //System.out.print("people leaving" + passagersLeavingLift);
                    while(moving) {
                        wait();
                    }
    

    

        }

        public synchronized void finishingLine(Passenger pass, int toFloor, int fromFloor) throws InterruptedException {

            finishedExiting();
                    lift.showDebugInfo(toEnter, toExit);
            
                    //Notifierar
                    notifyAll();
                    

        }
        

        public synchronized void finishedEntering() throws InterruptedException {
            passagersIn--;
                //System.out.print("people finished entering" +passagersEnteringLift);
                if(passagersIn == 0) {
                    notifyAll();
                }
        }

        public synchronized void finishedExiting() throws InterruptedException {
            passagersOut--;
                //System.out.print("people finished entering" +passagersEnteringLift);
                if(passagersOut == 0) {
                    notifyAll();
                }
        }


/* 
 * 
 * public void moveLift() throws InterruptedException {

            if(directionUp) {
                
                int nextFloor = currentFloor + 1;
                lift.moveLift(currentFloor, nextFloor);
                currentFloor++;

                if(currentFloor == nbrFloors -1) {
                    directionUp = !directionUp;
                }
                } else {
                    int nextFloor = currentFloor - 1;
                    lift.moveLift(currentFloor, nextFloor);
                    currentFloor--;

                        if(currentFloor == 0) {
                            directionUp = !directionUp;
                        }
                }
        
        }
*/
    
        public synchronized void beforeLifting() throws InterruptedException {
            // Kolla om det finns någon passagerare på någon våning
            while (noPassengersWaiting()) {
                lift.openDoors(currentFloor); // Öppna dörrarna
                moving = false; // Stanna hissen
                notifyAll(); // Signalera att hissen är stilla
        
                    // Vänta på att nya passagerare ska komma
                    while (noPassengersWaiting() || passagersIn > 0) {
                        wait(); // Vänta tills någon väntar eller hissen har passagerare i sig
                    }
                
                        moving = true;  // Hissen får nu röra sig igen
                        lift.closeDoors();  // Stäng dörrarna
                        notifyAll();  // Meddela att hissen nu är i rörelse
            }
        }
        
        // En metod som kollar om det inte finns några passagerare som väntar någonstans
        private boolean noPassengersWaiting() {
            // Gå igenom alla våningar och kontrollera om någon väntar att gå in eller ut
            for (int i = 0; i < nbrFloors; i++) {
                if (toEnter[i] > 0 || toExit[i] > 0) {
                    return false;  // Någon väntar, så hissen ska inte stanna
                }
            }
            return true;  // Ingen väntar på någon våning
        }
    
        public synchronized void liftControl() throws InterruptedException {

            //Om det finns passagerare på våningen och om hissen inte är full eller om det finns passagerare som vill sin destination
            if((toEnter[currentFloor] > 0 && passengerInLift < MAX_PASSENGERS) || toExit[currentFloor] > 0){
            //öppnas portarna till hissen
            lift.openDoors(currentFloor);
                
                //hissen stanna
                moving = false;
                
                //notifierar att den gör det
                notifyAll();
                
                //Så länge det finns folk på den våningen och att hissen inte är full väntar vi eller om det finns folk som vill ut på den nuvarande destinationen..
                //Har försökt få hissen o vänta tills folk har kommit om bord eller gått ombord
                while((toEnter[currentFloor] > 0 && passengerInLift < MAX_PASSENGERS) || toExit[currentFloor] > 0 || passagersIn > 0 || passagersOut > 0){
                    wait();
                }
    
                    //Stänger dörren för tillståndet har ändrats
                    lift.closeDoors();
                    }
                //Hissen kan nu röra sig
                    moving = true; 
            
                    //Notifierar alla
                    notifyAll();
                }

                //Den nya
                
                public synchronized int getCurrentFloor() {
                    return currentFloor;
                }
        
                public synchronized int getNbrOfFloors() {
                    return nbrFloors;
                }
        
                public synchronized void setCurrentFloor(int newFloor) {
                    currentFloor = newFloor;
                }
                

}
