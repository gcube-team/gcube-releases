package gr.cite.geoanalytics.logicallayer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Protocol {

	private String name;

	public String getName() {
		return name;
	}

	@Value("${gr.cite.logicallayer.protocol}")
	public void setName(String name) {
		this.name = name;
	}
	
}
