package org.gcube.portlets.docxgenerator.content;

import java.math.BigInteger;

import javax.xml.bind.JAXBElement;

import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTVerticalAlignRun;
import org.docx4j.wml.Color;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.STVerticalAlignRun;
import org.docx4j.wml.Text;
import org.docx4j.wml.U;
import org.docx4j.wml.UnderlineEnumeration;
import org.docx4j.wml.R.Tab;

/**
 * 
 * R type content. Contains docx XML of the form, <br/>
 * <br/> {@code <w:r> ... </w:r>}
 * 
 * @author Luca Santocono
 * 
 */
public class RContent extends AbstractContent {

	private R run;
	private RPr rpr;
	private ObjectFactory factory;
	private Text itext;

	public enum RContentVertAlign {
		SUBSCRIPT,
		SUPERSCRIPT
	}
	
	/**
	 * Constructor.
	 */
	public RContent() {
		factory = new ObjectFactory();
		run = factory.createR();
		rpr = factory.createRPr();
		run.setRPr(rpr);
	}

	/**
	 * Configures this R content as Italic.
	 * 
	 */
	public void setItalic() {
		BooleanDefaultTrue booleanDefaultTrue = factory
				.createBooleanDefaultTrue();
		booleanDefaultTrue.setVal(true);
		rpr.setI(booleanDefaultTrue);
		run.setRPr(rpr);
	}

	/**
	 * Configures this R content as Bold.
	 */
	public void setBold() {
		BooleanDefaultTrue booleanDefaultTrue = factory
				.createBooleanDefaultTrue();
		booleanDefaultTrue.setVal(true);
		rpr.setB(booleanDefaultTrue);
		run.setRPr(rpr);
	}

	/**
	 * Configures this R content as underlined.
	 */
	public void setUnderlined() {
		U underlineType = factory.createU();
		underlineType.setVal(UnderlineEnumeration.SINGLE);
		rpr.setU(underlineType);
		run.setRPr(rpr);
	}

	/**
	 * Configures this R content as strikethrough.
	 */
	public void setStrike() {
		BooleanDefaultTrue booleanDefaultTrue = factory
				.createBooleanDefaultTrue();
		booleanDefaultTrue.setVal(true);
		rpr.setStrike(booleanDefaultTrue);
		run.setRPr(rpr);
	}

	/**
	 * Set the vertAlign
	 * 
	 * @param val
	 * 			The value of the vertical alignment 
	 */
	public void setVertAlign(RContentVertAlign val) { 

		CTVerticalAlignRun vertAlign = new CTVerticalAlignRun();
		if(val == RContentVertAlign.SUBSCRIPT) {
			vertAlign.setVal(STVerticalAlignRun.SUBSCRIPT);
		} else {
			vertAlign.setVal(STVerticalAlignRun.SUPERSCRIPT);
		}
		rpr.setVertAlign(vertAlign);
		run.setRPr(rpr);
	}
	
	/**
	 * Sets the color of this R content.
	 * 
	 * @param colorName
	 *            The Name of the color.
	 */
	public void setColor(final String colorName) {
		Color color = factory.createColor();
		color.setVal(colorName);
		rpr.setColor(color);
		//run.setRPr(rpr);
	}

	/**
	 * Adds a text into this R content.
	 * 
	 * @param text
	 *            The text to be added.
	 */
	public void addText(final String text) {
		itext = factory.createText();
		itext.setValue(text);
		run.getRunContent().add(itext);
	}

	/**
	 * Tells if space should be preserved.
	 * 
	 */
	public void preserveSpace() {
		itext.setSpace("preserve");
	}

	/**
	 * Inserts an image into this R content.
	 * 
	 * @param inline
	 *            Contains the image to be added.
	 */
	public void insertImage(final Inline inline) {
		Drawing drawing = factory.createDrawing();
		drawing.getAnchorOrInline().add(inline);
		run.getRunContent().add(drawing);
	}

	/**
	 * Adds a page break to this R content.
	 */
	public void addPageBreak() {
		Br br = factory.createBr();
		br.setType(STBrType.PAGE);
		run.getRunContent().add(br);
	}

	/**
	 * Adds a new line to this R content.
	 */
	public void addNewLine() {
		Br br = factory.createBr();
		run.getRunContent().add(br);

	}

	/**
	 * Sets the font size of this R content.
	 * 
	 * @param value
	 *            The size of the font.
	 */
	public void setFontSize(final int value) {
		HpsMeasure hpsMeasure = factory.createHpsMeasure();
		hpsMeasure.setVal(BigInteger.valueOf(Integer.valueOf(value)));
		rpr.setSz(hpsMeasure);
		rpr.setSzCs(hpsMeasure);
		run.setRPr(rpr);
	}

	/**
	 * Adds a tab stop to this R content.
	 */
	public void addTab() {
		Tab tab = factory.createRTab();
		run.getRunContent().add(tab);
	}

	/**
	 * Adds a field code to this R content.
	 * 
	 * @param type
	 *            The type of the field code.
	 */
	public void addFldchar(STFldCharType type) {
		FldChar fldchar = factory.createFldChar();
		fldchar.setFldCharType(type);
		JAXBElement<FldChar> rfldchar = factory.createRFldChar(fldchar);
		run.getRunContent().add(rfldchar);
	}

	/**
	 * @return it.cnr.isti.docxgenerator.content.AbstractContent#getContent()
	 * @see it.cnr.isti.docxgenerator.content.AbstractContent#getContent()
	 */
	@Override
	public Object getContent() {
		return run;
	}
}
