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
 * Filename: TupleEqualsTest.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.local.testsuite;

import org.gcube.vremanagement.resourcebroker.impl.support.types.Tuple;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class TupleEqualsTest {
	public static void main (String[] args) {
		Tuple<String> t1 = new Tuple<String>("elem1", "elem2", "elem3");
		Tuple<String> t2 = new Tuple<String>("elem2", "elem1", "elem3");
		Tuple<String> t3 = new Tuple<String>("elem1", "elem2", "elem3", "elem4");
		Tuple<String> t4 = new Tuple<String>("elem1", "elem2", "elem3");
		Tuple<Integer> t5 = new Tuple<Integer>(1, 2, 3, 4);

		System.out.println(t1 + " == " + t2 + ": " + t1.equals(t2));
		System.out.println(t1 + " == " + t3 + ": " + t1.equals(t3));
		System.out.println(t1 + " == " + t4 + ": " + t1.equals(t4));
		System.out.println(t1 + " == " + t5 + ": " + t1.equals(t5));
	}
}
