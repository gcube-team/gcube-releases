package org.gcube.contentmanagement.graphtools.tests.old;

import java.io.File;

import com.rapidminer.RapidMiner;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.ModelApplier;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorChain;
import com.rapidminer.operator.io.ModelLoader;
import com.rapidminer.tools.OperatorService;

public class TextTest {

	public static void main(String[] argv) throws Exception {
		
		String pluginDirString = new File("C:\\Dokumente und Einstellungen\\Mierswa\\Eigene Dateien\\workspace\\RMTextTest\\lib").getAbsolutePath();
		System.setProperty(RapidMiner.PROPERTY_RAPIDMINER_INIT_PLUGINS_LOCATION, pluginDirString);

		File wordListFile = new File(".");
		File modelFile = new File(".");

		RapidMiner.init();
		/*
		OperatorChain wvtoolOperator = (OperatorChain) OperatorService.createOperator(SingleTextInput.class);
		wvtoolOperator.setParameter("input_word_list", wordListFile.getAbsolutePath());
		
		wvtoolOperator.addOperator(OperatorService.createOperator(SimpleTokenizer.class));
		wvtoolOperator.addOperator(OperatorService.createOperator(PorterStemmer.class));
		
		Operator modelApplier = OperatorService.createOperator(ModelApplier.class);
		Operator modelLoader = OperatorService.createOperator(ModelLoader.class);
		modelLoader.setParameter(ModelLoader.PARAMETER_MODEL_FILE, modelFile.getAbsolutePath());
		IOContainer container = modelLoader.apply(new IOContainer());
		
		Model model = container.get(Model.class);
		*/
	}
}
