package org.gcube.portlets.widgets.file_dw_import_wizard.client;
import java.io.File;
import java.util.logging.Level;

import org.fao.fi.comet.domain.species.tools.converters.dwca.cli.DWCAConverter;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.DWCAelaboration;

import com.liferay.portal.kernel.search.SearchException;


public class TestDWCConverter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
	

		DWCAelaboration converter= new DWCAelaboration("/home/angela/ASFIS-DWCA.zip", "./DWCAmain", "angela");
		converter.elaborations();
		System.out.println("***********END***************++");
		
		
	


	}

}
