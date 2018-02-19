package gr.cite.regional.data.collection.dataaccess.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.cite.regional.data.collection.dataaccess.daos.CdtDao;
import gr.cite.regional.data.collection.dataaccess.dsd.DataModelDefinition;
import gr.cite.regional.data.collection.dataaccess.dsd.DsdProcessing;
import gr.cite.regional.data.collection.dataaccess.dsd.Field;
import gr.cite.regional.data.collection.dataaccess.dsd.ColumnAndType;
import gr.cite.regional.data.collection.dataaccess.constraints.AttributeDatatypeConstraint;
import gr.cite.regional.data.collection.dataaccess.entities.Cdt;
import gr.cite.regional.data.collection.dataaccess.entities.Constraint;
import gr.cite.regional.data.collection.dataaccess.entities.DataCollection;
import gr.cite.regional.data.collection.dataaccess.entities.DataSubmission;
import gr.cite.regional.data.collection.dataaccess.entities.Status;
import gr.cite.regional.data.collection.dataccess.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CdtServiceImpl implements CdtService {
	private static final ObjectMapper mapper = new ObjectMapper();
	
	private CdtDao cdtDao;
	private ConstraintsService constraintsService;
	private DsdProcessing dsdProcessing;
	
	@Autowired
	public CdtServiceImpl(CdtDao cdtDao, ConstraintsService constraintsService, DsdProcessing dsdProcessing) {
		this.cdtDao = cdtDao;
		this.constraintsService = constraintsService;
		this.dsdProcessing = dsdProcessing;
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public void createCdtTable(DataCollection dataCollection) throws ServiceException {
		try {
			this.cdtDao.createTable(dataCollection.getId().toString(), getColumnsAndTypes(dataCollection));
		} catch (Exception e) {
			throw new ServiceException("Error on CDT table creation", e);
		}
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public Cdt addCdt(Cdt cdt) throws ServiceException {
		DataCollection dataCollection = cdt.getDataSubmission().getDataCollection();
		cdt.setStatus(Status.ACTIVE.getStatusCode());
		
		try {
			return this.cdtDao.create(cdt, getColumnsAndTypes(dataCollection), dataCollection.getId().toString());
		} catch (Exception e) {
			throw new ServiceException("Persistence error on CDT insertion", e);
		}
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public List<Cdt> addCdts(List<Cdt> cdts) throws ServiceException {
		List<Cdt> newCdts = new ArrayList<>();
		for (Cdt cdt: cdts) {
			DataCollection dataCollection = cdt.getDataSubmission().getDataCollection();
			cdt.setStatus(Status.ACTIVE.getStatusCode());
			
			try {
				newCdts.add(this.cdtDao.create(cdt, getColumnsAndTypes(dataCollection), dataCollection.getId().toString()));
			} catch (Exception e) {
				throw new ServiceException("Persistence error on CDT insertion", e);
			}
		}
		
		return newCdts;
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public Cdt updateCdt(Cdt cdt) throws ServiceException {
		DataCollection dataCollection = cdt.getDataSubmission().getDataCollection();
		Cdt currentCdt = getCdt(cdt.getId(), dataCollection.getId());
		replaceModifiedFields(cdt, currentCdt);
		
		try {
			return this.cdtDao.update(cdt, getColumnsAndTypes(dataCollection), dataCollection.getId().toString());
		} catch (Exception e) {
			throw new ServiceException("Persistence error on CDT [" + cdt.getId() + "] update", e);
		}
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public List<Cdt> updateCdts(List<Cdt> cdts) throws ServiceException {
		List<Cdt> newCdts = new ArrayList<>();
		for (Cdt cdt: cdts) {
			DataCollection dataCollection = cdt.getDataSubmission().getDataCollection();
			Cdt currentCdt = getCdt(cdt.getId(), dataCollection.getId());
			replaceModifiedFields(cdt, currentCdt);
			
			try {
				newCdts.add(this.cdtDao.update(cdt, getColumnsAndTypes(dataCollection), dataCollection.getId().toString()));
			} catch (Exception e) {
				throw new ServiceException("Persistence error on CDT [" + cdt.getId() + "] update", e);
			}
		}
		return newCdts;
	}
	
	private Cdt replaceModifiedFields(Cdt cdt, Cdt currentCdt) {
		if (cdt.getOrdinal() != null) {
			currentCdt.setOrdinal(cdt.getOrdinal());
		}
		if (cdt.getStatus() != null) {
			currentCdt.setStatus(cdt.getStatus());
		}
		if (cdt.getDataSubmission() != null) {
			cdt.getData().forEach((key, value) -> currentCdt.getData().put(key, value));
		}
		return currentCdt;
	}
	
	private List<ColumnAndType> getColumnsAndTypes(DataCollection dataCollection) throws ServiceException {
		String definition = dataCollection.getDataModel().getDefinition();
		List<String> columns = this.dsdProcessing.getDefinitionForExcelAddIn(definition).getFields().stream()
				.sorted(Comparator.comparingInt(Field::getOrder)).map(Field::getLabel).collect(Collectors.toList());
		
		List<Constraint> constraints = this.constraintsService.getDatatypeAttributeConstraintsByDataModelId(dataCollection.getDataModel().getId());
		
		//Map<String, AttributeDatatypeConstraint.DataType> types = this.constraintsService.getDatatypeAttributeConstraintsByDataModelId(dataCollection.getDataModel().getId()).stream()
		Map<String, AttributeDatatypeConstraint.DataType> types = constraints.stream()
				.map(constraint -> mapper.convertValue(constraint.getConstraint(), AttributeDatatypeConstraint.class))
				.collect(Collectors.toMap(AttributeDatatypeConstraint::getField, AttributeDatatypeConstraint::getDatatype));
		
		return columns.stream()
				.map(column -> new ColumnAndType(column, types.get(column) != null ? types.get(column).toValue() : "string"))
				.collect(Collectors.toList());
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public void deactivateCdtsOfDataSubmission(DataSubmission dataSubmission) throws ServiceException {
		List<Cdt> cdts = getCdtByDataSubmission(dataSubmission);
		cdts.forEach(cdt -> cdt.setStatus(Status.INACTIVE.getStatusCode()));
		for (Cdt cdt: cdts) {
			this.cdtDao.update(cdt, getColumnsAndTypes(dataSubmission.getDataCollection()), dataSubmission.getDataCollection().getId().toString());
		}
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public Cdt addCdts(Set<Cdt> cdt) throws ServiceException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public Cdt getCdt(UUID id, Integer dataCollectionId) throws ServiceException {
		Cdt cdt;
		try {
			cdt = this.cdtDao.read(id, dataCollectionId.toString());
		} catch (Exception e) {
			throw new ServiceException("Error on CDT [" + id + "] retrieval");
		}
		
		if (cdt == null) {
			throw new NoSuchElementException("The requested CDT [" + id + "] does not exist");
		}
		
		return cdt;
	}
	
	@Override
	public List<Cdt> getCdtByDataSubmission(DataSubmission dataSubmission) throws ServiceException {
		DataModelDefinition dataModelDefinition = this.dsdProcessing.getDefinitionForExcelAddIn(dataSubmission.getDataCollection().getDataModel().getDefinition());
		try {
			return this.cdtDao.getByDataSubmissionId(dataSubmission.getId(),dataSubmission.getDataCollection().getId().toString(), dataModelDefinition.getFields()
					.stream().map(Field::getLabel).collect(Collectors.toList()));
		} catch (Exception e) {
			throw new ServiceException("Error on Cdt by DataSubmission [" + dataSubmission.getId() + "] retrieval", e);
		}
	}
	
	@Override
	public void deleteCdtsOfDataCollection(Integer dataCollectionId) {
		this.cdtDao.deleteTable(dataCollectionId.toString());
		
	}
	
}
