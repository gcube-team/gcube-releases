package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThemeCreation {
	private static Logger logger = LoggerFactory.getLogger(ThemeCreation.class);

	private String name;
	private String template;
	

	public ThemeCreation() {
		super();
		logger.trace("Initialized default contructor for ThemeCreation");

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
}
