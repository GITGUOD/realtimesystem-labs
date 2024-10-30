import java.util.concurrent.Semaphore;

import clock.AlarmClockEmulator;
import clock.io.Choice;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;

public class ClockMain {

	public static void main(String[] args) throws InterruptedException {
		AlarmClockEmulator emulator = new AlarmClockEmulator();

		ClockInput in = emulator.getInput();
		ClockOutput out = emulator.getOutput();

		Semaphore semaphore = in.getSemaphore();
		ClockMonitor cm = new ClockMonitor(out, 00, 00, 00);
		Clock clock = new Clock(cm); // Skapa en instans
		Thread clockThread = new Thread(clock);
		clockThread.start();

		// out.displayTime(15, 2, 37); // arbitrary time: just an example
		// Vi har lagt in den i klocka-klassen

		// Vi behöver en semaphore för att kontrollera att programmet väntar på att
		// användaren ger en ny input, finns där för att undvika busy wait där vår tråd
		// skulle loopa kontinuerligt utan att vänta på input

		// Semafor används här som signalering alltså.

		while (true) {
			semaphore.acquire();

			UserInput userInput = in.getUserInput();
			Choice c = userInput.choice();
			int h = userInput.hours();
			int m = userInput.minutes();
			int s = userInput.seconds();

			System.out.println("choice=" + c + " h=" + h + " m=" + m + " s=" + s);
			// Om användaren väljer att ställa in tid, uppdatera klockan
			if (c == Choice.SET_TIME) {
				cm.setTime(h, m, s);
			} else if (c == Choice.SET_ALARM) {
				cm.setAlarm(h, m, s);
			} else if (c == Choice.TOGGLE_ALARM) {
				cm.toggleAlarm();
			}

		}
	}

}
