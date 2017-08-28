package visualization;

import controller.Controller;

public class ClasterizatorThread extends Thread {

	private Controller controller;
	private String searchString;
	private double[] limits;

	public ClasterizatorThread(Controller controller, String searchString,
			double[] limits) {
		this.controller = controller;
		this.searchString = searchString;
		this.limits = limits;
	}

	@Override
	public void run() {
		controller.getTreeWithParameters(searchString, limits);
	}
}
