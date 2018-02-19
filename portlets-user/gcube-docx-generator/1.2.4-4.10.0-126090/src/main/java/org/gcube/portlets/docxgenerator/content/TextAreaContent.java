package org.gcube.portlets.docxgenerator.content;

import java.util.Vector;

public class TextAreaContent extends AbstractContent  {

	private Vector<PContent> listPContent;
	
	public TextAreaContent() {
		listPContent = new Vector<PContent>(); 
	}
	
	/**
	 * @return @see it.cnr.isti.docxgenerator.content.Content#getContent()
	 * @see it.cnr.isti.docxgenerator.content.AbstractContent#getContent()
	 */
	@Override
	public Object getContent() {
		return listPContent;
	}
	
	public void addPContent(PContent p){
		listPContent.addElement(p);
	}

}
