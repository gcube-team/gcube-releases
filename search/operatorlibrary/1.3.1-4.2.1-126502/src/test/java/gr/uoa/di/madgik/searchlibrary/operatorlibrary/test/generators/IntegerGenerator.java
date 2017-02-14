package gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators;

import java.util.Random;

public class IntegerGenerator implements Generator<Integer> {
	
	private Random rnd = null;
	private Integer limit = null;
	
	public IntegerGenerator(Integer limit, Long seed) {
		if(seed != null)
			rnd = new Random(seed);
		else
			rnd = new Random();
	
		this.limit = limit;
		
	}
	public Integer next() {
		return (limit != null ? rnd.nextInt(limit) : rnd.nextInt());
	}

}
