/**
 * 
 */
package org.gcube.portlets.widgets.guidedtour.resources;

import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.portlets.widgets.guidedtour.client.steps.TourStep;
import org.gcube.portlets.widgets.guidedtour.client.types.VerticalAlignment;
import org.gcube.portlets.widgets.guidedtour.shared.TourLanguage;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class GuidedTourSourceGenerator {
	
	public static final String STEPS_VARIABLE = "steps";
	public static final String STEP_VARIABLE_PREFIX = "step";
	public static final String[] SETBODY_METHOD_NAMES = new String[]{"setStepBody", "setStepOtherBody"};
	public static final String[] SETIMAGE_METHOD_NAMES = new String[]{"setStepImage", "setStepOtherImage"};
	
	protected SourceWriter sw;
	protected int stepCounter = 0;
	protected int bodyCounter = 0;
	protected int imageCounter = 0;
	protected String currentStepVariable;
	
	/**
	 * @param sw
	 */
	public GuidedTourSourceGenerator(SourceWriter sw) {
		this.sw = sw;
	}
	
	public void initializeSteps()
	{
		sw.println("%1$s<%2$s> %3$s = new %1$s<%2$s>();", ArrayList.class.getName(), TourStep.class.getName(), STEPS_VARIABLE);
	}
	
	public void startStep(String stepClass, String showTitle)
	{
		bodyCounter = 0;
		imageCounter = 0;
		currentStepVariable = getStepVariable();
		sw.println("%s %s = new %s(%s) {", TourStep.class.getName(), currentStepVariable, stepClass, showTitle);
		sw.indent();
	}
	
	protected String getStepVariable()
	{
		stepCounter++;
		return STEP_VARIABLE_PREFIX+stepCounter;
	}

	public void startTitle()
	{
		sw.println("@Override");
		sw.println("public %s<%s, String> setStepTitle() {", HashMap.class.getName(), TourLanguage.class.getName());
		sw.indent();	
		sw.println("%1$s<%2$s, String> titles = new %1$s<%2$s, String>();", HashMap.class.getName(), TourLanguage.class.getName());
	}
	
	public void addTitle(String language, String title)
	{
		sw.println("titles.put(%s.%s, \"%s\");", TourLanguage.class.getName(), language, Generator.escape(title));
	}
	
	public void endTitle()
	{
		sw.println("return titles;");
		sw.outdent();
		sw.println("}");
	}
	
	public void setStepImage(String image)
	{
		sw.println("@Override");
		String methodName = SETIMAGE_METHOD_NAMES[imageCounter++%SETIMAGE_METHOD_NAMES.length];
		sw.println("public String %s() {",methodName);
		sw.indent();
		sw.println("return \"%s\";", Generator.escape(image));
		sw.outdent();
		sw.println("}");
	}
	
	public void startBody()
	{
		sw.println("@Override");
		String methodName = SETBODY_METHOD_NAMES[bodyCounter++%SETBODY_METHOD_NAMES.length];
		sw.println("public %s<%s, String> %s() {", HashMap.class.getName(), TourLanguage.class.getName(),methodName);
		sw.indent();
		sw.println("%1$s<%2$s, String> bodies = new %1$s<%2$s, String>();", HashMap.class.getName(), TourLanguage.class.getName());
	}
	
	public void addBody(String language, String body)
	{
		sw.println("bodies.put(%s.%s, \"%s\");", TourLanguage.class.getName(), language, Generator.escape(body));
	}
	
	public void endBody()
	{
		sw.println("return bodies;");
		sw.outdent();
		sw.println("}");
	}
	
	public void endStep()
	{
		sw.outdent();
		sw.println("};");
		sw.println("%s.add(%s);", STEPS_VARIABLE, currentStepVariable);
	}
	
	public void setTextVerticalAlignment(String alignment)
	{
		sw.println("%s.setTextVerticalAlignment("+VerticalAlignment.class.getName()+".%s);", currentStepVariable, alignment);
	}
	
	public void finalizeSteps()
	{
		sw.println("return steps;");
	}

}
