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
 * Filename: SortableElement.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.support.types;

/**
 * Generic container for sortable elements.
 * Wraps generic elements inside it and is built up an index that
 * is used to apply sorting algorithms.
 *
 * <pre>
 * <b>Example:</b>
 * 	// This example applies sorting on {@link org.gcube.vremanagement.resourcebroker.impl.support.types.Tuple} elements
 * 	{@link java.util.TreeSet}&lt;SortableElement&lt;?&gt;&gt; sortingTable = new TreeSet&lt;SortableElement&lt;?&gt;&gt;();
 *	sortingTable.add(new SortableElement&lt;Tuple&lt;String&gt;&gt;(3, new Tuple&lt;String&gt;("Hello", "My element")));
 *	sortingTable.add(new SortableElement&lt;Tuple&lt;String&gt;&gt;(1, new Tuple&lt;String&gt;("I am an element", "My element")));
 *	sortingTable.add(new SortableElement&lt;Tuple&lt;String&gt;&gt;(2, new Tuple&lt;String&gt;("I am an element too", "My third element")));
 *	sortingTable.add(new SortableElement&lt;Tuple&lt;String&gt;&gt;(0, new Tuple&lt;String&gt;("I should be the first", "My first element")));
 *	sortingTable.add(new SortableElement&lt;Tuple&lt;String&gt;&gt;(7, new Tuple&lt;String&gt;("I should be the last", "Name", "surname")));
 * </pre>
 * <pre>
 * <b>Other usages (on demand sorting):</b>
 * 	{@link java.util.List}&lt;SortableElement&gt; sortableList = new Vector&lt;SortableElement&gt;();
 * 	{@link java.util.Collections#sort}(sortableList);
 * </pre>
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class SortableElement<V extends Comparable<V>, T> implements Comparable<SortableElement<V, T>> {
	protected T elem = null;
	protected V sortIdx = null;

	public SortableElement(final V sortIdx, final T elem) {
		super();
		this.elem = elem;
		this.sortIdx = sortIdx;
	}

	public V getSortIndex() {
		return this.sortIdx;
	}

	public final T getElement() {
		return this.elem;
	}

	@Override
	public final String toString() {
		return "idx: #" + this.getSortIndex() + " - " + this.getElement().toString();
	}

	public final int compareTo(final SortableElement<V, T> o) {
		return this.getSortIndex().compareTo(
				((SortableElement<V, ?>) o).getSortIndex());
	}

}
