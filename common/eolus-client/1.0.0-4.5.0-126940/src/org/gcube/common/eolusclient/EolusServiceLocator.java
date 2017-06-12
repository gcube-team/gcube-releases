/**
 * EolusServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Nov 14, 2006 (10:23:53 EST) WSDL2Java emitter.
 */

package org.gcube.common.eolusclient;

public class EolusServiceLocator extends org.apache.axis.client.Service implements org.gcube.common.eolusclient.EolusService {

    public EolusServiceLocator() {
    }


    public EolusServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EolusServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for EolusPort
    private java.lang.String EolusPort_address = "http://n18.di.uoa.gr:8080/Madgik/Eolus";

    public java.lang.String getEolusPortAddress() {
        return EolusPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EolusPortWSDDServiceName = "EolusPort";

    public java.lang.String getEolusPortWSDDServiceName() {
        return EolusPortWSDDServiceName;
    }

    public void setEolusPortWSDDServiceName(java.lang.String name) {
        EolusPortWSDDServiceName = name;
    }

    public org.gcube.common.eolusclient.Eolus getEolusPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EolusPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEolusPort(endpoint);
    }

    public org.gcube.common.eolusclient.Eolus getEolusPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.gcube.common.eolusclient.EolusBindingStub _stub = new org.gcube.common.eolusclient.EolusBindingStub(portAddress, this);
            _stub.setPortName(getEolusPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEolusPortEndpointAddress(java.lang.String address) {
        EolusPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.gcube.common.eolusclient.Eolus.class.isAssignableFrom(serviceEndpointInterface)) {
                org.gcube.common.eolusclient.EolusBindingStub _stub = new org.gcube.common.eolusclient.EolusBindingStub(new java.net.URL(EolusPort_address), this);
                _stub.setPortName(getEolusPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("EolusPort".equals(inputPortName)) {
            return getEolusPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://eolus.uoa.org/", "EolusService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://eolus.uoa.org/", "EolusPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        if ("EolusPort".equals(portName)) {
            setEolusPortEndpointAddress(address);
        }
        else { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
