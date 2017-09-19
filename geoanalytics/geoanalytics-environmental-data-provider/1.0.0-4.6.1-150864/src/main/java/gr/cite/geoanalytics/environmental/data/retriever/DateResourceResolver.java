package gr.cite.geoanalytics.environmental.data.retriever;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import gr.cite.geoanalytics.environmental.data.retriever.utils.ResourceUtils;

public class DateResourceResolver {

	TreeMap<Integer, Map<Integer, TreeMap<Integer, String>>> dateToResource;

	public DateResourceResolver(String folder, String suffix) throws Exception {
		this.dateToResource = new TreeMap<>();

		try {
			Map<String, String> resourceNames = ResourceUtils.getResourcesNames(folder, suffix);
			resourceNames.entrySet().forEach(e -> addDateResourcePair(e.getKey(), e.getValue()));
		} catch (Exception e) {
			throw new Exception("Could not initialize resources", e);
		}
	}

	public String getResourceFromDate(String date) {
		String[] tokens = date.split("-");

		int day = Integer.parseInt(tokens[0]);
		int month = Integer.parseInt(tokens[1]);
		int year = Integer.parseInt(tokens[2]);

		Integer ceilKey = dateToResource.ceilingKey(year);
		Integer floorKey = dateToResource.floorKey(year);

		if (ceilKey != null && floorKey != null) {
			int floorDistance = year - floorKey;
			int ceilDistance = ceilKey - year;

			if (ceilDistance < floorDistance) {
				return dateToResource.ceilingEntry(year).getValue().get(month).ceilingEntry(day).getValue();
			} else {
				return dateToResource.floorEntry(year).getValue().get(month).ceilingEntry(day).getValue();
			}
		} else if (ceilKey != null) {
			return dateToResource.ceilingEntry(year).getValue().get(month).ceilingEntry(day).getValue();
		} else {
			return dateToResource.floorEntry(year).getValue().get(month).ceilingEntry(day).getValue();
		}
	}

	public void addDateResourcePair(String date, String resourcePath) {
		String[] tokens = date.split("-");

		int day = Integer.parseInt(tokens[0]);
		int month = Integer.parseInt(tokens[1]);
		int year = Integer.parseInt(tokens[2]);

		if (!dateToResource.containsKey(year)) {
			dateToResource.put(year, new HashMap<>());
		}

		if (!dateToResource.get(year).containsKey(month)) {
			dateToResource.get(year).put(month, new TreeMap<>());
		}

		dateToResource.get(year).get(month).put(day, resourcePath);
	}

	public List<String> getAllDates() {
		List<String> results = new ArrayList<String>();

		for (Map.Entry<Integer, Map<Integer, TreeMap<Integer, String>>> yearToMonth : dateToResource.entrySet()) {
			int year = yearToMonth.getKey();
			for (Map.Entry<Integer, TreeMap<Integer, String>> monthToDay : yearToMonth.getValue().entrySet()) {
				int month = monthToDay.getKey();
				for (Map.Entry<Integer, String> dayToResource : monthToDay.getValue().entrySet()) {
					int day = dayToResource.getKey();
					results.add(day + "-" + month + "-" + year);
				}
			}
		}

		return results;
	}

	public List<String> getAllResourceNames() {
		List<String> results = new ArrayList<String>();

		for (Map.Entry<Integer, Map<Integer, TreeMap<Integer, String>>> yearToMonth : dateToResource.entrySet()) {
			for (Map.Entry<Integer, TreeMap<Integer, String>> monthToDay : yearToMonth.getValue().entrySet()) {
				results.addAll(monthToDay.getValue().values());
			}
		}

		return results;
	}
}
