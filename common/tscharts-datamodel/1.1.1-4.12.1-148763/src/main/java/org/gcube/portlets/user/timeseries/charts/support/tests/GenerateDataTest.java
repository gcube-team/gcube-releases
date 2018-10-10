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
 * Filename: GenerateDataTest.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.user.timeseries.charts.support.tests;

import java.util.List;
import java.util.Vector;

import org.gcube.portlets.user.timeseries.charts.support.exceptions.InvalidParameterException;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphData;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;
import org.gcube.portlets.user.timeseries.charts.support.types.Point;
import org.gcube.portlets.user.timeseries.charts.support.types.ValueEntry;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class GenerateDataTest {
	@SuppressWarnings("unchecked")
	public static GraphGroups populateData(final int groupNum) {
		final boolean REVERSE = true;
		GraphGroups retval = new GraphGroups();

		for (int g = 0; g < groupNum; g++) {
			List<Point<? extends Number, ? extends Number>> points =
				new Vector<Point<? extends Number, ? extends Number>>();

			if (!REVERSE) {
				try {
					Point<Double, Double> xLine = new Point<Double, Double>("Tilapias and other cichlids", 1d);
					for (int i = 2005; i < 2020; i++) {
						xLine.addEntry(new ValueEntry<Double>(String.valueOf(i), Math.random() * 2000));
					}
					points.add(xLine);

					xLine = new Point<Double, Double>("Trupians", 2d);
					for (int i = 2005; i < 2020; i++) {
						xLine.addEntry(new ValueEntry<Double>(String.valueOf(i), Math.random() * 2000));
					}
					points.add(xLine);

					xLine = new Point<Double, Double>("Salmons", 3d);
					for (int i = 2005; i < 2020; i++) {
						xLine.addEntry(new ValueEntry<Double>(String.valueOf(i), Math.random() * 2000));
					}
					points.add(xLine);

				} catch (Exception e) {}
			} else {
				for (int i = 2005; i < 2020; i++) {
					try {
						points.add(
								new Point<Double, Double>(String.valueOf(i),
										new Double(i),
										new ValueEntry<Double>("Salmons, trouts, smelts", Math.random() * 2024),
										new ValueEntry<Double>("Trupians", Math.random() * 1024),
										new ValueEntry<Double>("Tilapias and other cichlids", Math.random() * 2000)
								)
						);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			try {
				retval.addGraph("Group #" + g, new GraphData(points, REVERSE));
			} catch (InvalidParameterException e) {
				e.printStackTrace();
			}
		}

		return retval;
	}

	public static final void main(final String[] args) {
		GenerateDataTest.populateData(3);
	}
}
