package gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators;

import java.util.Random;

public class StringGenerator implements Generator<String> {
	
	private Random rnd = null;
	private Random rndLen = null;
	private Integer length = 100;
	private Boolean varyLength = false;
	
	public StringGenerator(Integer length, Long seed, Boolean varyLength) {
		if(seed != null)
			rnd = new Random(seed);
		else
			rnd = new Random();
	
		if(length != null)
			this.length = length;
		
		if(varyLength != null) {
			this.varyLength = varyLength;
			if(seed != null)
				this.rndLen = new Random(seed);
			else
				this.rndLen = new Random();
		}
	}
	
	public String next() {
		int len = this.length;
		if(varyLength == true)
			len = rndLen.nextInt(this.length) + 1;
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < len; i++)
			builder.append((char)('a' + rnd.nextInt(24)));
		return builder.toString();
	}

}

