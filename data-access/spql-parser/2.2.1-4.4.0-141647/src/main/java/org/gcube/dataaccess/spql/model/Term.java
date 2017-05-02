/**
 * 
 */
package org.gcube.dataaccess.spql.model;

import java.util.List;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class Term {
	
	protected TermType type;
	protected List<String> words;
	
	protected ExpandClause expandClause;
	protected ResolveClause resolveClause;
	protected UnfoldClause unfoldClause;
	
	/**
	 * @param type
	 */
	public Term(TermType type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public TermType getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(TermType type) {
		this.type = type;
	}
	
	/**
	 * @return the words
	 */
	public List<String> getWords() {
		return words;
	}
	
	/**
	 * @param words the words to set
	 */
	public void setWords(List<String> words) {
		this.words = words;
	}

	/**
	 * @return the expandClause
	 */
	public ExpandClause getExpandClause() {
		return expandClause;
	}

	/**
	 * @param expandClause the expandClause to set
	 */
	public void setExpandClause(ExpandClause expandClause) {
		this.expandClause = expandClause;
	}

	/**
	 * @return the resolveClause
	 */
	public ResolveClause getResolveClause() {
		return resolveClause;
	}

	/**
	 * @param resolveClause the resolveClause to set
	 */
	public void setResolveClause(ResolveClause resolveClause) {
		this.resolveClause = resolveClause;
	}

	public UnfoldClause getUnfoldClause() {
		return unfoldClause;
	}

	public void setUnfoldClause(UnfoldClause unfoldClause) {
		this.unfoldClause = unfoldClause;
	}

	
	
}
