package gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators;

public class SerialIntegerGenerator implements Generator<Integer> {

	private int i;
	
	public SerialIntegerGenerator() {
		this.i = 0;
		
	}
	public Integer next() {
		return i++;
	}

}
