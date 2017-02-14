package gr.cite.geoanalytics.dataaccess.test;

import gr.cite.gaap.viewbuilders.PostGISMaterializedViewBuilder;
import gr.cite.geoanalytics.common.ViewBuilder;
import gr.cite.geoanalytics.dataaccess.entities.accounting.Accounting;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.mimetype.MimeType;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalData;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeImport;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.SysConfig;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.tenant.TenantActivation;
import gr.cite.geoanalytics.manager.UserManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

public class TestEntities {
	/**
	 * TODO Check shapeterm unique constraint taxonomytermlinks unique
	 * constraints taxonomyterm string id taxonomyterm creator is not null,
	 * verify taxonomyterm extradata uuid??? -> text, verify taxonomytermshape
	 * check constraints project shape uuid instead of foreign key?
	 * 
	 * GeoServer CQL Queries GeoServer DB/SQL Views? GeoServer API (check code)
	 * 
	 * Check if feature in bbox or satisfies criteria using GeoServer? Or: use
	 * taxonomies etc..
	 * 
	 * @param args
	 * @throws Exception
	 */
	
	PostGISMaterializedViewBuilder vb = null;
	
	@Inject
	public void setVb(PostGISMaterializedViewBuilder vb) {
		this.vb = vb;
	}
	
	@Test
	public void test() throws Exception {
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("gr.cite.geopolis");
		Principal principal = new Principal();
		PrincipalData principalData = new PrincipalData();
		principal.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		principalData.setFullName("__System_User__");
		principalData.setInitials("__SU__");
		principalData.setEmail("sys@example.com");
		principal.setCreationDate(Calendar.getInstance().getTime());
		principal.setLastUpdate(Calendar.getInstance().getTime());
		principalData.setExpirationDate(new Date(3000 - 1900, 12, 31));
		principal.setName("___System_Usr___");
		principal.setCreator(principal);
		principal.setPrincipalData(principalData);

		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		Tenant cusOne = new Tenant();
		cusOne.setLastUpdate(Calendar.getInstance().getTime());
		cusOne.setName("Customer one");
		cusOne.setCode("A1");
		cusOne.setEmail("cusOne@example.com");
		cusOne.setCreationDate(Calendar.getInstance().getTime());
		cusOne.setCreator(principal);

		Principal usOne = new Principal();
		PrincipalData usDataOne = new PrincipalData();
		usDataOne.setFullName("John Smith");
		usDataOne.setInitials("JS");
		usDataOne.setEmail("j.smith@example.com");
		usOne.setCreationDate(Calendar.getInstance().getTime());
		usOne.setLastUpdate(Calendar.getInstance().getTime());
		usDataOne.setExpirationDate(new Date(2014 - 1900, 3, 29));
		usOne.setName("John.Smith");
		usOne.setCreator(principal);
		usOne.setPrincipalData(usDataOne);

		Accounting acOne = new Accounting();
		acOne.setCreationDate(Calendar.getInstance().getTime());
		acOne.setCreator(principal);
		acOne.setTenant(cusOne);
		acOne.setPrincipal(usOne);
		acOne.setDate(Calendar.getInstance().getTime());
		acOne.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		acOne.setIsValid(true);
		acOne.setLastUpdate(Calendar.getInstance().getTime());
		acOne.setReferenceData("data");
		acOne.setType(Accounting.AccountingType.Payment);
		acOne.setUnits(4);
		
		double lon = -105.0;
		double lat = 40.0;
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 8307);
		Geometry point = geometryFactory.createPoint(new Coordinate(lon, lat));
		ShapeImport shImpOne = new ShapeImport();
		shImpOne.setCreationDate(Calendar.getInstance().getTime());
		shImpOne.setCreator(usOne);
		shImpOne.setData("data");
		shImpOne.setGeography(point);
		shImpOne.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		shImpOne.setLastUpdate(Calendar.getInstance().getTime());
		shImpOne.setShapeIdentity("id");
		shImpOne.setShapeImport(UUID.fromString("00000000-0000-0000-0000-000000000001"));

