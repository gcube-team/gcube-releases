/**
 * EolusService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Nov 14, 2006 (10:23:53 EST) WSDL2Java emitter.
 */

package org.gcube.common.eolusclient;

public interface EolusService extends javax.xml.rpc.Service {
    public java.lang.String getEolusPortAddress();

    public org.gcube.common.eolusclient.Eolus getEolusPort() throws javax.xml.rpc.ServiceException;

    public org.gcube.common.eolusclient.Eolus getEolusPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
