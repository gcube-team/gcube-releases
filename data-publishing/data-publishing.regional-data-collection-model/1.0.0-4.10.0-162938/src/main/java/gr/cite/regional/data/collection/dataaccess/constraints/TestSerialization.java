package gr.cite.regional.data.collection.dataaccess.constraints;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class TestSerialization {
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static void main(String[] args) throws IOException {
		//mapper.enableDefaultTyping();

		/*AttributeCodelistConstraint clConstraint = new AttributeCodelistConstraint();

		clConstraint.setTrigger(ConstraintDefinition.Trigger.ON_CHANGE);
		clConstraint.setErrorMessage("cl error");

		clConstraint.setField("field");

		clConstraint.setDisplayField("Name");
		clConstraint.setPersistField("Alpha3IsoCode");

		clConstraint.setCodelist("Country");*/

/*
		AttributeDatatypeConstraint range = new AttributeDatatypeConstraint();
		range.setTrigger(ConstraintDefinition.Trigger.ON_CHANGE);
		range.setErrorMessage("datatype error");

		range.setConstraintType("attribute");
		range.setAttributeConstraintType("datatypeConstraint");

		range.setDatatype(AttributeDatatypeConstraint.DataType.DATE);
		range.setMin("2");
		range.setMax("3");*/

		/*String rangeJson = mapper.writeValueAsString(range);
		System.out.println(rangeJson);
		ConstraintDefinition rangeConstraint = mapper.readValue(rangeJson, ConstraintDefinition.class);
		System.out.println(rangeConstraint);*/

		AttributeMandatoryConstraint mandatory = new AttributeMandatoryConstraint();
//		mandatory.setTrigger(ConstraintDefinition.Trigger.ON_CHANGE);
		mandatory.setErrorMessage("mandatory error");
		mandatory.setField("field");

		//mandatory.setConstraintType("attributeMandatory");
		//mandatory.setAttributeConstraintType("mandatory");

		String mandatoryJson = mapper.writeValueAsString(mandatory);
		ConstraintDefinition mandatoryConstraint = mapper.readValue(mandatoryJson, ConstraintDefinition.class);

		/*ConstraintDefinition constraints = new ConstraintDefinition();
		//constraints.getConstraint().add(clConstraint);
		constraints.getConstraint().add(range);
		//constraints.getConstraint().add(mandatory);
		//
		String json = mapper.writeValueAsString(constraints);

		System.out.println(json);

		constraints = mapper.readValue(json, ConstraintDefinition.class);

		System.out.println(((AttributeDatatypeConstraint) constraints.getConstraint().get(1)).getDatatype().toValue());
		constraints.getConstraint().forEach(System.out::println);*/
	}
}
