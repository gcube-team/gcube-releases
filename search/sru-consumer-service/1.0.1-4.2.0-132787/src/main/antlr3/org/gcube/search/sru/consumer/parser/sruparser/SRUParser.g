grammar SRUParser;

@header {
    package org.gcube.search.sru.consumer.parser.sruparser;
    import java.lang.Double;
    import java.util.ArrayList;
    import org.gcube.search.sru.consumer.parser.sruparser.tree.*;
    import org.gcube.search.sru.consumer.parser.sruparser.tree.geo.*;
    
    
}
@lexer::header {
    package org.gcube.search.sru.consumer.parser.sruparser;
}

@members {
			private String lang;
			private List<String> allIndexes;
         }

sruQuery[String lang, List<String> allIndexes] returns[GCQLNode node]
	@init { boolean s = false, p = false, f = false; this.lang = lang; this.allIndexes = allIndexes; }
	: TWODOUBLEQUOTES EOF
	| pr = prefixAssignment q = cqlQuery EOF { $node = new GCQLPrefixNode($pr.prefix.identifier, $pr.prefix.name, $q.node); }
	| sc = scopedClause (( { s == false }?=> sort = sortSpec { s = true; } ) | ( { p == false }?=> proj = projectSpec { p = true; } ) | ( { f == false }?=> fuse = fuseSpec { f = true; } ) )* EOF 
		{ 
			$node = $sc.node; 
			if(f && p && s)
			{
				$sort.node.subtree = $sc.node;
				$proj.node.subtree = $sort.node;
				$fuse.node.subtree = $proj.node;
				$node = $fuse.node;
			}
			else if (f && p)
			{
				$proj.node.subtree = $sc.node;
				$fuse.node.subtree = $proj.node;
				$node = $fuse.node;
			}
			else if (f && s)
			{
				$sort.node.subtree = $sc.node;
				$fuse.node.subtree = $sort.node;
				$node = $fuse.node;
			}
			else if (f)
			{
				$fuse.node.subtree = $sc.node;
				$node = $fuse.node;
			}
			else if (p && s)
			{
				$sort.node.subtree = $sc.node;
				$proj.node.subtree = $sort.node;
				$node = $proj.node;
			}
			else if (p)
			{
				$proj.node.subtree = $sc.node;
				$node = $proj.node;
			}
			else if (s)
			{
				$sort.node.subtree = $sc.node;
				$node = $sort.node;
			}
			else
			{
				$node = $sc.node;
			}
		}
	| EOF
	;

sortSpec returns[GCQLSortNode node]
	: SORTBY {$node = new GCQLSortNode(); }( s = singleSpec (o = order { $s.set.addModifier($o.text);})? { $node.addSortIndex($s.set);} )+
	;
	
projectSpec returns[GCQLProjectNode node]
	: PROJECT {$node = new GCQLProjectNode(); }( s = singleSpec {$node.addProjectIndex($s.set); })+
	;	
	
fuseSpec returns[GCQLFuseNode node]
	: FUSE s = singleSpec { $node = new GCQLFuseNode(); $node.setFuseMode($s.set); }
	;

singleSpec returns[ModifierSet set]
	: i = index {$set = new ModifierSet($i.node.toCQL()); } ( ml = modifierList { $set.setModifiers($ml.ml); } )?
	;

cqlQuery returns[GCQLNode node]
	: p = prefixAssignment q = cqlQuery { $node = new GCQLPrefixNode($p.prefix.identifier, $p.prefix.name, $q.node); } 
	| sc = scopedClause { $node = $sc.node; }
	;

prefixAssignment returns[GCQLPrefix prefix]
	: GREATER p = prefix EQUALS u = uri { prefix = new GCQLPrefix($p.node.toCQL(), $u.node.toCQL()); }
	;

scopedClause returns[GCQLNode node]
	: s1 = searchClause	{ $node = s1; } ( b = booleanGroup s2 = searchClause 
				{
					if(((GCQLTermNode)s1).toCQL()==null)
						$node = s2;
					else if(((GCQLTermNode)s2).toCQL()==null)
						$node = s1;
					else
					{
						b.left = s1;
						b.right = s2;
						$node = b;
					}
				}
			)? 
	;

booleanGroup returns[GCQLBooleanNode node]
	: b = booleanOp 
			{ 
				$node = b;
			} 
		( ml = modifierList 
			{
				$node.ms.setModifiers($ml.ml);
			}
			 
		)?
	;

