package org.gcube.portlets.docxgenerator;

//import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


public class DocxGeneratorTest {

	//private static final String OUT_TEST = "MergedSpecies-Test";
	
	private final static String testpathToModel =  "src/test/resources/CURRENT_OPEN.d4st";
	private final static String modelFile = "CURRENT_OPEN.d4st";
	
	private static DocxGenerator generator;
	
	@BeforeClass
	public static void initTest() {
//		generator = new DocxGenerator(testpathToModel,false,false); 
		
	}

	@Test
	public void doxGenerationTest() throws Exception {
	//	generator.parseInput(testpathToModel);
	//	generator.outputFile("MyTest.docx");
	//	generator.outputPDFTmpFile();
		
	}
}
