package org.gcube.common.resources.gcore;

import static org.gcube.common.resources.gcore.Resources.*;
import static org.gcube.common.resources.gcore.TestUtils.*;
import static org.junit.Assert.*;
import java.util.List;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.Resource.Type;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;

public class GenericResourceTests {

	@Test
	public void bindGeneric() throws Exception {

		//unmarshal resource
		GenericResource generic = unmarshal(GenericResource.class, "generic.xml");

		//print it
		print(generic);

		//resource-specific tests
		assertEquals(Type.GENERIC,generic.type());
		
		XPathHelper helper = new XPathHelper(generic.profile().body());
		
		assertFalse(helper.evaluate("other").isEmpty());

		//ensure schema conformance
		validate(generic);

		//test for equality
		GenericResource clone = unmarshal(GenericResource.class, "generic.xml");
		
		assertEquals(generic,clone);
	}
	
	@Test
	public void bindTextGeneric() throws Exception {

		//unmarshal resource
		GenericResource generic = unmarshal(GenericResource.class, "textgeneric.xml");

		//print it
		print(generic);
		
		assertEquals("text",generic.profile().bodyAsString());
	}
	
	//helper
	private GenericResource minimalGeneric() {
		
		GenericResource generic = new GenericResource();
		
		generic.scopes().add("/some/scope");
		
		generic.newProfile().type("type").name("name").newBody();
		
		return generic;
	}
	
	@Test 
	public void buildMinimalGeneric() throws Exception {
		
		GenericResource generic = minimalGeneric();
		
		print(generic);
		
		validate(generic);
	}
	
	@Test 
	public void buildTextGeneric() throws Exception {
		
		GenericResource generic = minimalGeneric();
		
		generic.profile().newBody("<a>hello</a><b>world</b>");
		
		print(generic);
		
		validate(generic);
		
		generic.profile().newBody("mixed<a>hello</a>mixed");
		
		print(generic);
		
		validate(generic);
		
		generic.profile().newBody("freetext");
		
		print(generic);
		
		validate(generic);
	}
	
	@Test
	public void buildMaximalGeneric() throws Exception {
		
		GenericResource generic = minimalGeneric();
		
		generic.scopes().add("/some/other/scope");
		
		assertEquals(2,generic.scopes().size());
		
		generic.profile().description("desc");

		Element body = generic.profile().body();
		body.appendChild(body.getOwnerDocument().createElement("added"));
		
		print(generic);
		
		validate(generic);
	}
	
	@Test
	public void updateGeneric() throws Exception {
		
		GenericResource generic = unmarshal(GenericResource.class, "generic.xml");
		
		generic.scopes().add("/some/other/scope");
		
		generic.profile().description("descupdated").type("type");

		Element body = generic.profile().body();
		body.appendChild(body.getOwnerDocument().createElement("added"));
		
		print(generic);
		
		List<String> results = new XPathHelper(body).evaluate("added");
		
		Assert.assertEquals(1,results.size());
		
		validate(generic);
	}
}
