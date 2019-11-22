package org.gcube.data.analysis.tabulardata.utils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.data.ResourceList;
import org.gcube.smartgears.context.application.ApplicationContext;

@Specializes
public class ResourceFinderSpecialized extends ResourceList {

	ApplicationContext context;
	
	@Inject
	public ResourceFinderSpecialized(ApplicationContext context){
	 	this.context = context;
	}
	
	
	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.cube.data.ResourceList#getStream(java.lang.String)
	 */
	@Override
	public InputStream getStream(String file) {
		return context.application().getClassLoader().getResourceAsStream(file);
	}




	/**
	 * for all elements of java.class.path get a Collection of resources Pattern
	 * pattern = Pattern.compile(".*"); gets all resources
	 * 
	 * @param pattern
	 *            the pattern to match
	 * @return the resources in the order they are found
	 */
	public Collection<String> getResourcesPath(
			final Pattern pattern){
		final ArrayList<String> retval = new ArrayList<String>();
		
		String path = context.application().getRealPath("WEB-INF")+File.separator+"lib";
						
		final String[] classPathElements = new File(path).list();
		if (classPathElements == null ) return Collections.emptyList(); 
		for(final String element : classPathElements){
			retval.addAll(getResources(path+File.separator+element, pattern));
		}
		return retval;
	}
}
