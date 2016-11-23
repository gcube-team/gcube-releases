package gr.uoa.di.madgik.hive.utils;

import java.io.File;
import java.io.IOException;


public class MaskString {
	private String query;
	private String hidden;
	
	private File file;
	{
		try {
			file = File.createTempFile("mask", "");
			file.deleteOnExit();
		} catch (IOException e) {
		}
	}
	
	public MaskString(String query, String hidden) {
		this.query = query;
		this.hidden = hidden;
	}
	
	public String hide() {
		return 	query.replace(hidden, file.getAbsolutePath());
	}
	
	public String hideWithQuotes() {
		return 	query.replace(hidden, "'" + file.getAbsolutePath() + "'");
	}
	
	public String unhide() {
		return query;
	}
	
	public String getMask() {
		return hidden;
	}
	
	public static void main(String[] args) throws IOException {
		MaskString mask = new MaskString("INSERT OVERWRITE LOCAL DIRECTORY 'jdbc:postgresql://localhost:5432/mydb?user=postgres&password=aplagiadb/INSERT INTO wikimulti_new VALUES (?, ?, ?)' SELECT id, property, content FROM wiki;", "jdbc:postgresql://localhost:5432/mydb?user=postgres&password=aplagiadb/INSERT INTO wikimulti_new VALUES (?, ?, ?)");
		
		System.out.println(mask.hide());
		System.out.println(mask.unhide());
		System.out.println(mask.getMask());
	}
}
