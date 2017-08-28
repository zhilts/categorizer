package visualization;

import javax.swing.JTextArea;

public class ActivityThread extends Thread {

	private String[] states = { "/", "-", "\\", "|" };
	private int state = 0;
	private long delay;
	private JTextArea textArea;
	private boolean running;

	public ActivityThread(JTextArea textArea, long delay) {
		this.textArea = textArea;
		this.delay = delay;
		running = false;
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			textArea.setText("\n    " + states[state]);
			state++;
			state = state % states.length;
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void pauseActivity() {
		running = false;
	}

}
