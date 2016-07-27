/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: Tuple.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.support.types;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * General purpose tuple representation.
 * A tuple is a sequence (or ordered list) of finite length.
 *
 * <pre>
 * Example:
 *
 * <b>1) <i>Creation</i></b>
 * // single typed tuple
 * Tuple&lt;Long&gt; nt = new Tuple&lt;Long&gt;(42L);
 *
 * // multi typed tuple
 * Tuple&lt;Object&gt; ot = new Tuple&lt;Object&gt;(&quot;Lars Tackmann&quot;,
 *    		&quot;Age&quot;, 26);
 *
 * <b>2) <i>Usage</i></b>
 * // get single element
 * Integer val = (Integer) ot.get(2);
 * // iterate tuple
 * for (Object o : ot)
 *   System.out.printf(&quot;'%s' &quot;, o.toString());
 * // print all elems
 * System.out.printf(&quot;Object tuple: %s\n&quot;, ot.toString());
 *
 *
 * <b>3) <i>Operations</i></b>
 * // The elements of two tuples a and b can be joined with
 * // union operation that returns a new tuple.
 * Tuple c = a.union (b);
 * </pre>
 *
 */
public class Tuple<T> implements Iterable<T>, Serializable {
	private static final long serialVersionUID = 5783359179069297888L;
	private List<T> content = new LinkedList<T>();

	/**
	 * @deprecated For serialization purpose use the other constructors
	 */
	public final List<T> getContent() {
		return content;
	}

	/**
	 * @deprecated For serialization purpose use the other constructors
	 */
	public final void setContent(final List<T> content) {
		this.content = content;
	}

	public Tuple() {
		super();
	}

	public Tuple(final T... args) {
		for (T t : args) {
			content.add(t);
		}
	}

	/**
	 * Appends elements inside a tuple.
	 */
	public final void append(final T... args) {
		if (content != null) {
			for (T t : args) {
				content.add(t);
			}
		}
	}

	@SuppressWarnings({ "unchecked" })
	public final Tuple<? extends T> union(final Tuple<? extends T> t) {
		Tuple<T> retval = new Tuple<T>();
		for (T elem : content) {
			retval.append(elem);
		}
		for (int i = 0; i < t.size(); i++) {
			retval.append(t.get(i));
		}
		return retval;
	}

	public final T get(final int index) {
		return content.get(index);
	}

	public final Iterator<T> iterator() {
		return content.iterator();
	}

	public final int size() {
		return content.size();
	}

	/**
	 * Compares two tuples.
	 * The comparison is applied to all the contained elements.
	 *
	 * @param obj the {@link Tuple} element to compare
	 * @return true if the number of contained elements is the same and
	 * all the elements are equals.
	 */
	@SuppressWarnings("unchecked")
	public final boolean equals(final Object obj) {
		if (!(obj instanceof Tuple<?>)) {
			return false;
		}
		Tuple<T> tuple = (Tuple<T>) obj;
		if (tuple.size() != this.content.size()) {
			return false;
		}
		Iterator<T> internalElems = this.content.iterator();
		for (T elem : tuple) {
			if (!elem.equals(internalElems.next())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public final int hashCode() {
		int retval = 0;
		Iterator<T> internalElems = this.content.iterator();
		while (internalElems.hasNext()) {
			retval += internalElems.next().hashCode();
		}
		return retval;
	}

	@Override
	public final String toString() {
		StringBuilder retval = new StringBuilder();
		for (Object o : content) {
			retval.append(o.toString() + "/");
		}
		return "(" + retval.substring(0, retval.length() - 1) + ")";
	}
}
