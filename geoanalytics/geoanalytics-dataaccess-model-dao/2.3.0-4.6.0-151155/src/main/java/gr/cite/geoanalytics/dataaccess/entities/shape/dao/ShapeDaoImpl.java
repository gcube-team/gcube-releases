package gr.cite.geoanalytics.dataaccess.entities.shape.dao;

import gr.cite.geoanalytics.common.ShapeAttributeDataType;
import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;
import gr.cite.geoanalytics.dataaccess.typedefinition.DataType;
import gr.cite.geoanalytics.dataaccess.typedefinition.DatabaseColumnType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
//import javax.ws.rs.WebApplicationException;
//import javax.ws.rs.core.StreamingOutput;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.gson.Gson;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;

@Repository
public class ShapeDaoImpl extends JpaDao<Shape, UUID> implements ShapeDao {
	
	private DatabaseColumnType dtc;
	
	
	public static ObjectMapper mapper = new ObjectMapper();
	
	public static Logger log = LoggerFactory.getLogger(ShapeDaoImpl.class);

	@Inject
	public void setDtc(DatabaseColumnType dtc) {
		this.dtc = dtc;
	}
	
	
	public List<Shape> findShapesByClass(short shp_class) {

		List<Shape> result = null;

		TypedQuery<Shape> query = entityManager.createQuery(
				"from Shape where shapeClass = :code", Shape.class);
		query.setParameter("code", shp_class);

		result = query.getResultList();

		log.debug("Find shapes with class: " + shp_class);
		log.debug((result != null ? result.size() : 0) + " results");
		if (log.isDebugEnabled() && result != null) {
			for (Shape sh : (List<Shape>) result) {
				log.debug("Shape (" + sh.getId() + ")");
			}
		}
		return result;
	}

	@Override
	public List<Shape> searchShapesByName(String term) {
		List<Shape> result = null;
		
		StringBuilder queryB = new StringBuilder();
		queryB.append("from Shape s");
		queryB.append(" where ");
		queryB.append("lower(s.name) like :term");
		TypedQuery<Shape> query = entityManager.createQuery(queryB.toString(), Shape.class);
		query.setParameter("term", "%"+term.toLowerCase()+"%");

		result = query.getResultList();
		
		log.debug("Shapes by name pattern matching:");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null) {
			for (Shape s : (List<Shape>) result) 
				log.debug("Shape (" + s.getId() + ":" + s.getName() + ")");
		}
		
