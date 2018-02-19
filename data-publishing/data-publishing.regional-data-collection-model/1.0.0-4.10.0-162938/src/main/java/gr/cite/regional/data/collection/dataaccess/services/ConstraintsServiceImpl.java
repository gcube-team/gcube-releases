package gr.cite.regional.data.collection.dataaccess.services;

import java.util.List;

import javax.transaction.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.cite.regional.data.collection.dataaccess.constraints.ConstraintDefinition;
import gr.cite.regional.data.collection.dataaccess.daos.ConstraintDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gr.cite.regional.data.collection.dataaccess.daos.DataModelDao;
import gr.cite.regional.data.collection.dataaccess.entities.Constraint;
import gr.cite.regional.data.collection.dataaccess.entities.DataModel;
import gr.cite.regional.data.collection.dataccess.exceptions.ServiceException;

@Service
public class ConstraintsServiceImpl implements ConstraintsService {

	static final ObjectMapper objectMapper = new ObjectMapper();
	private ConstraintDao constraintDao;
	private DataModelDao dataModelDao;

	@Autowired
	ConstraintsServiceImpl(ConstraintDao constraintDao, DataModelDao dataModelDao){
		this.constraintDao = constraintDao;
		this.dataModelDao = dataModelDao;
	}

	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public Constraint addConstraint(Constraint constraint) throws ServiceException {
		try {
			setManagedReferences(constraint);

			constraint = this.constraintDao.create(constraint);
		} catch(Exception e) {
			throw new ServiceException("Persistence error on Constraint insertion", e);
		}

		return constraint;
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public List<Constraint> addConstraints(List<Constraint> constraints) throws ServiceException {
		for (Constraint constraint: constraints) {
			addConstraint(constraint);
		}
		return constraints;
	}
	

	@Transactional(rollbackOn = ServiceException.class)
	public void setManagedReferences(Constraint constraint) throws ServiceException {
		DataModel dm = this.dataModelDao.read(constraint.getDataModel().getId());

		constraint.setDataModel(dm);
	}

	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public void deleteConstraint(Integer constraintId) throws ServiceException {
		try {
			Constraint constraint = this.constraintDao.read(constraintId);
			if (constraint == null) {
				throw new ServiceException("Constraint [" + constraintId + "] does not exist");
			}
			this.constraintDao.delete(constraint);
		} catch (Exception e) {
			throw new ServiceException("Persistence error on Constraint deletion", e);
		}
	}

	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public Constraint getConstraint(Integer id) throws ServiceException {
		Constraint constraint;
		try {
			constraint = this.constraintDao.read(id);
		} catch (Exception e) {
			throw new ServiceException("Persistence error on Constraint [" + id + "] retrieval", e);
		}
		
		if (constraint == null) {
			throw new ServiceException("Constraint [" + id + "] does not exist");
		}
		
		return constraint;
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public List<Constraint> getConstraintsByDataModelId(Integer dataModelId) throws ServiceException {
		try {
			return this.constraintDao.getConstraintsByDataModelId(dataModelId);
		} catch (Exception e) {
			throw new ServiceException("Persistence error on Constraint selection", e);
		}
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public List<Constraint> getDatatypeAttributeConstraintsByDataModelId(Integer dataModelId) throws ServiceException {
		try {
			return this.constraintDao.getDatatypeAttributeConstraintsByDataModelId(dataModelId);
		} catch (Exception e) {
			throw new ServiceException("Persistence error on DatatypeAttributeConstraint selection", e);
		}
	}

	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public Constraint updateConstraint(Constraint constraint) throws ServiceException {
		Constraint currentConstraint = getConstraint(constraint.getId());
		replaceModifiedFields(constraint, currentConstraint);
		
		try {
			return this.constraintDao.update(currentConstraint);
		} catch (Exception e) {
			throw new ServiceException("Persistence error on Constraint [" + constraint.getId() + "] update", e);
		}
	}

	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public Constraint updateConstraintFromDto(ConstraintDefinition constraintDefinition) throws ServiceException {
		Constraint constraint = getConstraint(constraintDefinition.getId());
		
		replaceModifiedFieldsFromDto(constraint, constraintDefinition);
		
		try {
			constraint = this.constraintDao.update(constraint);
		} catch (Exception e) {
			throw new ServiceException("Constraint [" + constraint.getId() + "] update failed", e);
		}

		return constraint;
	}

	private void replaceModifiedFields(Constraint newConstraint, Constraint currentConstraint) {
		if (newConstraint.getDataModel() != null) {
			currentConstraint.setDataModel(newConstraint.getDataModel());
		}
		if (newConstraint.getConstraint() != null) {
			currentConstraint.setConstraint(newConstraint.getConstraint());
		}
		if ( newConstraint.getConstraint() != null && newConstraint.getConstraint().getConstraintType() != null ) {
			currentConstraint.setConstraintType(newConstraint.getConstraint().getConstraintType());
		}
	}

	private void replaceModifiedFieldsFromDto(Constraint constraint, ConstraintDefinition constraintDefinition) {
		if (constraintDefinition != null) {
			constraint.setConstraint(constraintDefinition);
			if (constraintDefinition.getConstraintType() != null) {
				constraint.setConstraintType(constraintDefinition.getConstraintType());
			}
		}
		/*Map<String, Object> jsonMap;
		jsonMap = objectMapper.convertValue(constraintDefinition, new TypeReference<Map<String, Object>>() {});*/
		
		//constraint.setConstraintType(constraintDefinition.getConstraintType());
	}
	
}