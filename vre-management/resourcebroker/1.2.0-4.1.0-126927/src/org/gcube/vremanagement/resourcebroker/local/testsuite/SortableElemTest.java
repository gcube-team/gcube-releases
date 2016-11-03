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
 * Filename: SortableElemTest.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.local.testsuite;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcebroker.impl.support.types.GHNDescriptor;
import org.gcube.vremanagement.resourcebroker.impl.support.types.GHNReservation;
import org.gcube.vremanagement.resourcebroker.impl.support.types.SortableElement;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanBuilderIdentifier;

/**
 * For migration issues passing from jdk1.6 to jdk1.5 this test has been added.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class SortableElemTest {
	private static final int LOOP_COUNT = 100;

	public static void main(final String[] args) throws GCUBEFault {
		Random random = new Random();
		List<SortableElement<Integer, String>> list =
			new Vector<SortableElement<Integer, String>>();

		// ************* GENERIC SORTABLE_ELEMENTS *****************
		// Inserts the element to sort
		for (int i = 0; i < LOOP_COUNT; i++) {
			list.add(new SortableElement<Integer, String>(random.nextInt(LOOP_COUNT),
					"This element has been inserted at position #" + i));
		}
		System.out.println("Total inserted SORTABLE_ELEMENTS: " + list.size());
		// sorts all the elements inside the list
		Collections.sort(list);
		for (SortableElement<Integer, String> elem : list) {
			System.out.println(elem.getSortIndex() + ">\t" + elem.getElement());
		}

		// ************* SORTING ON GHN_DESCRIPTORS *****************
		List<GHNDescriptor> ghns = new Vector<GHNDescriptor>();
		for (int i = 0; i < LOOP_COUNT; i++) {
			ghns.add(new GHNDescriptor(
					random.nextInt(LOOP_COUNT),
					"I'm GHN #" + i,
					GCUBEScope.getScope("/gcube/devsec"),
					null));
		}
		System.out.println("Total inserted GHN_DESCRIPTORS: " + ghns.size());
		// sorts all the elements inside the list
		Collections.sort(ghns);
		for (SortableElement<Float, String> elem : ghns) {
			System.out.println(elem.getSortIndex() + ">\t" + elem.getElement());
		}

		List<GHNReservation> reservations = new Vector<GHNReservation>();
		GHNReservation reservation = null;
		for (int i = 0; i < LOOP_COUNT; i++) {
			reservation = new GHNReservation(new PlanBuilderIdentifier(), 1000);
			reservation.addGHN(new GHNDescriptor(1, "GHN_ID", GCUBEScope.getScope("/gcube/devsec"), null));
			reservations.add(reservation);
		}
		System.out.println("Total inserted GHN_RESERVATIONS: " + reservations.size());
		// sorts all the elements inside the list
		Collections.sort(reservations);
		for (GHNReservation elem : reservations) {
			System.out.println(elem.getSortIndex() + ">\t" + elem.getElement() + "\t" + elem.getGHNsForScope(GCUBEScope.getScope("/gcube/devsec")));
		}
	}
}