		return result;
	}
	
	@Override
	public List<Shape> searchShapes(List<String> terms) {
		List<Shape> result = null;
		
		StringBuilder queryB = new StringBuilder();
		queryB.append("from Shape s");

		if(!terms.isEmpty()) queryB.append(" where ");
		int j = 0;
		for(int i=0; i<terms.size(); i++) {
			queryB.append("lower(s.name) like :term" + j);
//			queryB.append(" or lower(SHP_ExtraData::text) like :term" + j);
			j++;
			if(i < terms.size()-1)
				queryB.append(" or ");
		}
		//select * from "Shape" where lower("SHP_Name") like '%test%' or lower("SHP_ExtraData"::text) like '%test%'
		
		queryB.append(")");
		TypedQuery<Shape> query = entityManager.createQuery(queryB.toString(), Shape.class);
		j = 0;
		for(int i=0; i<terms.size(); i++) {
			String lower = terms.get(i).toLowerCase();
			query.setParameter("term"+(i), "%"+lower+"%");
		}

		result = query.getResultList();
		
		log.debug("Shapes by name/description/extra data pattern matching:");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null) {
			for (Shape p : (List<Shape>) result) 
				log.debug("Project (" + p.getId() + ")");
		}
		
		return result;
	}
	
	@Override
	public List<Shape> searchShapesWithin(List<String> terms, Shape container)
	{
		List<Shape> result = null;
		
		StringBuilder queryB = new StringBuilder();
		queryB.append("select * from \"Shape\" s");
		queryB.append(" where ST_Within(cast (s.\"SHP_Geography\" as geometry),ST_GeometryFromText(?1,4326))=true");
		if(!terms.isEmpty()) queryB.append(" and ");
		int j = 1;
		for(int i=0; i<terms.size(); i++) {
			queryB.append("lower(s.\"SHP_Name\") like ?" + j);
			j++;
			queryB.append(" or lower(s.\"SHP_Description\") like ?" + j);
			j++;
			queryB.append(" or lower(s.\"SHP_ExtraData\") like ?" + j);
			j++;
			if(i < terms.size()-1)
				queryB.append(" or ");
		}
		//queryB.append(")");
		
		entityManager.flush();
		
		Query query = entityManager.createNativeQuery(queryB.toString());
		query.setParameter(1, container.getGeography().toText());
		j = 1;
		for(int i=0; i<terms.size(); i++) {
			String lower = terms.get(i).toLowerCase();
			query.setParameter("term"+(i), "%"+lower+"%");
		}

		result = (List<Shape>)query.getResultList();
		
		log.debug("Shapes by name/description/extra data pattern matching:");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null) {
			for (Shape p : (List<Shape>) result) 
				log.debug("Project (" + p.getId() + ")");
		}
		
		return result;
	}

	@Override
	public long countShapes(UUID si) {

		TypedQuery<Long> query = entityManager
				.createQuery(
						"select count(s) from Shape s where s.id = :id",
						Long.class);
		query.setParameter("id", si);

		return query.getSingleResult();
	}
	
	//TODO dependency to PostgreSQL
	private String getDBColumnTypeForXmlAttribute(ShapeAttributeDataType dt) throws Exception {
		switch(dt)
		{
		case SHORT:
			return dtc.getType(DataType.SHORT);
		case INTEGER:
			return dtc.getType(DataType.INTEGER);
		case LONG:
			return dtc.getType(DataType.LONG);
		case FLOAT:
			return dtc.getType(DataType.FLOAT);
		case DOUBLE:
			return dtc.getType(DataType.DOUBLE);
		case DATE:
			return dtc.getType(DataType.DATE);
		case STRING:
			return dtc.getType(DataType.STRING);
		case LONGSTRING:
			return dtc.getType(DataType.TEXT);
		}
		throw new Exception("Unrecognized data type " + dt);
	}
	
	public boolean isDBTypeTextual(String type) {
		if(type.equals("text") || type.contains("character varying"))
			return true;
		return false;
	}
	
	@Override
	public List<Shape> searchShapesWithinByAttributes(Map<String, Attribute> attrs, Shape container) {
		try {
			List<Shape> result = null;
			
			StringBuilder queryB = new StringBuilder();
			queryB.append("select s.\"SHP_ID\\:\\:character varying(36) from \"Shape\" s");
	
			queryB.append(" where ST_Within(cast (s.\"SHP_Geography\" as geometry),ST_GeometryFromText('"+container.getGeography().toText()+"',4326))=true");
			if(!attrs.isEmpty()) queryB.append(" and ");
			Iterator<Map.Entry<String, Attribute>> attrsIt = attrs.entrySet().iterator();
			while(attrsIt.hasNext()) {
				Map.Entry<String, Attribute> attr = attrsIt.next();
				queryB.append("(xpath('//extraData/*[@taxonomy=\"");
				queryB.append(attr.getValue().getTaxonomy());
				queryB.append("\"]/text()'\\:\\:text, s.shp_extradata))[1]\\:\\:text");
				String type = getDBColumnTypeForXmlAttribute(ShapeAttributeDataType.valueOf(attr.getValue().getType()));
				//queryB.append("\\:\\:");
				//queryB.append(type);
				if(isDBTypeTextual(type))
					queryB.append(" like ");
				else
					queryB.append(" = ");
				//queryB.append((isDBTypeTextual(type) ? "'":"") +  attr.getValue().getValue() + (isDBTypeTextual(type) ? "'":""));
				queryB.append("'" +  attr.getValue().getValue() + "'");
				if(attrsIt.hasNext())
					queryB.append(" and ");
			}
			//queryB.append(")");
			
			entityManager.flush();
			
			Query query = entityManager.createNativeQuery(queryB.toString());
			result = new ArrayList<Shape>();
			List<String> ids = (List<String>)query.getResultList();
			for(String id : ids)
				result.add(read(UUID.fromString(id)));
			
			log.debug("Shapes by shape/extra data matching:");
			log.debug((result != null ? result.size() : 0) + " results");
			if(log.isDebugEnabled() && result != null)
			{
				for (Shape p : (List<Shape>) result) 
					log.debug("Project (" + p.getId() + ")");
			}
			
			return result;
		}catch(Exception e) {
			log.error("An error has occurred while searching for shapes", e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public long countShapesOfLayer(UUID layerID) throws Exception {
		
		TypedQuery<Long> query = entityManager
				.createQuery(
						"select count(s) from Shape s where s.layerID = :layerID",
						Long.class);
		query.setParameter("layerID", layerID);

		return query.getSingleResult();
	}
	

	@Override
	public List<String> getAllLayerIDs(){
		log.debug("Finding all layerIDs (distinct) within Shape table ");
		List<String> result = null;
		TypedQuery<UUID> query = entityManager
				.createQuery(
						"select distinct(s.layerID) from Shape s",
						UUID.class);
		result = query.getResultList().parallelStream().map(uuid -> uuid.toString()).collect(Collectors.toList());
		log.debug("Found "+(result != null ? result.size() : 0) + " results");
		return result;
	}
	
	
	@Override
	public List<Shape> findShapesOfLayerSimple(UUID layerID) {

		log.debug("Finding shapes of layer (layerID): " + layerID);
		List<Shape> result = null;
		TypedQuery<Shape> query = entityManager
				.createQuery(
						"select s from Shape s where s.layerID = :layerID",
						Shape.class);
		query.setParameter("layerID", layerID);
		result = query.getResultList();
		log.debug("Found "+(result != null ? result.size() : 0) + " results");
		return result;

	}
	
	
	@Override
	public List<Shape> findShapesOfLayer(UUID layerID) {
		List<Shape> result = null;
		TypedQuery<Shape> query = entityManager
				.createQuery("select s from Shape s where s.layerID = :layerID", Shape.class);
		query.setParameter("layerID", layerID);
		result = query.getResultList();
		return result;
	}
	
//	@Override
//	public List<Shape> findShapesOfLayer(UUID layerID) {
//		ScrollableResults results = findShapesOfLayerScrollable(layerID);
//		List<Shape> output = new ArrayList<Shape>();
//		while(results.next())
//			output.add((Shape) results.get()[0]);
//		return output;
//	}
	
	@Override
	public ScrollableResults findShapesOfLayerScrollable(UUID layerID) {
		Session session = entityManager.unwrap(Session.class);
		return session
				.createQuery("select s from Shape s where s.layerID = :layerID")
				.setParameter("layerID", layerID)
				.setReadOnly(true)
				.setCacheable(false)
				.scroll(ScrollMode.FORWARD_ONLY);
	}
	
//	@Override
//	public StreamingOutput findShapesOfLayerStream(UUID layerID) {
//		ScrollableResults results = findShapesOfLayerScrollable(layerID);
//		StreamingOutput stream = new StreamingOutput() {
//            @Override
//            public void write(OutputStream os) throws IOException, WebApplicationException {
////                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
////                writer.write("{[");
////                if(results.next()){
////                	writer.write(mapper.writeValueAsString(new ShapeMessenger((Shape) results.get()[0])));
////                	while(results.next())
////                		writer.write("," + mapper.writeValueAsString(new ShapeMessenger((Shape) results.get()[0])));
////                }
////                writer.write("]}");
////                writer.flush();
//            }
//        };
//		return stream;
//	}
	
	
	
	
	
	
	
	
	
	//TODO: CHANGE THIS (NIKOLAS)
//	@Override
//	public List<TaxonomyTermShape> findTermMappingsOfLayerShapes(TaxonomyTerm tt) {
//		TypedQuery<TaxonomyTermShape> query = entityManager.createQuery("select tts from TaxonomyTermShape tts, ShapeTerm st, Shape s " + 
//												"join fetch tts.term as ttsTerm join fetch ttsTerm.taxonomy where st.shape=s and st.term=:tt and tts.shape=s", TaxonomyTermShape.class);
//		query.setParameter("tt", tt);
//	
//		return query.getResultList().stream().filter(StreamUtils.distinctByKey(TaxonomyTermShape::getId)).collect(Collectors.toList());
//	}
	
	
	//TODO native query depends on PostgreSQL
	@Override
	public Set<String> getAttributeValuesOfShapesByLayer(UUID layerID, Attribute attribute) {
		try {
			List<String> result = null;
	
			String type = getDBColumnTypeForXmlAttribute(ShapeAttributeDataType.valueOf(attribute.getType()));
			StringBuilder queryB = new StringBuilder();
			queryB.append("select distinct");
			queryB.append("(xpath('//extraData/*[@taxonomy=\"");
			queryB.append(attribute.getTaxonomy());
			queryB.append("\"]/text()'\\:\\:text, s.\"SHP_ExtraData\"))[1]\\:\\:text ");
			queryB.append("from \"Shape\" s, \"ShapeTerm\" st");
	
			queryB.append(" where st.\"SHPT_Term\" = '");
			queryB.append(layerID.toString());
			queryB.append("' and st.\"SHPT_Shape\" = s.shp_id");
			
			entityManager.flush();
			
			Query query = entityManager.createNativeQuery(queryB.toString());
			
			result = (List<String>)query.getResultList();
			
			log.debug("Shape attribute values for term " + layerID + " and attribute " + attribute.getName() + "(tax:" + attribute.getTaxonomy() +"):");
			log.debug((result != null ? result.size() : 0) + " results");
			if(log.isDebugEnabled() && result != null) {
				for (String p : (List<String>) result) 
					log.debug(p);
			}
			
			return new HashSet<String>(result);
		}catch(Exception e) {
			log.error("An error has occurred while searching for shapes", e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void deleteShapesOfLayer(UUID layerID) {
		
		Query query = entityManager
				.createQuery("delete from Shape s where s.layerID = :layerID)");
		query.setParameter("layerID", layerID);

		int num = query.executeUpdate();

		log.debug("Deleted all shapes of layer " + layerID.toString() + " -> " + num + " shapes deleted");
	}

	@Override
	public void deleteByShapeID(UUID shapeID){
		Query query = entityManager
				.createQuery("delete from Shape s where s.id = :shapeID)");
		query.setParameter("shapeID", shapeID);
		int num = query.executeUpdate();
		log.debug("Deleted "+num+" shapes");
	}
	
	
	@Override
	public boolean existShapesOfLayer(UUID layerID) {
		TypedQuery<Long> query = entityManager
				.createQuery(
						"select count(s) from Shape s where s.layerID = :layerID",
						Long.class);
		query.setParameter("layerID", layerID);

		if (query.getSingleResult() > 0)
			return true;
		return false;
	}

	@Override
	public UUID findLayerIDOfShape(Shape s) throws Exception {
		TypedQuery<UUID> query = entityManager.createQuery(
				"select s.layerID from Shape s where s.id = :s",
				UUID.class);
		query.setParameter("s", s.getId());

		List<UUID> res = query.getResultList();
		if (res.size() > 1)
			throw new Exception("More than one layers found for shape (that's bad)"
					+ s.getId()); // superfluous check
		if (res.isEmpty())
			return null;
		return res.get(0);
	}

	@Override
	public List<Document> findDocumentsOfShape(Shape s) {
		TypedQuery<Document> query = entityManager.createQuery(
				"select sd.document from ShapeDocument sd where sd.shape = :s",
				Document.class);
		query.setParameter("s", s);

		return query.getResultList();
	}
	
	//TODO: CHANGE THESE (NIKOLAS)
	@Override
	public Document findDocumentOfShape(Shape s, GeocodeSystem t) throws Exception {
		TypedQuery<Document> query = entityManager.createQuery(
				"select sd.document "+
				"from ShapeDocument sd, Geocode tt, GeocodeSystem t, Document d  "+
				"where tts.taxonomy = :t and tts.shape = s and sd.document = d and sd.shape = :s",
				Document.class);
		query.setParameter("s", s);
		query.setParameter("t", t);

		List<Document> res = query.getResultList();
		if (res.size() > 1)
			throw new Exception("More than one documents found for shape "
					+ s.getId() + " and taxonomy " + t.getId());
		if (res.isEmpty())
			return null;
		return res.get(0);
	}
	
	
	@Override
	public Shape envelope(Shape s) throws Exception {
		// TypedQuery<Geometry> query =
		// entityManager.createQuery("Select envelope(s.geography) from Shape s where s.id = :s",
		// Geometry.class);
		// query.setParameter("s", s.getId());
		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		Query query = entityManager
				.createNativeQuery(
						"select ST_AsText(ST_Envelope(cast (s.\"SHP_Geography\" as geometry))) from \"Shape\" s where s.\"SHP_ID\"=cast (?1 as uuid)");
		
		query.setParameter(1, s.getId().toString());
		
		String e = (String) query.getSingleResult();
		WKTReader reader = new WKTReader();
		Geometry envelope = reader.read(e);
		envelope.setSRID(4326);
		
		Shape res = new Shape();
		res.setCode(s.getCode());
		res.setCreatorID(s.getCreatorID());
		res.setGeography(envelope);
		res.setName(s.getName() + "_envelope");
		res.setShapeClass(s.getShapeClass());
		return res;
	}

	@Override
	public Shape boundary(Shape s) throws Exception {
		// TypedQuery<Geometry> query =
		// entityManager.createQuery("Select boundary(s.geography) from Shape s where s.id = :s",
		// Geometry.class);
		// query.setParameter("s", s.getId());

		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		Query sel = entityManager.createNativeQuery("select cast(s.shp_id as text) from \"Shape\" s where s.\"SHP_ID\"=cast (?1 as uuid)");
		sel.setParameter(1, s.getId().toString());
		List<Object> selRes = sel.getResultList();
		
		//TODO fix
		Query query = entityManager
				.createNativeQuery(
						"select ST_AsText(ST_Boundary(cast (s.\"SHP_Geography\" as geometry))) from \"Shape\" s where s.\"SHP_ID\"=cast (?1 as uuid)");

		query.setParameter(1, s.getId().toString());
		
		String b = (String) query.getSingleResult();
		WKTReader reader = new WKTReader();
		Geometry boundary = reader.read(b);
		boundary.setSRID(4326);

		Shape res = new Shape();
		res.setCode(s.getCode());
		res.setCreatorID(s.getCreatorID());
		res.setGeography(boundary);
		res.setName(s.getName() + "_boundary");
		res.setShapeClass(s.getShapeClass());
		return res;
	}

	@Override
	public List<Shape> findEqualsGeom(Shape s) {
		// TypedQuery<Shape> query =
		// entityManager.createQuery("from Shape s where s.id != :s and equalsGeom(s.geography, :g)",
		// Shape.class);
		// query.setParameter("s", s.getId());
		// query.setParameter("g", s.getGeography());
		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		Query query = entityManager.createNativeQuery(
				"select * from \"Shape\" s where s.\"SHP_ID\" != cast (?1 as uuid) and ST_Equals(ST_GeometryFromText(?2,4326), cast (s.\"SHP_Geography\" as geometry))=true",
				Shape.class);
		
		query.setParameter(1, s.getId().toString());
		query.setParameter(2, s.getGeography().toText());
		
		return query.getResultList();
	}

	@Override
	public boolean equalsGeom(Shape s1, Shape s2) {
		// TypedQuery<Boolean> query =
		// entityManager.createQuery("select equalsGeom(s1.geography, s2.geography) from Shape s1, s2 where s1.id=:s1 and s2.id=:s2",
		// Boolean.class);
		// query.setParameter("s1", s1.getId());
		// query.setParameter("s2", s2.getId());
		entityManager.flush();
		
		String queryString = null;
		if(s1.getId() != null)
			queryString = "select ST_Equals(cast (s1.\"SHP_Geography\" as geometry), cast (s2.\"SHP_Geography\" as geometry)) from \"Shape\" s1, \"Shape\" s2 where s1.\"SHP_ID\"=cast (?1 as uuid) and s2.\"SHP_ID\"=cast (?2 as uuid)";
		else
			queryString = "select ST_Equals(ST_GeometryFromText(?1,4326), cast (s2.\"SHP_Geography\" as geometry)) from \"Shape\" s2 where s2.\"SHP_ID\"=cast (?2 as uuid)";
		Query query = entityManager.createNativeQuery(queryString);

		if(s1.getId() != null)
			query.setParameter(1, s1.getId().toString());
		else
			query.setParameter(1, s1.getGeography().toText());
		query.setParameter(2, s2.getId().toString());
		return (Boolean) query.getSingleResult();

	}

	@Override
	public List<Shape> findDisjoint(Shape s) {
		// TypedQuery<Shape> query =
		// entityManager.createQuery("from Shape s where s.id != :s and disjoint(s.geography, :g)",
		// Shape.class);
		// query.setParameter("s", s.getId());
		// query.setParameter("g", s.getGeography());
		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		Query query = entityManager.createNativeQuery(
				"select * from \"Shape\" s where s.\"SHP_ID\" != cast (?1 as uuid) and ST_Disjoint(ST_GeometryFromText(?2,4326),cast (s.\"SHP_Geography\" as geometry))=true",
				Shape.class);
		
		query.setParameter(1, s.getId().toString());
		query.setParameter(2, s.getGeography().toText());
		
		return query.getResultList();
	}

	@Override
	public boolean disjoint(Shape s1, Shape s2) {
		// TypedQuery<Boolean> query =
		// entityManager.createQuery("select disjoint(s1.geography, s2.geography) from Shape s1, s2 where s1.id=:s1 and s2.id=:s2",
		// Boolean.class);
		// query.setParameter("s1", s1.getId());
		// query.setParameter("s2", s2.getId());

		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		String queryString = null;
		if(s1.getId() != null)
			queryString = "select ST_Disjoint(cast (s1.SHP_Geography as geometry), cast (s2.\"SHP_Geography\" as geometry)) from \"Shape\" s1, \"Shape\" s2 where s1.\"SHP_ID\"=cast (?1 as uuid) and s2.\"SHP_ID\"=cast (?2 as uuid)";
		else
			queryString = "select ST_Disjoint(ST_GeometryFromText(?1,4326), cast (s2.\"SHP_Geography\" as geometry)) from \"Shape\" s2 where s2.\"SHP_ID\"=cast (?2 as uuid)";
		Query query = entityManager.createNativeQuery(queryString);
		
		if(s1.getId() != null)
			query.setParameter(1, s1.getId().toString());
		else
			query.setParameter(1, s1.getGeography().toText());
		query.setParameter(2, s2.getId().toString());
		
		return (Boolean) query.getSingleResult();
	}

	@Override
	public List<Shape> findIntersects(Shape s) {
		TypedQuery<Shape> query = entityManager
				.createQuery(
						"from Shape s where s.id != :s and intersects(s.geography, :g)",
						Shape.class);
		query.setParameter("s", s.getId());
		query.setParameter("g", s.getGeography());

		return query.getResultList();
	}

	@Override
	public boolean intersects(Shape s1, Shape s2) {
		String queryString = null;
		if(s1.getId() != null)
			queryString = "select intersects(s1.geography, s2.geography) from Shape s1, s2 where s1.id=:s1 and s2.id=:s2";
		else
			queryString = "select intersects(:g1, s2.geography) from Shape s2 where s2.id=:s2";
		
		TypedQuery<Boolean> query = entityManager
				.createQuery(queryString, Boolean.class);
		if(s1.getId() != null) query.setParameter("s1", s1.getId());
		else query.setParameter("g1", s1.getGeography());
		query.setParameter("s2", s2.getId());

		return query.getSingleResult();
	}

	@Override
	public List<Shape> findTouches(Shape s) {
		// TypedQuery<Shape> query =
		// entityManager.createQuery("from Shape s where s.id != :s and touches(s.geography, :g)",
		// Shape.class);
		// query.setParameter("s", s.getId());
		// query.setParameter("g", s.getGeography());

		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		Query query = entityManager.createNativeQuery(
				"select * from \"Shape\" s where s.\"SHP_ID\" != cast (?1 as uuid) and ST_Touches(ST_GeometryFromText(?2,4326),cast (s.\"SHP_Geography\" as geometry))=true",
				Shape.class);

		query.setParameter(1, s.getId().toString());
		query.setParameter(2, s.getGeography().toText());
		
		return query.getResultList();
	}

	@Override
	public boolean touches(Shape s1, Shape s2)
	{
		// TypedQuery<Boolean> query =
		// entityManager.createQuery("select touches(s1.geography, s2.geography) from Shape s1, s2 where s1.id=:s1 and s2.id=:s2",
		// Boolean.class);
		// query.setParameter("s1", s1.getId());
		// query.setParameter("s2", s2.getId());
		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		String queryString = null;
		if(s1.getId() != null)
			queryString = "select ST_Touches(cast (s1.\"SHP_Geography\" as geometry), cast (s2.\"SHP_Geography\" as geometry)) from \"Shape\" s1, \"Shape\" s2 where s1.\"SHP_ID\"=cast (?1 as uuid) and s2.\"SHP_ID\"=cast (?2 as uuid)";
		else
			queryString = "select ST_Touches(ST_GeometryFromText(?1,4326), cast (s2.\"SHP_Geography\" as geometry)) from \"Shape\" s2 where s2.\"SHP_ID\"=cast (?2 as uuid)";
		
		Query query = entityManager.createNativeQuery(queryString);

		if(s1.getId() != null)
			query.setParameter(1, s1.getId().toString());
		else
			query.setParameter(1, s1.getGeography().toText());
		query.setParameter(2, s2.getId().toString());
		
		return (Boolean) query.getSingleResult();
	}

	@Override
	public List<Shape> findCrosses(Shape s)
	{
		// TypedQuery<Shape> query =
		// entityManager.createQuery("from Shape s where s.id != :s and crosses(s.geography, :g)",
		// Shape.class);
		// query.setParameter("s", s.getId());
		// query.setParameter("g", s.getGeography());

		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		Query query = entityManager.createNativeQuery(
				"select * from \"Shape\" s where s.\"SHP_ID\" != cast(?1 as uuid) and ST_Crosses(ST_GeometryFromText(?2,4326),cast (s.\"SHP_Geography\" as geometry))=true",
				Shape.class);

		query.setParameter(1, s.getId().toString());
		query.setParameter(2, s.getGeography().toText());
		
		return query.getResultList();
	}

	@Override
	public boolean crosses(Shape s1, Shape s2)
	{
		// TypedQuery<Boolean> query =
		// entityManager.createQuery("select crosses(s1.geography, s2.geography) from Shape s1, s2 where s1.id=:s1 and s2.id=:s2",
		// Boolean.class);
		// query.setParameter("s1", s1.getId());
		// query.setParameter("s2", s2.getId());
		entityManager.flush();
		
		String queryString = null;
		if(s1.getId() != null)
			queryString = "select ST_Crosses(cast (s1.\"SHP_Geography\" as geometry), cast (s2.\"SHP_Geography\" as geometry)) from \"Shape\" s1, \"Shape\" s2 where s1.\"SHP_ID\"=cast(?1 as uuid) and s2.\"SHP_ID\"=cast(?2 as uuid)";
		else
			queryString = "select ST_Crosses(ST_GeometryFromText(?1,4326), cast (s2.\"SHP_Geography\" as geometry)) from \"Shape\" s2 where s2.\"SHP_ID\"=cast(?2 as uuid)";
		
		Query query = entityManager.createNativeQuery(queryString);
		
		if(s1.getId() != null)
			query.setParameter(1, s1.getId().toString());
		else
			query.setParameter(1, s1.getGeography().toText());
		query.setParameter(2, s2.getId().toString());
		
		return (Boolean) query.getSingleResult();
	}

//	@Override
//	public List<Shape> findWithin(Shape s) {
//		return findWithin(s, null);
//	}
	
//	@Override
//	public List<Shape> findWithin(Shape s, Layer layerTerm) {
//		return findWithin(s, layerTerm, null);
//	}
	
	//TODO: CHANGE THIS (NIKOLAS)
	@Override
	public List<Shape> findWithin(Shape s/*, Layer layerTerm, Geocode term*/) {
		// TypedQuery<Shape> query =
		// entityManager.createQuery("from Shape s where s.id != :s and within(s.geography, :g)",
		// Shape.class);
		// query.setParameter("s", s.getId());
		// query.setParameter("g", s.getGeography());
		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		StringBuilder queryString = new StringBuilder("select * from \"Shape\" s");
//		if(term != null)
//			queryString.append(" join \"TaxonomyTermShape\" tts on tts.\"TAXTS_Shape\" = s.shp_id");
//		if(layerTerm != null)
//			queryString.append(" join \"ShapeTerm\" st on st.\"SHPT_Shape\" = s.shp_id");
		queryString.append(" where ");
//		if(term != null) 
//			queryString.append(" (tts.\"TAXTS_Term\" = cast(?3 as uuid)) and ");
//		if(layerTerm != null)
//			queryString.append(" (st.\"SHPT_Term\" = cast(?4 as uuid)) and ");
		if(s.getId() != null)
			queryString.append("(s.\"SHP_ID\" != cast(?1 as uuid)) and ");
		queryString.append("ST_Within(ST_GeometryFromText(?2,4326),cast (s.\"SHP_Geography\" as geometry))=true"); 
		
		Query query = entityManager.createNativeQuery(
				queryString.toString(),
				Shape.class);

		if(s.getId() != null)
			query.setParameter(1, s.getId().toString());
		query.setParameter(2, s.getGeography().toText());
//		if(term != null)
//			query.setParameter(3, term.getId().toString());
//		if(layerTerm != null)
//			query.setParameter(4,  layerTerm.getId().toString());
		
		return query.getResultList();
	}

	@Override
	public boolean within(Shape s1, Shape s2) {
		// TypedQuery<Boolean> query =
		// entityManager.createQuery("select within(s1.geography, s2.geography) from Shape s1, s2 where s1.id=:s1 and s2.id=:s2",
		// Boolean.class);
		// query.setParameter("s1", s1.getId());
		// query.setParameter("s2", s2.getId());
		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		String queryString = null;
		if(s1.getId() != null && s2.getId() != null) {
			//if(s1.getGeography() instanceof Point)
			//	queryString = "select ST_Within(s1.shp_geography,s2.shp_geography) from shape s1, shape s2 where s1.shp_id=cast (?1 as uuid) and s2.shp_id=cast (?2 as uuid)";
			//else
				queryString = "select ST_Within(cast (s1.\"SHP_Geography\" as geometry), cast (s2.\"SHP_Geography\" as geometry)) from \"Shape\" s1, \"Shape\" s2 where s1.\"SHP_ID\"=cast (?1 as uuid) and s2.\"SHP_ID\"=cast (?2 as uuid)";
		}
		else if(s1.getId() != null && s2.getId() == null) {
			queryString = "select ST_Within(cast (s1.\"SHP_Geography\" as geometry), ST_GeometryFromText(?1,4326)) from \"Shape\" s1 where s1.\"SHP_ID\"=cast (?2 as uuid)";
		}
		else if(s1.getId() == null && s2.getId() != null) {
			//if(s1.getGeography() instanceof Point)
			//	queryString = "select ST_Within(ST_GeographyFromText(?1), s2.shp_geography) from shape s2 where s2.shp_id=cast (?2 as uuid)";
			//else
				queryString = "select ST_Within(ST_GeometryFromText(?1,4326), cast (s2.\"SHP_Geography\" as geometry)) from \"Shape\" s2 where s2.\"SHP_ID\"=cast (?2 as uuid)";
		}
		
		Query query = entityManager.createNativeQuery(queryString);
		
		if(s1.getId() != null && s2.getId() != null) {
			query.setParameter(1, s1.getId().toString());
			query.setParameter(2, s2.getId().toString());
		}
		else if(s1.getId() != null && s2.getId() == null) {
			query.setParameter(1, s2.getGeography().toText());
			query.setParameter(2, s1.getId().toString());
		}
		else if(s1.getId() == null && s2.getId() != null) {
			query.setParameter(1, s1.getGeography().toText());
			query.setParameter(2, s2.getId().toString());
		}
		
		return (Boolean) query.getSingleResult();
	}
	
	@Override
	public List<Shape> findCovers(Shape s) {
		// TypedQuery<Shape> query =
		// entityManager.createQuery("from Shape s where s.id != :s and within(s.geography, :g)",
		// Shape.class);
		// query.setParameter("s", s.getId());
		// query.setParameter("g", s.getGeography());
		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		Query query = entityManager.createNativeQuery(
				"select * from \"Shape\" s where s.\"SHP_ID\" != cast(?1 as uuid) and ST_Covers(cast(s.\"SHP_Geography\" as geometry), ST_GeometryFromText(?2,4326))=true",
				Shape.class);

		query.setParameter(1, s.getId().toString());
		query.setParameter(2, s.getGeography().toText());
		
		return query.getResultList();
	}

	@Override
	public boolean covers(Shape s1, Shape s2) {
		// TypedQuery<Boolean> query =
		// entityManager.createQuery("select within(s1.geography, s2.geography) from Shape s1, s2 where s1.id=:s1 and s2.id=:s2",
		// Boolean.class);
		// query.setParameter("s1", s1.getId());
		// query.setParameter("s2", s2.getId());
		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		String queryString = null;
		if(s2.getId() != null) {
			if(s2.getGeography() instanceof Point)
				queryString = "select ST_Covers(s1.\"SHP_Geography\", s2.\"SHP_Geography\") from \"Shape\" s1, \"Shape\" s2 where s1.\"SHP_ID\"=cast (?1 as uuid) and s2.\"SHP_ID\"=cast (?2 as uuid)";
			else
				queryString = "select ST_Covers(cast(s1.\"SHP_Geography\" as geometry), cast (s2.\"SHP_Geography\" as geometry)) from \"Shape\" s1, \"Shape\" s2 where s1.\"SHP_ID\"=cast (?1 as uuid) and s2.\"SHP_ID\"=cast (?2 as uuid)";
		}else {
			if(s2.getGeography() instanceof Point)
				queryString = "select ST_Covers(s2.\"SHP_Geography\", ST_GeographyFromText(?2)) from \"Shape\" s2 where s2.\"SHP_ID\"=cast (?1 as uuid)";
			else
				queryString = "select ST_Covers(cast (s2.\"SHP_Geography\" as geometry), ST_GeometryFromText(?2,4326)) from \"Shape\" s2 where s2.\"SHP_ID\"=cast (?1 as uuid)";
		}
		
		Query query = entityManager.createNativeQuery(queryString);
		
		if(s2.getId() != null)
			query.setParameter(2, s2.getId().toString());
		else
			query.setParameter(2, s2.getGeography().toText());
		query.setParameter(1, s1.getId().toString());
		
		return (Boolean) query.getSingleResult();
	}

	@Override
	public List<Shape> findContains(Shape s) {
		// TypedQuery<Shape> query =
		// entityManager.createQuery("from Shape s where s.id != :s and contains(s.geography as(geometry), :g) = true",
		// Shape.class);
		// query.setParameter("s", s.getId());
		// query.setParameter("g", s.getGeography());
		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		Query query = entityManager.createNativeQuery(
				"select * from \"Shape\" s where s.\"SHP_ID\" != cast (?1 as uuid) and ST_Contains(ST_GeometryFromText(?2,4326),cast (s.\"SHP_Geography\" as geometry))=true",
				Shape.class);
		
		query.setParameter(1, s.getId().toString());
		query.setParameter(2, s.getGeography().toText());

		return query.getResultList();
	}

	@Override
	public boolean contains(Shape s1, Shape s2) {
		// TypedQuery<Boolean> query =
		// entityManager.createQuery("select contains(s1.geography, s2.geography) from Shape s1, s2 where s1.id=:s1 and s2.id=:s2",
		// Boolean.class);
		// query.setParameter("s1", s1.getId());
		// query.setParameter("s2", s2.getId());

		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		String queryString = null;
		if(s1.getId() != null)
			queryString = "select ST_Contains(cast (s1.\"SHP_Geography\" as geometry), cast (s2.\"SHP_Geography\" as geometry)) from \"Shape\" s1, \"Shape\" s2 where s1.\"SHP_ID\"=cast (?1 as uuid) and s2.\"SHP_ID\"=cast (?2 as uuid)";
		else
			queryString = "select ST_Contains(ST_GeometryFromText(?1,4326), cast (s2.\"SHP_Geography\" as geometry)) from \"Shape\" s2 where s2.\"SHP_ID\" =cast (?2 as uuid)";
		
		Query query = entityManager.createNativeQuery(queryString);
		
		if(s1.getId() != null)
			query.setParameter(1, s1.getId().toString());
		else
			query.setParameter(1, s1.getGeography().toText());
		query.setParameter(2, s2.getId().toString());

		return (Boolean) query.getSingleResult();
	}

	@Override
	public List<Shape> findOverlaps(Shape s) {
		// TypedQuery<Shape> query =
		// entityManager.createQuery("from Shape s where s.id != :s and overlaps(s.geography, :g)",
		// Shape.class);
		// query.setParameter("s", s.getId());
		// query.setParameter("g", s.getGeography());
		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		Query query = entityManager.createNativeQuery(
				"select * from \"Shape\" s where s.\"SHP_ID\" != cast (?1 as uuid) and ST_Overlaps(ST_GeometryFromText(?2,4326),cast (s.\"SHP_Geography\" as geometry))=true",
				Shape.class);

		query.setParameter(1, s.getId().toString());
		query.setParameter(2, s.getGeography().toText());

		return query.getResultList();
	}

	@Override
	public boolean overlaps(Shape s1, Shape s2) {
		// TypedQuery<Boolean> query =
		// entityManager.createQuery("select overlaps(s1.geography, s2.geography) from Shape s1, s2 where s1.id=:s1 and s2.id=:s2",
		// Boolean.class);
		// query.setParameter("s1", s1.getId());
		// query.setParameter("s2", s2.getId());

		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		String queryString = null;
		if(s1.getId() != null)
			queryString = "select ST_Overlaps(cast (s1.\"SHP_Geography\" as geometry), cast (s2.\"SHP_Geography\" as geometry)) from \"Shape\" s1, \"Shape\" s2 where s1.\"SHP_ID\"=cast (?1 as uuid) and s2.\"SHP_ID\"=cast (?2 as uuid)";
		else
			queryString = "select ST_Overlaps(ST_GeometryFromText(?1,4326), cast (s2.\"SHP_Geography\" as geometry)) from \"Shape\" s2 where s2.\"SHP_ID\" =cast (?2 as uuid)";

		Query query = entityManager.createNativeQuery(queryString);
		
		if(s1.getId() != null)
			query.setParameter(1, s1.getId().toString());
		else
			query.setParameter(1, s1.getGeography().toText());
		query.setParameter(2, s2.getId().toString());

		return (Boolean) query.getSingleResult();
	}

	@Override
	public List<Shape> findRelate(Shape s) {
		// TypedQuery<Shape> query =
		// entityManager.createQuery("from Shape s where s.id != :s and relate(s.geography, :g)",
		// Shape.class);
		// query.setParameter("s", s.getId());
		// query.setParameter("g", s.getGeography());
		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		Query query = entityManager.createNativeQuery(
				"select * from \"Shape\" s where s.\"SHP_ID\" != cast(?1 as uuid) and ST_Relate(ST_GeometryFromText(?2,4326),cast (s.\"SHP_Geography\" as geometry))=true",
				Shape.class);

		query.setParameter(1, s.getId().toString());
		query.setParameter(2, s.getGeography().toText());
		
		return query.getResultList();
	}

	@Override
	public boolean relate(Shape s1, Shape s2) {
		// TypedQuery<Boolean> query =
		// entityManager.createQuery("select relate(s1.geography, s2.geometry) from Shape s1, s2 where s1.id=:s1 and s2.id=:s2",
		// Boolean.class);
		// query.setParameter("s1", s1.getId());
		// query.setParameter("s2", s2.getId());

		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		String queryString = null;
		if(s1.getId() != null)
			queryString = "select ST_Relate(cast (s1.\"SHP_Geography\" as geometry), cast (s2.\"SHP_Geography\" as geometry)) from \"Shape\" s1, \"Shape\" s2 where s1.\"SHP_ID\" =cast (?1 as uuid) and s2.\"SHP_ID\"=cast (?2 as uuid)";
		else
			queryString = "select ST_Relate(ST_GeometryFromText(?1,4326), cast (s2.\"SHP_Geography\" as geometry)) from \"Shape\" s2 where s2.\"SHP_ID\" =cast (?2 as uuid)";
		
		Query query = entityManager.createNativeQuery(queryString);
		
		if(s1.getId() != null)
			query.setParameter(1, s1.getId().toString());
		else
			query.setParameter(1, s1.getGeography().toText());
		query.setParameter(2, s2.getId().toString());

		return (Boolean) query.getSingleResult();
	}

	@Override
	public List<Shape> findDistanceEqual(Shape s, double d) {
		TypedQuery<Shape> query = entityManager
				.createQuery(
						"from Shape s where s.id != :s and distance(s.geography, :g) = :d",
						Shape.class);
		query.setParameter("s", s.getId());
		query.setParameter("g", s.getGeography());
		query.setParameter("d", d);

		return query.getResultList();
	}

	@Override
	public List<Shape> findDistanceGreater(Shape s, double d) {
		TypedQuery<Shape> query = entityManager
				.createQuery(
						"from Shape s where s.id != :s and distance(s.geography, :g) > :d",
						Shape.class);
		query.setParameter("s", s.getId());
		query.setParameter("g", s.getGeography());
		query.setParameter("d", d);

		return query.getResultList();
	}

	@Override
	public List<Shape> findDistanceGreaterOrEqual(Shape s, double d) {
		TypedQuery<Shape> query = entityManager
				.createQuery(
						"from Shape s where s.id != :s and distance(s.geography, :g) >= :d",
						Shape.class);
		query.setParameter("s", s.getId());
		query.setParameter("g", s.getGeography());
		query.setParameter("d", d);

		return query.getResultList();
	}

	@Override
	public List<Shape> findDistanceLess(Shape s, double d) {
		TypedQuery<Shape> query = entityManager
				.createQuery(
						"from Shape s where s.id != :s and distance(s.geography, :g) < :d",
						Shape.class);
		query.setParameter("s", s.getId());
		query.setParameter("g", s.getGeography());
		query.setParameter("d", d);

		return query.getResultList();
	}

	@Override
	public List<Shape> findDistanceLessOrEqual(Shape s, double d)
	{
		TypedQuery<Shape> query = entityManager
				.createQuery(
						"from Shape s where s.id != :s and distance(s.geography, :g) <= :d",
						Shape.class);
		query.setParameter("s", s.getId());
		query.setParameter("g", s.getGeography());
		query.setParameter("d", d);

		return query.getResultList();
	}

	@Override
	public double distance(Shape s1, Shape s2) {
		TypedQuery<Double> query = entityManager
				.createQuery(
						"select distance(s1.geography, s2.geography) from Shape s1, s2 where s1.id=:s1 and s2.id=:s2",
						Double.class);
		query.setParameter("s1", s1.getId());
		query.setParameter("s2", s2.getId());

		return query.getSingleResult();
	}
	
	@Override
	public double area(Shape s) {
		entityManager.flush();
		
		Query query = entityManager
				.createNativeQuery(
						"select cast(ST_Area(s.\"SHP_Geography\") as text) from \"Shape\" s where s.\"SHP_ID\"=cast (?1 as uuid)");

		query.setParameter(1, s.getId().toString());
		
		String res = (String)query.getSingleResult();
		return Double.parseDouble(res);
	}

	@Override
	public Shape buffer(Shape s, float d) throws Exception {
		TypedQuery<Geometry> query = entityManager.createQuery(
				"select buffer(s.geography,:d) from Shape s where s.id = :s",
				Geometry.class);
		query.setParameter("s", s.getId());
		query.setParameter("d", d);

		Geometry buffer = query.getSingleResult();
		buffer.setSRID(4326);
		
		Shape res = new Shape();
		res.setCode(s.getCode());
		res.setCreatorID(s.getCreatorID());
		res.setGeography(buffer);
		res.setName(s.getName() + "_buffer");
		res.setShapeClass(s.getShapeClass());

		return res;
	}

	@Override
	public Shape convexHull(Shape s) throws Exception
	{
		// TypedQuery<Geometry> query =
		// entityManager.createQuery("select convexHull(s) from Shape s where s.id = :s",
		// Geometry.class);
		// query.setParameter("s", s.getId());
		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		Query query = entityManager
				.createNativeQuery(
						"select ST_AsText(ST_ConvexHull(cast (s.\"SHP_Geography\" as geometry))) from \"Shape\" s where s.\"SHP_ID\"=cast (?1 as uuid)");

		query.setParameter(1, s.getId().toString());
		
		String ch = (String) query.getSingleResult();
		WKTReader reader = new WKTReader();
		Geometry convexHull = reader.read(ch);
		convexHull.setSRID(4326);
		
		Shape res = new Shape();
		res.setCode(s.getCode());
		res.setCreatorID(s.getCreatorID());
		res.setGeography(convexHull);
		res.setName(s.getName() + "_convexHull");
		res.setShapeClass(s.getShapeClass());

		return res;
	}

	@Override
	public Shape intersection(Shape s1, Shape s2) throws Exception {
		TypedQuery<Geometry> query = entityManager
				.createQuery(
						"select intersection(s1.geography, s2.geography) from Shape s1, s2 where s1.id = :s1 and s2.id = :s2",
						Geometry.class);
		query.setParameter("s1", s1.getId());
		query.setParameter("s2", s2.getId());

		Geometry intersection = query.getSingleResult();
		intersection.setSRID(4326);

		Shape res = new Shape();
		res.setCode(s1.getCode());
		res.setCreatorID(s1.getCreatorID());
		res.setGeography(intersection);
		res.setName(s1.getName());
		res.setShapeClass(s1.getShapeClass());

		return res;
	}
	
	@Override
	public Shape union(Shape s1, Shape s2) throws Exception {
		// TypedQuery<Geometry> query =
		// entityManager.createQuery("select geomUnion(s1, s2) from Shape s1, s2 where s1.id = :s1 and s2.id = :s2",
		// Geometry.class);
		// query.setParameter("s1", s1.getId());
		// query.setParameter("s2", s2.getId());

		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		Query query = entityManager
				.createNativeQuery(
						"select ST_AsText(ST_Union(cast (s1.\"SHP_Geography\" as geometry), cast (s2.\"SHP_Geography\" as geometry))) from \"Shape\" s1, \"Shape\" s2 where s1.\"SHP_ID\"=cast (?1 as uuid) and s2.\"SHP_ID\"=cast (?2 as uuid)");
		
		query.setParameter(1, s1.getId().toString());
		query.setParameter(2, s2.getId().toString());

		Object u1 = query.getSingleResult();
		String u = (String) query.getSingleResult();
		WKTReader reader = new WKTReader();
		Geometry union = reader.read(u);
		union.setSRID(4326);
		
		Shape res = new Shape();
		res.setCode(s1.getCode());
		res.setCreatorID(s1.getCreatorID());
		res.setGeography(union);
		res.setName(s1.getName());
		res.setShapeClass(s1.getShapeClass());

		return res;
	}

	@Override
	public Shape difference(Shape s1, Shape s2) throws Exception {
		// TypedQuery<Geometry> query =
		// entityManager.createQuery("select difference(s1, s2) from Shape s1, s2 where s1.id = :s1 and s2.id = :s2",
		// Geometry.class);
		// query.setParameter("s1", s1.getId());
		// query.setParameter("s2", s2.getId());

		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		Query query = entityManager
				.createNativeQuery(
						"select ST_AsText(ST_Difference(cast (s1.\"SHP_Geography\" as geometry), cast (s2.\"SHP_Geography\" as geometry))) from \"Shape\" s1, \"Shape\" s2 where s1.\"SHP_ID\"=cast (?1 as uuid) and s2.\"SHP_ID\"=cast (?2 as uuid)");

		query.setParameter(1, s1.getId().toString());
		query.setParameter(2, s2.getId().toString());
		
		String d = (String) query.getSingleResult();
		WKTReader reader = new WKTReader();
		Geometry difference = reader.read(d);
		difference.setSRID(4326);
		
		Shape res = new Shape();
		res.setCode(s1.getCode());
		res.setCreatorID(s1.getCreatorID());
		res.setGeography(difference);
		res.setName(s1.getName());
		res.setShapeClass(s1.getShapeClass());

		return res;
	}

	@Override
	public Shape symDifference(Shape s1, Shape s2) throws Exception {
		// TypedQuery<Geometry> query =
		// entityManager.createQuery("select symDifference(s1, s2) from Shape s1, s2 where s1.id = :s1 and s2.id = :s2",
		// Geometry.class);
		// query.setParameter("s1", s1.getId());
		// query.setParameter("s2", s2.getId());

		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		Query query = entityManager
				.createNativeQuery(
						"select ST_AsText(ST_SymDifference(cast (s1.\"SHP_Geography\" as geometry), cast (s2.\"SHP_Geography\" as geometry))) from \"Shape\" s1, \"Shape\" s2 where s1.\"SHP_ID\"=cast (?1 as uuid) and s2.\"SHP_ID\"=cast (?2 as uuid)");

		query.setParameter(1, s1.getId().toString());
		query.setParameter(2, s2.getId().toString());
		
		String d = (String) query.getSingleResult();
		WKTReader reader = new WKTReader();
		Geometry difference = reader.read(d);
		difference.setSRID(4326);

		Shape res = new Shape();
		res.setCode(s1.getCode());
		res.setCreatorID(s1.getCreatorID());
		res.setGeography(difference);
		res.setName(s1.getName());
		res.setShapeClass(s1.getShapeClass());

		return res;
	}

	@Override
	public Shape transform(Shape s, int srid) throws Exception {
		// TypedQuery<Geometry> query =
		// entityManager.createQuery("select transform(s,:srid) from Shape s where s.id = :s",
		// Geometry.class);
		// query.setParameter("s", s.getId());
		// query.setParameter("srid", srid);
		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		Query query = entityManager.createNativeQuery(
				"select ST_AsText(ST_Transform(cast (s.\"SHP_Geography\" as geometry)),?1) from \"Shape\" s where s.\"SHP_ID\"=cast (?2 as uuid)");

		query.setParameter(1, srid);
		query.setParameter(2, s.getId().toString());
		
		String t = (String) query.getSingleResult();
		WKTReader reader = new WKTReader();
		Geometry transformed = reader.read(t);
		transformed.setSRID(4326);

		Shape res = new Shape();
		res.setCode(s.getCode());
		res.setCreatorID(s.getCreatorID());
		res.setGeography(transformed);
		res.setName(s.getName());
		res.setShapeClass(s.getShapeClass());

		return res;
	}

	@Override
	public Shape extent(Shape s) throws Exception {
		// TypedQuery<Geometry> query =
		// entityManager.createQuery("select extent(s) from Shape s where s.id = :s",
		// Geometry.class);
		// query.setParameter("s", s.getId());

		// TODO replace native query with JPQL when geography type is supported
		// for the query
		entityManager.flush();
		
		Query query = entityManager
				.createNativeQuery(
						"select ST_AsText(ST_Extent(cast (s.\"SHP_Geography\" as geometry))) from \"Shape\" s where s.\"SHP_ID\"=cast (?1 as uuid)");

		query.setParameter(1, s.getId().toString());
		
		String e = (String) query.getSingleResult();
		WKTReader reader = new WKTReader();
		Geometry extent = reader.read(e);
		extent.setSRID(4326);

		Shape res = new Shape();
		res.setCode(s.getCode());
		res.setCreatorID(s.getCreatorID());
		res.setGeography(extent);
		res.setName(s.getName() + "_extent");
		res.setShapeClass(s.getShapeClass());

		return res;
	}
	
	
//	//THIS SHOULD BE DELETED (TAXONOMIES WILL NOT HAVE SHAPES ANYMORE, BUT WILL ACT AS TAGS)
//	@Override
//	public Shape getShapeFromLayerTermAndShapeTerm(TaxonomyTerm layerTerm, TaxonomyTerm termForShape){
//		
//		List<Shape> result = null;
//		
//		TypedQuery<Shape> query = entityManager.createQuery("select tts.shape from TaxonomyTermShape tts, ShapeTerm st " + 
//				"where st.term=:layerTerm and tts.term=:termForShape and tts.shape=st.shape", Shape.class);
//		
//		query.setParameter("layerTerm", layerTerm);
//		query.setParameter("termForShape", termForShape);
//		
//		result = query.getResultList();
//		
//		if (!result.isEmpty()){
//			return result.get(0);
//		}else{
//			return null;
//		}
//	}
	
//	//THIS SHOULD BE DELETED (TAXONOMIES WILL NOT HAVE SHAPES ANYMORE, BUT WILL ACT AS TAGS)
//	@Override
//	public Map<String, Shape> getShapesFromLayerTerm(TaxonomyTerm layerTerm){		
//		List<Object[]> queryResults = null;
//		
//		TypedQuery<Object[]> query = entityManager.createQuery("select tt.name, tts.shape from TaxonomyTermShape tts, ShapeTerm st, TaxonomyTerm tt " + 
//				"where st.term=:layerTerm and tts.shape=st.shape and tts.term = tt.id", Object[].class);
//		
//		query.setParameter("layerTerm", layerTerm);
//		
//		queryResults = query.getResultList();
//		
//		Map<String, Shape> results = new HashMap<>();
//		for(Object[] entry : queryResults){
//			String name = (String) entry[0];
//			Shape shape = (Shape) entry[1];
//			results.put(name, shape);
//		}
//		
//		return results;
//	}
	
//	//THIS SHOULD BE DELETED (TAXONOMIES WILL NOT HAVE SHAPES ANYMORE, BUT WILL ACT AS TAGS)
//	@Override
//	public TaxonomyTerm getTermFromLayerTermAndShape(TaxonomyTerm layerTerm, Shape shape) {
//		
//		List<TaxonomyTerm> result = null;
//		
//		TypedQuery<TaxonomyTerm> query = entityManager.createQuery("select tts.term from TaxonomyTermShape tts, ShapeTerm st " + 
//				"where st.term=:layerTerm and tts.shape=:shape and tts.shape=st.shape", TaxonomyTerm.class);
//		
//		query.setParameter("layerTerm", layerTerm);
//		query.setParameter("shape", shape);
//		
//		result = query.getResultList();
//		
//		if (!result.isEmpty()){
//			return result.get(0);
//		}else{
//			return null;
//		}
//	}

//	//THIS SHOULD BE DELETED (TAXONOMIES WILL NOT HAVE SHAPES ANYMORE, BUT WILL ACT AS TAGS)
//	@Override
//	public List<TaxonomyTerm> findTaxononyTermShapes(Shape shape) throws Exception {
//		
//		TypedQuery<TaxonomyTerm> query = entityManager.createQuery("select tts.term from TaxonomyTermShape tts " + 
//		"where tts.shape = :shape", TaxonomyTerm.class);
//		
//		query.setParameter("shape", shape);
//		
//		return query.getResultList();
//	}

	@Override
	public Shape loadDetails(Shape s) {
//		s.getExtraData();
//		if(s.getShapeImport() != null)
//			s.getShapeImport().getId();
		return s;
	}
}
