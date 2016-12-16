package org.gcube.informationsystem.registry.impl.preprocessing.filters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericResourceQuery;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.registry.impl.contexts.ServiceContext;
import org.gcube.informationsystem.registry.impl.preprocessing.filters.Filter.FILTEROPERATION;
import org.kxml2.io.KXmlParser;

public class FilterReader {

	private GCUBEGenericResource resource;
	
	protected GCUBELog logger = new GCUBELog(this);
	
	protected FilterReader() {
		
		if (!this.loadFiltersFromIS(ServiceContext.getContext().getInstance().getScopes().values())) 
			this.loadFiltersFromFile(ServiceContext.getContext().getFile("ResourceFilters.xml", false));
			
	}
	
	/**
	 * Loads the ISFilters resource from a file
	 * @param file the file
	 * 
	 * @return true if the resource is found and loaded, false otherwise
	 */
	private boolean loadFiltersFromFile(File file) {
		
		// get the resource implementation
		try {
			this.resource = GHNContext.getImplementation(GCUBEGenericResource.class);
			this.resource.load(new FileReader(file));
			this.parseFilters(this.resource.getBody());
			return true;
		} catch (Exception e) {			
			try {
				logger.error("Unable to load the ISFilters Resource from " + file.getAbsolutePath() ,e);
				throw new Exception("Unable to load the ISFilters Resource from " + file.getAbsolutePath());
			} catch (Exception e1) {
				logger.error("",e1 );
			}
		}
		
		return false;
	}
	
	/**
	 * Loads the ISFilters resource from the IS
	 * @param resource the resource to load
	 * 
	 * @return true if the resource is found and loaded, false otherwise
	 */
	private boolean loadFiltersFromIS(Collection<GCUBEScope> scopes) {
		try {
			ISClient client = GHNContext.getImplementation(ISClient.class);
			GCUBEGenericResourceQuery query = client.getQuery(GCUBEGenericResourceQuery.class);
			query.addAtomicConditions(new AtomicCondition("//SecondaryType", "ISFilters"));
			logger.trace(query.toString());
			for (GCUBEScope scope : scopes) {
				List<GCUBEGenericResource> results = client.execute(query, scope);
				if ((results != null) && (results.size() > 0)) {
					this.resource = results.get(0);				
					this.parseFilters(this.resource.getBody());								
					logger.debug("ISFilters Resource loaded from the IS");
					return true;
				} else
					logger.warn("Unable to load the ISFilters for "+ scope.toString() + " from the IS");
			}					
			
		} catch (Exception e) {logger.warn("ISFilters not available on the IS", e);}
		return false;
	}


	private void parseFilters(String body) throws Exception {
		KXmlParser parser = new KXmlParser();
		parser.setInput(new BufferedReader(new StringReader(body)));		
		loop: while (true) {
			try {
				switch (parser.next()) {
					case KXmlParser.START_TAG:
							if (parser.getName().equals("Filter")) {
								logger.debug("New filter found for " + parser.getAttributeValue("","resourceType"));
								FilterManager.getFilters(parser.getAttributeValue("", "resourceType")).add(this.parseFilter(parser));								
							} 
							break;
					case KXmlParser.END_TAG: if (parser.getName().equals("Filters")) break loop;
					case KXmlParser.END_DOCUMENT: break loop;
				}				
			} catch (Exception e) {
				logger.error("",e);
				throw new Exception ("Unable to parse the ISFilters body");
			}
		}
	}


	private Filter parseFilter(KXmlParser parser) throws Exception {

		Filter filter = new Filter();
		loop: while (true) {
			try {
				switch (parser.next()) {
					case KXmlParser.START_TAG: 
						if (parser.getName().equals("Target")) filter.setTarget(parser.nextText().trim());
						else if (parser.getName().equals("Value")) filter.setValue((parser.nextText().trim()));
						else if (parser.getName().equals("Operation")) filter.setOperation(FILTEROPERATION.valueOf(parser.nextText().trim()));
						else parser.nextText();//just skip the text
						break;
					case KXmlParser.END_TAG: if (parser.getName().equals("Filter")) break loop;
				}				
			} catch (Exception e) {
				throw new Exception ("Unable to parse at Filter");
			}
		}
		return filter;
	}


}
