package gr.cite.gaap.utilities;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class PasswordGenerator
{
	private static final char[] keys = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789".toCharArray();
	private static final int LengthDefault = 8;
	private int length = LengthDefault;
	
	public PasswordGenerator() { }
	
	public PasswordGenerator(int length)
	{
		this.length = length;
	}
	
	public String generate()
	{
		Random rnd = new Random();
		StringBuilder pass = new StringBuilder();
		for(int i=0; i<length; i++)
			pass.append(keys[rnd.nextInt(keys.length)]);
		return pass.toString();
	}
}
