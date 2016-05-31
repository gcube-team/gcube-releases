/**
 * 
 */
package org.gcube.portlets.widgets.guidedtour.resources;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.gcube.portlets.widgets.guidedtour.client.steps.GCUBETemplate1Text1ImageML;
import org.gcube.portlets.widgets.guidedtour.client.steps.GCUBETemplate1Text2ImageML;
import org.gcube.portlets.widgets.guidedtour.client.steps.GCUBETemplate2Text2ImageML;
import org.gcube.portlets.widgets.guidedtour.client.steps.TourStep;
import org.gcube.portlets.widgets.guidedtour.client.types.ThemeColor;
import org.gcube.portlets.widgets.guidedtour.client.types.VerticalAlignment;
import org.gcube.portlets.widgets.guidedtour.resources.client.GuidedTourResource;
import org.gcube.portlets.widgets.guidedtour.resources.model.GuidedTour;
import org.gcube.portlets.widgets.guidedtour.resources.model.Image;
import org.gcube.portlets.widgets.guidedtour.resources.model.Step;
import org.gcube.portlets.widgets.guidedtour.shared.TourLanguage;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.resources.ext.AbstractResourceGenerator;
import com.google.gwt.resources.ext.ResourceContext;
import com.google.gwt.resources.ext.ResourceGeneratorUtil;
import com.google.gwt.resources.ext.SupportsGeneratorResultCaching;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.StringSourceWriter;

/**
 * The QuickTourResource generator.
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 */
public class GuidedTourResourceGenerator extends AbstractResourceGenerator implements SupportsGeneratorResultCaching {

	/**
	 * Allowed template types.
	 */
	static Map<String,String> allowedTemplateTypes = new HashMap<String, String>();
	static {
		allowedTemplateTypes.put(GCUBETemplate1Text1ImageML.class.getSimpleName(), GCUBETemplate1Text1ImageML.class.getName());
		allowedTemplateTypes.put(GCUBETemplate1Text2ImageML.class.getSimpleName(), GCUBETemplate1Text2ImageML.class.getName());
		allowedTemplateTypes.put(GCUBETemplate2Text2ImageML.class.getSimpleName(), GCUBETemplate2Text2ImageML.class.getName());
	}

	protected Unmarshaller unmarshaller;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String createAssignment(TreeLogger logger, ResourceContext context, JMethod method) throws UnableToCompleteException {
		
		URL[] resources = ResourceGeneratorUtil.findResources(logger, context, method);

		if (resources.length != 1) {
			logger.log(TreeLogger.ERROR, "Exactly one resource must be specified", null);
			throw new UnableToCompleteException();
		}

		SourceWriter sw = new StringSourceWriter();

		URL mainResource = resources[0];
		// Convenience when examining the generated code.
		sw.println("// " + mainResource.toExternalForm());

		Map<TourLanguage, URL> localConfigurations = GuidedTourGeneratorUtil.findResources(logger, context, method, TourLanguage.values());

		GuidedTour mainConfiguration = loadConfiguration(logger, mainResource, TourLanguage.EN);
		List<GuidedTour> languageConfigurations = new LinkedList<GuidedTour>();
		for (Map.Entry<TourLanguage, URL> configuration:localConfigurations.entrySet()) {
			logger.log(TreeLogger.SPAM, "Found "+configuration.getKey()+" file: "+configuration.getValue());
			// Convenience when examining the generated code.
			sw.println("// " + configuration.getKey() + " " + configuration.getValue().toExternalForm());
			languageConfigurations.add(loadConfiguration(logger, configuration.getValue(), configuration.getKey()));
		}

		// Write the expression to create the subtype.
		sw.println("new " + GuidedTourResource.class.getName() + "() {");
		sw.indent();

		generateTourPropertiesMethod(logger, sw, mainConfiguration);

		generateGetStepsMethod(logger, sw, mainConfiguration, languageConfigurations);

		generateGetLanguageMethod(sw, localConfigurations.keySet());

		sw.println("public String getName() {");
		sw.indent();
		sw.println("return \"" + method.getName() + "\";");
		sw.outdent();
		sw.println("}");

		sw.outdent();
		sw.println("}");

		return sw.toString();
	}

