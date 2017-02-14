/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software 
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors 
 * "as is" and no expressed or 
 * implied warranty is given for its use, quality or fitness for a 
 * particular case.
 ****************************************************************************
 * Filename: GHNList.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a> 
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.serialization.types;

import java.util.List;
import java.util.Vector;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class GHNList {
	@XStreamImplicit(itemFieldName="GHN")
	private List<String> GHNs = new Vector<String>();

	public GHNList() {
		super();
	}
	
	public void setGHNs(List<String> GHNs) {
		this.GHNs = GHNs;
	}
	
	public List<String> getGHNs() {
		synchronized (this) {
			if (this.GHNs == null) {
				this.GHNs = new Vector<String>();
			}
		}
		return this.GHNs;
	}

	public void addGHN(String elem) {
		this.getGHNs().add(elem);
	}

}
