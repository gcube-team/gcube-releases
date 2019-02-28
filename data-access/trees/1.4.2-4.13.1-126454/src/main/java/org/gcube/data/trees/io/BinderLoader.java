package org.gcube.data.trees.io;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple implementation of {@link BinderHome} based on a {@link ServiceLoader}.
 * 
 * @author Fabio Simeoni
 *
 */
public class BinderLoader implements BinderHome {

	private static Logger log = LoggerFactory.getLogger(BinderLoader.class);
	
	/**
	 * {@inheritDoc}
	 */
	public synchronized Map<String,TreeBinder<?>> binders() {
		
		Map<String,TreeBinder<?>> binders = new HashMap<String, TreeBinder<?>>();
		
		@SuppressWarnings("all")
		ServiceLoader<TreeBinder<?>> loader = (ServiceLoader) ServiceLoader.load(TreeBinder.class);
		for (TreeBinder<?> binder : loader)
			try {
				checkBinder(binder);
				binders.put(binder.info().name(),binder);				
			}
			catch(Throwable error) {
				log.warn("problem loading tree binder "+error.getMessage());
			}
		
		return binders;
	}
	
	//helper
	private void checkBinder(TreeBinder<?> binder) throws Exception {
		
		BinderInfo info = binder.info();
		
		if (info==null)
			throw new Exception("malformed binder: info is null");
		
		if (info.name()==null || info.name().isEmpty())
			throw new Exception("malformed binder: name is null or empty");

		QName name = info.treeForm();
		if (name==null || name.getLocalPart()==null || name.getLocalPart().isEmpty())
			throw new Exception("malformed binder: tree form is null or empty");
		
		name = info.type();
		if (name==null || name.getLocalPart()==null || name.getLocalPart().isEmpty())
			throw new Exception("malformed binder: data type is null or empty");
		

	}
}
