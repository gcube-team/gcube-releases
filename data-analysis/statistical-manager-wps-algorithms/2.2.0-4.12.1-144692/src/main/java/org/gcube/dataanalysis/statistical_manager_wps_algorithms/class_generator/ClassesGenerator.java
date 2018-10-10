package org.gcube.dataanalysis.statistical_manager_wps_algorithms.class_generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import org.gcube.dataanalysis.statistical_manager_wps_algorithms.utils.SMutils;



public class ClassesGenerator {
	private String classPackagePath = "src/main/java/org/gcube/dataanalysis/"
			+ "statistical_manager_wps_algorithms/generated";
	// private final String packageLocationPath = getClass().getClassLoader()
	// .getResource(".").getPath()+"/../../"+classPackagePath;
	private final String packageLocationPath = "/home/angela/workspaceEcl/statistical-manager-wps-algorithms/"
			+ classPackagePath;

	public ClassesGenerator() {
		
	}

	public void engine() {
		System.out.print(packageLocationPath);
		ArrayList<String> algorithms=new ArrayList<>();;
		try {
			algorithms = SMutils.getAlgorithmsId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (String algorithm : algorithms) {
			writeClass(algorithm);
		}

	}

	private String formatName(String algName) {
		String firstPart = algName.substring(0, 1).toUpperCase()
				+ algName.substring(1, algName.length() - 1).toLowerCase();
		return firstPart.replace(" ", "_");
	}

	public void writeClass(String idAlgorithm) {

		try {
			FileWriter fstream = new FileWriter(packageLocationPath + "/"
					+ formatName(idAlgorithm) + ".java");
			BufferedWriter output = new BufferedWriter(fstream);
			String className = formatName(idAlgorithm);
			output.write("package org.gcube.dataanalysis.statistical_manager_wps_algorithms.generated;");
			output.newLine();

			output.write(writeImport());
			output.newLine();

			output.write("public class " + className
					+ " extends SMAlgorithmHarvest  {");

			output.write("public " + formatName(idAlgorithm) + " (){");
			output.newLine();
			output.write("super();");
			output.newLine();

			output.write("algorithmId=\"" + idAlgorithm + "\";");
			output.newLine();
			
			output.write("}");
			output.newLine();
			output.write(getOvverideDescriptionIsValid());
			output.newLine();
			output.write(getOvverideProcessDescriptionType());
			output.newLine();

			output.write(getSelfInitializeDescription());
			output.newLine();

			output.write("}");
			output.close();
			fstream.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public String getOvverideDescriptionIsValid() {
		String method = "@Override public boolean processDescriptionIsValid() {return true;"
				+ "}";
		return method;
	}

	public String getOvverideProcessDescriptionType() {
		String method = "@Override protected ProcessDescriptionType initializeDescription()"
				+ " {return SelfInitializeDescription();}";
		return method;
	}

	public String getSelfInitializeDescription() {
		String method = "	protected ProcessDescriptionType SelfInitializeDescription() {"
				+ "		ProcessDescriptionsDocument document = ProcessDescriptionsDocument.Factory"
				+ ".newInstance();	"
				+ " ProcessDescriptions processDescriptions = document"
				+ ".addNewProcessDescriptions();	"
				+ " ProcessDescriptionType processDescription = processDescriptions"
				+ ".addNewProcessDescription();		"
				+ " processDescription.setStatusSupported(true);	"
				+ " processDescription.setStoreSupported(true);		"
				+ " processDescription.setProcessVersion(\"1.0.0\");	"
				+

				"processDescription.addNewIdentifier().setStringValue("
				+ "this.getClass().getName());	"
				+ "processDescription.addNewTitle().setStringValue(this.algorithmId);	"
				+ "String descr = SMutils.getAlgorithmDescription(this.algorithmId,"
				+ "this.scope);	"
				+ "System.out.println(\" Description:\" + descr);	"
				+

				"if (descr != null)	"
				+ "processDescription.addNewAbstract().setStringValue(descr);	"
				+ "List<String> identifiers = this.getInputIdentifiers();	"
				+ "DataInputs dataInputs = null;	"
				+ "if (identifiers.size() > 0) {	"
				+ "dataInputs = processDescription.addNewDataInputs();	"
				+ "}	"
				+

				"for (String identifier : identifiers) {	"
				+ "System.out.println(\" identifie\" + identifier);	"
				+ "Parameter parameter = defaultParameterValue.get(identifier);	"
				+

				"InputDescriptionType dataInput = dataInputs.addNewInput();	"
				+ "if (defaultParameterValue.containsKey(identifier)) {	"
				+ "String title = parameter.getDescription() + \"; \";	"
				+ "dataInput.addNewTitle().setStringValue(title);	"
				+

				"String possibleValue = \"Suggested Value: \"	"
				+ "+ parameter.getDefaultValue() + \" \"; 	"
				+ "int i = 0;	"
				+ "for (String s : parameter.getPossibleValues()) {	"
				+ "if (i == 0)	"
				+ "possibleValue = possibleValue + \"Possible Values : \";	"
				+ "if (i != parameter.getPossibleValues().size() - 1)	"
				+ "possibleValue = possibleValue + s + \"; \";	"
				+ "else	"
				+ "possibleValue = possibleValue + s;	"
				+ "i++;	"
				+

				"}	"
				+ "dataInput.addNewAbstract().setStringValue(possibleValue);	"
				+

				"}	"
				+ "dataInput.setMinOccurs(getMinOccurs(identifier));	"
				+ "dataInput.setMaxOccurs(getMaxOccurs(identifier));	"
				+ "dataInput.addNewIdentifier().setStringValue(identifier);	"
				+

				"LiteralInputType literalData = dataInput.addNewLiteralData();	"
				+

				"int j = 0;	"
				+ "AllowedValues values = null;	"
				+ "for (String s : parameter.getPossibleValues()) {	"
				+ "if (j == 0)	"
				+ "values = literalData.addNewAllowedValues();	"
				+ "values.addNewValue().setStringValue(s);	"
				+ "j++;	"
				+

				"}	"
				+ "if (j == 0)	"
				+ "literalData.addNewAnyValue();	"
				+

				"String inputClassType = \"\";	"
				+ "Class<?> inputDataTypeClass;	"
				+ "if (parameter.getType() != null)	"
				+ "inputDataTypeClass = parameter.getType();	"
				+

				"else	"
				+ "inputDataTypeClass = this.getInputDataType(identifier);	"
				+

				"Constructor<?>[] constructors = inputDataTypeClass"
				+ ".getConstructors();	"
				+ "for (Constructor<?> constructor : constructors) {	"
				+ "Class<?>[] parameters = constructor.getParameterTypes();	"
				+ "if (parameters.length == 1) {	"
				+ "inputClassType = parameters[0].getSimpleName();	"
				+ "}	"
				+ "}	"
				+

				"if (inputClassType.length() > 0) {	"
				+ "DomainMetadataType datatype = literalData.addNewDataType();	"
				+ "datatype.setReference(\"xs:\" + inputClassType.toLowerCase());	"
				+

				"}	"
				+

				"}	"
				+

				"ProcessOutputs dataOutputs = processDescription.addNewProcessOutputs();	"
				+ "List<String> outputIdentifiers = this.getOutputIdentifiers();	"
				+ "for (String identifier : outputIdentifiers) {	"
				+ "OutputDescriptionType dataOutput = dataOutputs.addNewOutput();	"
				+

				"dataOutput.addNewIdentifier().setStringValue(identifier);	"
				+ "dataOutput.addNewTitle().setStringValue(identifier);	"
				+ "dataOutput.addNewAbstract().setStringValue(identifier);	"
				+

				"Class<?> outputDataTypeClass = this.getOutputDataType(identifier);	"
				+ "Class<?>[] interfaces = outputDataTypeClass.getInterfaces();	"
				+

				"for (Class<?> implementedInterface : interfaces) {	"
				+

				"if (implementedInterface.equals(ILiteralData.class)) {	"
				+ "LiteralOutputType literalData = dataOutput"
				+ ".addNewLiteralOutput();	"
				+ "String outputClassType = \"\";	"
				+

				"Constructor<?>[] constructors = outputDataTypeClass"
				+ ".getConstructors();	"
				+ "for (Constructor<?> constructor : constructors) {	"
				+ "Class<?>[] parameters = constructor.getParameterTypes();	"
				+ "if (parameters.length == 1) {	"
				+ "outputClassType = parameters[0].getSimpleName();	"
				+ "}	"
				+ "}	"
				+

				"if (outputClassType.length() > 0) {	"
				+ "literalData.addNewDataType().setReference("
				+ "\"xs:\" + outputClassType.toLowerCase());	"
				+ "}	"
				+

				"} else if (implementedInterface.equals(IBBOXData.class)) {	"
				+ "SupportedCRSsType bboxData = dataOutput"
				+ ".addNewBoundingBoxOutput();	"
				+ "String[] supportedCRSAray = getSupportedCRSForBBOXOutput(identifier);	"
				+ "for (int i = 0; i < supportedCRSAray.length; i++) {	"
				+ "if (i == 0) {	"
				+ "Default defaultCRS = bboxData.addNewDefault();	"
				+ "defaultCRS.setCRS(supportedCRSAray[0]);	"
				+ "if (supportedCRSAray.length == 1) {	"
				+ "CRSsType supportedCRS = bboxData"
				+ ".addNewSupported();	"
				+ "supportedCRS.addCRS(supportedCRSAray[0]);	"
				+ "}	"
				+ "} else {	"
				+ "if (i == 1) {	"
				+ "CRSsType supportedCRS = bboxData"
				+ ".addNewSupported();	"
				+ "supportedCRS.addCRS(supportedCRSAray[1]);	"
				+ "} else {	"
				+ "bboxData.getSupported().addCRS("
				+ "supportedCRSAray[i]);	"
				+ "}	"
				+ "}	"
				+ "}	"
				+

				"} else if (implementedInterface.equals(IComplexData.class)) {	"
				+ "SupportedComplexDataType complexData = dataOutput"
				+ ".addNewComplexOutput();	"
				+

				"complexData"
				+ ".addNewDefault()"
				+ ".addNewFormat()"
				+ ".setMimeType("
				+ "GenericFileDataConstants.MIME_TYPE_PLAIN_TEXT"
				+ ".toString());"
				+ "List<IGenerator> generators = GeneratorFactory"
				+ ".getInstance().getAllGenerators();	"
				+ "List<IGenerator> foundGenerators = new ArrayList<IGenerator>();	"
				+ "for (IGenerator generator : generators) {	"
				+ "Class<?>[] supportedClasses = generator"
				+ ".getSupportedDataBindings();	"
				+ "for (Class<?> clazz : supportedClasses) {	"
				+ "if (clazz.equals(outputDataTypeClass)) {	"
				+ "foundGenerators.add(generator);	"
				+ "}	"
				+

				"}	"
				+ "}	"
				+

				// addOutputFormats(complexData, foundGenerators);

				"}	"
				+ "}	"
				+ "}	"
				+

				"System.out.println(document.getProcessDescriptions()	"
				+ ".getProcessDescriptionArray(0).toString());	"
				+

				"return document.getProcessDescriptions().getProcessDescriptionArray(0);	"
				+ "}";
		return method;
	}

	public String writeImport() {

		String importStr = "import java.lang.reflect.Constructor;"
				+ "import java.util.ArrayList;"
				+ "import java.util.List;"
				+ "import net.opengis.ows.x11.DomainMetadataType;"
				+ "import net.opengis.wps.x100.CRSsType;"
				+ "import net.opengis.wps.x100.InputDescriptionType;"
				+ "import net.opengis.wps.x100.LiteralInputType;"
				+ "import net.opengis.wps.x100.LiteralOutputType;"
				+ "import net.opengis.wps.x100.OutputDescriptionType;"
				+ "import net.opengis.wps.x100.ProcessDescriptionType;"
				+ "import net.opengis.wps.x100.SupportedCRSsType;"
				+ "import net.opengis.wps.x100.ProcessDescriptionType.DataInputs;"
				+ "import net.opengis.wps.x100.ProcessDescriptionType.ProcessOutputs;"
				+ "import net.opengis.wps.x100.ProcessDescriptionsDocument;"
				+ "import net.opengis.wps.x100.ProcessDescriptionsDocument.ProcessDescriptions;"
				+ "import net.opengis.wps.x100.SupportedCRSsType.Default;"
				+ "import net.opengis.wps.x100.SupportedComplexDataType;"
				+ "import org.gcube.dataanalysis.statistical_manager_wps_algorithms.SMAlgorithmHarvest;"
				+ "import org.gcube.dataanalysis.statistical_manager_wps_algorithms.parameters.Parameter;"
				+ "import org.gcube.dataanalysis.statistical_manager_wps_algorithms.utils.SMutils;"
				+ "import org.n52.wps.io.GeneratorFactory;"
				+ "import org.n52.wps.io.IGenerator;"
				+ "import org.n52.wps.io.data.GenericFileDataConstants;"
				+ "import org.n52.wps.io.data.IBBOXData;"
				+ "import org.n52.wps.io.data.IComplexData;"
				+ "import org.n52.wps.io.data.ILiteralData;";
		return importStr;
	}

}