	/**
	 * Generates the code for the tour properties getter.
	 * @param logger the generator logger.
	 * @param sw the source writer.
	 * @param mainConfiguration the main configuration resource.
	 * @throws UnableToCompleteException if an error occurs during the methods creation
	 */
	protected void generateTourPropertiesMethod(TreeLogger logger, SourceWriter sw, GuidedTour mainConfiguration) throws UnableToCompleteException
	{
		//title
		if (mainConfiguration.getTitle()==null) {
			logger.log(TreeLogger.ERROR, "Not title specified in main configuration file "+mainConfiguration.getSource().toExternalForm(), null);
			throw new UnableToCompleteException();
		}
		//String getTitle();
		sw.println("public String getTitle() {");
		sw.indent();
		sw.println("return \"%s\";", Generator.escape(mainConfiguration.getTitle()));
		sw.outdent();
		sw.println("}");


		//guide
		if (mainConfiguration.getGuide()==null) {
			logger.log(TreeLogger.WARN, "Not guide url specified in main configuration file "+mainConfiguration.getSource().toExternalForm(), null);
		}
		//String getGuide();
		sw.println("public String getGuide() {");
		sw.indent();
		sw.println("return \"%s\";", Generator.escape(mainConfiguration.getGuide()));
		sw.outdent();
		sw.println("}");


		//theme color
		if (mainConfiguration.getThemeColor()==null) {
			logger.log(TreeLogger.ERROR, "Not themecolor specified in main configuration file "+mainConfiguration.getSource().toExternalForm(), null);
			throw new UnableToCompleteException();
		}
		//ThemeColor getThemeColor();
		sw.println("public %s getThemeColor() {", ThemeColor.class.getName());
		sw.indent();
		try{
			ThemeColor theme = ThemeColor.valueOf(mainConfiguration.getThemeColor());
			sw.println("return %s.%s;", ThemeColor.class.getName(), theme.toString());

		}catch (Exception e)
		{
			StringBuilder errorMessage = new StringBuilder("Wrong value for themecolor tag in main configuration file ");
			errorMessage.append(mainConfiguration.getSource().toExternalForm());
			errorMessage.append("\nExpected values are: ");
			for (ThemeColor color:ThemeColor.values()) {
				errorMessage.append(color.toString());
				errorMessage.append(" ");
			}
			logger.log(TreeLogger.ERROR, errorMessage.toString(), e);
			throw new UnableToCompleteException();
		}
		sw.outdent();
		sw.println("}");
		
		//width
		if (mainConfiguration.getWidth()==null) {
			logger.log(TreeLogger.ERROR, "Not width attribute specified in main configuration file "+mainConfiguration.getSource().toExternalForm(), null);
			throw new UnableToCompleteException();
		}
		int width = 0;
		try{
			width = Integer.parseInt(mainConfiguration.getWidth());
		} catch(Exception e)
		{
			logger.log(TreeLogger.ERROR, "Wrong value for width attribute specified in main configuration file "+mainConfiguration.getSource().toExternalForm()+"\nExpected values are positive numbers", null);
			throw new UnableToCompleteException();
		}
		//int getWidth();
		sw.println("public int getWidth() {");
		sw.indent();
		sw.println("return %s;", String.valueOf(width));
		sw.outdent();
		sw.println("}");
		
		
		//height
		if (mainConfiguration.getHeight()==null) {
			logger.log(TreeLogger.ERROR, "Not height attribute specified in main configuration file "+mainConfiguration.getSource().toExternalForm(), null);
			throw new UnableToCompleteException();
		}
		int height = 0;
		try{
			height = Integer.parseInt(mainConfiguration.getHeight());
		} catch(Exception e)
		{
			logger.log(TreeLogger.ERROR, "Wrong value for height attribute specified in main configuration file "+mainConfiguration.getSource().toExternalForm()+"\nExpected values are positive numbers", null);
			throw new UnableToCompleteException();
		}
		//int getHeight();
		sw.println("public int getHeight() {");
		sw.indent();
		sw.println("return %s;", String.valueOf(height));
		sw.outdent();
		sw.println("}");
		
		
		//useMask
		if (mainConfiguration.getUseMask()==null) {
			logger.log(TreeLogger.ERROR, "Not useMask attribute specified in main configuration file "+mainConfiguration.getSource().toExternalForm(), null);
			throw new UnableToCompleteException();
		}
		boolean useMask = false;
		try{
			useMask = Boolean.parseBoolean(mainConfiguration.getUseMask());
		} catch(Exception e)
		{
			logger.log(TreeLogger.ERROR, "Wrong value for useMask attribute specified in main configuration file "+mainConfiguration.getSource().toExternalForm()+"\nExpected values are: true, false", null);
			throw new UnableToCompleteException();
		}
		//boolean useMask();
		sw.println("public boolean useMask() {");
		sw.indent();
		sw.println("return %s;", String.valueOf(useMask).toLowerCase());
		sw.outdent();
		sw.println("}");
	}

