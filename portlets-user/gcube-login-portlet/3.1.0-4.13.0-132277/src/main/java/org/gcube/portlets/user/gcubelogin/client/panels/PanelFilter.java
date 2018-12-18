package org.gcube.portlets.user.gcubelogin.client.panels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.portlets.user.gcubelogin.client.commons.UIConstants;
import org.gcube.portlets.user.gcubelogin.shared.VO;
import org.gcube.portlets.user.gcubelogin.shared.VRE;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;


public class PanelFilter extends Composite {
	
	private AbsolutePanel main_panel;
	private static PanelFilter singleton = null;
	private MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
	private ArrayList<String> words = new ArrayList<String>();
	protected TextBox filterTextBox = null;
	int filterTexBoxPreviousLen = 0;
	
	
	public static PanelFilter get()
	{ 
		return singleton;
	}
	
	public PanelFilter() {
		super();
		Init();
		initWidget(main_panel);
		if (singleton == null) singleton = this;
		main_panel.setStyleName("p4");
	}

	private void Init() {
		
		HorizontalPanel hp = new HorizontalPanel();
		this.main_panel = new AbsolutePanel();
		// Define the oracle that finds suggestions
		this.oracle = new MultiWordSuggestOracle();
		this.filterTextBox = new TextBox();
		filterTextBox.setStyleName("textboxFilter");
		
		
	    // Create the suggest box
	    //final SuggestBox suggestBox = new SuggestBox(oracle, filterTextBox);
	    
	    filterTextBox.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				int code = event.getNativeKeyCode();
				//if textLen == 0 and you push backSpace do nothing
				if (! (filterTexBoxPreviousLen == 0 && code == UIConstants.BACK_SPACE_CODE) )
					refreshSuggestions();	
			}
		});
	    
	    
	    //suggestBox.ensureDebugId("cwSuggestBox");
	    HTML label = new HTML(UIConstants.filter_label + "&nbsp;&nbsp;");
	    label.setStyleName("nowrap_text font_family font_12");
	    hp.add(label);
	    hp.add(filterTextBox);
	    hp.setCellHorizontalAlignment(label, HasAlignment.ALIGN_RIGHT);
	    hp.setCellVerticalAlignment(label, HasAlignment.ALIGN_MIDDLE);
	    hp.setCellHorizontalAlignment(filterTextBox, HasAlignment.ALIGN_RIGHT);
	    hp.add(new HTML("&nbsp;&nbsp;"));
	    
	    this.main_panel.add(hp);
	    
	}

	public void add(String name) {
		this.oracle.add(name);
		this.words.add(name);
	}
	
	/**
	   * Create a VO tree with some data in it.
	 * @param result 
	   * 
	   * @return the new tree
	   */
	public void setVO(List<VO> result) {
		for (VO vo: result) {
			if (!vo.isRoot()) {
				addVOSection(vo.getVres());
				add(vo.getName());
			}
		}
	}
	/**
	   * Add a new VO
	   * 
	   * @param parent the parent {@link TreeItem} where the section will be added
	   * @param vres the list ov VRE
	   */
	  private void addVOSection(List<VRE> vres) {
		  for (VRE vre: vres) {
			  add(vre.getName());
		  }
	  }
	  
	  
	  private void refreshSuggestions() {
		  String text = filterTextBox.getText();
		  //loads the select items in the suggester just at the beginning
		  
		  if  (filterTexBoxPreviousLen > 0 && text.length() == 0) {
			  resetSelect();
		  } else {
			  showSuggestions(text);
		  }
		  filterTexBoxPreviousLen = text.length();
	  }
		
		/**
		 * called when the textbox over the select goes to len = 0
		 * or
		 * when moving removing or adding ones
		 *
		 */
		public void resetSelect() {
			filterTextBox.setText("");
			PanelBody.get().setFilter(false, null);
			PanelBody.get().refreshSize();
		}
		
		/**
		 * Show the given collection of suggestions.
		 * @param query
		 */

		public void showSuggestions(final String query) {

			final Callback callBack = new Callback() {
				@SuppressWarnings("unchecked")
				public void onSuggestionsReady(Request request, Response response) {
					Collection suggestions = (Collection) response.getSuggestions();
					//filterSelect(suggestions);
					PanelBody.get().setFilter(true, suggestions);
					PanelBody.get().refreshSize();
				}
			};
			oracle.requestSuggestions(new Request(query, 20), callBack);
		}
		
}