// TODO no parentheses added because GCQLBooleanNode.toCQL() inserts them as well
searchClause returns[GCQLNode node]
	: LPAREN q = cqlQuery RPAREN 
			{
				node = new GCQLTermNode();
				((GCQLTermNode)$node).setTerm($q.node.toCQL());
			} 
	| i = index r = relation s = searchTerm 
			{
				if($i.node.toCQL().equals("gDocCollectionID"))
				{
					node = new GCQLTermNode();
					break;
				}
				if($i.node.toCQL().equals("gDocCollectionLang"))
				{
					if(this.lang!=null)
					{
						if(this.lang.length()>0)
						{
							node = new GCQLTermNode();
							((GCQLTermNode)$node).setIndex(this.lang);
							((GCQLTermNode)$node).setRelation($r.node);
							((GCQLTermNode)$node).setTerm($s.node.toCQL());
						}
					}
					else
					{
						node = new GCQLTermNode();
					}
					break;
				}
				if($i.node.toCQL().equals("allIndexes"))
				{
					if(this.allIndexes!=null)
						if(this.allIndexes.size()>0)
						{
							GCQLNode root = null;
							for(String index : this.allIndexes)
							{
								GCQLTermNode temp = new GCQLTermNode();
								temp.setIndex(index);
								temp.setRelation($r.node);
								temp.setTerm($s.node.toCQL());
								if(root == null)
								{
									root = temp;
								}
								else if (root instanceof GCQLTermNode)
								{
									GCQLOrNode newRoot = new GCQLOrNode();
									newRoot.left = root;
									newRoot.right = temp;
									root = newRoot;
								}
								else if (root instanceof GCQLOrNode)
								{
									GCQLOrNode newRoot = new GCQLOrNode();
									newRoot.left = root;
									newRoot.right = temp;
									root = newRoot;
								}
							}
							node = root;
						}
						else
						{
							node = new GCQLTermNode();
							((GCQLTermNode)$node).setTerm($s.node.toCQL());
						}
						break;
				}
				node = new GCQLTermNode();
				((GCQLTermNode)$node).setIndex($i.node.toCQL());
				((GCQLTermNode)$node).setRelation($r.node);
				((GCQLTermNode)$node).setTerm($s.node.toCQL());
			}
	| s = searchTerm { $node = $s.node; }
	| d = distanceClause { $node = $d.node; }
	| o = overlapsClause { $node = $o.node; }
	;
	
distanceClause returns [GCQLDistanceNode node]
	: DISTANCE LPAREN i = index COMMA n = num COMMA g = geoPoint RPAREN {$node = new GCQLDistanceNode($i.node.getTerm(), Double.parseDouble($num.number), $g.gp);}
	;
	
overlapsClause returns [GCQLOverlapsNode node]
			: OVERLAPS LPAREN i = index COMMA g = geoPointList RPAREN { $node = new GCQLOverlapsNode($i.node.getTerm(), $g.gps); }
			;	

relation returns[GCQLRelation node]
	: c = comparitor
		{
			node = new GCQLRelation();
			$node.setBase($c.value);
		} 
		(ml = modifierList 
			{
				$node.setModifiers($ml.ml);
			} 
		)?
		
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


modifierList returns[ArrayList<Modifier> ml]
	: { $ml = new ArrayList<Modifier>(); } ( m = modifier { $ml.add($m.modifier); })+  
	;
	
modifier returns [Modifier modifier]
	: SLASH n = modifierName { $modifier = new Modifier(n); } ( s = comparitorSymbol m = modifierValue {$modifier = new Modifier(n, s, ((GCQLTermNode)m).getTerm()); } )?
	;
	
prefix returns[GCQLNode node]
	: p = term { $node = $p.node; }
	;


uri returns[GCQLNode node]
	: u = term { $node = $u.node; }
	;
	

modifierName returns[String name]
	: mn = term { $name = $mn.node.toCQL(); }
	;


modifierValue returns[GCQLNode node]
	: mv = term { $node = $mv.node; }
	;

searchTerm returns[GCQLTermNode node]
	: st = term { $node = (GCQLTermNode)$st.node; }
	;


index returns[GCQLTermNode node]
	: t = term { $node = (GCQLTermNode)$t.node; }
	;


term returns[GCQLNode node]
	: i = identifier 
		{
			node = new GCQLTermNode();
			((GCQLTermNode)$node).setTerm($i.value);
		}
	| n = num
		{
			node = new GCQLTermNode();
			((GCQLTermNode)$node).setTerm($n.number.toString());
		}
	| b = booleanOp { $node = b; } 
	;

identifier returns[String value]
	: i = (CHARSTRING1 | CHARSTRING2) { $value = $i.text; }
	;
	

booleanOp returns[GCQLBooleanNode node]
	: AND  { $node = new GCQLAndNode(); }
	| OR   { $node = new GCQLOrNode(); }
	| NOT  { $node = new GCQLNotNode(); }
	| PROX { $node = new GCQLProxNode(); }
	;

geoPointList returns [ArrayList<GeoPoint> gps]
	: LBRACK g1 = geoPoint
			{ 
				gps = new ArrayList<GeoPoint>();
				$gps.add($g1.gp);
			} 
		(
			COMMA g2 = geoPoint
			{
				$gps.add($g2.gp);
			}
		)* RBRACK
	;

geoPoint returns [GeoPoint gp]
	: LBRACK lat = num COMMA lon = num RBRACK { $gp = new GeoPoint(Double.parseDouble($lat.number), Double.parseDouble($lon.number)); }
	;

num returns [String number]
	: d = DOUBLE { $number = $d.text; }
    | i = INT { $number = $i.text; }
    ;
    
//TODO put it correctly!
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

CHARSTRING1      : ~('(' | ')' | ' ' | '=' | '<' | '>' | '/' | '[' | ']' | ',')+;
CHARSTRING2      : '\"' ~('(' | ')' | '=' | '<' | '>' )+ '\"';

//| < CHARSTRING1:(["A"-"Z","0"-"9","_",".","-",":","*","&",";","\u00bf"-"\u00ff", "\u0100"-"\uffff"])+ >
//| < CHARSTRING2:("\""(["A"-"Z","0"-"9","_",".",",","(",")","'"," ","<",">","/",":","-","*","&",";","+","=","%", "\u00bf"-"\u00ff", "\u0100"-"\uffff"])+)"\"" >
