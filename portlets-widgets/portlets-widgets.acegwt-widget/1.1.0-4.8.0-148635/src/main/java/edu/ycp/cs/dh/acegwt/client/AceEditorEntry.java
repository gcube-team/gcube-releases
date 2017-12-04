package edu.ycp.cs.dh.acegwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorTheme;

/**
 * 
 * @author giancarlo
 * 
 *
 */
public class AceEditorEntry implements EntryPoint {
	
	private String text="Code Test....";
	
	
	public void onModuleLoad() {
	    AceEditor editor = new AceEditor();

	    editor.setWidth("800px");
	    editor.setHeight("600px");

	    RootPanel.get().add(editor);

	    editor.startEditor();
	    editor.setShowPrintMargin(false);
	    editor.setMode(AceEditorMode.R);
	    editor.setTheme(AceEditorTheme.ECLIPSE);
	    
	    
	    
	    editor.setText(text);
	    
	}
}
