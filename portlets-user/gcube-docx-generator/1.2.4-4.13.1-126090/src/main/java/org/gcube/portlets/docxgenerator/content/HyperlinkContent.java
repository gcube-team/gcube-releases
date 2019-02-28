package org.gcube.portlets.docxgenerator.content;

import org.docx4j.XmlUtils;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.RStyle;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.Text;
import org.docx4j.wml.P.Hyperlink;

/**
 * Convenience class for creating a hyperlink in a docx document.
 * 
 * 
 * @author Luca Santocono
 * 
 */
public class HyperlinkContent extends AbstractContent {

	/**
	 * Field that represents the docx4j basic building block.
	 */
	private Hyperlink hyperlink;

	/**
	 * The url to which the hyperlink points to.
	 */
	private String url;

	/**
	 * Factory object to instanciate docx4j objects.
	 * 
	 */
	private ObjectFactory factory;

	/**
	 * Default constructor
	 * 
	 */
	public HyperlinkContent() {
		factory = new ObjectFactory();
		hyperlink = factory.createPHyperlink();
	}

	/**
	 * Adds the text to be shown as hyperlink.
	 * 
	 * @param text
	 *            Name of the hyperlink.
	 */
	public void addText(final String text) {
		R run = factory.createR();
		Text ptext = factory.createText();
		ptext.setValue(text);
		RPr ppr = factory.createRPr();
		RStyle rstyle = factory.createRStyle();
		rstyle.setVal("Hyperlink");
		ppr.setRStyle(rstyle);
		run.getRunContent().add(ptext);
		run.setRPr(ppr);
		hyperlink.getParagraphContent().add(run);
	}

	/**
	 * Adds an anchor to the Hyperlink.
	 * 
	 * @param anchor
	 *            The name of the anchor.
	 */
	public void addAnchor(final String anchor) {
		hyperlink.setAnchor(anchor);
	}

	/**
	 * Sets an ID to the hyperlink.
	 * 
	 * @param id
	 *            The ID of the hyperlink.
	 */
	public void setId(final String id) {
		hyperlink.setId(id);
	}

	/**
	 * Sets weather it should be taken track if a hyperlink was clicked or not.
	 * 
	 * @param history
	 *            True if the history should be tracked, false otherwise.
	 */
	public void setHistory(final boolean history) {
		hyperlink.setHistory(history);
	}

	/**
	 * Gets the URL represented by the hyperlink.
	 * 
	 * @return The URL to which the hyperlink points to.
	 * 
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the URL to which the hyperlink should point.
	 * 
	 * @param url
	 *            to which the hyperlink should point.
	 */
	public void setUrl(final String url) {
		this.url = url;
	}

	/**
	 * Adds an {@link RContent} to the hyperlink.
	 * 
	 * @param rcontent
	 *            to be added to the hyperlink.
	 */
	public void addRun(final RContent rcontent) {
		hyperlink.getParagraphContent().add(rcontent.getContent());
	}

	/**
	 * @return @see
	 *         it.cnr.isti.docxgenerator.content.AbstractContent#getContent()
	 * 
	 */
	@Override
	public Object getContent() {
		return hyperlink;
	}

	/**
	 * Adds a custom XML string to the hyperlink.
	 * 
	 * @param instrText
	 *            Custom XML string to be added.
	 */
	public void addCustomXml(final String instrText) throws Exception {
		hyperlink.getParagraphContent()
				.add(XmlUtils.unmarshalString(instrText));
	}

	/**
	 * Adds a <a
	 * href="http://office.microsoft.com/en-us/word/HP051862011033.aspx">Field
	 * code</a> to the hyperlink.
	 * 
	 * @param type
	 *            Type of the field code.
	 */
	public void addFldchar(final STFldCharType type) {
		RContent rcontent = new RContent();
		rcontent.addFldchar(type);
		this.addRun(rcontent);
	}

}
