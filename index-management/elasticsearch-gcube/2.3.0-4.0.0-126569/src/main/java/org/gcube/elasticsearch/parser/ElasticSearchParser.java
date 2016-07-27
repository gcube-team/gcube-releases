package org.gcube.elasticsearch.parser;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.OrFilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.gcube.elasticsearch.helpers.ElasticSearchHelper;
import org.gcube.indexmanagement.resourceregistry.RRadaptor;


public class ElasticSearchParser {

	private static Logger logger = LoggerFactory.getLogger(ElasticSearchParser.class);
	private RRadaptor adaptor;
	private ElasticSearchParserLexer lexer;
	private ElasticSearchParserParser parser;
	private QueryBuilder q = null;
	private Set<String> sids;
	
	public ElasticSearchParser(String query, RRadaptor adaptor, Set<String> sids)
	{
		this.adaptor = adaptor;
		this.sids = sids;
		lexer = new ElasticSearchParserLexer(new ANTLRStringStream(query));
		parser = new ElasticSearchParserParser(new CommonTokenStream(lexer));
	}
	
	public QueryBuilder parse()
	{
		if(q!=null)
			return q;
		try {
			q = parser.esQuery(adaptor);
			if(sids!=null && !sids.isEmpty())
			{
				// query should match at least one of the sids or should not have an sid field
				OrFilterBuilder f = FilterBuilders.orFilter();
				f = f.add(FilterBuilders.missingFilter(ElasticSearchHelper.SECURITY_FIELD).existence(true).nullValue(true));
				for(String sid : sids)
					f = f.add(FilterBuilders.termFilter(ElasticSearchHelper.SECURITY_FIELD, sid));
				q = QueryBuilders.filteredQuery(q, f);
			}
			return q;
		} catch (RecognitionException e) {
			logger.error("Error while parsing: ",e);
		}
		return null;
	}

	public ArrayList<String> getDistincts()
	{
		return parser.distincts;
	}
	
	public ArrayList<String> getProjects()
	{
		return parser.projects;
	}
	
	public ArrayList<SimpleEntry<String, String>> getSortBys()
	{
		return parser.sortbys;
	}
	
	public Set<String> getCollections()
	{
		return parser.collections;
	}

