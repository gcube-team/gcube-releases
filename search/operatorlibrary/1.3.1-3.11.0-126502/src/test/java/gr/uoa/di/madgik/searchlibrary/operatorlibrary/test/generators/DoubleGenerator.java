package gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators;

import java.util.Random;

public class DoubleGenerator implements Generator<Double> {
	
	private Random rnd = null;
	
	public DoubleGenerator(Long seed) {
		if(seed != null)
			rnd = new Random(seed);
		else
			rnd = new Random();	
	}
	
	public Double next() {
		return rnd.nextDouble();
	}

}
