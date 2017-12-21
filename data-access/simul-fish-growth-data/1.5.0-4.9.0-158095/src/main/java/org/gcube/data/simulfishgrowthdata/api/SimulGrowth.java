package org.gcube.data.simulfishgrowthdata.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;

@Path("/")
public class SimulGrowth {

	@Context
	UriInfo uriInfo;

	@XmlRootElement
	class Test {
		String name;
		String id;

		public Test() {

		}

		public Test(String name, String id) {
			this.name = name;
			this.id = id;
		}
	}

	@GET
	@Path("/test/list")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON })
	public List<Test> test1() {
		try {
			List<Test> list = new ArrayList<Test>();
			list.add(new Test("foo", "1"));
			list.add(new Test("bar", "2"));
			return list;
			// List<?> list = new BroodstockQualityUtil().getBroodstockQualities();
			// Gson gson = new Gson();
			// return new Gson().toJson(list);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GET
	@Path("/test/string")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public String test2() {
		try {
			return new BroodstockQualityUtil().getBroodstockQualities().toString();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public String sayPlainTextHello() {
		return String.format("uriInfo [%s]", uriInfo.getAbsolutePath());
		// return String.format("Hello SimulGrowth [%s]", "foo");
	}

	@GET
	@Path("/positive/count")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public String CountPos() {
		return String.format("Count [%s]", 1);
	}

	@GET
	@Path("/count")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public String Count() {
		return String.format("Count [%s]", 12);
	}

	@GET
	@Path("/negative/count")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public String CountNeg() {
		return String.format("Count [%s]", -1);
	}

	static public class Foo {
		String foo;
		String bar;

		public Foo() {

		}

//		@JsonCreator
//		public Foo(@JsonProperty("foo") String foo, @JsonProperty("bar") String bar) {
//
//			this.foo = foo;
//			this.bar = bar;
//		}

		public String getFoo() {
			return foo;
		}

		public void setFoo(String foo) {
			this.foo = foo;
		}

		public String getBar() {
			return bar;
		}

		public void setBar(String bar) {
			this.bar = bar;
		}
	}

	@POST
	//@Path("/postme")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response testPut(Foo json) {
	//@Consumes(MediaType.TEXT_PLAIN)
	//public Response testPut(String json) {
		return Response.status(200).entity(String.format("passed ~~%s~~", json)).build();
	}

	/*-
		// This method is called if XML is request
		@GET
		@Produces(MediaType.TEXT_XML)
		public String sayXMLHello() {
			return "<?xml version=\"1.0\"?>" + "<hello> Hello SimulGrowth" + "</hello>";
		}
	
		// This method is called if HTML is request
		@GET
		@Produces(MediaType.TEXT_HTML)
		public String sayHtmlHello() {
			return "<html> " + "<title>" + "Hello SimulGrowth" + "</title>" + "<body><h1>" + "Hello SimulGrowth" + "</body></h1>"
					+ "</html> ";
		}
		*/
}
