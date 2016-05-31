/**
 * 
 */
package org.gcube.accounting.analytics.persistence.couchdb.couchapp;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.gcube.accounting.analytics.TemporalConstraint.AggregationMode;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.analytics.persistence.couchdb.AccountingPersistenceQueryCouchDB;
import org.gcube.accounting.persistence.AccountingPersistenceFactory;
import org.gcube.documentstore.records.AggregatedRecord;
import org.gcube.documentstore.records.RecordUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
class VelocityGenerator {

	private static final Logger logger = LoggerFactory
			.getLogger(VelocityGenerator.class);

	protected static final String SNAPSHOT = "-SNAPSHOT";

	protected static final String VERSION;
	protected static final String NAME;
	protected static final String DESCRIPTION;

	protected static final String DEFAULT_VERSION = "LATEST";
	protected static final String DEFAULT_NAME = "Accounting Analytics CouchDB";
	protected static final String DEFAULT_DESCRIPTION = "Accounting Analytics CouchDB";

	static {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		String version = DEFAULT_VERSION;
		String name = DEFAULT_NAME;
		String description = DEFAULT_DESCRIPTION;

		Model model;
		try {
			model = reader.read(new FileReader("pom.xml"));

			if (model.getVersion().compareTo("") != 0) {
				version = model.getVersion().replace(SNAPSHOT, "");
			}

			if (model.getName().compareTo("") != 0) {
				name = model.getName().trim();
			}

			if (model.getDescription().compareTo("") != 0) {
				description = model.getDescription().trim();
			}
		} catch (IOException | XmlPullParserException e) {
			logger.error(
					"Error Getting Version from pom.xml. LATEST will be used.",
					e);
		}

		VERSION = version;
		NAME = name;
		DESCRIPTION = description;
	}

	protected static final File SRC_DIRECTORY = new File("src");
	protected static final File TEST_DIRECTORY = new File(SRC_DIRECTORY, "test");
	protected static final File RESOURCES_DIRECTORY = new File(TEST_DIRECTORY,
			"resources");
	protected static final File VELOCITY_TEMPLATES_DIRECTORY = new File(
			RESOURCES_DIRECTORY, "velocity_templates");

	protected static final File COUCHAPPS_TEMPLATES_DIRECTORY = new File(
			VELOCITY_TEMPLATES_DIRECTORY, "couchapp_templates");
	protected static final File MAP_REDUCE_TEMPLATES_DIRECTORY = new File(
			VELOCITY_TEMPLATES_DIRECTORY, "map_reduce_templates");

	protected static final String PROPERTIES_MAP_FILE_PROPERTY = "properties";
	protected static final String SLICE_MAP_FILE_PROPERTY = "slice";

	protected static final String MAP_TEMPLATE_FILENAME = "map.vm";
	protected static final String REDUCE_TEMPLATE_FILENAME = "reduce.vm";

	protected static final File TARGET_DIRECTORY = new File("target");
	protected static final File COUCHAPP_DIRECTORY = new File(TARGET_DIRECTORY,
			"couchapp");
	protected static final File CURRENT_VERSION_DIRECTORY = new File(
			COUCHAPP_DIRECTORY, VERSION);
	protected static final File DESIGN_DIRECTORY = new File(
			CURRENT_VERSION_DIRECTORY, "_design");

	protected static final String VIEWS_DIRECTORY_NAME = "views";

	protected static final String INVALID_DESIGN_DOCUMENT_NAME = "Invalid";

	protected static final String MAP_OUTPUT_FILENAME = "map.js";
	protected static final String REDUCE_OUTPUT_FILENAME = "reduce.js";

	protected static final String NAME_COUCHAPP_FILE_PROPERTY = "name";
	protected static final String DESCRIPTION_COUCHAPP_FILE_PROPERTY = "description";

