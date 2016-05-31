/**
 * 
 */
package org.gcube.accounting.analytics.persistence.couchdb.couchapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.datamodel.aggregation.AggregatedStorageUsageRecord;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class VelocityGeneratorTest {

	private static final Logger logger = LoggerFactory
			.getLogger(VelocityGeneratorTest.class);

	private Set<String> getTestSet() {
		Set<String> set = new HashSet<>();
		set.add("1");
		set.add("2");
		set.add("3");
		set.add("4");
		set.add("5");
		set.add("6");
		set.add("7");
		set.add("8");
		return set;
	}

	@Test
	public void minimalSet() throws Exception {
		AggregatedStorageUsageRecord aggregatedStorageUsageRecord = new AggregatedStorageUsageRecord();
		Set<String> properties = AccountingPersistenceQuery
				.getQuerableKeys(aggregatedStorageUsageRecord);

		properties = getTestSet();
		Collection< ? extends SortedSet<String>> collections = VelocityGenerator.getMinimalSets(properties);

		int i = 1;
		for (Set<String> set : collections) {
			logger.trace("{}) - {}", i++, set);
		}
	}

	@Test
	public void combinationTestWithPermutations() throws Exception {
		AggregatedStorageUsageRecord aggregatedUsageRecord = new AggregatedStorageUsageRecord();
		Set<String> properties = AccountingPersistenceQuery
				.getQuerableKeys(aggregatedUsageRecord);

		properties = getTestSet();
		Collection<Collection<String>> collections = VelocityGenerator
				.getCombinationsFor(new ArrayList<>(properties), 3);
		int i = 1;
		for (Collection<String> collection : collections) {
			logger.trace("{}) --- {}", i++, collection);
			Collection<Collection<String>> permutations = VelocityGenerator
					.permute(collection);
			for (Collection<String> permutation : permutations) {
				logger.trace("{}) - {}", i++, permutation);
			}
		}

	}

	@Test
	public void combinationTest() throws Exception {
		AggregatedStorageUsageRecord aggregatedUsageRecord = new AggregatedStorageUsageRecord();
		Set<String> properties = AccountingPersistenceQuery
				.getQuerableKeys(aggregatedUsageRecord);

		properties = getTestSet();
		
		Collection<Collection<String>> collections = VelocityGenerator
				.getCombinationsFor(new ArrayList<>(properties), 3);
		collections.addAll(VelocityGenerator.getCombinationsFor(new ArrayList<>(
				properties), 2));
		collections.addAll(VelocityGenerator.getCombinationsFor(new ArrayList<>(
				properties), 1));

		int i = 1;
		for (Collection<String> collection : collections) {
			logger.trace("{}) - {}", i++, collection);
		}
	}

	@Test
	public void generate() throws Exception {
		VelocityGenerator.generate();
	}

}
