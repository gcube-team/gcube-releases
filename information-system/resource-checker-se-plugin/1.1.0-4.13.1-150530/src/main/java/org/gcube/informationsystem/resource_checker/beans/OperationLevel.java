package org.gcube.informationsystem.resource_checker.beans;

/**
 * Enumerators for the actions to be taken wrt missing resources: ALERT only or ALERT and READD
 * @author Costantino Perciante at ISTI-CNR  (costantino.perciante@isti.cnr.it)
 */
public enum OperationLevel {
	
	ALERT_READD, // alert and readd the resource
	ALERT // just alert it is missing

}