	protected static final File COUCHAPP_TEMPLATE_FILE = new File(
			COUCHAPPS_TEMPLATES_DIRECTORY, "couchapp.vm");
	protected static final File COUCHAPP_OUTPUT_FILE = new File(
			CURRENT_VERSION_DIRECTORY, "couchapp.json");

	protected static final File LANGUAGE_TEMPLATE_FILE = new File(
			COUCHAPPS_TEMPLATES_DIRECTORY, "language.vm");
	protected static final File LANGUAGE_OUTPUT_FILE = new File(
			CURRENT_VERSION_DIRECTORY, "language");

	protected static final String DESIGN_DOCUMENTS_README_FILE_PROPERTY = "designDocuments";
	protected static final File README_TEMPLATE_FILE = new File(
			COUCHAPPS_TEMPLATES_DIRECTORY, "README.vm");
	protected static final File README_OUTPUT_FILE = new File(
			CURRENT_VERSION_DIRECTORY, "README.md");

	protected static final String DESIGN_DOCUMENTS_INSTALL_ALL_FILE_PROPERTY = DESIGN_DOCUMENTS_README_FILE_PROPERTY;
	protected static final File INSTALL_ALL_TEMPLATE_FILE = new File(
			COUCHAPPS_TEMPLATES_DIRECTORY, "installAll.vm");
	protected static final File INSTALL_ALL_OUTPUT_FILE = new File(
			CURRENT_VERSION_DIRECTORY, "installAll.sh");

