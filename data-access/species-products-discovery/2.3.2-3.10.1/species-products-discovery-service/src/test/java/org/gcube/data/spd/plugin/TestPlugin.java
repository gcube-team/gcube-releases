package org.gcube.data.spd.plugin;

import java.util.UUID;

import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.RepositoryInfo;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.capabilities.ExpansionCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPlugin extends AbstractPlugin {

	Logger logger = LoggerFactory.getLogger(TestPlugin.class); 
	
	String name;
	 
		
	public TestPlugin(String name) {
		super();
		this.name = name;
	}

	@Override
	public RepositoryInfo getRepositoryInfo() {
		return null;
	}

	@Override
	public void searchByScientificName(String word,
			ObjectWriter<ResultItem> writer, Condition... properties) {
		logger.debug("writing from "+name);
		for (int i=0;i<10;i++){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			writer.write(new ResultItem(UUID.randomUUID().toString(),word));
		}
	}

	@Override
	public String getRepositoryName() {
		return name;
	}

	@Override
	public String getDescription() {
		return name+" plugin description";
	}

	@Override
	public MappingCapability getMappingInterface() {
		// TODO Auto-generated method stub
		return new MappingCapability() {

			@Override
			public void getRelatedScientificNames(ObjectWriter<String> writer,
					String commonName) {
				writer.write("pippo"+name);
				writer.write("pluto2"+name);
				writer.write("pluto3"+name);
				writer.write("pluto4"+name);


			}
		};
	}

	@Override
	public ExpansionCapability getExpansionInterface() {
		return new ExpansionCapability() {

			@Override
			public void getSynonyms(ObjectWriter<String> writer, String scientifcName) {
				writer.write("pippopippo"+name);
				writer.write("pippopippopipppo"+name);
				writer.write("pippo4"+name);

			}
		};
	}

		
}
