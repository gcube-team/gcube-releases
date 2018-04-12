package org.gcube.dataanalysis.ecoengine.models.cores.neuralnetworks.neurosolutions;

import java.io.*;

// this class reads one line at a time and returns it as a string
public class LineReader
{
	String[] column;
	FileInputStream fis;
	BufferedInputStream bis;
	
	// constructor
	public LineReader (String path) {
		try {
			fis = new FileInputStream(path);
			bis = new BufferedInputStream(fis);
		}	      
		catch (IOException e) {}
		/*
		this.inputFile = new File(path);
		try {in = new FileReader(inputFile);} catch (IOException e) {}
		*/
	}

	// read the next line, split it and return the parts as a string array
	public boolean NextLineSplitted () {
		column = null;
		column = NextLine().split(";");
		if (column[0] != "#EOF#") {
			for (int i = 0; i < column.length; i++) {
				column[i] = column[i].trim();
			}
			return true;
		}
		else{return false;}
	}
	
	// read the next line, return the line as string
	public String NextLine() {
		int i;
		char[] temp_array = new char[50000];
		char[] temp_array2;
		boolean last_line;
		int counter;
		String temp_line = "";
		
		do {
			temp_array2 = null;
			counter = 0;
			last_line = true;
			// read a line
			try {
				while ( (i = bis.read()) != -1 ) {
					last_line = false;
					if (i == 13 || i == 10) {
						break;
					}
					else if( i != 10 && i != 13) {
						temp_array[counter++] = (char)i;
					}
				}
			}
			catch (IOException e) {}
			// put the array into a string
			if (last_line) {
				temp_line = "#EOF#";
			}
			else if (counter != 0) {
				temp_array2 = new char[counter];
				boolean all_spaces = true;
				for (int j = 0; j < counter; j++) {
					if (temp_array[j] != ' ') {all_spaces = false;}
					temp_array2[j] = temp_array[j];
				}
				if (all_spaces) {temp_line = "";}
				else {temp_line = new String(temp_array2);}
				if (temp_line.length() >= 2 && temp_line.charAt(0) == '/' && temp_line.charAt(1) == '/') {
					temp_line = "";
				}
			}
			else {
				temp_line = "";
			}

		} while (temp_line == "");
		return temp_line.trim();
	}
}