	protected static void generateFile(VelocityContext context, File templateFile,
			File outputFile) {
		Template template = Velocity.getTemplate(templateFile.getPath());
		Writer writer;
		try {
			writer = new FileWriter(outputFile);
			template.merge(context, writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			logger.error("Error generating Map Reduce", e);
		}
	}

	protected static void renderTemplate(VelocityContext context,
			String desingDocumentName, String desingDocumentNameSuffix,
			String reduceFunction) throws IOException {
		File templateDirectory = new File(MAP_REDUCE_TEMPLATES_DIRECTORY,
				desingDocumentName);
		File designDocumentDirectory = new File(DESIGN_DIRECTORY,
				desingDocumentName + desingDocumentNameSuffix);

		File outputDirectory = new File(designDocumentDirectory,
				VIEWS_DIRECTORY_NAME);

		File viewDirectory = new File(outputDirectory, reduceFunction);
		viewDirectory.mkdirs();

		File mapTemplateFile = new File(templateDirectory,
				MAP_TEMPLATE_FILENAME);
		File mapOutputFile = new File(viewDirectory, MAP_OUTPUT_FILENAME);
		generateFile(context, mapTemplateFile, mapOutputFile);

		File reduceTemplateFile = new File(templateDirectory,
				REDUCE_TEMPLATE_FILENAME);
		if (!reduceTemplateFile.exists()) {
			return;
		}
		File reduceOutputFile = new File(viewDirectory, REDUCE_OUTPUT_FILENAME);

		generateFile(context, reduceTemplateFile, reduceOutputFile);
	}

	protected static void generateCouchAppDirectory(
			Map<String, Class<? extends AggregatedRecord<?,?>>> map)
			throws IOException {
		DESIGN_DIRECTORY.mkdirs();
		VelocityContext context = new VelocityContext();

		context.put(NAME_COUCHAPP_FILE_PROPERTY, NAME);
		context.put(DESCRIPTION_COUCHAPP_FILE_PROPERTY, DESCRIPTION);
		generateFile(context, COUCHAPP_TEMPLATE_FILE, COUCHAPP_OUTPUT_FILE);

		generateFile(context, LANGUAGE_TEMPLATE_FILE, LANGUAGE_OUTPUT_FILE);

		List<String> documentProperties = new ArrayList<>();
		for (String key : map.keySet()) {
			documentProperties.add(key);
			for (AggregationMode mode : AggregationMode.values()) {
				documentProperties.add(key + mode.name());
			}
		}
		documentProperties.add(INVALID_DESIGN_DOCUMENT_NAME);
		
		context.put(DESIGN_DOCUMENTS_README_FILE_PROPERTY, documentProperties);
		generateFile(context, README_TEMPLATE_FILE, README_OUTPUT_FILE);

		generateFile(context, INSTALL_ALL_TEMPLATE_FILE,
				INSTALL_ALL_OUTPUT_FILE);

		renderTemplate(context, INVALID_DESIGN_DOCUMENT_NAME, "",
				INVALID_DESIGN_DOCUMENT_NAME);
	}

	protected static <T> Collection<Collection<T>> permute(
			Collection<T> input) {
		Collection<Collection<T>> output = new ArrayList<>();
		if (input.isEmpty()) {
			output.add(new ArrayList<T>());
			return output;
		}
		List<T> list = new ArrayList<T>(input);
		T head = list.get(0);
		List<T> rest = list.subList(1, list.size());
		for (Collection<T> permutations : permute(rest)) {
			List<List<T>> subLists = new ArrayList<List<T>>();
			for (int i = 0; i <= permutations.size(); i++) {
				List<T> subList = new ArrayList<T>();
				subList.addAll(permutations);
				subList.add(i, head);
				subLists.add(subList);
			}
			output.addAll(subLists);
		}
		return output;
	}

	protected static void generateAggregationMapReduce(
			Collection<Collection<String>> collections,
			String desingDocumentName) throws Exception {
		VelocityContext context = new VelocityContext();

		for (Collection<String> collection : collections) {
			List<String> emittingProperties = new ArrayList<>();
			context.put(PROPERTIES_MAP_FILE_PROPERTY, emittingProperties);
			//emittingProperties.add(UsageRecord.SCOPE);
			emittingProperties.addAll(collection);
			String reduceFunction = AccountingPersistenceQueryCouchDB.getMapReduceFunctionName(collection);

			for (AggregationMode mode : AggregationMode.values()) {
				context.put(SLICE_MAP_FILE_PROPERTY, mode.ordinal() + 1);
				renderTemplate(context, desingDocumentName, mode.name(),
						reduceFunction);
			}

			context.remove(PROPERTIES_MAP_FILE_PROPERTY);
		}
	}
	
	protected static void generateExactMatchMapReduce(
			Collection<? extends Collection<String>> collections,
			String desingDocumentName) throws Exception {
		VelocityContext context = new VelocityContext();

		for (Collection<String> collection : collections) {
			List<String> emittingProperties = new ArrayList<>();
			context.put(PROPERTIES_MAP_FILE_PROPERTY, emittingProperties);
			//emittingProperties.add(UsageRecord.SCOPE);
			emittingProperties.addAll(collection);
			String reduceFunction = AccountingPersistenceQueryCouchDB.getMapReduceFunctionName(collection);

			renderTemplate(context, desingDocumentName, "", reduceFunction);

			context.remove(PROPERTIES_MAP_FILE_PROPERTY);
		}
	}

	protected static <T> Collection<Collection<T>> getCombinationsFor(
			List<T> group, int subsetSize) {
		Collection<Collection<T>> resultingCombinations = new HashSet<>();
		int totalSize = group.size();
		if (subsetSize == 0) {
			resultingCombinations.add(new TreeSet<T>());
		} else if (subsetSize <= totalSize) {
			List<T> remainingElements = new ArrayList<T>(group);
			T t = popLast(remainingElements);

			Collection<Collection<T>> combinationsExclusiveX = getCombinationsFor(
					remainingElements, subsetSize);
			Collection<Collection<T>> combinationsInclusiveX = (Collection<Collection<T>>) getCombinationsFor(
					remainingElements, subsetSize - 1);
			for (Collection<T> combination : combinationsInclusiveX) {
				combination.add(t);
			}
			resultingCombinations.addAll(combinationsExclusiveX);
			resultingCombinations.addAll(combinationsInclusiveX);
		}
		return resultingCombinations;
	}

	private static <T> T popLast(List<T> elementsExclusiveX) {
		return elementsExclusiveX.remove(elementsExclusiveX.size() - 1);
	}

	/**
	 * Returned Set are 2^(n-1)
	 * 
	 * @param elements
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static <T extends Comparable<T>> Collection<? extends SortedSet<T>> getMinimalSets(
			Set<T> elements) {
		
		Comparator<TreeSet<T>> comparator = new Comparator<TreeSet<T>>() {

			@Override
			public int compare(TreeSet<T> treeSet1, TreeSet<T> treeSet2) {
				Integer size1 = treeSet1.size();
				Integer size2 = treeSet2.size();
				Integer compareResult = size2.compareTo(size2);
				if (compareResult == 0) {
					TreeSet<T> auxTreeSet1 = new TreeSet<>(treeSet1);
					TreeSet<T> auxTreeSet2 = new TreeSet<>(treeSet2);
					for (int i = 0; i < size1; i++) {
						T first1 = auxTreeSet1.pollFirst();
						T first2 = auxTreeSet2.pollFirst();
						compareResult = first1.compareTo(first2);
						if (compareResult != 0) {
							return compareResult;
						}
					}
					return 0;
				}
				return compareResult;
			}
		};
		
		Collection<TreeSet<T>> output = new TreeSet<>(comparator);

		TreeSet<T> treeSet = new TreeSet<>(elements);
		output.add(treeSet);
		for (T t : treeSet) {
			Set<T> subSet = new TreeSet<>(treeSet);
			subSet.remove(t);
			if (subSet.size() > 0) {
				output.addAll((Collection<TreeSet<T>>) getMinimalSets(subSet));
			}
		}
		
		return output;
	}

	protected static void generateAllMapReduceForExactMatches(
			Set<String> properties, String desingDocumentName) throws Exception {

		Collection<Collection<String>> collections = getCombinationsFor(new ArrayList<>(properties), 3);
		collections.addAll(getCombinationsFor(new ArrayList<>(properties), 2));
		collections.addAll(getCombinationsFor(new ArrayList<>(properties), 1));
		generateExactMatchMapReduce(collections, desingDocumentName);

		logger.trace(
				"Going to calculate minimal combinations for {} of properites {}",
				desingDocumentName, properties);
		
		Collection<? extends SortedSet<String>> reducedSet = getMinimalSets(properties);
		generateExactMatchMapReduce(reducedSet, desingDocumentName);
	}

	protected static void generateAllAggregationMapReduce(Set<String> properties, String desingDocumentName) throws Exception {
		Collection<Collection<String>> collections = getCombinationsFor(new ArrayList<>(properties), 3);
		for (Collection<String> collection : collections) {
			Collection<Collection<String>> permutations = permute(collection);
			generateAggregationMapReduce(permutations, desingDocumentName);
		}
	}

	protected static void generateMapReduce(@SuppressWarnings("rawtypes") Class<? extends AggregatedRecord> aggregatedRecord) throws Exception {
		@SuppressWarnings("rawtypes")
		AggregatedRecord instance = aggregatedRecord.newInstance();
		String desingDocumentName = instance.getRecordType();

		Set<String> properties = AccountingPersistenceQuery.getQuerableKeys(instance);

		generateAllAggregationMapReduce(properties, desingDocumentName);

		generateAllMapReduceForExactMatches(properties, desingDocumentName);
	}
	
	
	protected static void generate() throws Exception {
		Velocity.init();

		AccountingPersistenceFactory.initAccountingPackages();
		Map<String, Class<? extends AggregatedRecord<?,?>>> map = RecordUtility
				.getAggregatedRecordClassesFound();

		generateCouchAppDirectory(map);

		for (@SuppressWarnings("rawtypes")
		Class<? extends AggregatedRecord> aggregatedRecord : map.values()) {
			generateMapReduce(aggregatedRecord);
		}

	}

}