	/**
	 * Generates the getLanguage method for the resource.
	 * @param sw the source writer
	 * @param foundLanguages the list of found languages
	 */
	protected void generateGetLanguageMethod(SourceWriter sw, Set<TourLanguage> foundLanguages)
	{
		sw.println("public "+TourLanguage.class.getName()+"[] getLanguages() {");
		sw.indent();
		StringBuilder sb = new StringBuilder();

		sb.append(TourLanguage.class.getName());
		sb.append(".");
		sb.append(TourLanguage.EN.toString());

		for (TourLanguage language:foundLanguages){
			sb.append(", ");
			sb.append(TourLanguage.class.getName());
			sb.append(".");
			sb.append(language.toString());
		}
		sw.println("return new "+TourLanguage.class.getName()+"[]{"+sb.toString()+"};");
		sw.outdent();
		sw.println("}");
	}

	/**
	 * Generates the code for steps instantiation.
	 * @param logger the generator logger.
	 * @param sw the source writer
	 * @param mainConfiguration the main configuration resource for the quick tour.
	 * @param languageConfigurations the language specific configuration for the quick tour.
	 * @throws UnableToCompleteException
	 */
	protected void generateGetStepsMethod(TreeLogger logger, SourceWriter sw, GuidedTour mainConfiguration, List<GuidedTour> languageConfigurations) throws UnableToCompleteException
	{
		sw.println("public %1$s<%2$s> getSteps() {", ArrayList.class.getName(), TourStep.class.getName());
		sw.indent();

		GuidedTourSourceGenerator sourceGenerator = new GuidedTourSourceGenerator(sw);

		//equal number of steps for all the configurations
		for (GuidedTour quickTour:languageConfigurations) {

			//steps tag are required in xml definition, so no check on null
			if (quickTour.getSteps().size()!=mainConfiguration.getSteps().size()) {
				logger.log(TreeLogger.ERROR, "Expected "+mainConfiguration.getSteps().size()+" steps in configuration file "+quickTour.getSource().toExternalForm()+" found "+quickTour.getSteps().size(), null);
				throw new UnableToCompleteException();
			}
			
			//we check if all steps have the same number of bodies
			for (int i = 0; i<mainConfiguration.getSteps().size(); i++) {
				Step mainStep = mainConfiguration.getSteps().get(i);
				Step languageStep = quickTour.getSteps().get(i);
				if (mainStep.getBodies().size()!=languageStep.getBodies().size()) {
					logger.log(TreeLogger.ERROR, "Expected "+mainStep.getBodies().size()+" bodies in step #"+(i+1)+" in configuration file "+quickTour.getSource().toExternalForm()+" found "+languageStep.getBodies().size(), null);
					throw new UnableToCompleteException();
				}
			}
		}

		sourceGenerator.initializeSteps();

		//we start generation for each step
		for (int stepIndex = 0; stepIndex< mainConfiguration.getSteps().size(); stepIndex++) {

			Step mainStep = mainConfiguration.getSteps().get(stepIndex);

			logger.log(TreeLogger.SPAM, mainStep.getTitle());
			logger.log(TreeLogger.SPAM, "#b "+mainStep.getBodies().size()+" #i "+mainStep.getImages().size());

			String type = null;
			try {
				type = calculateTemplateClassName(mainStep);

				logger.log(TreeLogger.SPAM, "guessed type: "+type);
			} catch(Exception e)
			{
				logger.log(TreeLogger.ERROR, "An error occurred calculating the template type for step #"+(stepIndex+1)+" specified in main configuration file "+mainConfiguration.getSource().toExternalForm(), e);
				throw new UnableToCompleteException();
			}
			
			String showTitle = Boolean.FALSE.toString().toLowerCase();
			
			if (mainStep.getShowTitle()!=null) {
				try{
					Boolean show = Boolean.parseBoolean(mainStep.getShowTitle());
					showTitle = String.valueOf(show).toLowerCase();
				} catch(Exception e)
				{
					logger.log(TreeLogger.ERROR, "Wrong showtitle attribute value for step #"+(stepIndex+1)+" specified in main configuration file "+mainConfiguration.getSource().toExternalForm()+"\nExpected values are: true, false", e);
					throw new UnableToCompleteException();
				}
			}
			
			sourceGenerator.startStep(type, showTitle);

			//TITLE
			sourceGenerator.startTitle();
			sourceGenerator.addTitle(mainConfiguration.getLanguage(), mainStep.getTitle());
			for (GuidedTour configuration:languageConfigurations) {
				String title = configuration.getSteps().get(stepIndex).getTitle();
				sourceGenerator.addTitle(configuration.getLanguage(), title);
			}
			sourceGenerator.endTitle();
			

			//BODIES
			for (int bodyIndex = 0; bodyIndex<mainStep.getBodies().size(); bodyIndex++) {
				
				String mainBody = mainStep.getBodies().get(bodyIndex);
				sourceGenerator.startBody();
				sourceGenerator.addBody(mainConfiguration.getLanguage(), mainBody);

				for (GuidedTour configuration:languageConfigurations) {
					Step step = configuration.getSteps().get(stepIndex);
					String body = step.getBodies().get(bodyIndex);
					sourceGenerator.addBody(configuration.getLanguage(), body);
				}
				sourceGenerator.endBody();
			}

			//IMAGES
			for (Image image:mainStep.getImages()) sourceGenerator.setStepImage(image.getUrl());

			sourceGenerator.endStep();

			//VerticalAlignment
			if (mainStep.getVerticalAlignment()!=null) {
				try{
					VerticalAlignment alignment = VerticalAlignment.valueOf(mainStep.getVerticalAlignment());
					sourceGenerator.setTextVerticalAlignment(alignment.toString());
				}catch(Exception e)
				{
					StringBuilder errorMessage = new StringBuilder("Wrong value for v-alignment attribute for step #"+(stepIndex+1)+" in main configuration file ");
					errorMessage.append(mainConfiguration.getSource().toExternalForm());
					errorMessage.append("\nExpected values are: ");
					for (VerticalAlignment alignment:VerticalAlignment.values()) {
						errorMessage.append(alignment.toString());
						errorMessage.append(" ");
					}
					logger.log(TreeLogger.ERROR, errorMessage.toString(), e);
					throw new UnableToCompleteException();
				}
			}
		}

		sourceGenerator.finalizeSteps();


		sw.outdent();
		sw.println("}");

	}