		Shape s = new Shape();
		s.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		s.setCode("testCode");
		s.setCreationDate(new Date());
		s.setCreator(principal);
		s.setLastUpdate(new Date());
		s.setName("shape name");
		s.setExtraData("<shapeInfo><attr1 type=\"int\">1</attr1><attr2>val2</attr2></shapeInfo>");
		geometryFactory = new GeometryFactory(new PrecisionModel(), 8307);
		point = geometryFactory.createPoint(new Coordinate(lon, lat));
		s.setGeography(point);
		s.setShapeClass(3);
		s.setShapeImport(shImpOne);
		
		
		TenantActivation cusActOne = new TenantActivation();
		cusActOne.setActivationConfig("dfa");
		cusActOne.setCreationDate(Calendar.getInstance().getTime());
		cusActOne.setCreator(usOne);
		cusActOne.setTenant(cusOne);
		cusActOne.setEnd(Calendar.getInstance().getTime());
		cusActOne.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		cusActOne.setIsActive(false);
		cusActOne.setLastUpdate(Calendar.getInstance().getTime());
		cusActOne.setShape(s);
		cusActOne.setStart(Calendar.getInstance().getTime());
		
		Document doc = new Document();
		doc.setCreationDate(Calendar.getInstance().getTime());
		doc.setCreator(usOne);
		doc.setTenant(cusOne);
		doc.setDescription("description");
		doc.setLastUpdate(Calendar.getInstance().getTime());
		doc.setMimeSubType("ty");
		doc.setMimeType("t");
		doc.setName("document name");
		doc.setSize(4);
		doc.setUrl("url");
		
		MimeType mt = new MimeType();
		mt.setCreationDate(Calendar.getInstance().getTime());
		mt.setCreator(usOne);
		mt.setMimeType("type");
		mt.setMimeSubType("subtype");
		mt.setFileNameExtention("sub");
		mt.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		mt.setLastUpdate(Calendar.getInstance().getTime());
		
		SysConfig sc = new SysConfig();
		sc.setConfig("config");
		sc.setCreationDate(Calendar.getInstance().getTime());
		sc.setCreator(principal);
		sc.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		sc.setLastUpdate(Calendar.getInstance().getTime());
		
		Taxonomy t = new Taxonomy();
		t.setCreationDate(Calendar.getInstance().getTime());
		t.setCreator(principal);
		t.setIsActive(true);
		t.setIsUserTaxonomy(true);
		t.setLastUpdate(Calendar.getInstance().getTime());
		t.setName("Taxonomy Name");
		
