package org.gcube.data.analysis.statisticalmanager;


import java.util.List;

import org.gcube.data.analysis.statisticalmanager.persistence.SMPersistenceManager;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMResource;



public class HibernateManagerTest {

	public static void main(String[] args) throws Exception {
		
		
		
//		HibernateManager.buildSessionFactory(null, null, null);
//		SMComputations list = SMPersistenceManager.getComputations("gianpaolo.coro", null, null);
//		System.out.println("" +list.getList().length);
//		SMPersistenceManager.removeImporter(1464);
//		SMPersistenceManager.removeResource("timeseries_id76ec48a4_e46e_4a45_a95c_460a33888c46");
		
//		Session session = HibernateManager.getSessionFactory().openSession();
//		try {
//			Query query = session
//			.createQuery("select importer from SMImport  importer "
//					+ "where importer.portalLogin like :name ");
//
//							
//			query.setParameter("name", "gianpaolo.coro");
//
//			@SuppressWarnings("unchecked")
//			List<Object> objects = query.list();
//
//			SMImport[] importers = objects
//					.toArray(new SMImport[objects.size()]);
//			SMImporters imps =  new SMImporters(importers);
//
//		} finally {
//			session.close();
//		}
		
//		
		List<SMResource> list =  SMPersistenceManager.getResources("gianpaolo.coro", null);
		System.out.println("SIZE" + list.size());
		for (SMResource resource : list) {
			System.out.println(resource.getResourceType());
			System.out.println("" +resource.getPortalLogin());
		}
		
//		SMEntries entries = new SMEntries();
//		SMComputationConfig config = new SMComputationConfig(new SMAlgorithm("algoCategory", "algoDescript", "algoName"), entries);
//		SMComputationRequest request = new SMComputationRequest(config, "compDesc", "compTitle", "antonio.gioia");
//
//		long computId = SMPersistenceManager.addComputation(request);
//
//		SMTable table = new SMTable("tableTemplate");
//		table.setPortalLogin("antonio.gioia");
//		table.setAlgorithm("tableAlgo");
//		table.setCreationDate(Calendar.getInstance());
//		table.setDescription("tableDescr");
//		table.setName("tableName");
//		table.setProvenance(SMOperationType.COMPUTED.ordinal());
//		table.setResourceId(UUID.randomUUID().toString());
//		table.setResourceType(SMResourceType.TABULAR.ordinal());
//		table.setTemplate("Template");		
//
//		SMPersistenceManager.addCreatedResource(computId, table);
		
//		SMPersistenceManager.getComputations("gianpaolo.coro", null, null);
		
//		SMFile file = new SMFile("mimeType", "remoteName", "url");
//		file.setResourceId("resourceId");
//		file.setPortalLogin("antonio.gioia");
//		file.setProvenance(0);
//		SMPersistenceManager.addCreatedResource(file);
		
//		SMImporters list = SMPersistenceManager.getUncheckedImports("gianpaolo.coro");

//		String computationId = "464";
//		Session session = getSessionFactory().openSession();
//		Transaction t = session.beginTransaction();
//		try {
			
//			String user = "gianpaolo.coro";
//			String resourceId = "presence_data_latimeria";
//			
//			SMImport importer = new SMImport();
//			
//			SMAbstractResource resource = new SMAbstractResource();
//			resource.setAbstractResourceId(resourceId);
//			
//			SMTable table = new SMTable();
//			table.setResourceType(SMResourceType.TABULAR.ordinal());
//			table.setResourceId(resourceId);
//			table.setDescription(resourceId);
//			table.setTemplate(TableTemplates.HCAF.toString());
//			table.setName(resourceId);
//			session.save(table);
//			
//			resource.setResource(table);
//			session.save(resource);
//			
//			importer.setAbstractResource(resource);
//			importer.setPortalLogin(user);
//			importer.setDescription("table description");
//			importer.setOperationStatus(SMOperationStatus.COMPLETED.ordinal());
//			importer.setSubmissionDate(Calendar.getInstance());
//			importer.setCompletedDate(Calendar.getInstance());
			
//			SMTable table = new SMTable("tableTemplate");
//			table.setPortalLogin("antonio.gioia");
//			table.setAlgorithm("tableAlgo");
//			table.setCreationDate(Calendar.getInstance());
//			table.setDescription("tableDescr");
//			table.setName("tableName");
//			table.setProvenance(SMOperationType.COMPUTED.ordinal());
//			table.setResourceId(UUID.randomUUID().toString());
//			table.setResourceType(SMResourceType.TABULAR.ordinal());
//			table.setTemplate("Template");	
			
//			session.save(table);
//			t.commit();
//		} finally {
//			session.close();
//		}
		

		
//		SMComputations computations = SMPersistenceManager.getComputations("fabio.sinibaldi","OCCURRENCES_INSEAS_ONEARTH", "TRANSDUCERERS");
//		System.out.println(" computations size " + computations.getList().length);
		
//		SMImporters importers = SMPersistenceManager.getUncheckedImports("gianpaolo.coro");
//		System.out.println(importers.getList().length);
//		
//		String resourceId = "occurrencecells";
//		SMTable table = new SMTable();
//		table.setDescription(resourceId);
//		table.setName(resourceId);
//		table.setResourceId(resourceId);
//		table.setResourceType(SMResourceType.TABULAR.ordinal());
//		table.setTemplate(TableTemplates.OCCURRENCE_AQUAMAPS.toString());
//		
//		HibernateManager.buildSessionFactory(null, null, null);
//		SMPersistenceManager.addSystemImporter(resourceId, table);
		
		
//		List<SMTableMetadata> tables = SMPersistenceManager.getTables("gianpaolo.coro", null);
//		System.out.println("size " + tables.size());
//		System.out.println("table id" +tables.get(0).getTableId());
	}

}
