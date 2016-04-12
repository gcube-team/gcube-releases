/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gcube.semantic.annotator.utils;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class ANNOTATIONS {
    
    public static final String PROPERT_NS = "http://www.fao.org/figis/onto/smartfish/annotation.owl#";
    
    public static final String BYCATCH = PROPERT_NS+"bycatch";
    public static final String INCATCH = PROPERT_NS+"incatch";
    public static final String TARGET = PROPERT_NS+"target";
    public static final String DISCARD = PROPERT_NS+"discard";
    public static final String COUNTRY = PROPERT_NS+"country";
    public static final String GEAR = PROPERT_NS+"gear";
    public static final String VESSEL = PROPERT_NS+"vessel";
    public static final String SPECIES = PROPERT_NS+"species";
    public static final String THRETENED = PROPERT_NS+"thretened";
    public static final String STATUS = PROPERT_NS+"status";
    public static final String AUTHORITY = PROPERT_NS+"authority";
    public static final String FISHING_CONTROL = PROPERT_NS+"fishing_control";
    public static final String ACCESS_CONTROL= PROPERT_NS+"access_control";
    public static final String MANAGEMENT = PROPERT_NS+"management";
    public static final String SEASONALITY = PROPERT_NS+"seasonality";
    public static final String SECTOR = PROPERT_NS+"sector";
    public static final String ENFORCEMENT_METHOD = PROPERT_NS+"enforcement_method";
    public static final String MEASURE = PROPERT_NS+"measure";
    public static final String CONTROL = PROPERT_NS+"control";
    public static final String METHOD = PROPERT_NS+"method";
    public static final String FINANCE_MGT_AUTHORITY = PROPERT_NS+"finance_mgmt_authority";
    public static final String MANAGEMENT_INDICATOR = PROPERT_NS+"management_indicator";
    public static final String DECISION_MAKER = PROPERT_NS+"decision_maker";
    public static final String POST_PROCESSING_METHOD = PROPERT_NS+"post_processing_method";
    public static final String MARKET = PROPERT_NS+"market";
    public static final String OTHER_INCOME_SOURCE = PROPERT_NS+"other_income_source";
    public static final String OWNER_OF_ACCESS_RIGHT = PROPERT_NS+"owner_of_access_right";
    public static final String APPLICANT_FOR_ACCESS_RIGHT = PROPERT_NS+"applicant_for_access_right";
    public static final String TECHNOLOGY_IN_USE = PROPERT_NS+"technology";
    public static final String WATER_AREA = PROPERT_NS+"water_area";
    public static final String LAND_AREA = PROPERT_NS+"land_area";
    public static final String YEAR = PROPERT_NS+"year";
    public static final String STATISTICS = PROPERT_NS+"statistics";
    
    public static String getLocalName(String annotation){
        return annotation.split("#")[1];
    }
    
}