	/**
	 * Calculate the template class name using provided step information.
	 * @param step the step to analyze.
	 * @return the class name.
	 * @throws Exception if something go wrong in the calculation.
	 */
	protected String calculateTemplateClassName(Step step) throws Exception
	{
		String type = step.getTemplate();

		String expectedType = guessTemplateClassName(step);
		
		//no specification from the user, so we return the guessed one
		if (type == null) return expectedType;

		String mappedType = allowedTemplateTypes.get(type);

		//the specified template name is unknown 
		if (mappedType == null) {
			StringBuilder errorMessage = new StringBuilder("Unknown template type ");
			errorMessage.append(type);
			errorMessage.append("\nExpected template types are: ");
			for (String key:allowedTemplateTypes.keySet()) {
				errorMessage.append(key);
				errorMessage.append(" ");
			}
			throw new Exception(errorMessage.toString());
		}
		
		//the guessed template type and the specified template type are different 
		if (!mappedType.equals(expectedType)) {
			throw new Exception("There is no corrispondence between the number of bodies and images and the template type declare, expected type "+expectedType);
		}

		return mappedType;
	}

	/**
	 * Guesses the template class name from the step informations.
	 * @param step the step to analyze.
	 * @return the guessed class name.
	 * @throws Exception if something go wrong during the guessing phase.
	 */
	protected String guessTemplateClassName(Step step) throws Exception
	{
		int numBodies = step.getBodies().size();
		int numImages = step.getImages().size();

		if (numBodies <= 0 || numBodies > 2 
				|| numImages <= 0 || numImages > 2) {
			throw new Exception("The number of bodies and images have to be comprise between 1 and 2, found "+numBodies+" bodies and "+numImages+" images.");
		}

		switch (numBodies*10+numImages) {
			case 11: return GCUBETemplate1Text1ImageML.class.getName();
			case 12: return GCUBETemplate1Text2ImageML.class.getName();
			//case 21: 
			case 22: return GCUBETemplate2Text2ImageML.class.getName();				

			default:{
				throw new Exception("Impossible to match a corresponding template with the given number of bodies and images, found "+numBodies+" bodies and "+numImages+" images.");
			}
		}
	}

	/**
	 * Load the configuration from the specified url.
	 * @param logger the generator logger.
	 * @param configurationFile the configuration file url.
	 * @param language the configuration language.
	 * @return the loaded configuration.
	 * @throws UnableToCompleteException
	 */
	protected GuidedTour loadConfiguration(TreeLogger logger, URL configurationFile, TourLanguage language) throws UnableToCompleteException
	{
		try {
			Unmarshaller unm = getUnmarshaller();
			GuidedTour quickTour = (GuidedTour) unm.unmarshal(configurationFile);
			quickTour.setLanguage(language.toString());
			quickTour.setSource(configurationFile);
			return quickTour;
		}catch(JAXBException e)
		{
			logger.log(TreeLogger.ERROR, "An error occurred loading the configuration file  "+configurationFile.toExternalForm(), e);
			throw new UnableToCompleteException();
		}
	}

	/**
	 * Lazy initializator for the unmarshaller.
	 * @return
	 * @throws JAXBException
	 */
	protected Unmarshaller getUnmarshaller() throws JAXBException
	{
		if (unmarshaller == null) {
			JAXBContext context = JAXBContext.newInstance(GuidedTour.class);
			unmarshaller = context.createUnmarshaller();
		}
		return unmarshaller;

	}

}
