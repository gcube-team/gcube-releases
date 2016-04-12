package org.gcube.portlets.user.searchportlet.client.utils;

public class SearchHelper
{
	public static native String anchor(String txt)/*-{
	 var x = $doc.getElementsByTagName("a");
	 for(i=0;i<x.length;i++)
	 {
	 if(x[i].innerHTML.toLowerCase().indexOf(txt,0) != -1)
	 return x[i].href;
	 }
	 return "";
	 }-*/;

}
