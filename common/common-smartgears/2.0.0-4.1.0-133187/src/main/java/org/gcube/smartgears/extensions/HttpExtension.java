package org.gcube.smartgears.extensions;

import static org.gcube.common.events.impl.Utils.*;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.xml.bind.annotation.XmlAttribute;

import org.gcube.common.validator.annotations.NotEmpty;
import org.gcube.smartgears.context.application.ApplicationContext;

/**
 * An {@link ApplicationExtension} that implements the {@link HttpServlet} interface
 * 
 * @author Fabio Simeoni
 *
 */
public abstract class HttpExtension extends HttpServlet implements ApplicationExtension {

	private static final long serialVersionUID = 1L;

	/**
	 * Enumeration of HTTP methods.
	 *
	 */
	public static enum Method {
		GET, PUT, POST, HEAD, DELETE, OPTIONS, TRACE
	}

	@XmlAttribute @NotEmpty
	private String name;

	@XmlAttribute @NotEmpty
	private String mapping;

	private ApplicationContext context;

	protected HttpExtension() {}
	
	public HttpExtension(String name, String mapping) {
	
		valid("extension name",name);
		valid("extension mapping",name);
		
		name(name);
		mapping(mapping);
	}

	//extensions use init(context) instead
	public final void init() throws javax.servlet.ServletException {
	};

	@Override
	public void init(ApplicationContext context) throws Exception {
		this.context=context;
	}
	
	@Override
	public Set<String> excludes() {
		return new HashSet<String>(); //all managed by default
	}
	
	protected ApplicationContext context() {
		return context;
		
	}
	
	@Override
	public String name() {
		return name;
	}

	public void name(String name) {
		this.name = name;
	}

	@Override
	public String mapping() {
		return mapping;
	}

	public void mapping(String mapping) {
		this.mapping = mapping;
	}
}
