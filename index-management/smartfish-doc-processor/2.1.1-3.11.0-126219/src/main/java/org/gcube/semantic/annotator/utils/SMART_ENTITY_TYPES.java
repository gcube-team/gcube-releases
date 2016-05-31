/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gcube.semantic.annotator.utils;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class SMART_ENTITY_TYPES {

    public static final String SMARTFISH_NS = "http://smartfish.d4science.org/ontology/1.0/smartfish.owl#";

    public static final String EXPLOITATION_STATUS = SMARTFISH_NS + "ExploitationStatus";
    public static final String SECTOR = SMARTFISH_NS + "Sector";
    public static final String MANAGEMENT = SMARTFISH_NS + "Management";
    public static final String ACCESS_CONTROL = SMARTFISH_NS + "AccessControl";
    public static final String FISHING_CONTROL = SMARTFISH_NS + "FishingControl";
    public static final String ENFORCEMENT_METHOD = SMARTFISH_NS + "EnforcementMethod";
    public static final String CONSERVATION_MEASURE = SMARTFISH_NS + "ConservationMeasure";
    public static final String MARKET_PLACE = SMARTFISH_NS + "MarketPlace";
    public static final String AUTHORITY = SMARTFISH_NS + "Authority";
    public static final String STATISTICAL_INDICATOR = SMARTFISH_NS + "StatisticalIndicator";
    public static final String POST_HARVESTING_PROCESS = SMARTFISH_NS + "PostHarvestingProcess";
    public static final String INCOME_SOURCE = SMARTFISH_NS + "IncomeSource";
    public static final String LEGAL_ENTITY = SMARTFISH_NS + "LegalEntity";
    public static final String TECHNOLOGY = SMARTFISH_NS + "Technology";
    public static final String SEASONALITY = SMARTFISH_NS + "Season";
    public static final String WATER_AREA = SMARTFISH_NS + "WaterArea";
    public static final String LAND_AREA = SMARTFISH_NS + "LandArea";
    public static final String YEAR = SMARTFISH_NS + "TimeYear";
    public static final String STATISTICS = SMARTFISH_NS + "Statistics";

    public static final String SPECIES = "http://www.fao.org/figis/flod/onto/linneanspecies.owl#SpeciesCode";
    public static final String VESSEL = "http://www.fao.org/figis/flod/onto/vessel.owl#VesselCode";
    public static final String GEAR = "http://www.fao.org/figis/flod/onto/gear.owl#GearCode";
    public static final String FLAGSTATE = "http://www.fao.org/figis/flod/onto/flagstate.owl#FlagStateCode";

    public static boolean isFLODtype(String typeUri) {
        return typeUri.equals(SPECIES)
                || typeUri.equals(VESSEL)
                || typeUri.equals(GEAR)
                || typeUri.equals(FLAGSTATE);
    }

    public static boolean isSMARTFISHtype(String typeUri) {
        return typeUri.equals(EXPLOITATION_STATUS)
                || typeUri.equals(SECTOR)
                || typeUri.equals(MANAGEMENT)
                || typeUri.equals(ACCESS_CONTROL)
                || typeUri.equals(FISHING_CONTROL)
                || typeUri.equals(ENFORCEMENT_METHOD)
                || typeUri.equals(MARKET_PLACE)
                || typeUri.equals(AUTHORITY)
                || typeUri.equals(STATISTICAL_INDICATOR)
                || typeUri.equals(POST_HARVESTING_PROCESS)
                || typeUri.equals(INCOME_SOURCE)
                || typeUri.equals(LEGAL_ENTITY)
                || typeUri.equals(TECHNOLOGY)
                || typeUri.equals(SEASONALITY)
                || typeUri.equals(WATER_AREA)
                || typeUri.equals(YEAR)
                || typeUri.equals(STATISTICS)
                || typeUri.equals(CONSERVATION_MEASURE)
                || typeUri.equals(LAND_AREA);
    }

//    public static String getTypeUri(String localName) throws Exception {
//        if (localName.equals(SPECIES.split("#")[1])) {
//            return SPECIES;
//        } else if (localName.equals(FLAGSTATE.split("#")[1])) {
//            return FLAGSTATE;
//        } else if (localName.equals(GEAR.split("#")[1])) {
//            return GEAR;
//        } else if (localName.equals(VESSEL.split("#")[1])) {
//            return VESSEL;
//        }
//
//        if (localName.equals(EXPLOITATION_STATUS.split("#")[1])
//                || localName.equals(SECTOR.split("#")[1])
//                || localName.equals(MANAGEMENT.split("#")[1])
//                || localName.equals(ACCESS_CONTROL.split("#")[1])
//                || localName.equals(FISHING_CONTROL.split("#")[1])
//                || localName.equals(ENFORCEMENT_METHOD.split("#")[1])
//                || localName.equals(MARKET_PLACE.split("#")[1])
//                || localName.equals(AUTHORITY.split("#")[1])
//                || localName.equals(STATISTICAL_INDICATOR.split("#")[1])
//                || localName.equals(POST_HARVESTING_PROCESS.split("#")[1])
//                || localName.equals(INCOME_SOURCE.split("#")[1])
//                || localName.equals(LEGAL_ENTITY.split("#")[1])
//                || localName.equals(TECHNOLOGY.split("#")[1])
//                || localName.equals(SEASONALITY.split("#")[1])
//                || localName.equals(WATER_AREA.split("#")[1])
//                || localName.equals(STATISTICS.split("#")[1])
//                || localName.equals(YEAR.split("#")[1])
//                || localName.equals(LAND_AREA.split("#")[1])) {
//            return SMARTFISH_NS + localName;
//        } else {
//            throw new Exception("Unrecognized type : " + localName);
//        }
//    }

}
