package gr.cite.geoanalytics.dataaccess.dao;

import java.util.HashMap;
import java.util.Map;

import gr.cite.geoanalytics.dataaccess.entities.accounting.dao.AccountingDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.annotation.dao.AnnotationDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing;
import gr.cite.geoanalytics.dataaccess.entities.auditing.dao.AuditingDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.document.dao.DocumentDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.geocode.TaxonomyTermLink;
import gr.cite.geoanalytics.dataaccess.entities.geocode.dao.GeocodeSystemDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.geocode.dao.TaxonomyTermLinkDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerTag;
import gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerTagDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.mimetype.MimeType;
import gr.cite.geoanalytics.dataaccess.entities.mimetype.dao.MimeTypeDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.project.ProjectDocument;
import gr.cite.geoanalytics.dataaccess.entities.project.ProjectLayer;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectDocumentDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectLayerDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeDocument;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDaoImpl;
//import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDocumentDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.SysConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.dao.SysConfigDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.tag.Tag;
import gr.cite.geoanalytics.dataaccess.entities.tag.dao.TagDaoImpl;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao.GeocodeShapeDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.tenant.TenantActivation;
import gr.cite.geoanalytics.dataaccess.entities.tenant.dao.TenantActivationDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.tenant.dao.TenantDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.workflow.Workflow;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTaskDocument;
import gr.cite.geoanalytics.dataaccess.entities.workflow.dao.WorkflowDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.workflow.dao.WorkflowTaskDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.workflow.dao.WorkflowTaskDocumentDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.Entity;
import gr.cite.geoanalytics.dataaccess.entities.accounting.Accounting;
import gr.cite.geoanalytics.dataaccess.entities.annotation.Annotation;

/**
 * A DAO factory for non-managed environments
 * 
 * @author Gerasimos Farantatos
 *
 */
public class JpaDaoFactory implements DaoFactory
{
	private static String persistenceUnit = null;
	private static Map<String, String> daoImpls = null;
	
	public static void setPersistenceContext(String persistenceUnit)
	{
		JpaDaoFactory.persistenceUnit = persistenceUnit;
	}
	
	private static void populateMappings()
	{
		daoImpls = new HashMap<String, String>();
		daoImpls.put(Accounting.class.getName(), AccountingDaoImpl.class.getName());
		daoImpls.put(Auditing.class.getName(), AuditingDaoImpl.class.getName());
		daoImpls.put(Annotation.class.getName(), AnnotationDaoImpl.class.getName());
		daoImpls.put(Tenant.class.getName(), TenantDaoImpl.class.getName());
		daoImpls.put(TenantActivation.class.getName(), TenantActivationDaoImpl.class.getName());
		daoImpls.put(Document.class.getName(), DocumentDaoImpl.class.getName());
		daoImpls.put(GeocodeSystem.class.getName(), GeocodeSystemDaoImpl.class.getName());
		daoImpls.put(LayerTag.class.getName(), LayerTagDaoImpl.class.getSimpleName());	
		daoImpls.put(MimeType.class.getName(), MimeTypeDaoImpl.class.getName());
		daoImpls.put(Project.class.getName(), ProjectDaoImpl.class.getName());
		daoImpls.put(ProjectDocument.class.getName(), ProjectDocumentDaoImpl.class.getName());
		daoImpls.put(ProjectLayer.class.getName(), ProjectLayerDaoImpl.class.getName());
		daoImpls.put(Principal.class.getName(), PrincipalDaoImpl.class.getName());
		daoImpls.put(Shape.class.getName(), ShapeDaoImpl.class.getName());
//		daoImpls.put(ShapeImport.class.getName(), ShapeImportDaoImpl.class.getName());
		daoImpls.put(SysConfig.class.getName(), SysConfigDaoImpl.class.getName());
		daoImpls.put(Tag.class.getName(), TagDaoImpl.class.getSimpleName());
		daoImpls.put(TaxonomyTermLink.class.getName(), TaxonomyTermLinkDaoImpl.class.getName());
		daoImpls.put(Workflow.class.getName(), WorkflowDaoImpl.class.getName());
		daoImpls.put(WorkflowTask.class.getName(), WorkflowTaskDaoImpl.class.getName());
		daoImpls.put(WorkflowTaskDocument.class.getName(), WorkflowTaskDocumentDaoImpl.class.getName());
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Dao getDao(Class<? extends Entity> type) throws Exception
	{
		if(daoImpls == null) populateMappings();
		return (Dao)Class.forName(daoImpls.get(type.getName())).newInstance();
	}

	@Override
	public void overrideMappings(Map<String, String> mappings)
	{
		populateMappings();
		daoImpls.putAll(mappings);
	}
}
