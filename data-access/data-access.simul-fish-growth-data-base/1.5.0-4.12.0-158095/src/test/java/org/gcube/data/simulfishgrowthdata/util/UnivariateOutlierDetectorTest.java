package org.gcube.data.simulfishgrowthdata.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import org.gcube.data.simulfishgrowthdata.model.UnivariateOutlierDetector;
import org.gcube.data.simulfishgrowthdata.model.UnivariateOutlierDetector.IValue;
import org.gcube.data.simulfishgrowthdata.model.UnivariateOutlierDetector.SimpleValue;

import junit.framework.TestCase;

public class UnivariateOutlierDetectorTest extends TestCase {

	public void testSimpleDoubles() throws Exception {
		Double lowerperc = 25.0;
		Double upperperc = 75.0;

		ArrayList<IValue> values_list = new ArrayList<IValue>(Arrays.asList(new SimpleValue(4.0), new SimpleValue(17.0),
				new SimpleValue(7.0), new SimpleValue(7.5), new SimpleValue(14.0), new SimpleValue(18.0),
				new SimpleValue(100.5), new SimpleValue(12.0), new SimpleValue(3.0), new SimpleValue(16.0),
				new SimpleValue(0.0), new SimpleValue(-250.45), new SimpleValue(10.0), new SimpleValue(4.0),
				new SimpleValue(4.0), new SimpleValue(11.0), new SimpleValue(1000.0), new SimpleValue(-240.0),
				new SimpleValue(650.0), new SimpleValue(750.0), new SimpleValue(5.4), new SimpleValue(600.75),
				new SimpleValue(-290.0)));

		UnivariateOutlierDetector detector = new UnivariateOutlierDetector().addValues(values_list)
				.defineLowerPercentage(lowerperc).defineUpperPercentage(upperperc).execute();
		assertEquals("proper setup values", 23, detector.getValues().size());
		assertEquals("proper setup values", 8, detector.getOutliers().size());

		detector.cleanFromOutliers();
		assertEquals("proper setup values", 23 - 8, detector.getValues().size());
	}

	static class SampleDatabase extends SimpleValue {
		Integer idx;

		public SampleDatabase(final Integer idx, final Double value) {
			super(value);
			this.idx = idx;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("SampleValue at [").append(idx).append("] = [").append(value).append("]");
			return builder.toString();
		}

	}

	public void testSample() throws Exception {
		Double lowerperc = 25.0;
		Double upperperc = 75.0;

		Set<Integer> toMark = new HashSet<Integer>();

		ArrayList<IValue> firstList = new ArrayList<IValue>(Arrays.asList(new SampleDatabase(401, 4.0),
				new SampleDatabase(170, 17.0), new SampleDatabase(70, 7.0), new SampleDatabase(75, 7.5),
				new SampleDatabase(114, 14.0), new SampleDatabase(180, 18.0), new SampleDatabase(1005, 100.5),
				new SampleDatabase(120, 12.0), new SampleDatabase(30, 3.0), new SampleDatabase(160, 16.0),
				new SampleDatabase(0, 0.0), new SampleDatabase(25045000, -250.45), new SampleDatabase(100, 10.0),
				new SampleDatabase(403, 4.0), new SampleDatabase(402, 4.0), new SampleDatabase(110, 11.0),
				new SampleDatabase(10000, 1000.0), new SampleDatabase(240000, -240.0), new SampleDatabase(6500, 650.0),
				new SampleDatabase(7500, 750.0), new SampleDatabase(54, 5.4), new SampleDatabase(6007, 600.75),
				new SampleDatabase(290000, -290.0)));

		UnivariateOutlierDetector detector = new UnivariateOutlierDetector().addValues(firstList)
				.defineLowerPercentage(lowerperc).defineUpperPercentage(upperperc).execute();
		assertEquals("proper setup values", 23, detector.getValues().size());
		assertEquals("proper setup values", 8, detector.getOutliers().size());

		detector.cleanFromOutliers();
		assertEquals("proper setup values", 23 - 8, detector.getValues().size());

		for (IValue value : detector.getOutliers()) {
			SampleDatabase sampleDatabase = (SampleDatabase) value;
			toMark.add(sampleDatabase.idx);
		}
		assertEquals("proper marked values", 8, toMark.size());

		//touched 170, 70, 75, 25145000, 291000
		ArrayList<IValue> secondList = new ArrayList<IValue>(Arrays.asList(new SampleDatabase(401, 4.0),
				new SampleDatabase(180, 18.0), new SampleDatabase(80, 8.0), new SampleDatabase(85, 8.5),
				new SampleDatabase(114, 14.0), new SampleDatabase(180, 18.0), new SampleDatabase(1005, 100.5),
				new SampleDatabase(120, 12.0), new SampleDatabase(30, 3.0), new SampleDatabase(160, 16.0),
				new SampleDatabase(0, 0.0), new SampleDatabase(25145000, -251.45), new SampleDatabase(100, 10.0),
				new SampleDatabase(403, 4.0), new SampleDatabase(402, 4.0), new SampleDatabase(110, 11.0),
				new SampleDatabase(10000, 1000.0), new SampleDatabase(240000, -240.0), new SampleDatabase(6500, 650.0),
				new SampleDatabase(7500, 750.0), new SampleDatabase(54, 5.4), new SampleDatabase(6007, 600.75),
				new SampleDatabase(291000, -291.0)));

		detector.cleanOutliers().cleanValues().addValues(secondList).execute().cleanFromOutliers();

		for (IValue value : detector.getOutliers()) {
			SampleDatabase sampleDatabase = (SampleDatabase) value;
			toMark.add(sampleDatabase.idx);
		}
		//addedd 25145000 and 291000
		assertEquals("proper add values", 8+2, toMark.size());

	}

}
