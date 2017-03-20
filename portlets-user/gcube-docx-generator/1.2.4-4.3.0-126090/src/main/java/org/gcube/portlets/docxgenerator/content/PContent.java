package org.gcube.portlets.docxgenerator.content;

import java.math.BigInteger;
import javax.xml.bind.JAXBException;

import org.docx4j.XmlUtils;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.CTTabStop;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.STTabJc;
import org.docx4j.wml.Tabs;
import org.docx4j.wml.Text;
import org.docx4j.wml.PPrBase.PStyle;

/**
 * P type content. Contains docx XML of the form, <br/>
 * <br/> {@code <w:p> ... </w:p>}
 * 
 * @author Luca Santocono
 * 
 */
public class PContent extends AbstractContent {

	private P p;
	private R run;
	private Text text;
	protected ObjectFactory factory;
	private RPr rpr;
	private PStyle pstyle;
	private PPr ppr;

	//private List<HyperlinkContent> links;

	/**
	 * Constructor.
	 * 
	 */
	public PContent() {
		factory = new ObjectFactory();
		//links = new ArrayList<HyperlinkContent>();
		p = factory.createP();
		ppr = factory.createPPr();
		
	}

	/**
	 * @return @see it.cnr.isti.docxgenerator.content.Content#getContent()
	 * @see it.cnr.isti.docxgenerator.content.AbstractContent#getContent()
	 */
	@Override
	public Object getContent() {
		return p;
	}

	/**
	 * Tells weather the content is empty.
	 * 
	 * @return True if the content is empty, false otherwise.
	 */
	public boolean isEmpty() {
		if (p == null)
			return true;
		return false;
	}

	/**
	 * 
	 * Creates a {@code <w:p> </w:p>} from a String.
	 * 
	 * @param text
	 *            The String that should be inserted between {@code <w:p>
	 *            </w:p>}.
	 */
	public void createContent(final String text) {
		factory = new ObjectFactory();
		p = factory.createP();
		R run = factory.createR();
		this.text = new Text();
		this.text.setValue(text);
		rpr = factory.createRPr();
		pstyle = factory.createPPrBasePStyle();
		pstyle.setVal("normal");
		run.getRunContent().add(this.text);
		run.setRPr(rpr);
		ppr = factory.createPPr();
		ppr.setPStyle(pstyle);
		p.setPPr(ppr);
		p.getParagraphContent().add(run);
	}

	/**
	 * 
	 * Sets the style of the P content. Valid styles are i.e. Heading1,
	 * Heading2, Heading3, Title.
	 * 
	 * @param style
	 *            The style name.
	 */
	public void setStyle(final String style) {
		pstyle = factory.createPPrBasePStyle();
		pstyle.setVal(style);
		// PPr ppr = factory.createPPr();
		ppr.setPStyle(pstyle);
		p.setPPr(ppr);
	}

	/**
	 * Configures this P content as Italic.
	 * 
	 * @param isItalic
	 *            If true the P content is configured as Italic.
	 */
	public void setItalic(boolean isItalic) {
		RPr rpr = factory.createRPr();
		BooleanDefaultTrue booleanDefaultTrue = factory
				.createBooleanDefaultTrue();
		booleanDefaultTrue.setVal(isItalic);
		rpr.setI(booleanDefaultTrue);
		run.setRPr(rpr);
	}

	/**
	 * Configures this P content as Bold.
	 * 
	 * @param isBold
	 *            If true the P content is configured as Bold.
	 */
	public void setBold(boolean isBold) {
		RPr rpr = factory.createRPr();
		BooleanDefaultTrue booleanDefaultTrue = factory
				.createBooleanDefaultTrue();
		booleanDefaultTrue.setVal(isBold);
		rpr.setB(booleanDefaultTrue);
		run.setRPr(rpr);
	}

	/**
	 * Bookmarks this P content.
	 * 
	 * @param bookmarkNum
	 *            The number of the bookmark.
	 */
	/*public void addBookmark(int bookmarkNum) {
		BookmarkStartContent bookmarkStart = new BookmarkStartContent(
				bookmarkNum, "_TOC" + bookmarkNum);

		bookmarkNumber = bookmarkNum;

		BookmarkEndContent bookmarkEnd = new BookmarkEndContent(bookmarkNum);
		p.getParagraphContent().add(bookmarkStart.getContent());
		p.getParagraphContent().add(bookmarkEnd.getContent());
	}*/

