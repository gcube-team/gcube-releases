package org.gcube.portlets.user.results.client.dialogBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author massimiliano.assante@isti.cnr.it
 * @author Valia Tsagkalidou, UoA v.tsagkalidou@di.uoa.gr
 *
 */
public class NoResultsPopup extends PopupPanel implements ClickListener {
	//private static String urlimg;
	/**
	 * @param autoHide auto hide
	 */
	public NoResultsPopup(String errorMsg, boolean autoHide, boolean showMessage) {

		super(autoHide);
		HTML msg = new HTML(setToDisplay(errorMsg, showMessage), false);
		setWidget(msg);
	}

		public void onClick(Widget sender) {
		hide();
	}

	/**
	 * @return inner html
	 */
	protected static String setToDisplay(String errorMsg, boolean showMessage) {
		String msg = (showMessage) ? getErrorMessage() : "No results were found";
		String url = anchor("search",1);
		if(url.equals(""))
			url = anchor("browse",0);
		return 
		"<center><table height=\"60\" width=\"250\" border=\"0\">"+
		"<tr>"+
		"<td height=\"30\" valign=\"middle\" align=\"center\">"+
		msg + " <div style=\"color:red;\">" + errorMsg +
		"</div></td></tr>"+
		//"<tr><td align=\"center\"><p><a href=\""+ url +"\"> back to search </a></p></td></tr>"+
		"</table></center>" ;
	}
	
	/**
	 * @param txt the text the should exist in the &lt;a&gt;&lt;/a&gt; statement 
	 * @param j d 
	 * @return the coresponding URL (form href attribute of "a" element
	 */
	public static native String anchor(String txt, int j)/*-{
	 var x = $doc.getElementsByTagName("a");
	 for(i=0;i<x.length;i++)
	 {
	 if(x[i].innerHTML.toLowerCase().indexOf(txt,0) != -1)
	 {
	 	if(j==0)
	 		return x[i].href;
	 	else
	 		j--;
	 }
	 }
	 return "";
	 }-*/;
	

	public static native String getErrorMessage()/*-{
	return $wnd.errorMessage;
		 }-*/;

}