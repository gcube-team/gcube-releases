package gr.cite.regional.data.collection.dataaccess.daos;

import gr.cite.regional.data.collection.dataaccess.entities.Constraint;
import org.dom4j.datatype.DatatypeAttribute;

import java.util.List;

public interface ConstraintDao extends Dao<Constraint, Integer> {
	public List<Constraint> getConstraintsByDataModelId(Integer dataModelId);
	public List<Constraint> getDatatypeAttributeConstraintsByDataModelId(Integer dataModelId);
}
