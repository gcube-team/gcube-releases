package org.gcube.contentmanagement.graphtools.tests.old;

import java.util.LinkedList;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DoubleArrayDataRow;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.tools.Ontology;

public class ExampleUserSampleSet {

	public static void main(String[] argv) {
		// create attribute list
		List<Attribute> attributes = new LinkedList<Attribute>();
		for (int a = 0; a < getMyNumOfAttributes(); a++) {
			attributes.add(AttributeFactory.createAttribute("att" + a, Ontology.REAL));
		}
		Attribute label = AttributeFactory.createAttribute(" label ", Ontology.NOMINAL);
		attributes.add(label);
		// create table
		MemoryExampleTable table = new MemoryExampleTable(attributes);
		// ll table (here : only real values )
		for (int d = 0; d < getMyNumOfDataRows(); d++) {
			double[] data = new double[attributes.size()];
			for (int a = 0; a < getMyNumOfAttributes(); a++) {
				// all with proper data here
				data[a] = getMyValue(d, a);
			}
			// maps the nominal classi cation to a double value
			data[data.length - 1] = label.getMapping().mapString(getMyClassification(d));
			// add data row
			table.addDataRow(new DoubleArrayDataRow(data));
		}
		// create example set
		ExampleSet exampleSet = table.createExampleSet(label);
	}

	
	//to be defined in future applications
	private static int getMyNumOfAttributes() {
		return 3;
	}

	private static int getMyNumOfDataRows() {
		return 10;
	}

	private static double getMyValue(int d, int a) {
		return 10;
	}

	private static String getMyClassification(int d) {
		return "ciao";
	}
}
