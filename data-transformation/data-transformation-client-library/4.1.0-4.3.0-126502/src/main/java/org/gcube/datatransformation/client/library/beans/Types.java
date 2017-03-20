package org.gcube.datatransformation.client.library.beans;

import java.util.List;

public class Types {
	// REQUEST AND RESPONSE ELEMENTS

	// REQUESTS

	// For method transformData
	public static class TransformData {
		public Input input;
		public ContentType targetContentType;
		public Output output;
		public Boolean createReport;
	}

	// For method transformDataWithTransformationProgram
	public static class TransformDataWithTransformationProgram {
		public Input input;
		public String tpID;
		public ContentType targetContentType;
		public List<Parameter> tProgramUnboundParameters;
		public Output output;
		public Boolean createReport;
	}

	// For method transformDataWithTransformationUnit
	public static class TransformDataWithTransformationUnit {
		public List<Input> inputs;
		public String tpID;
		public String transformationUnitID;
		public ContentType targetContentType;
		public List<Parameter> tProgramUnboundParameters;
		public Output output;
		public Boolean filterSources;
		public Boolean createReport;
	}

	// For method findApplicableTransformationUnits
	public static class FindApplicableTransformationUnits {
		public ContentType sourceContentType;
		public ContentType targetContentType;
		public Boolean createAndPublishCompositeTP;
	}

	// For method findAvailableTargetContentTypes
	public static class FindAvailableTargetContentTypes {
		public ContentType sourceContentType;
	}

	// For method QueryTransformationPrograms
	public static class QueryTransformationPrograms {
		public String queryTransformationPrograms;
	}

	// For method EvaluateContentTypeByDataElementID
	public static class EvaluateContentTypeByDataElementID {
		public String evaluatorType;
		public List<String> dataElementIDs;
	}

	// RESPONSES

	// For method transformData
	public static class TransformDataResponse {
		public String output;
		public String reportEPR;
	}

	// For method transformDataWithTransformationProgram
	public static class TransformDataWithTransformationProgramResponse {
		public String output;
		public String reportEPR;
	}

	// For method transformDataWithTransformationUnit
	public static class TransformDataWithTransformationUnitResponse {
		public String output;
		public String reportEPR;
	}

	// For method findApplicableTransformationUnits
	public static class FindApplicableTransformationUnitsResponse {
		public List<TPAndTransformationUnit> TPAndTransformationUnitIDs;
	}

	// For method findAvailableTargetContentTypes
	public static class FindAvailableTargetContentTypesResponse {
		public List<ContentType> targetContentTypes;
	}

	// For method queryTransformationPrograms
	public static class QueryTransformationProgramsResponse {
		public String queryTransformationProgramsResponse;
	}

	// For method evaluateContentTypeByDataElementID
	public static class EvaluateContentTypeByDataElementIDResponse {
		public ArrayOfDataElementIDandContentType evaluateContentTypeByDataElementIDResponse;
	}

	// DTS Types

	public static class Input {
		public String inputType;
		public String inputValue;
		public List<Parameter> inputparameters;
	}

	public static class Output {
		public String outputType;
		public String outputValue;
		public List<Parameter> outputparameters;
	}

	public static class ContentType {
		public String mimeType;
		public List<Parameter> contentTypeParameters;
	}

	public static class Parameter {
		public String name;
		public String value;

		public Parameter() {
		}

		public Parameter(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

	public static class TPAndTransformationUnit {
		public String transformationProgramID;
		public String transformationUnitID;
	}

	public static class DataElementIDandContentType {
		public String dataElementID;
		public ContentType contentType;
	}

	public static class ArrayOfDataElementIDandContentType {
		public List<DataElementIDandContentType> dataElementIDAndContentType;
	}
}
