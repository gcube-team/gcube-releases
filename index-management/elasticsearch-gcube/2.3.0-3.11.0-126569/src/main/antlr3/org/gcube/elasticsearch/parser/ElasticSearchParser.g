grammar ElasticSearchParser;

options{
//    backtrack=true;
    language=Java;
}


@header {
    package org.gcube.elasticsearch.parser;
    import java.lang.Double;
    import java.util.HashSet;
    import java.util.AbstractMap.SimpleEntry;
    import java.util.Set;
    import org.gcube.elasticsearch.parser.helpers.ParserHelpers;
    import org.elasticsearch.common.geo.GeoDistance;
    import org.elasticsearch.common.unit.DistanceUnit;
    import org.elasticsearch.common.xcontent.ToXContent;
    import org.elasticsearch.index.query.QueryBuilder;
    import org.elasticsearch.index.query.QueryBuilders;
    import org.elasticsearch.index.query.FilterBuilder;
    import org.elasticsearch.index.query.FilterBuilders;
    import org.elasticsearch.index.query.GeoDistanceFilterBuilder;
    import org.elasticsearch.index.query.GeoPolygonFilterBuilder;
    import com.spatial4j.core.context.SpatialContext;
    import com.spatial4j.core.shape.Point;
    import com.spatial4j.core.shape.impl.PointImpl;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.gcube.indexmanagement.resourceregistry.RRadaptor;
}
@lexer::header {
    package org.gcube.elasticsearch.parser;
    import java.lang.Double;
    import java.util.HashSet;
    import java.util.AbstractMap.SimpleEntry;
    import java.util.Set;
    import org.gcube.elasticsearch.parser.helpers.ParserHelpers;
    import org.elasticsearch.common.geo.GeoDistance;
    import org.elasticsearch.common.unit.DistanceUnit;
    import org.elasticsearch.common.xcontent.ToXContent;
    import org.elasticsearch.index.query.QueryBuilder;
    import org.elasticsearch.index.query.QueryBuilders.*;
    import org.elasticsearch.index.query.FilterBuilder;
    import org.elasticsearch.index.query.FilterBuilders.*;
    import org.elasticsearch.index.query.GeoDistanceFilterBuilder;
    import org.elasticsearch.index.query.GeoPolygonFilterBuilder;
    import com.spatial4j.core.context.SpatialContext;
    import com.spatial4j.core.shape.Point;
    import com.spatial4j.core.shape.impl.PointImpl;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.gcube.indexmanagement.resourceregistry.RRadaptor;
}

@members {
			public ArrayList<String> projects = new ArrayList<String>();
			public ArrayList<SimpleEntry<String,String>> sortbys = new ArrayList<SimpleEntry<String,String>>();
			public ArrayList<String> distincts = new ArrayList<String>();
			public Set<String> collections = new HashSet<String>();
			
			private final String ASCENDING = "ASC";
			private final String DISTINCT = "distinct";
			private final String ALLINDEXES = "allindexes";
			
			private RRadaptor adaptor;
			private static final Logger logger = LoggerFactory.getLogger(ElasticSearchParserParser.class);
			
			private int count;
         }

@lexer::members {
	private static final Logger logger = LoggerFactory.getLogger(ElasticSearchParserParser.class);
}

esQuery [RRadaptor adaptor] returns[QueryBuilder qb]
	@init { boolean s = false, p = false; this.adaptor = adaptor; }
	: TWODOUBLEQUOTES EOF
	/*| pr = prefixAssignment q = cqlQuery EOF { $node = new GCQLPrefixNode($pr.prefix.identifier, $pr.prefix.name, $q.node); }*/
	| sc = scopedClause (( { s == false }?=> sortSpec { s = true; } ) | ( { p == false }?=> projectSpec { p = true; } ) )* EOF 
			{$qb = $sc.qb;}
	| EOF
	;

sortSpec returns[String value]
	: SORTBY ( { o = this.ASCENDING; } s = singleSpec ( o = order)? { sortbys.add(new SimpleEntry<>(ParserHelpers.translateField($s.value,adaptor), $o.value)); })+
	;
	
projectSpec returns[String value]
	: PROJECT ( s = singleSpec { projects.add(ParserHelpers.translateField($s.value,adaptor)); })+
	;	

singleSpec returns[String value]
	: i = index {$value = $i.value; } 
		( ml = modifierList
			{ 
				for(String s : $ml.ml)
					if(s.toLowerCase().equals(this.DISTINCT))
							distincts.add(ParserHelpers.translateField($i.value,adaptor));
			}
		)?
	;

cqlQuery returns[QueryBuilder qb]
	/*: p = prefixAssignment q = cqlQuery { $node = new GCQLPrefixNode($p.prefix.identifier, $p.prefix.name, $q.node); }*/ 
	: sc = scopedClause { $qb = $sc.qb; }
	;

/*prefixAssignment returns[GCQLPrefix prefix]
	: GREATER p = prefix EQUALS u = uri { prefix = new GCQLPrefix($p.node.toCQL(), $u.node.toCQL()); }
	;*/

