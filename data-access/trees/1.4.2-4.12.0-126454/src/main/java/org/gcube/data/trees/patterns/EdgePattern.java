/**
 * 
 */
package org.gcube.data.trees.patterns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Nodes;

/**
 * A {@link Pattern} over the {@link Edge}s of a {@link Node}.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement
@XmlType(propOrder={"label","pattern","condition"})
public abstract class EdgePattern implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	
	@XmlElementRefs({
			@XmlElementRef(type=TreePattern.class),
			@XmlElementRef(type=CutTreePattern.class),
			@XmlElementRef(type=NumPattern.class),
			@XmlElementRef(type=BoolPattern.class),
			@XmlElementRef(type=TextPattern.class),
			@XmlElementRef(type=DatePattern.class),
			@XmlElementRef(type=CalendarPattern.class),
			@XmlElementRef(type=URIPattern.class),
			@XmlElementRef(type=AnyPattern.class)
	}) 
	private Pattern pattern;
	
	@XmlElement(name="l") 
	private QName label;
	
	@XmlAttribute(name="cond") 
	private boolean condition;
	
	/**
	 * Marks the pattern as a condition.
	 */
	public void setAsCondition() {
		condition = true;
	}
	
	/**
	 * Unmarks the pattern as a condition.
	 */
	public void unsetAsCondition() {
		condition = false;
	}
	
	/**
	 * Indicates whether the pattern is to be processed as a condition.
	 * @return <code>true</code> if it is, <code>false</code> otherwise.
	 */
	public boolean isCondition() {
		return condition;
	}
	
	EdgePattern(){} //here for deserialisation
	
	/**
	 * Creates an instance from a label and a pattern.
	 * 
	 * @param l the label.
	 * @param p the pattern.
	 */
	EdgePattern(QName l,Pattern p) {this.pattern=p;this.label=l;}
	
	/**
	 * Returns the target pattern.
	 * @return the pattern.
	 */
	public Pattern pattern() {return pattern;}
	
	/**
	 * Sets the target pattern.
	 * @param p the pattern.
	 */
	public void setPattern(Pattern p) {
		pattern=p;
	}
	/**
	 * Returns the label.
	 * @return the label.
	 */
	public QName label() {return label;}
	
	/**
	 * Indicates whether the pattern matches some of the edges in a given list.
	 * @param edges the list.
	 * @return <code>true</code> if it does, <code>false</code> otherwise.
	 */
	abstract boolean matches(List<Edge> edges);
	
	/**
	 * Returns the edges from a given list which match the pattern.
	 * @param edges the list.
	 * @return the matching edges.
	 * @throws Exception if an attempt to match an edge fails with an error.
	 */
	abstract List<Edge> prune(List<Edge> edges) throws Exception;
	
	//helper
	protected List<Edge> matchLabels(List<Edge> edges) {
		List<Edge> matches = new ArrayList<Edge>();
		for (Edge edge : edges)
			if (Nodes.matches(edge.label(),label()))
				matches.add(edge);
		return matches;
	}
	
	/**
	 * Returns the {@link Range} of the pattern.
	 * @return the range.
	 */
	public abstract Range range();
	
	/**
	 * Returns the name of the pattern.
	 * @return the name
	 */
	public abstract String name();
	
	/**{@inheritDoc}*/
	@Override public String toString() {
		return (isCondition()?"[COND]":"")+name()+" "+label+":"+pattern;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (condition ? 1231 : 1237);
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result
				+ ((pattern == null) ? 0 : pattern.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EdgePattern))
			return false;
		EdgePattern other = (EdgePattern) obj;
		if (condition != other.condition)
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		return true;
	}
	
	/**
	 * The minimum and maximum number of {@link Edge}s that may match a given {@link EdgePattern}.
	 * @author Fabio Simeoni
	 *
	 */
	public static class Range {
		
		private int min;
		private int max;
		
		/**
		 * VCreates an instance with a minimum and maximum value.
		 * @param min the minimum value
		 * @param max the maximum value
		 */
		Range(int min, int max) {
			this.min=min;this.max=max;
		}
		
		/**
		 * Returns the minimum number of {@link Edge}s that may match a given {@link EdgePattern}.
		 * @return the minimum number.
		 */
		public int max() {
			return max;
		}
		
		/**
		 * Returns the maximum number of {@link Edge}s that may match a given {@link EdgePattern}. 
		 * @return the maximum number.
		 */
		public int min() {
			return min;
		}
		
		/**
		 * Returns <code>true</code> if this range is included in another.
		 * @param other the other range
		 * @return <code>true</code> if this range is included in the other, <code>false</code> otherwise
		 */
		public boolean includes(Range other) {
			return min<=other.min && max>=other.max;
		}
	}

}
