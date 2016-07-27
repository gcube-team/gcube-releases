package org.gcube.datatransformation.datatransformationlibrary.transformation.model;

import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.record.GenericRecord;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.datatransformation.datatransformationlibrary.DTSCore;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.model.Input;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.model.Output;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;

/**
 * Represents a data transformation description.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class TransformationDescription {
	/** The {@link Input} for the corresponding DataSource. */
	private Input input;

	/** The {@link Output} for the corresponding DataSink. */
	private Output output;

	private boolean createAndPublish = false;
	
	/** Description of Transformation */
	private ArrayList<TransformationPath> tPaths;

	/** Number of plans added */
	private int numOfPlansAdded = 0;

	private URI locator = null;
	
	private String returnedValue = null;
	private boolean initialized = false;
	private boolean timedOut = false;
	
	private Object syncInitialization = new Object();
	
	/**
	 * @return the returnedValue
	 */
	public String getReturnedValue() {
		synchronized (syncInitialization) {
			if (!initialized)
				try {
					syncInitialization.wait(DTSCore.TIMEOUT);
					if (!initialized)
						timedOut = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		return returnedValue;
	}

	/**
	 * @param returnedValue the returnedValue to set
	 */
	public void setReturnedValue(String returnedValue) {
		this.returnedValue = returnedValue;
		synchronized (syncInitialization) {
			initialized = true;
			syncInitialization.notify();
			if (timedOut) {
				if (output.getOutputType().endsWith("RS2") || output.equals("GRS2")){
					try {
						new ForwardReader<GenericRecord>(URI.create(returnedValue)).close();
					} catch (GRS2ReaderException e) {}
				}
			}
		}
	}

	/**
	 * @param input
	 * @param output
	 */
	public TransformationDescription(Input input, Output output) {
		this.tPaths = new ArrayList<TransformationPath>();
		this.input = input;
		this.output = output;
	}

	/**
	 * @param input
	 * @param output
	 * @param createAndPublish
	 */
	public TransformationDescription(Input input, Output output, boolean createAndPublish) {
		this(input, output);
		this.createAndPublish = createAndPublish;
	}

	/**
	 * @return the input
	 */
	public Input getInput() {
		List<Parameter> params = new ArrayList<Parameter>();
		for (TransformationPath tPath : tPaths) {
			List<Parameter> ctParams = tPath.gettPath().get(0).getSources().get(0).getContentType().getContentTypeParameters();
			for(Parameter param : ctParams)
				params.add(param);
		}
		
		if (input.getInputParameters() != null)
			for (Parameter param : input.getInputParameters())
				params.add(param);

		input.setInputParameters(params.toArray(new Parameter[params.size()]));

		return input;
	}

	/**
	 * @return the output
	 */
	public Output getOutput() {
		return output;
	}

	/**
	 * @return the createAndPublish
	 */
	public boolean isCreateAndPublish() {
		return createAndPublish;
	}

	/**
	 * @param createAndPublish the createAndPublish to set
	 */
	public void setCreateAndPublish(boolean createAndPublish) {
		this.createAndPublish = createAndPublish;
	}

	/**
	 * @return the sourceContentType
	 */
	public  ArrayList<ContentType> getContentTypes(int index) {
		return tPaths.get(index).getContentTypes();
	}

	/**
	 * @return the tUnits
	 */
	public ArrayList<TransformationUnit> getTransformationPath(int index) {
		return tPaths.get(index).gettPath();
	}

	
	/**
	 * @param transformationUnits
	 * @param contentTypes
	 */
	public void add(ArrayList<TransformationUnit> transformationUnits, ArrayList<ContentType> contentTypes) {
		tPaths.add(new TransformationPath(transformationUnits, contentTypes));
	}

	/**
	 * @return the numOfPlansAdded
	 */
	public int getNumOfPlansAdded() {
		return numOfPlansAdded;
	}

	/**
	 * @param numOfPlansAdded
	 *            the numOfPlansAdded to set
	 */
	public void setNumOfPlansAdded(int numOfPlansAdded) {
		this.numOfPlansAdded = numOfPlansAdded;
	}

	public boolean hasMorePlansToBeAdded() {
		return numOfPlansAdded < tPaths.size();
	}

	/**
	 * @return the locator
	 */
	public URI getLocator() {
		return locator;
	}

	/**
	 * @param locator the locator to set
	 */
	public void setLocator(URI locator) {
		this.locator = locator;
	}
	
}

class TransformationPath {
	private ArrayList<TransformationUnit> tPath;
	private ArrayList<ContentType> contentTypes;

	/**
	 * @param tPath
	 */
	public TransformationPath(ArrayList<TransformationUnit> tPath, ArrayList<ContentType> contentTypes) {
		super();
		this.tPath = tPath;
		this.contentTypes = contentTypes;
	}

	/**
	 * @return the tPath
	 */
	public ArrayList<TransformationUnit> gettPath() {
		return tPath;
	}

	/**
	 * @param tPath
	 *            the tPath to set
	 */
	public void settPath(ArrayList<TransformationUnit> tPath) {
		this.tPath = tPath;
	}

	/**
	 * @return the contentTypes
	 */
	public ArrayList<ContentType> getContentTypes() {
		return contentTypes;
	}

	/**
	 * @param contentTypes the contentTypes to set
	 */
	public void setContentTypes(ArrayList<ContentType> contentTypes) {
		this.contentTypes = contentTypes;
	}
}