scopedClause returns[QueryBuilder qb]
	: s1 = searchClause
		{ 
			count = 0;
			if(s1 instanceof FilterBuilder)
			{
				$qb = QueryBuilders.constantScoreQuery((FilterBuilder)s1);
			}
			else
			{
				$qb = (QueryBuilder)s1;
			}
		}
		(b = booleanGroup s2 = searchClause
			{
				count++;
				if($b.value.equals("AND"))
				{
					if(s2 instanceof FilterBuilder)
					{
						$qb = QueryBuilders.filteredQuery($qb, (FilterBuilder)s2);
					}
					else if (s1 instanceof FilterBuilder && count == 1)
					{
						$qb = QueryBuilders.filteredQuery((QueryBuilder)s2, (FilterBuilder)s1);
					}
					else
					{
						$qb = ParserHelpers.getCombinedQuery($qb, (QueryBuilder)s2);
					}	
				}
				else if($b.value.equals("OR"))
				{
					if(s2 instanceof FilterBuilder)
					{
						$qb = QueryBuilders.boolQuery().should($qb).should(QueryBuilders.constantScoreQuery((FilterBuilder)s2)).minimumNumberShouldMatch(1);
					}
					else
					{
						$qb = ParserHelpers.getCombinedOrQuery($qb, (QueryBuilder)s2);
						//$qb = QueryBuilders.boolQuery().should($qb).should((QueryBuilder)s2).minimumNumberShouldMatch(1);
					}	
				}
				else if($b.value.equals("NOT"))
				{
					if(s2 instanceof FilterBuilder)
					{
						$qb = QueryBuilders.boolQuery().must($qb).mustNot(QueryBuilders.constantScoreQuery((FilterBuilder)s2));
					}
					else
					{
						$qb = QueryBuilders.boolQuery().must($qb).mustNot((QueryBuilder)s2);
					}	
				}
			}		
		)*
	;

booleanGroup returns[String value]
	: b = booleanOp 
			{ 
				$value = b;
			} 
/*		( ml = modifierList 
			{
				$node.ms.setModifiers($ml.ml);
			}
			 
		)?*/
	;

searchClause returns[ToXContent qb]
	: LPAREN q = cqlQuery RPAREN { $qb = $q.qb;	}
	| i = index r = relation s = searchTerm 
			{ 
				String translated = ParserHelpers.translateField($i.value,adaptor);
				logger.info("Translated field " + $i.value + " to: "+translated);
				if($s.value.equals("\"*\""))
				{
					$qb = QueryBuilders.matchAllQuery();
				}
				else if($r.value.equals("=="))
				{
					$qb = FilterBuilders.boolFilter().must(FilterBuilders.termFilter($i.value, $s.value.replaceAll("\"","")));
				}
				else if(translated.toLowerCase().equals(ALLINDEXES))
				{
					$qb = QueryBuilders.queryString($s.value);
				}
				else if($r.value.equals("="))
				{
					$qb = QueryBuilders.queryString(translated + ":" + $s.value);
				}
				else if($r.value.toLowerCase().equals("within"))
				{
					String[] range = $s.value.replaceAll("\"","").split(" ");
					$qb = QueryBuilders
		                    .rangeQuery(translated)
		                    .from(range[0].trim())
		                    .to(range[1].trim())
		                    .includeLower(true)
		                    .includeUpper(true);
				}
				else
				{
					$qb = QueryBuilders.queryString(ParserHelpers.translateField($i.value,adaptor) + ":" + $r.value + $s.value);	
				}
				
				// if gDocCollectionID add it
				if($i.value.equals("gDocCollectionID"))
				{
					collections.add($s.value.replaceAll("\"",""));
				}
				
			}
	| s = searchTerm { $qb = QueryBuilders.queryString($s.value); }
	| d = distanceClause { $qb = $d.fb; }
	| o = overlapsClause { $qb = $o.fb; }
	;
	
distanceClause returns [GeoDistanceFilterBuilder fb]
	: DISTANCE LPAREN i = index COMMA n = num COMMA g = geoPoint RPAREN
		{
			$fb = FilterBuilders.geoDistanceFilter($i.value)
					.optimizeBbox("memory")
					.geoDistance(GeoDistance.ARC)
					.point($g.gp.getX(), $g.gp.getY())
					.distance(Double.parseDouble($n.number), DistanceUnit.METERS);
		}
	;
	
overlapsClause returns [GeoPolygonFilterBuilder fb]
	: OVERLAPS LPAREN i = index COMMA g = geoPointList RPAREN
		{ 
			$fb = FilterBuilders.geoPolygonFilter($i.value);
			for(Point p : $g.gps)
				$fb = $fb.addPoint(p.getX(), p.getY());
		}
	;

relation returns[String value]
	: c = comparitor { $value = $c.value; } 
/*		(ml = modifierList 
			{
				$node.setModifiers($ml.ml);
			} 
		)?*/
	;

