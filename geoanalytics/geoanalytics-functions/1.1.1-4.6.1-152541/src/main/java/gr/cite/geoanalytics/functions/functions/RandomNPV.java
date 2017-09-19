package gr.cite.geoanalytics.functions.functions;

import java.util.*;

public class RandomNPV implements Function {

	private static Random random = new Random();
	
	public double execute(double x, double y) {
		return random.nextInt(100);
	}
}