		TaxonomyTerm tte = new TaxonomyTerm();
		tte.setCreationDate(Calendar.getInstance().getTime());
		tte.setCreator(principal);
		tte.setIsActive(true);
		tte.setLastUpdate(Calendar.getInstance().getTime());
		tte.setName("taxonomy term name");
		tte.setOrder(7);
		tte.setRefClassSchema(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		tte.setTaxonomy(t);

		entityManager.getTransaction().begin();
		entityManager.persist(principal);
		entityManager.persist(usOne);
		entityManager.persist(cusOne);
		entityManager.persist(acOne);
		entityManager.persist(s);
		entityManager.persist(doc);
		entityManager.persist(sc);
		entityManager.persist(mt);
		entityManager.persist(t);
		entityManager.persist(tte);
		entityManager.persist(shImpOne);
		entityManager.persist(cusActOne);
		entityManager.getTransaction().commit();

		entityManager.close();
		
		UserManager um = new UserManager();
		String vs = vb.forShape(s).createViewStatement().getViewStatement();
		vb.execute();
		System.out.println(vs);

		// now lets pull events from the database and list them
		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		List result = entityManager.createQuery("from User", Principal.class)
				.getResultList();
		System.out.println(result.size() + " results");
		for (Principal us : (List<Principal>) result) {
			System.out.println("Principal (" + us.getId() + ") : " + us.getPrincipalData().getFullName());
			System.out.println("Principal (" + us.getId() + ") email : " + us.getPrincipalData().getEmail() + " expiration: " + us.getPrincipalData().getExpirationDate());
			System.out.println("Principal (" + us.getId() + ") creator : " + us.getCreator().getPrincipalData().getFullName());
		}

		result = entityManager.createQuery("from Customer", Tenant.class)
				.getResultList();
		System.out.println(result.size() + " results");
		for (Tenant cus : (List<Tenant>) result) {
			System.out.println("Customer (" + cus.getId() + ") : " + cus.getName());
		}

		result = entityManager.createQuery("from Accounting", Accounting.class)
				.getResultList();
		System.out.println(result.size() + " results");
		for (Accounting ac : (List<Accounting>) result) {
			System.out.println("Accounting (" + ac.getId() + ")");
			System.out.println("Accounting (" + ac.getId() + ") creator : " + ac.getCreator().getPrincipalData().getFullName());
			System.out.println("Accounting (" + ac.getId() + ") customer : " + ac.getTenant().getName());
			System.out.println("Accounting (" + ac.getId() + ") user : " + ac.getPrincipal().getPrincipalData().getFullName());
		}
		
		result = entityManager.createQuery("from Document", Document.class).getResultList();
		System.out.println(result.size() + " results");
		for (Document d : (List<Document>) result) {
			System.out.println("Document (" + d.getId() + ")");
			System.out.println("Document (" + d.getId() + ") customer : " + d.getTenant().getName());
			System.out.println("Document (" + d.getId() + ") creator : " + d.getCreator().getPrincipalData().getFullName());
		}

		result = entityManager.createQuery("from MimeType", MimeType.class).getResultList();
		System.out.println(result.size() + " results");
		for (MimeType m : (List<MimeType>) result) {
			System.out.println("MimeType (" + m.getId());
			System.out.println("MimeType (" + m.getId() + ") creator : " + m.getCreator().getPrincipalData().getFullName());
		}
		
		result = entityManager.createQuery("from Shape", Shape.class).getResultList();
		System.out.println(result.size() + " results");
		for (Shape sh : (List<Shape>) result) {
			System.out.println("Shape (" + sh.getId() + ") code : " + sh.getCode() + " creationDate : " + sh.getCreationDate() +
					" extraData" + sh.getExtraData() + " geometry: " + sh.getGeography().toText() + " creator: " + sh.getCreator().getId() +
					" creator name: " + sh.getCreator().getPrincipalData().getFullName());
		}
		
		result = entityManager.createQuery("from SysConfig", SysConfig.class).getResultList();
		System.out.println(result.size() + " results");
		for (SysConfig scn : (List<SysConfig>) result) {
			System.out.println("SysConfig (" + scn.getId());
			System.out.println("SysConfig (" + scn.getId() + ") creator : " + scn.getCreator().getPrincipalData().getFullName());
		}
		
		result = entityManager.createQuery("from Taxonomy", Taxonomy.class).getResultList();
		System.out.println(result.size() + " results");
		for (Taxonomy tt : (List<Taxonomy>) result) {
			System.out.println("Taxonomy (" + tt.getId());
			System.out.println("Taxonomy (" + tt.getId() + ") creator : " + tt.getCreator().getPrincipalData().getFullName());
		}

		result = entityManager.createQuery("from TaxonomyTerm", TaxonomyTerm.class).getResultList();
		System.out.println(result.size() + " results");
		for (TaxonomyTerm tterm : (List<TaxonomyTerm>) result) {
			System.out.println("TaxonomyTerm (" + tterm.getId());
			System.out.println("TaxonomyTerm (" + tterm.getId() + ") creator : " + tterm.getCreator().getPrincipalData().getFullName());
		}
		
		result = entityManager.createQuery("from CustomerActivation", TenantActivation.class)
				.getResultList();
		System.out.println(result.size() + " results");
		for (TenantActivation ca : (List<TenantActivation>) result) {
			System.out.println("PrincipalActivation (" + ca.getId() + ")");
			System.out.println("PrincipalActivation (" + ca.getId() + ") user : " + ca.getCreator().getPrincipalData().getFullName());
			System.out.println("PrincipalActivation (" + ca.getId() + ") customer : " + ca.getTenant().getName());
		}

		
		entityManager.getTransaction().commit();
		entityManager.close();
	}

}
