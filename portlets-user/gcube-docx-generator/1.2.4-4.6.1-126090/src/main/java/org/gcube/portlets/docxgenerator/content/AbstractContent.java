package org.gcube.portlets.docxgenerator.content;

/**
 * Abstract class for the {@linkplain Content} interface.
 * 
 * @author Luca Santocono
 * 
 */
public abstract class AbstractContent implements Content, Comparable<Content> {

	/**
	 * Field that tells weather for this Content a TOC entry should be created
	 * or not.
	 */
	private boolean toBeToclinked;

	/**
	 * Field that represents the entry to be used in the TOC of the generated
	 * docx document.
	 */
	private String tocEntry;

	/**
	 * Field that represents the position in the generated docx document.
	 */
	private int position;

	/**
	 * Field that represents the number of tabs used for identation.
	 */
	private int tocTabs;

	/**
	 * Field that represents the unique bookmarNumber.
	 */
	protected int bookmarkNumber;

	/**
	 * @return @see it.cnr.isti.docxgenerator.content.Content#getContent()
	 */
	@Override
	public abstract Object getContent();

	/**
	 * @return @see it.cnr.isti.docxgenerator.content.Content#getTocEntry()
	 */
	@Override
	public String getTocEntry() {
		return tocEntry;
	}

	/**
	 * @param tocEntry
	 *            String to be added to the TOC in the generated docx document.
	 * 
	 * 
	 * @see it.cnr.isti.docxgenerator.content.Content#addToTocEntry(java.
	 *      lang.String)
	 */
	@Override
	public void addToTocEntry(final String tocEntry) {
		if (this.tocEntry == null)
			this.tocEntry = tocEntry;
		else
			this.tocEntry += " " + tocEntry;
	}

	/**
	 * @return @see it.cnr.isti.docxgenerator.content.Content#isToBeToclinked()
	 */
	@Override
	public boolean isToBeToclinked() {
		return toBeToclinked;
	}

	/**
	 * @see it.cnr.isti.docxgenerator.content.Content#setToBeToclinked()
	 */
	@Override
	public void setToBeToclinked() {
		this.toBeToclinked = true;
	}

	/**
	 * @return @see it.cnr.isti.docxgenerator.content.Content#getPosition()
	 */
	@Override
	public int getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            The position
	 * @see it.cnr.isti.docxgenerator.content.Content#setPosition(int)
	 */
	@Override
	public void setPosition(final int position) {
		this.position = position;
	}

	/**
	 * @param content
	 *            that should be compared.
	 * 
	 * @return @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Content content) {
		return ((Integer) this.getPosition()).compareTo(content.getPosition());
	}

	/**
	 * @return @see
	 *         it.cnr.isti.docxgenerator.content.Content#getBookmarkNumber()
	 */
	@Override
	public int getBookmarkNumber() {
		return bookmarkNumber;
	}

	/**
	 * @return @see it.cnr.isti.docxgenerator.content.Content#getTocTabs()
	 */
	@Override
	public int getTocTabs() {
		return this.tocTabs;
	}

	/**
	 * @param numtabs
	 *            Number of tabs of indentation.
	 * @see it.cnr.isti.docxgenerator.content.Content#setTocTabs(int)
	 */
	@Override
	public void setTocTabs(final int numtabs) {
		this.tocTabs = numtabs;
	}

}
