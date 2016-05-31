/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client;

import java.io.Serializable;

public enum WizardState implements Serializable {
	
	SOURCE_SELECTION,
	UNDERUPLOAD,
	UPLOADTERMINATED,
	UNDERCREATION,
	CREATIONTERMINATED;

}
