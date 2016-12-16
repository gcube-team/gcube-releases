/**
 * EolusBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Nov 14, 2006 (10:23:53 EST) WSDL2Java emitter.
 */

package org.gcube.common.eolusclient;

public class EolusBindingStub extends org.apache.axis.client.Stub implements org.gcube.common.eolusclient.Eolus {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[68];
        _initOperationDesc1();
        _initOperationDesc2();
        _initOperationDesc3();
        _initOperationDesc4();
        _initOperationDesc5();
        _initOperationDesc6();
        _initOperationDesc7();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("VMtoTemplate");
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "templateName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        oper.setReturnClass(boolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("addScript");
        oper.addParameter(new javax.xml.namespace.QName("", "scriptName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "scriptContnet"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "scriptDescription"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"),
                      "org.uoa.eolus.DirectoryException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"), 
                      true
                     ));
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("addUser");
        oper.addParameter(new javax.xml.namespace.QName("", "username"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"),
                      "org.uoa.eolus.DirectoryException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "ReservedUserException"),
                      "org.uoa.eolus.ReservedUserException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "ReservedUserException"), 
                      true
                     ));
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminAddScript");
        oper.addParameter(new javax.xml.namespace.QName("", "userOwner"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "scriptName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "scriptContnet"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "scriptDescription"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"),
                      "org.uoa.eolus.DirectoryException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"), 
                      true
                     ));
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminApplyScript");
        oper.addParameter(new javax.xml.namespace.QName("", "userOwner"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "scriptName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "VMContactErrorException"),
                      "org.uoa.eolus.VMContactErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "VMContactErrorException"), 
                      true
                     ));
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminAssignVMtoUser");
        oper.addParameter(new javax.xml.namespace.QName("", "userOwner"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"),
                      "org.uoa.eolus.UnknownUserException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminAssignVNettoUser");
        oper.addParameter(new javax.xml.namespace.QName("", "user"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "VNetName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"),
                      "org.uoa.eolus.UnknownUserException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVNException"),
                      "org.uoa.eolus.UnknownVNException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVNException"), 
                      true
                     ));
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminCreateVM");
        oper.addParameter(new javax.xml.namespace.QName("", "userOwner"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "VMtemplateName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "hostname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "cores"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "memSize"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "networks"), new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"), net.java.dev.jaxb.array.StringArray.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"),
                      "org.uoa.eolus.DirectoryException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "VMExistsException"),
                      "org.uoa.eolus.VMExistsException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "VMExistsException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownTemplateException"),
                      "org.uoa.eolus.UnknownTemplateException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownTemplateException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "TemplateNotReadyException"),
                      "org.uoa.eolus.TemplateNotReadyException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "TemplateNotReadyException"), 
                      true
                     ));
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminCreateVNet");
        oper.addParameter(new javax.xml.namespace.QName("", "VNetName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "VNetSubnet"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "VNExistsException"),
                      "org.uoa.eolus.VNExistsException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "VNExistsException"), 
                      true
                     ));
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminExecCMD");
        oper.addParameter(new javax.xml.namespace.QName("", "userOwner"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "cmd"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"));
        oper.setReturnClass(net.java.dev.jaxb.array.StringArray.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "VMContactErrorException"),
                      "org.uoa.eolus.VMContactErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "VMContactErrorException"), 
                      true
                     ));
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminGetAllVNetList");
        oper.setReturnType(new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"));
        oper.setReturnClass(net.java.dev.jaxb.array.StringArray.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminGetDescription");
        oper.addParameter(new javax.xml.namespace.QName("", "userOwner"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "scriptName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownScriptException"),
                      "org.uoa.eolus.UnknownScriptException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownScriptException"), 
                      true
                     ));
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminGetScriptList");
        oper.addParameter(new javax.xml.namespace.QName("", "userOwner"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"));
        oper.setReturnClass(net.java.dev.jaxb.array.StringArray.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminGetStrayVMlist");
        oper.setReturnType(new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"));
        oper.setReturnClass(net.java.dev.jaxb.array.StringArray.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminGetTemplateStatus");
        oper.addParameter(new javax.xml.namespace.QName("", "userOwner"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "templateName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "MultipleTemplatesException"),
                      "org.uoa.eolus.MultipleTemplatesException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "MultipleTemplatesException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownTemplateException"),
                      "org.uoa.eolus.UnknownTemplateException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownTemplateException"), 
                      true
                     ));
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminGetTemplates");
        oper.addParameter(new javax.xml.namespace.QName("", "userOwner"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"));
        oper.setReturnClass(net.java.dev.jaxb.array.StringArray.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminGetUserVMlist");
        oper.addParameter(new javax.xml.namespace.QName("", "userOwner"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"));
        oper.setReturnClass(net.java.dev.jaxb.array.StringArray.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminGetVMIP");
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        _operations[17] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminGetVMInfo");
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminGetVMStatus");
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        _operations[19] = oper;

    }

    private static void _initOperationDesc3(){
        org.apache.axis.description.OperationDesc oper;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminGetVMlist");
        oper.setReturnType(new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"));
        oper.setReturnClass(net.java.dev.jaxb.array.StringArray.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        _operations[20] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminGetVNetInfo");
        oper.addParameter(new javax.xml.namespace.QName("", "VNetName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVNException"),
                      "org.uoa.eolus.UnknownVNException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVNException"), 
                      true
                     ));
        _operations[21] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminGetVNetList");
        oper.addParameter(new javax.xml.namespace.QName("", "user"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"));
        oper.setReturnClass(net.java.dev.jaxb.array.StringArray.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        _operations[22] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminRemovePublicTemplate");
        oper.addParameter(new javax.xml.namespace.QName("", "templateName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"),
                      "org.uoa.eolus.DirectoryException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "MultipleTemplatesException"),
                      "org.uoa.eolus.MultipleTemplatesException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "MultipleTemplatesException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "TemplateNotReadyException"),
                      "org.uoa.eolus.TemplateNotReadyException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "TemplateNotReadyException"), 
                      true
                     ));
        _operations[23] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminRemoveScript");
        oper.addParameter(new javax.xml.namespace.QName("", "userOwner"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "scriptName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"),
                      "org.uoa.eolus.DirectoryException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownScriptException"),
                      "org.uoa.eolus.UnknownScriptException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownScriptException"), 
                      true
                     ));
        _operations[24] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminRemoveTemplate");
        oper.addParameter(new javax.xml.namespace.QName("", "userOwner"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "templateName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"),
                      "org.uoa.eolus.DirectoryException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "MultipleTemplatesException"),
                      "org.uoa.eolus.MultipleTemplatesException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "MultipleTemplatesException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "TemplateNotReadyException"),
                      "org.uoa.eolus.TemplateNotReadyException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "TemplateNotReadyException"), 
                      true
                     ));
        _operations[25] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminRemoveVNet");
        oper.addParameter(new javax.xml.namespace.QName("", "VNetName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVNException"),
                      "org.uoa.eolus.UnknownVNException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVNException"), 
                      true
                     ));
        _operations[26] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminResumeVM");
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        _operations[27] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminShutdownVM");
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "forceShutdown"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        _operations[28] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminSuspendVM");
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        _operations[29] = oper;

    }

    private static void _initOperationDesc4(){
        org.apache.axis.description.OperationDesc oper;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminSyncUserScripts");
        oper.addParameter(new javax.xml.namespace.QName("", "user"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"),
                      "org.uoa.eolus.UnknownUserException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"), 
                      true
                     ));
        _operations[30] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminSyncUserTemplates");
        oper.addParameter(new javax.xml.namespace.QName("", "user"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"),
                      "org.uoa.eolus.UnknownUserException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"), 
                      true
                     ));
        _operations[31] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminTransferTemplate");
        oper.addParameter(new javax.xml.namespace.QName("", "userOwner"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "templateName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "newUser"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "move"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        _operations[32] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("adminVMtoTemplate");
        oper.addParameter(new javax.xml.namespace.QName("", "userOwner"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "templateName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        oper.setReturnClass(boolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[33] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("applyScript");
        oper.addParameter(new javax.xml.namespace.QName("", "scriptName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "VMContactErrorException"),
                      "org.uoa.eolus.VMContactErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "VMContactErrorException"), 
                      true
                     ));
        _operations[34] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("createHost");
        oper.addParameter(new javax.xml.namespace.QName("", "hostname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        _operations[35] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("createVM");
        oper.addParameter(new javax.xml.namespace.QName("", "VMtemplateName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "cores"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "memSize"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "networks"), new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"), net.java.dev.jaxb.array.StringArray.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"),
                      "org.uoa.eolus.DirectoryException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "VMExistsException"),
                      "org.uoa.eolus.VMExistsException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "VMExistsException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownTemplateException"),
                      "org.uoa.eolus.UnknownTemplateException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownTemplateException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "TemplateNotReadyException"),
                      "org.uoa.eolus.TemplateNotReadyException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "TemplateNotReadyException"), 
                      true
                     ));
        _operations[36] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("createVNet");
        oper.addParameter(new javax.xml.namespace.QName("", "VNetName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "VNetSubnet"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "VNExistsException"),
                      "org.uoa.eolus.VNExistsException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "VNExistsException"), 
                      true
                     ));
        _operations[37] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteHost");
        oper.addParameter(new javax.xml.namespace.QName("", "hostname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        _operations[38] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteUser");
        oper.addParameter(new javax.xml.namespace.QName("", "username"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"),
                      "org.uoa.eolus.UnknownUserException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"),
                      "org.uoa.eolus.DirectoryException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "ReservedUserException"),
                      "org.uoa.eolus.ReservedUserException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "ReservedUserException"), 
                      true
                     ));
        _operations[39] = oper;

    }

    private static void _initOperationDesc5(){
        org.apache.axis.description.OperationDesc oper;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("enableHost");
        oper.addParameter(new javax.xml.namespace.QName("", "hostname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "enable"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        _operations[40] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("execCMD");
        oper.addParameter(new javax.xml.namespace.QName("", "cmd"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"));
        oper.setReturnClass(net.java.dev.jaxb.array.StringArray.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "VMContactErrorException"),
                      "org.uoa.eolus.VMContactErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "VMContactErrorException"), 
                      true
                     ));
        _operations[41] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getDescription");
        oper.addParameter(new javax.xml.namespace.QName("", "scriptName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownScriptException"),
                      "org.uoa.eolus.UnknownScriptException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownScriptException"), 
                      true
                     ));
        _operations[42] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getHostInfo");
        oper.addParameter(new javax.xml.namespace.QName("", "hostname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        _operations[43] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getHostList");
        oper.setReturnType(new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"));
        oper.setReturnClass(net.java.dev.jaxb.array.StringArray.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        _operations[44] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getPublicTemplates");
        oper.setReturnType(new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"));
        oper.setReturnClass(net.java.dev.jaxb.array.StringArray.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[45] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getScriptList");
        oper.setReturnType(new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"));
        oper.setReturnClass(net.java.dev.jaxb.array.StringArray.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[46] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTemplateStatus");
        oper.addParameter(new javax.xml.namespace.QName("", "templateName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "MultipleTemplatesException"),
                      "org.uoa.eolus.MultipleTemplatesException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "MultipleTemplatesException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownTemplateException"),
                      "org.uoa.eolus.UnknownTemplateException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownTemplateException"), 
                      true
                     ));
        _operations[47] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTemplates");
        oper.setReturnType(new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"));
        oper.setReturnClass(net.java.dev.jaxb.array.StringArray.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[48] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getUsers");
        oper.setReturnType(new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"));
        oper.setReturnClass(net.java.dev.jaxb.array.StringArray.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[49] = oper;

    }

    private static void _initOperationDesc6(){
        org.apache.axis.description.OperationDesc oper;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVMIP");
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"),
                      "org.uoa.eolus.UnknownUserException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        _operations[50] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVMInfo");
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"),
                      "org.uoa.eolus.UnknownUserException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        _operations[51] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVMStatus");
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"),
                      "org.uoa.eolus.UnknownUserException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        _operations[52] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVMlist");
        oper.setReturnType(new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"));
        oper.setReturnClass(net.java.dev.jaxb.array.StringArray.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        _operations[53] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVNetInfo");
        oper.addParameter(new javax.xml.namespace.QName("", "VNetName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"),
                      "org.uoa.eolus.UnknownUserException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVNException"),
                      "org.uoa.eolus.UnknownVNException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVNException"), 
                      true
                     ));
        _operations[54] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVNetList");
        oper.setReturnType(new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray"));
        oper.setReturnClass(net.java.dev.jaxb.array.StringArray.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        _operations[55] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("makeTemplatePublic");
        oper.addParameter(new javax.xml.namespace.QName("", "templateName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "templatePublicName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"),
                      "org.uoa.eolus.DirectoryException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "MultipleTemplatesException"),
                      "org.uoa.eolus.MultipleTemplatesException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "MultipleTemplatesException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownTemplateException"),
                      "org.uoa.eolus.UnknownTemplateException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownTemplateException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "TemplateNotReadyException"),
                      "org.uoa.eolus.TemplateNotReadyException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "TemplateNotReadyException"), 
                      true
                     ));
        _operations[56] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("migrateVM");
        oper.addParameter(new javax.xml.namespace.QName("", "VMName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "hostname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        _operations[57] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("removeScript");
        oper.addParameter(new javax.xml.namespace.QName("", "scriptName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"),
                      "org.uoa.eolus.DirectoryException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownScriptException"),
                      "org.uoa.eolus.UnknownScriptException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownScriptException"), 
                      true
                     ));
        _operations[58] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("removeTemplate");
        oper.addParameter(new javax.xml.namespace.QName("", "templateName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"),
                      "org.uoa.eolus.DirectoryException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "MultipleTemplatesException"),
                      "org.uoa.eolus.MultipleTemplatesException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "MultipleTemplatesException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "TemplateNotReadyException"),
                      "org.uoa.eolus.TemplateNotReadyException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "TemplateNotReadyException"), 
                      true
                     ));
        _operations[59] = oper;

    }

    private static void _initOperationDesc7(){
        org.apache.axis.description.OperationDesc oper;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("removeVNet");
        oper.addParameter(new javax.xml.namespace.QName("", "VNetName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"),
                      "org.uoa.eolus.UnknownUserException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVNException"),
                      "org.uoa.eolus.UnknownVNException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVNException"), 
                      true
                     ));
        _operations[60] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("resumeVM");
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"),
                      "org.uoa.eolus.UnknownUserException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        _operations[61] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("setConfigurationParameter");
        oper.addParameter(new javax.xml.namespace.QName("", "key"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "value"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        _operations[62] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("shutdownVM");
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "forceShutdown"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"),
                      "org.uoa.eolus.UnknownUserException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        _operations[63] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("suspendVM");
        oper.addParameter(new javax.xml.namespace.QName("", "VMname"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"),
                      "org.uoa.eolus.UnknownUserException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"),
                      "org.uoa.eolus.UnknownVMException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException"), 
                      true
                     ));
        _operations[64] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("syncScripts");
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"),
                      "org.uoa.eolus.UnknownUserException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"), 
                      true
                     ));
        _operations[65] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("syncTemplates");
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"),
                      "org.uoa.eolus.UnknownUserException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException"), 
                      true
                     ));
        _operations[66] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("transferTemplate");
        oper.addParameter(new javax.xml.namespace.QName("", "templateName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "newUserOwner"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "move"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"),
                      "org.uoa.eolus.InternalErrorException",
                      new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException"), 
                      true
                     ));
        _operations[67] = oper;

    }

    public EolusBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public EolusBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public EolusBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://eolus.uoa.org/", "VMExistsException");
            cachedSerQNames.add(qName);
            cls = org.gcube.common.eolusclient.VMExistsException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://jaxb.dev.java.net/array", "stringArray");
            cachedSerQNames.add(qName);
            cls = net.java.dev.jaxb.array.StringArray.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownScriptException");
            cachedSerQNames.add(qName);
            cls = org.gcube.common.eolusclient.UnknownScriptException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownTemplateException");
            cachedSerQNames.add(qName);
            cls = org.gcube.common.eolusclient.UnknownTemplateException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://eolus.uoa.org/", "VMContactErrorException");
            cachedSerQNames.add(qName);
            cls = org.gcube.common.eolusclient.VMContactErrorException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://eolus.uoa.org/", "TemplateNotReadyException");
            cachedSerQNames.add(qName);
            cls = org.gcube.common.eolusclient.TemplateNotReadyException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVNException");
            cachedSerQNames.add(qName);
            cls = org.gcube.common.eolusclient.UnknownVNException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://eolus.uoa.org/", "DirectoryException");
            cachedSerQNames.add(qName);
            cls = org.gcube.common.eolusclient.DirectoryException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownVMException");
            cachedSerQNames.add(qName);
            cls = org.gcube.common.eolusclient.UnknownVMException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://eolus.uoa.org/", "MultipleTemplatesException");
            cachedSerQNames.add(qName);
            cls = org.gcube.common.eolusclient.MultipleTemplatesException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://eolus.uoa.org/", "InternalErrorException");
            cachedSerQNames.add(qName);
            cls = org.gcube.common.eolusclient.InternalErrorException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://eolus.uoa.org/", "VNExistsException");
            cachedSerQNames.add(qName);
            cls = org.gcube.common.eolusclient.VNExistsException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://eolus.uoa.org/", "ReservedUserException");
            cachedSerQNames.add(qName);
            cls = org.gcube.common.eolusclient.ReservedUserException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://eolus.uoa.org/", "UnknownUserException");
            cachedSerQNames.add(qName);
            cls = org.gcube.common.eolusclient.UnknownUserException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call =
                    (org.apache.axis.client.Call) super.service.createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public boolean VMtoTemplate(java.lang.String VMname, java.lang.String templateName) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "VMtoTemplate"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VMname, templateName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return ((java.lang.Boolean) _resp).booleanValue();
            } catch (java.lang.Exception _exception) {
                return ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_resp, boolean.class)).booleanValue();
            }
        }
    }

    public void addScript(java.lang.String scriptName, java.lang.String scriptContnet, java.lang.String scriptDescription) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "addScript"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {scriptName, scriptContnet, scriptDescription});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void addUser(java.lang.String username) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.ReservedUserException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "addUser"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {username});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void adminAddScript(java.lang.String userOwner, java.lang.String scriptName, java.lang.String scriptContnet, java.lang.String scriptDescription) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminAddScript"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {userOwner, scriptName, scriptContnet, scriptDescription});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void adminApplyScript(java.lang.String userOwner, java.lang.String scriptName, java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException, org.gcube.common.eolusclient.VMContactErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminApplyScript"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {userOwner, scriptName, VMname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void adminAssignVMtoUser(java.lang.String userOwner, java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminAssignVMtoUser"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {userOwner, VMname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void adminAssignVNettoUser(java.lang.String user, java.lang.String VNetName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVNException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminAssignVNettoUser"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {user, VNetName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void adminCreateVM(java.lang.String userOwner, java.lang.String VMtemplateName, java.lang.String VMname, java.lang.String hostname, int cores, int memSize, net.java.dev.jaxb.array.StringArray networks) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.VMExistsException, org.gcube.common.eolusclient.UnknownTemplateException, org.gcube.common.eolusclient.TemplateNotReadyException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminCreateVM"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {userOwner, VMtemplateName, VMname, hostname, new java.lang.Integer(cores), new java.lang.Integer(memSize), networks});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void adminCreateVNet(java.lang.String VNetName, int VNetSubnet) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.VNExistsException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminCreateVNet"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VNetName, new java.lang.Integer(VNetSubnet)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public net.java.dev.jaxb.array.StringArray adminExecCMD(java.lang.String userOwner, java.lang.String cmd, java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException, org.gcube.common.eolusclient.VMContactErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminExecCMD"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {userOwner, cmd, VMname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.java.dev.jaxb.array.StringArray) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.java.dev.jaxb.array.StringArray) org.apache.axis.utils.JavaUtils.convert(_resp, net.java.dev.jaxb.array.StringArray.class);
            }
        }
    }

    public net.java.dev.jaxb.array.StringArray adminGetAllVNetList() throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminGetAllVNetList"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.java.dev.jaxb.array.StringArray) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.java.dev.jaxb.array.StringArray) org.apache.axis.utils.JavaUtils.convert(_resp, net.java.dev.jaxb.array.StringArray.class);
            }
        }
    }

    public java.lang.String adminGetDescription(java.lang.String userOwner, java.lang.String scriptName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownScriptException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminGetDescription"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {userOwner, scriptName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
    }

    public net.java.dev.jaxb.array.StringArray adminGetScriptList(java.lang.String userOwner) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminGetScriptList"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {userOwner});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.java.dev.jaxb.array.StringArray) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.java.dev.jaxb.array.StringArray) org.apache.axis.utils.JavaUtils.convert(_resp, net.java.dev.jaxb.array.StringArray.class);
            }
        }
    }

    public net.java.dev.jaxb.array.StringArray adminGetStrayVMlist() throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminGetStrayVMlist"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.java.dev.jaxb.array.StringArray) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.java.dev.jaxb.array.StringArray) org.apache.axis.utils.JavaUtils.convert(_resp, net.java.dev.jaxb.array.StringArray.class);
            }
        }
    }

    public java.lang.String adminGetTemplateStatus(java.lang.String userOwner, java.lang.String templateName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.MultipleTemplatesException, org.gcube.common.eolusclient.UnknownTemplateException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminGetTemplateStatus"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {userOwner, templateName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
    }

    public net.java.dev.jaxb.array.StringArray adminGetTemplates(java.lang.String userOwner) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminGetTemplates"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {userOwner});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.java.dev.jaxb.array.StringArray) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.java.dev.jaxb.array.StringArray) org.apache.axis.utils.JavaUtils.convert(_resp, net.java.dev.jaxb.array.StringArray.class);
            }
        }
    }

    public net.java.dev.jaxb.array.StringArray adminGetUserVMlist(java.lang.String userOwner) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminGetUserVMlist"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {userOwner});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.java.dev.jaxb.array.StringArray) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.java.dev.jaxb.array.StringArray) org.apache.axis.utils.JavaUtils.convert(_resp, net.java.dev.jaxb.array.StringArray.class);
            }
        }
    }

    public java.lang.String adminGetVMIP(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminGetVMIP"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VMname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
    }

    public java.lang.String adminGetVMInfo(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[18]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminGetVMInfo"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VMname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
    }

    public java.lang.String adminGetVMStatus(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminGetVMStatus"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VMname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
    }

    public net.java.dev.jaxb.array.StringArray adminGetVMlist() throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[20]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminGetVMlist"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.java.dev.jaxb.array.StringArray) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.java.dev.jaxb.array.StringArray) org.apache.axis.utils.JavaUtils.convert(_resp, net.java.dev.jaxb.array.StringArray.class);
            }
        }
    }

    public java.lang.String adminGetVNetInfo(java.lang.String VNetName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVNException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[21]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminGetVNetInfo"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VNetName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
    }

    public net.java.dev.jaxb.array.StringArray adminGetVNetList(java.lang.String user) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[22]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminGetVNetList"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {user});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.java.dev.jaxb.array.StringArray) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.java.dev.jaxb.array.StringArray) org.apache.axis.utils.JavaUtils.convert(_resp, net.java.dev.jaxb.array.StringArray.class);
            }
        }
    }

    public void adminRemovePublicTemplate(java.lang.String templateName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.MultipleTemplatesException, org.gcube.common.eolusclient.TemplateNotReadyException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[23]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminRemovePublicTemplate"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {templateName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void adminRemoveScript(java.lang.String userOwner, java.lang.String scriptName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.UnknownScriptException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[24]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminRemoveScript"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {userOwner, scriptName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void adminRemoveTemplate(java.lang.String userOwner, java.lang.String templateName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.MultipleTemplatesException, org.gcube.common.eolusclient.TemplateNotReadyException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[25]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminRemoveTemplate"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {userOwner, templateName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void adminRemoveVNet(java.lang.String VNetName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVNException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[26]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminRemoveVNet"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VNetName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void adminResumeVM(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[27]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminResumeVM"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VMname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void adminShutdownVM(java.lang.String VMname, boolean forceShutdown) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[28]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminShutdownVM"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VMname, new java.lang.Boolean(forceShutdown)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void adminSuspendVM(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[29]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminSuspendVM"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VMname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void adminSyncUserScripts(java.lang.String user) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[30]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminSyncUserScripts"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {user});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void adminSyncUserTemplates(java.lang.String user) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[31]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminSyncUserTemplates"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {user});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void adminTransferTemplate(java.lang.String userOwner, java.lang.String templateName, java.lang.String newUser, boolean move) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[32]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminTransferTemplate"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {userOwner, templateName, newUser, new java.lang.Boolean(move)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public boolean adminVMtoTemplate(java.lang.String userOwner, java.lang.String VMname, java.lang.String templateName) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[33]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "adminVMtoTemplate"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {userOwner, VMname, templateName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return ((java.lang.Boolean) _resp).booleanValue();
            } catch (java.lang.Exception _exception) {
                return ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_resp, boolean.class)).booleanValue();
            }
        }
    }

    public void applyScript(java.lang.String scriptName, java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException, org.gcube.common.eolusclient.VMContactErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[34]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "applyScript"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {scriptName, VMname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void createHost(java.lang.String hostname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[35]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "createHost"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {hostname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void createVM(java.lang.String VMtemplateName, java.lang.String VMname, int cores, int memSize, net.java.dev.jaxb.array.StringArray networks) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.VMExistsException, org.gcube.common.eolusclient.UnknownTemplateException, org.gcube.common.eolusclient.TemplateNotReadyException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[36]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "createVM"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VMtemplateName, VMname, new java.lang.Integer(cores), new java.lang.Integer(memSize), networks});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void createVNet(java.lang.String VNetName, int VNetSubnet) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.VNExistsException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[37]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "createVNet"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VNetName, new java.lang.Integer(VNetSubnet)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void deleteHost(java.lang.String hostname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[38]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "deleteHost"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {hostname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void deleteUser(java.lang.String username) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.ReservedUserException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[39]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "deleteUser"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {username});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void enableHost(java.lang.String hostname, boolean enable) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[40]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "enableHost"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {hostname, new java.lang.Boolean(enable)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public net.java.dev.jaxb.array.StringArray execCMD(java.lang.String cmd, java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException, org.gcube.common.eolusclient.VMContactErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[41]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "execCMD"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {cmd, VMname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.java.dev.jaxb.array.StringArray) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.java.dev.jaxb.array.StringArray) org.apache.axis.utils.JavaUtils.convert(_resp, net.java.dev.jaxb.array.StringArray.class);
            }
        }
    }

    public java.lang.String getDescription(java.lang.String scriptName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownScriptException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[42]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "getDescription"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {scriptName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
    }

    public java.lang.String getHostInfo(java.lang.String hostname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[43]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "getHostInfo"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {hostname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
    }

    public net.java.dev.jaxb.array.StringArray getHostList() throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[44]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "getHostList"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.java.dev.jaxb.array.StringArray) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.java.dev.jaxb.array.StringArray) org.apache.axis.utils.JavaUtils.convert(_resp, net.java.dev.jaxb.array.StringArray.class);
            }
        }
    }

    public net.java.dev.jaxb.array.StringArray getPublicTemplates() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[45]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "getPublicTemplates"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.java.dev.jaxb.array.StringArray) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.java.dev.jaxb.array.StringArray) org.apache.axis.utils.JavaUtils.convert(_resp, net.java.dev.jaxb.array.StringArray.class);
            }
        }
    }

    public net.java.dev.jaxb.array.StringArray getScriptList() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[46]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "getScriptList"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.java.dev.jaxb.array.StringArray) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.java.dev.jaxb.array.StringArray) org.apache.axis.utils.JavaUtils.convert(_resp, net.java.dev.jaxb.array.StringArray.class);
            }
        }
    }

    public java.lang.String getTemplateStatus(java.lang.String templateName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.MultipleTemplatesException, org.gcube.common.eolusclient.UnknownTemplateException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[47]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "getTemplateStatus"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {templateName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
    }

    public net.java.dev.jaxb.array.StringArray getTemplates() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[48]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "getTemplates"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.java.dev.jaxb.array.StringArray) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.java.dev.jaxb.array.StringArray) org.apache.axis.utils.JavaUtils.convert(_resp, net.java.dev.jaxb.array.StringArray.class);
            }
        }
    }

    public net.java.dev.jaxb.array.StringArray getUsers() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[49]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "getUsers"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.java.dev.jaxb.array.StringArray) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.java.dev.jaxb.array.StringArray) org.apache.axis.utils.JavaUtils.convert(_resp, net.java.dev.jaxb.array.StringArray.class);
            }
        }
    }

    public java.lang.String getVMIP(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[50]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "getVMIP"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VMname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
    }

    public java.lang.String getVMInfo(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[51]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "getVMInfo"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VMname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
    }

    public java.lang.String getVMStatus(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[52]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "getVMStatus"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VMname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
    }

    public net.java.dev.jaxb.array.StringArray getVMlist() throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[53]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "getVMlist"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.java.dev.jaxb.array.StringArray) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.java.dev.jaxb.array.StringArray) org.apache.axis.utils.JavaUtils.convert(_resp, net.java.dev.jaxb.array.StringArray.class);
            }
        }
    }

    public java.lang.String getVNetInfo(java.lang.String VNetName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVNException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[54]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "getVNetInfo"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VNetName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
    }

    public net.java.dev.jaxb.array.StringArray getVNetList() throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[55]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "getVNetList"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.java.dev.jaxb.array.StringArray) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.java.dev.jaxb.array.StringArray) org.apache.axis.utils.JavaUtils.convert(_resp, net.java.dev.jaxb.array.StringArray.class);
            }
        }
    }

    public void makeTemplatePublic(java.lang.String templateName, java.lang.String templatePublicName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.MultipleTemplatesException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownTemplateException, org.gcube.common.eolusclient.TemplateNotReadyException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[56]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "makeTemplatePublic"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {templateName, templatePublicName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void migrateVM(java.lang.String VMName, java.lang.String hostname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[57]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "migrateVM"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VMName, hostname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void removeScript(java.lang.String scriptName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.UnknownScriptException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[58]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "removeScript"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {scriptName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void removeTemplate(java.lang.String templateName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.MultipleTemplatesException, org.gcube.common.eolusclient.TemplateNotReadyException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[59]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "removeTemplate"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {templateName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void removeVNet(java.lang.String VNetName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVNException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[60]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "removeVNet"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VNetName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void resumeVM(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[61]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "resumeVM"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VMname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void setConfigurationParameter(java.lang.String key, java.lang.String value) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[62]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "setConfigurationParameter"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {key, value});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void shutdownVM(java.lang.String VMname, boolean forceShutdown) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[63]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "shutdownVM"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VMname, new java.lang.Boolean(forceShutdown)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void suspendVM(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[64]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "suspendVM"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {VMname});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void syncScripts() throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[65]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "syncScripts"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void syncTemplates() throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[66]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "syncTemplates"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

    public void transferTemplate(java.lang.String templateName, java.lang.String newUserOwner, boolean move) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[67]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://n18.di.uoa.gr:8080/Madgik/Eolus?wsdl");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://eolus.uoa.org/", "transferTemplate"));

        setRequestHeaders(_call);
        setAttachments(_call);
        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {templateName, newUserOwner, new java.lang.Boolean(move)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
    }

}
