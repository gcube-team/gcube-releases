/**
 * 
 */
package org.gcube.data.oai.tmplugin;


import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.gcube.data.tmf.api.Property;
import org.gcube.data.tmf.impl.AbstractSource;


/**
 * @author lucio
 *
 */
public class OAISource extends AbstractSource {

	private static final long serialVersionUID = 1187888636624642253L;
	/**
	 * Creates an instance with a given identifer over given sets of a given repository.
	 */
	public OAISource(String id) {
		super(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Property> properties() {
		return null;
	}


	@Override
	public List<QName> types() {
		return Collections.singletonList(new QName("http://gcube-system.org/namespaces/data/oaiplugin","OAI"));
	}

	@Override
	public OAIReader reader() {
		return (OAIReader) super.reader();
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.tmf.impl.AbstractSource#cardinality()
	 */
	@Override
	public synchronized Long cardinality() {
		if (super.cardinality()==null)
			super.setCardinality((long) 0);
		return super.cardinality();
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.tmf.impl.AbstractSource#setCardinality(java.lang.Long)
	 */
	@Override
	public synchronized void setCardinality(Long cardinality) {
		super.setCardinality(cardinality);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.tmf.impl.AbstractSource#lastUpdate()
	 */
	@Override
	public synchronized Calendar lastUpdate() {
		if (super.lastUpdate()==null)
			super.setLastUpdate(Calendar.getInstance());
		return super.lastUpdate();
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.tmf.impl.AbstractSource#setLastUpdate(java.util.Calendar)
	 */
	@Override
	public synchronized void setLastUpdate(Calendar time) {
		super.setLastUpdate(time);
	}

	


}