	/**
	 * Adds a text to the existing text in this P content by creating a new R.
	 * 
	 * @param text
	 *            The text to be added.
	 */
	public void addText(final String text) {
		run = factory.createR();
		Text itext = factory.createText();
		itext.setValue(text);
		run.getRunContent().add(itext);
		p.getParagraphContent().add(run);
	}

	/**
	 * Centers the text.
	 * 
	 */
	public void setCentered() {
		Jc jc = factory.createJc();
		jc.setVal(JcEnumeration.CENTER);
		ppr.setJc(jc);
		p.setPPr(ppr);
	}

	/**
	 * Aligns the text on the right.
	 * 
	 */
	public void setRight() {
		Jc jc = factory.createJc();
		jc.setVal(JcEnumeration.RIGHT);
		ppr.setJc(jc);
		p.setPPr(ppr);
	}

	/**
	 * Aligns the text on the left.
	 * 
	 */
	public void setLeft() {
		Jc jc = factory.createJc();
		jc.setVal(JcEnumeration.LEFT);
		ppr.setJc(jc);
		p.setPPr(ppr);
	}

	/**
	 * Aligns the text on both sides.
	 */
	public void setBoth() {
		Jc jc = factory.createJc();
		jc.setVal(JcEnumeration.BOTH);
		ppr.setJc(jc);
		p.setPPr(ppr);
	}

	/**
	 * Adds a new R content to this P content.
	 * 
	 * @param runcontent
	 *            The RContent to be added.
	 */
	public void addRun(final RContent runcontent) {
		p.getParagraphContent().add(runcontent.getContent());
	}

	/**
	 * Adds a hyperlink content to this P content.
	 * 
	 * @param hyperlinkcontent
	 *            The HyperlinkContent to be added.
	 */
	public void addHyperlink(final HyperlinkContent hyperlinkcontent) {
		p.getParagraphContent().add(hyperlinkcontent.getContent());
		//links.add(hyperlinkcontent);
	}

	/**
	 * Tells weather this P content contains hyperlinks.
	 * 
	 * @return True if this P content contains hyperlinks, false otherwise.
	 */
	/*public boolean hasLinks() {
		return !links.isEmpty();
	}*/

	/**
	 * Adds a content to this P content.
	 * 
	 * @param content
	 *            The content to be added.
	 */
	public void addContent(final Content content) {
		p.getParagraphContent().add(content.getContent());

	}

	/**
	 * Getter for the links field.
	 * 
	 * @return A list of hyperlinks contained in this P content.
	 */
	/*public List<HyperlinkContent> getLinks() {
		return links;
	}*/

	/**
	 * Setter for the links field.
	 * 
	 * @param links
	 *            A list of hyperlinks.
	 */
	/*public void setLinks(final List<HyperlinkContent> links) {
		this.links = links;
	}*/

	/**
	 * Adds a tab stop to this P content.
	 * 
	 * @param position
	 *            The position of the tabstop.
	 */
	public void addTabs(final int position) {
		Tabs tabs = factory.createTabs();
		CTTabStop tabStop = factory.createCTTabStop();
		// tabStop.setPos(BigInteger.valueOf(9628));
		tabStop.setPos(BigInteger.valueOf(position));
		// tabStop.setLeader(STTabTlc.DOT);
		tabStop.setVal(STTabJc.LEFT);
		tabs.getTab().add(tabStop);
		ppr.setTabs(tabs);
	}

	/**
	 * Adds a field code to this P content.
	 * 
	 * @param type
	 *            The type of the field code.
	 */
	public void addFldchar(final STFldCharType type) {
		RContent rcontent = new RContent();
		rcontent.addFldchar(type);
		this.addRun(rcontent);
	}

	/**
	 * Adds a custom XML string to this P content.
	 * 
	 * @param instrText
	 *            The XML code that should be added.
	 */
	public void addCustomXml(final String instrText) throws JAXBException {
		p.getParagraphContent().add(XmlUtils.unmarshalString(instrText));
	}

	/**
	 * Adds a P content inside this P content.
	 * 
	 * @param pcontent
	 *            The P content to be added.
	 */
	public void addP(final PContent pcontent) {
		p.getParagraphContent().add(pcontent.getContent());
	}
}
