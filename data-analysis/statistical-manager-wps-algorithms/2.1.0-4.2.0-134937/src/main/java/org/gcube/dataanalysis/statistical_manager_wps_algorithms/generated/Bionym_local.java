package org.gcube.dataanalysis.statistical_manager_wps_algorithms.generated;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import net.opengis.ows.x11.AllowedValuesDocument.AllowedValues;
import net.opengis.ows.x11.DomainMetadataType;
import net.opengis.wps.x100.CRSsType;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.LiteralInputType;
import net.opengis.wps.x100.LiteralOutputType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType.DataInputs;
import net.opengis.wps.x100.ProcessDescriptionType.ProcessOutputs;
import net.opengis.wps.x100.ProcessDescriptionsDocument;
import net.opengis.wps.x100.ProcessDescriptionsDocument.ProcessDescriptions;
import net.opengis.wps.x100.SupportedCRSsType;
import net.opengis.wps.x100.SupportedCRSsType.Default;
import net.opengis.wps.x100.SupportedComplexDataType;

import org.gcube.data.analysis.dataminermanagercl.shared.parameters.EnumParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ObjectParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.dataanalysis.statistical_manager_wps_algorithms.SMAlgorithmHarvest;
import org.gcube.dataanalysis.statistical_manager_wps_algorithms.utils.SMutils;
import org.n52.wps.io.GeneratorFactory;
import org.n52.wps.io.IGenerator;
import org.n52.wps.io.data.GenericFileDataConstants;
import org.n52.wps.io.data.IBBOXData;
import org.n52.wps.io.data.IComplexData;
import org.n52.wps.io.data.ILiteralData;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bionym_local extends SMAlgorithmHarvest {
	private static final Logger logger = LoggerFactory
			.getLogger(Bionym_local.class);

	private String filippinoAlgorithmId = "org.gcube.dataanalysis.statistical_manager_wps_algorithms.generated.Bionym_local";

	public Bionym_local() {
		super();
		// algorithmId =
		// "org.gcube.dataanalysis.statistical_manager_wps_algorithms.generated.Bionym_local";

		algorithmId = "org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.BIONYM_LOCAL";

	}

	@Override
	public boolean processDescriptionIsValid() {
		return true;
	}

	@Override
	protected ProcessDescriptionType initializeDescription() {

		return SelfInitializeDescription();
	}

	protected ProcessDescriptionType SelfInitializeDescription() {

		logger.info("Algorithm description: Bionym_local");
		ProcessDescriptionsDocument document = ProcessDescriptionsDocument.Factory
				.newInstance();
		ProcessDescriptions processDescriptions = document
				.addNewProcessDescriptions();
		ProcessDescriptionType processDescription = processDescriptions
				.addNewProcessDescription();
		processDescription.setStatusSupported(true);
		processDescription.setStoreSupported(true);
		processDescription.setProcessVersion("1.0.0");

		processDescription.addNewIdentifier().setStringValue(
				this.getClass().getName());
		processDescription.addNewTitle().setStringValue(filippinoAlgorithmId);
		String descr = null;
		try {
			descr = SMutils.getAlgorithmDescription(algorithmId);
		} catch (Exception e) {
			logger.error("Error retrieving algorithm description", e);
			e.printStackTrace();
			return document.getProcessDescriptions()
					.getProcessDescriptionArray(0);

		}
		logger.info("Algorithm description: " + descr);

		if (descr != null)
			processDescription.addNewAbstract().setStringValue(descr);
		List<String> identifiers = getInputIdentifiers();
		DataInputs dataInputs = null;
		if (identifiers.size() > 0) {
			dataInputs = processDescription.addNewDataInputs();
		}

		for (String identifier : identifiers) {
			logger.info("identifier: " + identifier);
			String defaultValue = null;
			List<String> paramValues = new ArrayList<String>();
			Class<?> inputDataTypeClass = LiteralStringBinding.class;
			Parameter parameter = defaultParameterValue.get(identifier);
			InputDescriptionType dataInput = dataInputs.addNewInput();

			if (parameter != null) {
				switch (parameter.getTypology()) {
				case COLUMN:
					break;
				case COLUMN_LIST:
					break;
				case DATE:
					break;
				case ENUM:
					EnumParameter enumParameter = (EnumParameter) parameter;
					defaultValue = enumParameter.getDefaultValue();
					paramValues = enumParameter.getValues();
					inputDataTypeClass = LiteralStringBinding.class;
					break;
				case FILE:
					break;
				case LIST:
					break;
				case OBJECT:
					ObjectParameter objectParameter = (ObjectParameter) parameter;
					defaultValue = objectParameter.getDefaultValue();
					if (objectParameter.getType().compareTo(
							Boolean.class.getName()) == 0) {
						paramValues.add("true");
						paramValues.add("false");
					} else {
						if (objectParameter.getValue() != null
								&& !objectParameter.getValue().isEmpty()) {
							paramValues.add(objectParameter.getValue());
						}
					}
					inputDataTypeClass = retrieveInputDataTypeClass(objectParameter);
					break;
				case TABULAR:
					break;
				case TABULAR_LIST:
					break;
				case TIME:
					break;
				case WKT:
					break;
				default:
					break;

				}

				String title = parameter.getDescription() + "; ";
				dataInput.addNewTitle().setStringValue(title);

				String possibleValue = "Suggested Value: " + defaultValue + " ";
				int i = 0;
				for (String s : paramValues) {
					if (i == 0)
						possibleValue = possibleValue + "- Possible Values : ";
					if (i != paramValues.size() - 1)
						possibleValue = possibleValue + s + "; ";
					else
						possibleValue = possibleValue + s;
					i++;

				}
				dataInput.addNewAbstract().setStringValue(possibleValue);

			}
			dataInput.setMinOccurs(getMinOccurs(identifier));
			dataInput.setMaxOccurs(getMaxOccurs(identifier));
			dataInput.addNewIdentifier().setStringValue(identifier);

			LiteralInputType literalData = dataInput.addNewLiteralData();

			int j = 0;
			AllowedValues values = null;
			for (String s : paramValues) {
				if (j == 0)
					values = literalData.addNewAllowedValues();
				values.addNewValue().setStringValue(s);
				j++;

			}
			if (j == 0)
				literalData.addNewAnyValue();

			if (defaultValue != null && !defaultValue.isEmpty())
				literalData.setDefaultValue(defaultValue);

			String inputClassType = "";

			Constructor<?>[] constructors = inputDataTypeClass
					.getConstructors();

			logger.debug("List of costructors: " + constructors);
			for (Constructor<?> constructor : constructors) {
				Class<?>[] parameters = constructor.getParameterTypes();
				if (parameters.length == 1) {
					inputClassType = parameters[0].getSimpleName();
				}
			}

			if (inputClassType.length() > 0) {
				DomainMetadataType datatype = literalData.addNewDataType();
				datatype.setReference("xs:" + inputClassType.toLowerCase());

			}

		}

		ProcessOutputs dataOutputs = processDescription.addNewProcessOutputs();
		List<String> outputIdentifiers = this.getOutputIdentifiers();
		for (String identifier : outputIdentifiers) {
			OutputDescriptionType dataOutput = dataOutputs.addNewOutput();

			dataOutput.addNewIdentifier().setStringValue(identifier);
			dataOutput.addNewTitle().setStringValue(identifier);
			dataOutput.addNewAbstract().setStringValue(identifier);

			Class<?> outputDataTypeClass = this.getOutputDataType(identifier);
			Class<?>[] interfaces = outputDataTypeClass.getInterfaces();

			for (Class<?> implementedInterface : interfaces) {

				if (implementedInterface.equals(ILiteralData.class)) {
					LiteralOutputType literalData = dataOutput
							.addNewLiteralOutput();
					String outputClassType = "";

					Constructor<?>[] constructors = outputDataTypeClass
							.getConstructors();
					for (Constructor<?> constructor : constructors) {
						Class<?>[] parameters = constructor.getParameterTypes();
						if (parameters.length == 1) {
							outputClassType = parameters[0].getSimpleName();
						}
					}

					if (outputClassType.length() > 0) {
						literalData.addNewDataType().setReference(
								"xs:" + outputClassType.toLowerCase());
					}

				} else if (implementedInterface.equals(IBBOXData.class)) {
					SupportedCRSsType bboxData = dataOutput
							.addNewBoundingBoxOutput();
					String[] supportedCRSAray = getSupportedCRSForBBOXOutput(identifier);
					for (int i = 0; i < supportedCRSAray.length; i++) {
						if (i == 0) {
							Default defaultCRS = bboxData.addNewDefault();
							defaultCRS.setCRS(supportedCRSAray[0]);
							if (supportedCRSAray.length == 1) {
								CRSsType supportedCRS = bboxData
										.addNewSupported();
								supportedCRS.addCRS(supportedCRSAray[0]);
							}
						} else {
							if (i == 1) {
								CRSsType supportedCRS = bboxData
										.addNewSupported();
								supportedCRS.addCRS(supportedCRSAray[1]);
							} else {
								bboxData.getSupported().addCRS(
										supportedCRSAray[i]);
							}
						}
					}

				} else if (implementedInterface.equals(IComplexData.class)) {
					logger.info("Output is complex");
					SupportedComplexDataType complexData = dataOutput
							.addNewComplexOutput();

					complexData
							.addNewDefault()
							.addNewFormat()
							.setMimeType(
									GenericFileDataConstants.MIME_TYPE_PLAIN_TEXT
											.toString());
					List<IGenerator> generators = GeneratorFactory
							.getInstance().getAllGenerators();
					List<IGenerator> foundGenerators = new ArrayList<IGenerator>();
					for (IGenerator generator : generators) {
						Class<?>[] supportedClasses = generator
								.getSupportedDataBindings();
						for (Class<?> clazz : supportedClasses) {
							if (clazz.equals(outputDataTypeClass)) {
								foundGenerators.add(generator);
							}

						}
					}

					// addOutputFormats(complexData, foundGenerators);

				}
			}
		}

		logger.info(document.getProcessDescriptions()
				.getProcessDescriptionArray(0).toString());

		return document.getProcessDescriptions().getProcessDescriptionArray(0);
	}
}