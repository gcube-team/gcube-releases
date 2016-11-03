package org.gcube.datatransformation.datatransformationlibrary.model;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Maps the element and attribute names of the <tt>XML</tt> representation of the <tt>TransformationProgram</tt> to <tt>JAVA</tt> strings.
 * </p>
 */
public class XMLDefinitions {
	
	//Root Element
	/**
	 * The gcube resource element name.
	 */
	public static final String ELEMENT_resource="Resource";
	
	/**
	 * The profile element name.
	 */
	public static final String ELEMENT_profile="Profile";
	
	/**
	 * The resource id element name.
	 */
	public static final String ELEMENT_id="ID";
	
	/**
	 * The resource type element name.
	 */
	public static final String ELEMENT_type="Type";
	/**
	 * The generic resource type.
	 */
	public static final String VALUE_genericresource="GenericResource";
	
	/**
	 * The name element of the resource.
	 */
	public static final String ELEMENT_name="Name";
	/**
	 * The description element.
	 */
	public static final String ELEMENT_description="Description";
	
	/**
	 * The body element.
	 */
	public static final String ELEMENT_body="Body";
	
	/**
	 * The transformationUnit program element.
	 */
	public static final String ELEMENT_transformationProgram="gDTSTransformationProgram";
	
	//TransformationProgram
	//Transformer
	/**
	 * The transformer element.
	 */
	public static final String ELEMENT_transformer="Transformer";
	/**
	 * The class name of the program.
	 */
	public static final String ELEMENT_class="Class";
	/**
	 * The global parameters of the program.
	 */
	public static final String ELEMENT_globalprogramparams="GlobalProgramParameters";
	
	/**
	 * The location of the software of the program.
	 */
	public static final String ELEMENT_software="Software";
	/**
	 * The package element.
	 */
	public static final String ELEMENT_package="Package";
	/**
	 * The package id element.
	 */
	public static final String ELEMENT_packageID="PKGID";
	/**
	 * The location of the package.
	 */
	public static final String ELEMENT_packageLocation="Location";
	
	//IO
	/**
	 * The IO element.
	 */
	public static final String ELEMENT_IO="IO";
	/**
	 * The input element.
	 */
	public static final String ELEMENT_input="Input";
	/**
	 * The output element.
	 */
	public static final String ELEMENT_output="Output";
	/**
	 * The bridge element.
	 */
	public static final String ELEMENT_bridge="Bridge";
	/**
	 * The id attribute.
	 */
	public static final String ATTRIBUTE_IOID="id";
	
	//Transformation Units
	/**
	 * The transformationUnit units element.
	 */
	public static final String ELEMENT_transformationUnits="TransformationUnits";
	/**
	 * The transformationUnit unit element.
	 */
	public static final String ELEMENT_transformationUnit="TransformationUnit";
	/**
	 * The program parameters element.
	 */
	public static final String ELEMENT_transformationunitprogramparams="ProgramParameters";
	/**
	 * The id attribute.
	 */
	public static final String ATTRIBUTE_id="id";
	/**
	 * The is composite attribute.
	 */
	public static final String ATTRIBUTE_isComposite="isComposite";
	
	/**
	 * The sources element.
	 */
	public static final String ELEMENT_sources="Sources";
	/**
	 * The source element.
	 */
	public static final String ELEMENT_source="Source";
	/**
	 * The target element.
	 */
	public static final String ELEMENT_target="Target";

	//Content Type
	/**
	 * The content type element.
	 */
	public static final String ELEMENT_contenttype="ContentType";
	/**
	 * The mimetype element.
	 */
	public static final String ELEMENT_mimetype="Mimetype";
	/**
	 * The content type parameters element.
	 */
	public static final String ELEMENT_contenttypeparameters="Parameters";
	
	//Parameters
	/**
	 * The parameter element.
	 */
	public static final String ELEMENT_parameter="Parameter";
	/**
	 * The name attribute of a parameter.
	 */
	public static final String ATTRIBUTE_parameterName="name";
	/**
	 * The value attribute of a parameter.
	 */
	public static final String ATTRIBUTE_parameterValue="value";
	/**
	 * The is Optional attribute of a parameter.
	 */
	public static final String ATTRIBUTE_parameterIsOptional="isOptional";
	
	/**
	 * Any parameter value is supported. "any" is equal to "*"
	 */
	public static final String VALUE_any="*";
	/**
	 * This parameter is not set. if not optional it is obligatory to be set by the client.
	 */
	public static final String VALUE_notset="-";

	//Composition
	/**
	 * The target input element.
	 */
	public static final String ELEMENT_TargetInput="TargetInput";
	/**
	 * The target output element.
	 */
	public static final String ELEMENT_TargetOutput="TargetOutput";
	/**
	 * The target id attribute.
	 */
	public static final String ATTRIBUTE_IOTargetID="targetID";
	/**
	 * The reference to an io of the same tp element.
	 */
	public static final String ATTRIBUTE_IOThisID="thisID";
	/**
	 * The composition element.
	 */
	public static final String ELEMENT_composition="Composition";
	/**
	 * The external transformationUnit element.
	 */
	public static final String ELEMENT_extTransformation="ExtTransformationUnit";
	/**
	 * The reference to an external transformationUnit program.
	 */
	public static final String ATTRIBUTE_transformationprogramid="tpid";
	/**
	 * The reference to an external transformationUnit unit.
	 */
	public static final String ATTRIBUTE_transformationunitid="tuid";
	/**
	 * Any unbound parameters of the transformationUnit unit.
	 */
	public static final String ELEMENT_unboundparams="UnboundParameters";
}