	public static void main(String[] args) {
		

//		QueryBuilder qb1 = QueryBuilders
//                .boolQuery()
//                .must(QueryBuilders.termQuery("content", "test1")).minimumNumberShouldMatch(1)
//                .must(QueryBuilders.termQuery("content", "test4"))
//                .mustNot(QueryBuilders.termQuery("content", "test2"))
//                .should(QueryBuilders.termQuery("content", "test3"));
//		
//		
//		QueryBuilder qb2 = QueryBuilders
//                .boolQuery()
//                .must(QueryBuilders.termQuery("content", "test4"));
//		
//		
//		qb2 = QueryBuilders.queryString("content:>test4");
//		
//		QueryBuilder qb3 = QueryBuilders
//                .boolQuery()
//                .should(qb1)
//                .should(qb2);
//		
		
//		System.out.println(qb3.toString());
		
		
		String input = "((geo geosearch \"11 7 20 100\") and ((type exact \"new\") and ((geo geosearch \"-2 -6 8 4\") and ((gDocCollectionLang == \"en\")" 
				+ " and (((desci any \"new\") and (gDocCollectionID == \"C\")) or ((abstract exact \"new\") and ((spec any \"new\") or (tech any \"new\"))))))))";
//		
//		input = "(gDocCollectionID == \"faoCollection\")";
//		
//		input = "((c0b3f995-e51b-49fd-8413-47077791aa53 == \"*\") and (overlaps(cdcdcdcdc,[[3,4],[4,5],[44.5,44.3]]))) project dfdfdfdf  dfdfdfd asssaa sdsds  c0b3f995-e51b-49fd-8413-47077791aa53/distinct sortby c0b3f995-e51b-49fd-8413-47077791aa53 DESC";
		
		
		input = "(((aaaaa == vvv and dfdfdfd = fdfdfda) and paok = paokara) and fdfd=fdfdfd) or dsdsdasd = dsds";
		
//		input = "(((gDocCollectionID == \"5b268db0-9d63-11de-8d8f-a04a2d1ca936\") and (title = map)) and (number <= 2)) project title";
		
		input = "((((gDocCollectionID == \"c9076f3f-be8d-43e2-9f02-de35e6d8f72c\") and (gDocCollectionLang == \"unknown\"))) and (allIndexes = tuna)) project *";
		
		
		input = "((((gDocCollectionID == \"c9076f3f-be8d-43e2-9f02-de35e6d8f72c\") and (gDocCollectionLang == \"unknown\"))) and (allIn = tunaqa)) project 6ef6b515-6bcd-4007-af57-8394595fe584 87c4056b-447e-4c8b-90ee-2661e4bde7de";
		
		
		input = "((((gDocCollectionID == \"c9076f3f-be8d-43e2-9f02-de35e6d8f72c\") and (gDocCollectionLang == \"unknown\")) and (allIn = tunaqa)) and (another = other)) project 6ef6b515-6bcd-4007-af57-8394595fe584 87c4056b-447e-4c8b-90ee-2661e4bde7de";
		
		
//		input = "gDocCollectionID == \"c9076f3f-be8d-43e2-9f02-de35e6d8f72c\" and gDocCollectionLang == \"unknown\" and allIn = tunaqa and another = other project 6ef6b515-6bcd-4007-af57-8394595fe584 87c4056b-447e-4c8b-90ee-2661e4bde7de";
		
		input = "gDocCollectionID == \"paok\" and gDocCollectionLang == \"paokara\" and aaaaa = fdfds and dfdfdf = fdfd and ssss = aa";
		input = "aaaaa = fdfds and dfdfdf = fdfd and ssss = aa and gDocCollectionID == \"paok\" and gDocCollectionLang == \"paokara\" ";
		
		input = "gDocCollectionID == \"paok\" and (gDocCollectionLang = \"paokara\" or aaaaa = \"paokara\" or dfdfdf = \"paokara\" or ssss = \"paokara\")";
		
		input = "(text = tuna OR title = tuna OR species_english_name = tuna OR country = tuna OR type_of_vessel = tuna OR gear_used = tuna OR technology_used = tuna) AND gDocCollectionID == \"8dc17a91-378a-4396-98db-469280911b2f\" project gDocCollectionID gDocCollectionLang title text provenance country_uri vessel_uri gear_uri technology_used species_uri doc_uri gear_used technology_used management_uri sector_uri management sector ";
		
		
//		input = "gDocCollectionID == faoCollection AND (ObjectID = \"http://smartfish.collection/wiofish/342\") project gDocCollectionID";
//		input = "gDocCollectionLang == \"paokara\"";
		
		Set<String> set = new HashSet<>();
		set.add("alex.antoniadi");
		
		ElasticSearchParser out = new ElasticSearchParser(input, null, set);
		System.out.println(out.parse().getClass() + " " +out.parse().toString());
		
		System.out.println(out.getDistincts());
		
		System.out.println(out.getProjects());
		
		System.out.println(out.getSortBys());
		
		System.out.println(out.getCollections());

		
//		System.out.println(QueryBuilders.constantScoreQuery(FilterBuilders.geoDistanceFilter("paok")
//				.optimizeBbox("memory")
//				.geoDistance(GeoDistance.ARC)
//				.point(12.3, 11.4)
//				.distance(10, DistanceUnit.METERS)) instanceof ConstantScoreQueryBuilder);
		
		System.out.println(QueryBuilders.boolQuery().getClass());
		
		QueryBuilders.boolQuery().should(QueryBuilders.boolQuery()).should(QueryBuilders.boolQuery()).minimumNumberShouldMatch(1);
		
	}
	
}
