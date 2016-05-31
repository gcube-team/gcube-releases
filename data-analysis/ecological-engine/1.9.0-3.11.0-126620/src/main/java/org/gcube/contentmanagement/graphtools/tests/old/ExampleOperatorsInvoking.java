package org.gcube.contentmanagement.graphtools.tests.old;

import java.io.IOException;

import com.rapidminer.RapidMiner;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleReader;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.ModelApplier;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.io.ExampleSource;
import com.rapidminer.operator.learner.Learner;
import com.rapidminer.tools.OperatorService;

public class ExampleOperatorsInvoking {

	
	public static void main(String [] args) {
	try {
		
		System.setProperty("rapidminer.init.operators", "./cfg/operators.xml");
	RapidMiner. init ();
	// learn
	Operator exampleSource = OperatorService .createOperator(ExampleSource.class);
	exampleSource.setParameter(" attributes ",
	"/path/to/your/training data .xml");
	IOContainer container = exampleSource.apply(new IOContainer());
	ExampleSet exampleSet = container.get(ExampleSet.class);
	// here the string based creation must be used since the J48 operator
	// do not have an own class ( derived from the Weka library ).
	Learner learner = (Learner)OperatorService .createOperator("J48");
	Model model = learner. learn (exampleSet);
	// loading the test set (plus adding the model to result container )
	Operator testSource = 	OperatorService .createOperator(ExampleSource.class);
	testSource .setParameter(" attributes ", "/path/to/your/test data.xml");
	container = testSource.apply(new IOContainer());
	container = container.append(model);
	// applying the model
	Operator modelApp =
	OperatorService .createOperator(ModelApplier.class );
	container = modelApp.apply(container);
	// print results
	ExampleSet resultSet = container.get(ExampleSet.class);
//	Attribute predictedLabel = resultSet.getPredictedLabel ();
//	ExampleReader reader = resultSet.getExampleReader();
//	while (reader .hasNext()) {
//	System.out. println (reader .next (). getValueAsString( predictedLabel ));
//	}
	
	} catch (OperatorCreationException e) {
	System.err . println ("Cannot create operator:" + e.getMessage());
	} catch (OperatorException e) {
	System.err . println ("Cannot create model: " + e.getMessage());
	}
	}
	
	
}
