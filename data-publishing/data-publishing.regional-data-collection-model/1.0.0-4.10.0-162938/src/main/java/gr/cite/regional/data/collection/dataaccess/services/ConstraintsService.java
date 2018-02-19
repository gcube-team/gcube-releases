package gr.cite.regional.data.collection.dataaccess.services;

import java.util.List;

import gr.cite.regional.data.collection.dataaccess.constraints.ConstraintDefinition;
import gr.cite.regional.data.collection.dataaccess.entities.Constraint;
import gr.cite.regional.data.collection.dataccess.exceptions.ServiceException;

public interface ConstraintsService {
	public Constraint addConstraint(Constraint constraint) throws ServiceException;
	public List<Constraint> addConstraints(List<Constraint> constraints) throws ServiceException;
	public void deleteConstraint(Integer constraintId) throws ServiceException;
	public Constraint updateConstraint(Constraint constraint) throws ServiceException;
	public Constraint getConstraint(Integer id) throws ServiceException;
	public Constraint updateConstraintFromDto(ConstraintDefinition constraintDefinition) throws ServiceException;
	public List<Constraint> getConstraintsByDataModelId(Integer dataModelId) throws ServiceException;
	public List<Constraint> getDatatypeAttributeConstraintsByDataModelId(Integer dataModelId) throws ServiceException;
}
