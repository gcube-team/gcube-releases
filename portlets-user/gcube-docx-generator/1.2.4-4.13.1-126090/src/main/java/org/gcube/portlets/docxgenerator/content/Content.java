package org.gcube.portlets.docxgenerator.content;

/**
 * 
 * Interface for implementing concrete content types. A concrete content class
 * encapsulates docx4j basic building blocks and makes them easier to use.
 * 
 * @author Luca Santocono
 * 
 */
public interface Content extends Comparable<Content> {

	/**
	 * Returns the fundamental building block of this Content. Useful to insert
	 * the Content in the docx word package.
	 * 
	 * @return the fundamental building block, usually a P.
	 */
	Object getContent();

	/**
	 * Sets the position of the content in the docx document. Useful to sort all
	 * the contents and to guarantee that the original order is maintained in
	 * the generated docx document.
	 * 
	 * @param position
	 *            The position of the content that is going to be inserted in
	 *            the docx document
	 */
	void setPosition(int position);

	/**
	 * Returns the position of the content in the generated docx document.
	 * 
	 * @return The position of the content as Integer.
	 */
	int getPosition();

	/**
	 * Specifies that for this content an entry in the TOC should be created.
	 */
	void setToBeToclinked();

	/**
	 * Tells weather an entry should be generated in the TOC for the inserted
	 * Content.
	 * 
	 * @return true if this content should be inserted in the TOC, false
	 *         otherwise.
	 */
	boolean isToBeToclinked();

	/**
	 * Builds progressively the entry to be inserted in the TOC
	 * 
	 * @param tocEntry
	 *            String that is going to be appended to the TOC entry
	 */
	void addToTocEntry(final String tocEntry);

	/**
	 * Gets the TOC entry that is going to be inserted in the TOC of the word
	 * document.
	 * 
	 * @return the TOC entry
	 */
	String getTocEntry();

	/**
	 * Sets the number of tabs to insert before the TOC entry
	 * 
	 * @param numtabs
	 *            Number of tabs to indent.
	 */
	void setTocTabs(int numtabs);

	/**
	 * Gets the number of tabs to insert before the TOC entry.
	 * 
	 * @return Number of tabs to indent.
	 */
	int getTocTabs();

	/**
	 * Gets the bookmark number. Each bookmark in Word has a unique number.
	 * 
	 * @return the number of this 
	 */
	int getBookmarkNumber();

}
