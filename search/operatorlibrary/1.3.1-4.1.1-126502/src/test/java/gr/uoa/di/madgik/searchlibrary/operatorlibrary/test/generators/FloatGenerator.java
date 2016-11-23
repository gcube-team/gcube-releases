package gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators;

import java.util.Random;

public class FloatGenerator implements Generator<Float> {
	
	private Random rnd = null;
	
	public FloatGenerator(Long seed) {
		if(seed != null)
			rnd = new Random(seed);
		else
			rnd = new Random();	
	}
	
	public Float next() {
		return rnd.nextFloat();
	}

}
