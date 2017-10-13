/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client;

import java.io.Serializable;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public enum WizardState implements Serializable {
	
	SOURCE_SELECTION,
	UNDERUPLOAD,
	UPLOADTERMINATED,
	UNDERCREATION,
	CREATIONTERMINATED;

}
