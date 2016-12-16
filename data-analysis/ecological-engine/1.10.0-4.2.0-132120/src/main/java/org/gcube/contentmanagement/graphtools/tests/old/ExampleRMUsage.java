package org.gcube.contentmanagement.graphtools.tests.old;


import com.rapidminer.RapidMiner;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.generator.ExampleSetGenerator;
import com.rapidminer.tools.OperatorService;

public class ExampleRMUsage {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// create process
		com.rapidminer.Process process = createProcess();
		// print process setup
		System.out. println (process .getRootOperator().createProcessTree (0));
		try {
		// perform process
		process.run ();
		// to run the process with input created by your application use
		// process .run(new IOContainer(new IOObject[] f ... your objects ... g);
		}catch (OperatorException e) { e. printStackTrace (); }

	}

	public static com.rapidminer.Process createProcess() {

		System.out.println("INIT R-I");
		
		// invoke init before using the OperatorService
		RapidMiner.init();
		
		System.out.println("INIT FINISHED");
		// create process
		com.rapidminer.Process process = new com.rapidminer.Process();
		try {
			// create operator
			Operator inputOperator = OperatorService.createOperator(ExampleSetGenerator.class);
			// set parameters
			inputOperator.setParameter("target function", "sum classification");
			// add operator to process
			process.getRootOperator().addOperator(inputOperator,0);
			// add other operators and set parameters
			// [...]
			System.out.println("INIT PROCESS FINISHED");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return process;
	}
	
	
	public static IOContainer createInput () {
	// create a wrapper that implements the ExampleSet interface and
	// encapsulates your data
	// ...
		
	
	return new IOContainer();
	}
	
	
	
	
	
}
