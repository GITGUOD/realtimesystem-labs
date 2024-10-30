

public class Clock implements Runnable {
	
	//Trådklassen eftersom den implemnerar runnable vilket gör att den kan köras i en tråd

	private final ClockMonitor clockMonitor;

	public Clock(ClockMonitor clockMonitor) {
		this.clockMonitor = clockMonitor;

	}

	public void run() {

		long updateClockTime = System.currentTimeMillis();

		try {
			// Klockan ska ständigt ticka

			while (true) {

				clockMonitor.tick();
				
				//Updaterar klockan, låter den ticka varje sekund

				updateClockTime += 1000;

				//Koller koll på delayen och rättar till den
				long sleepTime = updateClockTime - System.currentTimeMillis();
				
				//Om den tickar för snabbt
				if (sleepTime > 0) {
					
					//Fixa till den så att den tickar långsammare
					Thread.sleep(sleepTime);
				} else {
					//Annars återställ den till normalt.
					updateClockTime = System.currentTimeMillis();
				}

			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	

}