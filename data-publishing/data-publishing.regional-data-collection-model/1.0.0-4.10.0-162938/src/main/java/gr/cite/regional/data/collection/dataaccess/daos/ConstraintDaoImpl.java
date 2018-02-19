package gr.cite.regional.data.collection.dataaccess.daos;


import gr.cite.regional.data.collection.dataaccess.constraints.ConstraintTypes;
import gr.cite.regional.data.collection.dataaccess.entities.Constraint;
import gr.cite.regional.data.collection.dataaccess.entities.DataModel;

import java.util.List;

public class ConstraintDaoImpl extends JpaDao<Constraint, Integer> implements ConstraintDao {

	@Override
	public Constraint loadDetails(Constraint t) {
		t.getConstraint();
		t.getConstraintType();
		t.getDataModel();
		t.getId();
		
		return t;
	}
	
	public List<Constraint> getConstraintsByDataModelId(Integer dataModelId) {
		DataModel dataModel = new DataModel();
		dataModel.setId(dataModelId);
		
		List<Constraint> constraints = this.entityManager.createQuery("FROM " + Constraint.class.getSimpleName() + " WHERE dataModel=:dataModel", Constraint.class)
				.setParameter("dataModel", dataModel).getResultList();
		return constraints;
	}
	
	public List<Constraint> getDatatypeAttributeConstraintsByDataModelId(Integer dataModelId) {
		String constraintType = ConstraintTypes.ATTRIBUTE_DATATYPE;
		DataModel dataModel = new DataModel();
		dataModel.setId(dataModelId);
		
		return this.entityManager.createQuery(
				"FROM " + Constraint.class.getSimpleName()
						+ " WHERE dataModel = :dataModel AND constraintType = :constraintType",
				Constraint.class)
				.setParameter("dataModel", dataModel)
				.setParameter("constraintType", constraintType).getResultList();
	}
	
}
