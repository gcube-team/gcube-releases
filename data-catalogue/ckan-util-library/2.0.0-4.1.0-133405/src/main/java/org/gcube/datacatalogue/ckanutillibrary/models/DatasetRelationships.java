package org.gcube.datacatalogue.ckanutillibrary.models;

/**
 * Allowed relationships between packages(datasets). Some of them are not supported yet due to the problem
 * reported here https://support.d4science.org/issues/4455
 * <ul>
 * <li> depends_on
 * <li> dependency_of
 * <li> derives_from
 * <li> has_derivation
 * <li> child_of
 * <li> parent_of
 * <li> links_to
 * <li> linked_from
 * </ul>
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum DatasetRelationships {
	depends_on,
	dependency_of,
	derives_from,
	has_derivation,
	child_of,
	parent_of,
	links_to,
	linked_from
}
