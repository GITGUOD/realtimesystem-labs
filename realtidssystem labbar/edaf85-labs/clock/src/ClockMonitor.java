
import clock.io.ClockOutput;
import java.util.concurrent.*;

public class ClockMonitor {
	private int hour, minute, second;
	private int alarmHour, alarmMinute, alarmSecond;
	private final ClockOutput out;
	private boolean alarmSet = false;
	private int counter = 0;;
	private final int ALARM_INTERVAL = 20;
	private final int beepEverySecond = 1;
	private Semaphore lock = new Semaphore(1);

	// Monitor klass, eftersom den hanterar delad data, använder en mekanism för
	// synchronisering, semaphore för att säkerställa trådsäkerhet

	public ClockMonitor(ClockOutput out, int startHour, int startMinute, int startSecond) {
		this.out = out;

		this.hour = startHour;
		this.minute = startMinute;
		this.second = startSecond;

		// alarmTime = -1;

	}
	// Semaphore -> signalering/syncrhonisering, semaphore -> körtillstånd om
	// semaphore(1), finns bara 1 körtillstånd, lock.acquire, körtilståndet, bara
	// den tråden kan köras, lock release, släpper körtillståndet så någon annan kan
	// köra.

	// En metod för setTime()
	public void setTime(int h, int m, int s) throws InterruptedException {

		// Låser
		lock.acquire();
		try {
			this.hour = h;
			this.minute = m;
			this.second = s;

			// Visa den nya tiden direkt efter inställning
			out.displayTime(hour, minute, second);
		} finally {
			// Öppnar
			lock.release();
		}

	}

	// Metod för setAlarm

	public void setAlarm(int h, int m, int s) throws InterruptedException {

		lock.acquire();

		try {
			this.alarmHour = h;
			this.alarmMinute = m;
			this.alarmSecond = s;
			this.counter = 0; // Nollställ räknaren vid inställning av nytt alarm
			this.alarmSet = true;

			this.out.setAlarmIndicator(true);
		} finally {
			lock.release();
		}

	}

	public void toggleAlarm() throws InterruptedException {
		lock.acquire();
		try {
			// Återställer state
			this.alarmSet = !this.alarmSet;
			out.setAlarmIndicator(alarmSet);

			// Återställer räknaren
			if (!alarmSet) {
				this.counter = 0;

			}
		} finally {
			lock.release();
		}

	}

	public void activateAlarm() throws InterruptedException {
		lock.acquire();
		try {
			if (alarmSet && hour == alarmHour && minute == alarmMinute && second == alarmSecond) {

				// Alarmet triggas första gången
				

				out.alarm();
				
				counter++;

			
			} else if (alarmSet && counter >= beepEverySecond) {
				// Om alarmen är på och räknaren överstiger intervallet 20, återställer vi
				// counter till 0 eftersom vi vill att larmet ska triggera varje 20s

				out.alarm();
				//counter = 0;
				counter++;
			
				if(counter > ALARM_INTERVAL){
					alarmSet = !alarmSet;
				}

			}

		} finally {
			lock.release();
		}
	}

	public void tick() throws InterruptedException {

		lock.acquire();
		// long currentTime = System.currentTimeMillis();

		try {

			// den här loopen uppdaterar tiden.
			second++;
			if (second >= 60) {
				second = 0;
				minute++;

				if (minute >= 60) {
					minute = 0;
					hour = (hour + 1) % 24;
				}
			}

			// Om larmet är på, räknar vi
//			if (alarmSet) {
//				counter++;
//			}

			out.displayTime(hour, minute, second);

		} finally {
			lock.release();
		}
		// Flytta ut alarm-logiken utanförlåsningen, för att undvika deadlock, utan att
		// blockera tickningen
		activateAlarm();

	}
}
