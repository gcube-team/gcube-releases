package org.gcube.portlets.user.statisticalalgorithmsimporter.server.generator;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.DataType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.GlobalVariables;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.IOType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InputOutputVariables;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AlgorithmGenerator {

	private static final String EXTENTION_JAVA = ".java";
	public static final Logger logger = LoggerFactory
			.getLogger(AlgorithmGenerator.class);

	private Project project;
	private HashMap<String, String> enumUUID;

	public AlgorithmGenerator(Project project) {
		super();
		this.project = project;
	}

	@Override
	public String toString() {
		return "AlgorithmGenerator [project=" + project + "]";
	}

	public Path createAlgorithm() throws StatAlgoImporterServiceException {
		try {
			Path tempFile = Files.createTempFile(project.getInputData()
					.getProjectInfo().getAlgorithmNameToClassName(),
					EXTENTION_JAVA);

			List<String> lines = createJavaCode();
			Files.write(tempFile, lines, Charset.defaultCharset(),
					StandardOpenOption.WRITE);
			logger.debug(tempFile.toString());
			return tempFile;

		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(),
					e);
		}

	}

	private List<String> createJavaCode() {
		String mainScriptRelativePath = retrieveMainScriptRelativePath();
		String packageUrl = retrievePackageUrl();
		enumUUID = new HashMap<>();
		ArrayList<String> code = new ArrayList<>();

		code.addAll(Arrays
				.asList("package org.gcube.dataanalysis.executor.rscripts;",
						"",
						"import java.io.File;",
						"import java.util.ArrayList;",
						"import java.util.LinkedHashMap;",
						"import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;",
						"import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;",
						"import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;",
						"import org.gcube.dataanalysis.executor.rscripts.generic.GenericRScript;",
						"import org.gcube.dataanalysis.ecoengine.utils.DynamicEnum;",
						"import java.lang.reflect.Field;", "", "public class "
								+ project.getInputData().getProjectInfo()
										.getAlgorithmNameToClassName()
								+ " extends GenericRScript {"));

		for (GlobalVariables globalVariable : project.getInputData()
				.getListGlobalVariables()) {
			if (globalVariable.getDataType().compareTo(DataType.ENUMERATED) == 0) {
				if (globalVariable.getDefaultValue() == null
						|| globalVariable.getDefaultValue().isEmpty()) {

				} else {
					String uuid = "" + UUID.randomUUID();
					uuid = uuid.replaceAll("-", "");
					String nameEnum = "opGV" + globalVariable.getId() + uuid;
					enumUUID.put("opGV" + globalVariable.getId(), nameEnum);

					code.add("");
					code.add("	static class " + nameEnum
							+ " extends DynamicEnum {");
					code.add("		public enum E" + nameEnum + " {};");
					code.add("		public Field[] getFields() {");
					code.add("			Field[] fields = E" + nameEnum
							+ ".class.getDeclaredFields();");
					code.add("			return fields;");
					code.add("		}");
					code.add("	}");

					/*
					 * code.add("	String[] "+ nameEnum + " = {"); String[]
					 * values = globalVariable.getDefaultValue().split( "\\|");
					 * if (values.length < 1) {
					 * 
					 * } else { for (int i = 0; i < values.length; i++) { if (i
					 * == values.length - 1) { code.add("		\"" + values[i] +
					 * "\""); } else { code.add("		\"" + values[i] + "\","); } }
					 * } code.add("	};");
					 */
					/*
					 * code.add("	public static enum opGV" +
					 * globalVariable.getId() + " {"); String[] values =
					 * globalVariable.getDefaultValue().split( "\\|"); if
					 * (values.length < 1) {
					 * 
					 * } else { for (int i = 0; i < values.length; i++) { String
					 * identifier = values[i].trim();
					 * identifier=identifier.replaceAll("\\s+","");
					 * 
					 * if (i == values.length - 1) { code.add("		" + identifier
					 * + "(\"" + values[i] + "\");"); } else { code.add("		" +
					 * identifier + "(\"" + values[i] + "\"),"); } } }
					 * 
					 * code.add("		"); code.add("		private final String id;");
					 * code.add("		"); code.add("		private opGV" +
					 * globalVariable.getId() + "(final String id) {");
					 * code.add("			this.id = id;"); code.add("		}");
					 * code.add("		"); code.add("		@Override");
					 * code.add("		public String toString() {");
					 * code.add("			return id;"); code.add("		}");
					 * code.add("		"); code.add("		public String getId() {");
					 * code.add("			return id;"); code.add("		}");
					 * code.add("		");
					 * 
					 * code.add("		public opIO" + globalVariable.getId() +
					 * " getIdentifier(String identifier){");
					 * code.add("			if(identifier==null|| identifier.isEmpty()){"
					 * ); code.add("				return null;"); code.add("			}");
					 * code.add("			"); code.add("			for(opIO" +
					 * globalVariable.getId() + " value:opIO" +
					 * globalVariable.getId() + ".values()){");
					 * code.add("				if(value.id.compareTo(identifier)==0){");
					 * code.add("					return value;"); code.add("				}");
					 * code.add("			}"); code.add("			return null;");
					 * code.add("		}"); code.add("		"); code.add("	}");
					 */
				}

			}

		}

		for (InputOutputVariables inputOutputVariable : project.getInputData()
				.getListInputOutputVariables()) {
			if (inputOutputVariable.getDataType()
					.compareTo(DataType.ENUMERATED) == 0) {
				if (inputOutputVariable.getDefaultValue() == null
						|| inputOutputVariable.getDefaultValue().isEmpty()) {

				} else {
					String uuid = "" + UUID.randomUUID();
					uuid = uuid.replaceAll("-", "");
					String nameEnum = "opIO" + inputOutputVariable.getId()
							+ uuid;
					enumUUID.put("opIO" + inputOutputVariable.getId(), nameEnum);

					code.add("");
					code.add("	static class " + nameEnum
							+ " extends DynamicEnum {");
					code.add("		public enum E" + nameEnum + " {};");
					code.add("		public Field[] getFields() {");
					code.add("			Field[] fields = E" + nameEnum
							+ ".class.getDeclaredFields();");
					code.add("			return fields;");
					code.add("		}");
					code.add("	}");

					/*
					 * code.add("	String[] opIO" + inputOutputVariable.getId() +
					 * " = {"); String[] values =
					 * inputOutputVariable.getDefaultValue() .split("\\|"); if
					 * (values.length < 1) {
					 * 
					 * } else { for (int i = 0; i < values.length; i++) { if (i
					 * == values.length - 1) { code.add("		\"" + values[i] +
					 * "\""); } else { code.add("		\"" + values[i] + "\","); } }
					 * } code.add("	};");
					 */

					/*
					 * code.add("	public static enum opIO"
					 * 
					 * + inputOutputVariable.getId() + " {"); String[] values =
					 * inputOutputVariable.getDefaultValue() .split("\\|"); if
					 * (values.length < 1) {
					 * 
					 * } else { for (int i = 0; i < values.length; i++) { String
					 * identifier = values[i].trim(); identifier =
					 * identifier.replaceAll("\\s+", ""); if (i == values.length
					 * - 1) { code.add("		" + identifier + "(\"" + values[i] +
					 * "\");"); } else { code.add("		" + identifier + "(\"" +
					 * values[i] + "\"),"); } } } code.add("		");
					 * code.add("		private final String id;"); code.add("		");
					 * code.add("		private opIO" + inputOutputVariable.getId() +
					 * "(final String id) {"); code.add("			this.id = id;");
					 * code.add("		}"); code.add("		"); code.add("		@Override");
					 * code.add("		public String toString() {");
					 * code.add("			return id;"); code.add("		}");
					 * code.add("		"); code.add("		public String getId() {");
					 * code.add("			return id;"); code.add("		}");
					 * code.add("		"); code.add("		public opIO" +
					 * inputOutputVariable.getId() +
					 * " getIdentifier(String identifier){");
					 * code.add("			if(identifier==null|| identifier.isEmpty()){"
					 * ); code.add("				return null;"); code.add("			}");
					 * code.add("			"); code.add("			for(opIO" +
					 * inputOutputVariable.getId() + " value:opIO" +
					 * inputOutputVariable.getId() + ".values()){");
					 * code.add("				if(value.id.compareTo(identifier)==0){");
					 * code.add("					return value;"); code.add("				}");
					 * code.add("			}"); code.add("			return null;");
					 * code.add("		}"); code.add("		"); code.add("	}");
					 */
				}

			}
		}

		code.addAll(Arrays.asList("", "	@Override",
				"	public String getDescription() {", "		return \""
						+ project.getInputData().getProjectInfo()
								.getAlgorithmDescription() + "\";", "	}", "",
				"	protected void initVariables(){", "		mainScriptName=\""
						+ mainScriptRelativePath + "\";", "		packageURL=\""
						+ packageUrl + "\";",
				"		environmentalvariables = new ArrayList<String>();"));

		for (GlobalVariables globalVariable : project.getInputData()
				.getListGlobalVariables()) {
			code.add("		environmentalvariables.add(\""
					+ globalVariable.getName() + "\");");
		}

		for (InputOutputVariables selVariable : project.getInputData()
				.getListInputOutputVariables()) {
			switch (selVariable.getIoType()) {
			case INPUT:
				code.add("		inputvariables.add(\"" + selVariable.getName()
						+ "\");");
				break;
			case OUTPUT:
				code.add("		outputvariables.add(\"" + selVariable.getName()
						+ "\");");
				break;
			default:
				break;

			}
		}

		code.add("	}");
		code.add("");
		code.add("	@Override");
		code.add("	protected void setInputParameters() {");
		createInputParameters(code);

		code.add("	}");
		code.add("");
		code.add("	@Override");
		code.add("	public StatisticalType getOutput() {");
		createOutputParameters(code);
		code.add("		PrimitiveType o = new PrimitiveType(LinkedHashMap.class.getName(), output, PrimitiveTypes.MAP, \"Output\", \"\");");
		code.add("		return o;");
		code.add("	}");
		code.add("}");
		return code;
	}

	private void createInputParameters(ArrayList<String> code) {

		for (InputOutputVariables selVariable : project.getInputData()
				.getListInputOutputVariables()) {
			if (selVariable.getIoType().compareTo(IOType.INPUT) == 0) {
				switch (selVariable.getDataType()) {
				case BOOLEAN:
					code.add("		inputs.add(new PrimitiveType(Boolean.class.getName(), null,PrimitiveTypes.BOOLEAN, \""
							+ selVariable.getName()
							+ "\", \""
							+ selVariable.getDescription()
							+ "\", \""
							+ selVariable.getDefaultValue() + "\"));");
					break;
				case DOUBLE:
					code.add("		inputs.add(new PrimitiveType(Double.class.getName(), null,PrimitiveTypes.NUMBER, \""
							+ selVariable.getName()
							+ "\", \""
							+ selVariable.getDescription()
							+ "\", \""
							+ selVariable.getDefaultValue() + "\"));");
					break;
				case ENUMERATED:
					String[] values = selVariable.getDefaultValue()
							.split("\\|");
					if (values.length > 0) {

						// TODO
						String nameEnum = enumUUID.get("opIO"
								+ selVariable.getId());

						code.add("		if (org.gcube.dataanalysis.executor.rscripts."
								+ project.getInputData().getProjectInfo()
										.getAlgorithmNameToClassName()
								+ "."
								+ nameEnum
								+ ".E"
								+ nameEnum
								+ ".values().length==0){");
						code.add("			" + nameEnum + " en = new " + nameEnum
								+ "();");

						for (String val : values) {
							code.add("			en.addEnum(org.gcube.dataanalysis.executor.rscripts."
									+ project.getInputData().getProjectInfo()
											.getAlgorithmNameToClassName()
									+ "."
									+ nameEnum
									+ ".E"
									+ nameEnum
									+ ".class, \"" + val + "\");");
						}
						code.add("		}");
						code.add("");
						code.add("		addEnumerateInput(org.gcube.dataanalysis.executor.rscripts."
								+ project.getInputData().getProjectInfo()
										.getAlgorithmNameToClassName()
								+ "."
								+ nameEnum
								+ ".E"
								+ nameEnum
								+ ".values(), \""
								+ selVariable.getName()
								+ "\", \""
								+ selVariable.getDescription()
								+ "\", \""
								+ values[0] + "\");");
						/*
						 * code.add(
						 * "		inputs.add(new PrimitiveType(Enum.class.getName(), opIO"
						 * + selVariable.getId() +
						 * ".values(),PrimitiveTypes.ENUMERATED, \"" +
						 * selVariable.getName() + "\", \"" +
						 * selVariable.getDescription() + "\", \"" + values[0] +
						 * "\"));");
						 */
					}
					break;
				case FILE:
					code.add("		inputs.add(new PrimitiveType(File.class.getName(), null,PrimitiveTypes.FILE, \""
							+ selVariable.getName()
							+ "\", \""
							+ selVariable.getDescription()
							+ "\", \""
							+ selVariable.getDefaultValue() + "\"));");

					break;
				case INTEGER:
					code.add("		inputs.add(new PrimitiveType(Integer.class.getName(), null,PrimitiveTypes.NUMBER, \""
							+ selVariable.getName()
							+ "\", \""
							+ selVariable.getDescription()
							+ "\", \""
							+ selVariable.getDefaultValue() + "\"));");
					break;
				case STRING:
					code.add("		inputs.add(new PrimitiveType(String.class.getName(), null,PrimitiveTypes.STRING, \""
							+ selVariable.getName()
							+ "\", \""
							+ selVariable.getDescription()
							+ "\", \""
							+ selVariable.getDefaultValue() + "\"));");

					break;
				default:
					break;

				}

			}
		}

	}

	/*
	 * PrimitiveTypes
	 * 
	 * STRING, NUMBER, ENUMERATED, CONSTANT, RANDOM, FILE, MAP, BOOLEAN, IMAGES
	 */

	private void createOutputParameters(ArrayList<String> code) {
		for (InputOutputVariables selVariable : project.getInputData()
				.getListInputOutputVariables()) {
			if (selVariable.getIoType().compareTo(IOType.OUTPUT) == 0) {
				switch (selVariable.getDataType()) {
				case BOOLEAN:
					code.add("		output.put(\""
							+ selVariable.getName()
							+ "\",new PrimitiveType(Boolean.class.getName(), new File(outputValues.get(\""
							+ selVariable.getName()
							+ "\")), PrimitiveTypes.BOOLEAN, \""
							+ selVariable.getName() + "\", \""
							+ selVariable.getName() + "\"));");
					break;
				case DOUBLE:
					code.add("		output.put(\""
							+ selVariable.getName()
							+ "\",new PrimitiveType(Double.class.getName(), new File(outputValues.get(\""
							+ selVariable.getName()
							+ "\")), PrimitiveTypes.NUMBER, \""
							+ selVariable.getName() + "\", \""
							+ selVariable.getName() + "\"));");
					break;
				case ENUMERATED:
					break;
				case FILE:
					code.add("		output.put(\""
							+ selVariable.getName()
							+ "\",new PrimitiveType(File.class.getName(), new File(outputValues.get(\""
							+ selVariable.getName()
							+ "\")), PrimitiveTypes.FILE, \""
							+ selVariable.getName() + "\", \""
							+ selVariable.getName() + "\"));");

					break;
				case INTEGER:
					code.add("		output.put(\""
							+ selVariable.getName()
							+ "\",new PrimitiveType(Integer.class.getName(), new File(outputValues.get(\""
							+ selVariable.getName()
							+ "\")), PrimitiveTypes.NUMBER, \""
							+ selVariable.getName() + "\", \""
							+ selVariable.getName() + "\"));");
					break;
				case STRING:
					code.add("		output.put(\""
							+ selVariable.getName()
							+ "\",new PrimitiveType(String.class.getName(), new File(outputValues.get(\""
							+ selVariable.getName()
							+ "\")), PrimitiveTypes.STRING, \""
							+ selVariable.getName() + "\", \""
							+ selVariable.getName() + "\"));");

					break;
				default:
					break;

				}

			}
		}
	}

	private String retrieveMainScriptRelativePath() {
		logger.debug("ProjectInfo: "+project);
		String projectPath = project.getProjectFolder().getFolder().getPath();
		String mainCodePath = project.getMainCode().getItemDescription()
				.getPath();
		logger.debug("ProjectPath: "+projectPath);
		logger.debug("MainCodePath: "+mainCodePath);
		
		String relativePath = project.getProjectFolder().getFolder().getName()
				+ mainCodePath.substring(projectPath.length());
		logger.debug("RelativePath:"+relativePath);
		return relativePath;

	}

	private String retrievePackageUrl() {
		String packageUrl = "";
		if (project.getProjectTarget() != null
				&& project.getProjectTarget().getProjectDeploy() != null
				&& project.getProjectTarget().getProjectDeploy()
						.getPackageProject() != null) {
			if (project.getProjectTarget().getProjectDeploy()
					.getPackageProject().getPublicLink() != null) {
				packageUrl = project.getProjectTarget().getProjectDeploy()
						.getPackageProject().getPublicLink();
			}
		}
		return packageUrl;
	}

}
