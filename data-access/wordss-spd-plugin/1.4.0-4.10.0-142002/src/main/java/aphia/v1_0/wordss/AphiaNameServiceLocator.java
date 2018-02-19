/**
 * AphiaNameServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Nov 16, 2004 (12:19:44 EST) WSDL2Java emitter.
 */

package aphia.v1_0.wordss;
import org.gcube.data.spd.wordssplugin.*;
public class AphiaNameServiceLocator extends org.apache.axis.client.Service implements aphia.v1_0.wordss.AphiaNameService {

    public AphiaNameServiceLocator() {
    }


    public AphiaNameServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    // Use to get a proxy class for AphiaNameServicePort
    public static java.lang.String AphiaNameServicePort_address = WordssPlugin.baseurl;

    public java.lang.String getAphiaNameServicePortAddress() {
        return AphiaNameServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AphiaNameServicePortWSDDServiceName = "AphiaNameServicePort";

    public java.lang.String getAphiaNameServicePortWSDDServiceName() {
        return AphiaNameServicePortWSDDServiceName;
    }

    public void setAphiaNameServicePortWSDDServiceName(java.lang.String name) {
        AphiaNameServicePortWSDDServiceName = name;
    }

    public aphia.v1_0.wordss.AphiaNameServicePortType getAphiaNameServicePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
        	endpoint = new java.net.URL(AphiaNameServicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAphiaNameServicePort(endpoint);
    }

    public aphia.v1_0.wordss.AphiaNameServicePortType getAphiaNameServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            aphia.v1_0.wordss.AphiaNameServiceBindingStub _stub = new aphia.v1_0.wordss.AphiaNameServiceBindingStub(portAddress, this);
            _stub.setPortName(getAphiaNameServicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAphiaNameServicePortEndpointAddress(java.lang.String address) {
        AphiaNameServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (aphia.v1_0.wordss.AphiaNameServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                aphia.v1_0.wordss.AphiaNameServiceBindingStub _stub = new aphia.v1_0.wordss.AphiaNameServiceBindingStub(new java.net.URL(AphiaNameServicePort_address), this);
                _stub.setPortName(getAphiaNameServicePortWSDDServiceName());
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
        if ("AphiaNameServicePort".equals(inputPortName)) {
            return getAphiaNameServicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://aphia/v1.0", "AphiaNameService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://aphia/v1.0", "AphiaNameServicePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        if ("AphiaNameServicePort".equals(portName)) {
            setAphiaNameServicePortEndpointAddress(address);
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