comparitor returns[String value]
	: c = comparitorSymbol { $value = $c.value; }
	| c = namedComparitor { $value = $c.value; }
	;

comparitorSymbol returns[String value]
	: c = EQUALS { $value = $c.text; }
	| c = GREATER { $value = $c.text; }
	| c = LESS { $value = $c.text; }
	| c = GEQUAL { $value = $c.text; }
	| c = LEQUAL { $value = $c.text; }
	| c = NOTEQUAL { $value = $c.text; }
	| c = EXACT { $value = $c.text; }
	;

namedComparitor returns[String value]
	: nc = identifier  { $value = $nc.value; }
	;

modifierList returns[ArrayList<String> ml]
	: { $ml = new ArrayList<String>(); } ( m = modifier { $ml.add($m.value); })+  
	;
	
modifier returns [String value]
	: SLASH n = modifierName { $value = n; } ( s = comparitorSymbol m = modifierValue { $value += $s.value + $m.value; System.out.println("MOD: "+$value);} )?
	;
	
prefix returns[String value]
	: p = term { $value = $p.value; }
	;

uri returns[String value]
	: u = term { $value = $u.value; }
	;
	
modifierName returns[String name]
	: mn = term { $name = $mn.value; }
	;

modifierValue returns[String value]
	: mv = term { $value = $mv.value; }
	;

searchTerm returns[String value]
	: st = term { $value = $st.value; logger.trace("searchTerm "+$st.text);}
	;


index returns[String value]
	: t = term { $value = $t.value; logger.trace("index "+$t.text);}
	;


term returns[String value]
	: i = identifier { $value = $i.value; logger.trace("term "+$i.text);}
	| n = num {$value = $n.number.toString(); }
/*	| b = booleanOp { $value = $b.op; }*/ 
	;

identifier returns[String value]
	: i = (CHARSTRING1 | CHARSTRING2) { $value = $i.text; logger.trace("identifier "+$i.text);}
	;
	

booleanOp returns[String op]
	: AND  { $op = "AND"; }
	| OR   { $op = "OR"; }
	| NOT  { $op = "NOT"; }
	| PROX { $op = "PROX"; }
	;

geoPointList returns [ArrayList<Point> gps]
	: LBRACK g1 = geoPoint
			{ 
				gps = new ArrayList<Point>();
				$gps.add($g1.gp);
			} 
		(
			COMMA g2 = geoPoint
			{
				$gps.add($g2.gp);
			}
		)* RBRACK
	;

geoPoint returns [Point gp]
	: LBRACK lat = num COMMA lon = num RBRACK { $gp = new PointImpl(Double.parseDouble($lat.number),Double.parseDouble($lon.number),SpatialContext.GEO); }
	;

num returns [String number]
	: d = DOUBLE { $number = $d.text; }
    | i = INT { $number = $i.text; }
    ;
    
order returns [String value]
	: ASC	{ $value = "ASC"; } 
	| DESC	{ $value = "DESC"; }
	;

DISTANCE        : ('d'|'D')('i'|'I')('s'|'S')('t'|'T')('a'|'A')('n'|'N')('c'|'C')('e'|'E');
OVERLAPS        : ('o'|'O')('v'|'V')('e'|'E')('r'|'R')('l'|'L')('a'|'A')('p'|'P')('s'|'S');
SORTBY          : ('s'|'S')('o'|'O')('r'|'R')('t'|'T')('b'|'B')('y'|'Y');
PROJECT         : ('p'|'P')('r'|'R')('o'|'O')('j'|'J')('e'|'E')('c'|'C')('t'|'T');
FUSE            : ('f'|'F')('u'|'U')('s'|'S')('e'|'E');
AND             : ('a'|'A')('n'|'N')('d'|'D');
OR              : ('o'|'O')('r'|'R');
NOT             : ('n'|'N')('o'|'O')('t'|'T');
PROX            : ('p'|'P')('r'|'R')('o'|'O')('x'|'X');
ASC				: ('a'|'A')('s'|'S')('c'|'C');
DESC			: ('d'|'D')('e'|'E')('s'|'S')('c'|'C');
LPAREN          : '(';
RPAREN          : ')';
LBRACK          : '[';
RBRACK          : ']';
COMMA           : ',';
NOTEQUAL        : '<''>';
GEQUAL          : '>''=';
LEQUAL          : '<''=';
GREATER         : '>';
LESS            : '<';
EXACT           : '=''=';
EQUALS          : '=';
SLASH           : '/';
WHITESPACE      : (' ' | '\t' | '\r' | '\n' | '\u000C')+ { skip(); } ;
TWODOUBLEQUOTES : '\"''\"';

DOUBLE          : '0'..'9'+'.''0'..'9'+ ;
INT             : '0'..'9'+ ;

CHARSTRING1      : ~('(' | ')' | ' ' | '=' | '<' | '>' | '/' | '[' | ']' | ',' | '"')+ {logger.trace("1 ");};
CHARSTRING2      : '\"' ~('(' | ')' | '=' | '<' | '>' | '"')+ '\"' {logger.trace("2 ");};