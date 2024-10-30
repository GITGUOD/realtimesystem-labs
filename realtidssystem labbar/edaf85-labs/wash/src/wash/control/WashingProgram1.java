package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

import static wash.control.WashingMessage.Order.*;

/**
 * Program 3 for washing machine. This also serves as an example of how washing
 * programs can be structured.
 * 
 * This short program stops all regulation of temperature and water levels,
 * stops the barrel from spinning, and drains the machine of water.
 * 
 * It can be used after an emergency stop (program 0) or a power failure.
 */
public class WashingProgram1 extends ActorThread<WashingMessage> {

    private WashingIO io;
    private ActorThread<WashingMessage> temp;
    private ActorThread<WashingMessage> water;
    private ActorThread<WashingMessage> spin;
    
    public WashingProgram1(WashingIO io,
                           ActorThread<WashingMessage> temp,
                           ActorThread<WashingMessage> water,
                           ActorThread<WashingMessage> spin) 
    {
        this.io = io;
        this.temp = temp;
        this.water = water;
        this.spin = spin;
    }
    
    @Override
    public void run() {
        try {

                        System.out.println("washing program 3 started");


                // Lock the hatch
                io.lock(true);

                //Fylla vattnet

                System.out.println("SETTING WATER");
                water.send(new WashingMessage(this, WATER_FILL));
                WashingMessage ackForFillingWater = receive();
                System.out.println("Washing program test got" + ackForFillingWater);

                //Temperaturen 40grader
                System.out.println("SETTING TEMP 40...");
                temp.send(new WashingMessage(this, TEMP_SET_40));
                WashingMessage ackForSettingTemp40 = receive();
                System.out.println("SWashing program test got" + ackForSettingTemp40);

               //Börja tvätta
                System.out.println("setting SPIN_SLOW...");
                spin.send(new WashingMessage(this, SPIN_SLOW));
                WashingMessage ack1 = receive();
                System.out.println("washing program 1 got " + ack1);
                // Spin for 30 simulated minutes (one minute == 60000 milliseconds)
                Thread.sleep(30 * 60000 / Settings.SPEEDUP);

                //Torka och stänga av värme

                System.out.println("setting TEMP_IDLE....");
                temp.send(new WashingMessage(this, TEMP_IDLE));
                WashingMessage ackForTempIdle = receive();
                System.out.println("washing program test got" + ackForTempIdle);

                System.out.println("SETTING WATER_DRAIN.......");
                water.send(new WashingMessage(this, WATER_DRAIN));
                WashingMessage ackForWaterDRAIN = receive();
                System.out.println("washing program test got" + ackForWaterDRAIN);


                //Rinse 5 ggr i 2 minuter

                for(int i = 0; i < 5; i++) {
                    System.out.println("SETTING WATER_FILL.......");
                    water.send(new WashingMessage(this, WATER_FILL));
                    WashingMessage ackForWaterFILL= receive();
                    System.out.println("washing program test got" + ackForWaterFILL);

                    System.out.println("SETTING SPIN_SLOW.......");
                    spin.send(new WashingMessage(this, SPIN_SLOW));
                    WashingMessage ackForSPINSlow= receive();
                    System.out.println("washing program test got" + ackForSPINSlow);

                    Thread.sleep(2 * 60000 / Settings.SPEEDUP); //Två minuter

                    System.out.println("SETTING WATER_DRAIN.......");
                    water.send(new WashingMessage(this, WATER_DRAIN));
                    WashingMessage ackForWaterDrain= receive();
                    System.out.println("washing program test got" + ackForWaterDrain);
                }

                //Drain water
                System.out.println("SETTING WATER_DRAIN.......");
                water.send(new WashingMessage(this, WATER_DRAIN));
                WashingMessage ackForWaterDrain= receive();
                System.out.println("washing program test got" + ackForWaterDrain);
                

                //Centrifuge 5 minutes
                System.out.println("setting SPIN_FAST...");
                spin.send(new WashingMessage(this, SPIN_FAST));
                WashingMessage ackSpinFast = receive();
                System.out.println("washing program 1 got " + ackSpinFast);

                Thread.sleep(5 * 60000 / Settings.SPEEDUP);

                // Turn off spin
                System.out.println("setting SPIN_OFF...");
                spin.send(new WashingMessage(this, SPIN_OFF));
                WashingMessage ackSpinOff = receive();
                System.out.println("washing program 1 got " + ackSpinOff);

                // water idle
                System.out.println("setting WATER_IDLE...");
                water.send(new WashingMessage(this, WATER_IDLE));
                WashingMessage ackWaterIdle = receive();
                System.out.println("washing program test got " + ackWaterIdle);
                // Now that the barrel has stopped, it is safe to open the hatch.
                io.lock(false);

            
            System.out.println("washing program 1 finished");
        } catch (InterruptedException e) {
            
            // If we end up here, it means the program was interrupt()'ed:
            // set all controllers to idle

            temp.send(new WashingMessage(this, TEMP_IDLE));
            water.send(new WashingMessage(this, WATER_IDLE));
            spin.send(new WashingMessage(this, SPIN_OFF));
            System.out.println("washing program terminated");
        }
    }
}
