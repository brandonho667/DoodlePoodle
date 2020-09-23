import java.util.Timer;
import java.util.TimerTask;

public class CountDown extends Timer {
	long delay = 1000;
	long period = 1000;
	Time t;

	/**
	 * Initializes Timer with TimerTask and given delay (seconds) between each
	 * run of TimerTask
	 * 
	 * @param secs
	 *            delay between TimerTask update (1 second)
	 */
	public CountDown(int secs) {
		t = new Time(secs);
		System.out.println(secs);
		scheduleAtFixedRate(t, delay, period);
	}

	public Time getTime() {
		return t;
	}
}

class Time extends TimerTask {
	int totTime;
	int time;
	boolean reset;

	/**
	 * Initializes the TimerTask with current time, total time, and reset.
	 * 
	 * @param interval
	 *            total time per turn
	 */
	public Time(int interval) {
		time = interval;
		totTime = interval;
		reset = false;
	}

	/**
	 * Reduces current time by one every run, countdown
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (time <= 0) {
			time = totTime;
			reset = true;
		}
		time--;
		System.out.println(time);

	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public boolean isReset() {
		return reset;
	}

	public void setReset(boolean reset) {
		this.reset = reset;
	}

}