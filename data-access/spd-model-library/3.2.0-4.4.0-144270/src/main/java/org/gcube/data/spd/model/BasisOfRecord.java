package org.gcube.data.spd.model;

/*
	FOSSIL_SPECIMEN
	An occurrence record describing a fossilized specimen.
HUMAN_OBSERVATION
	An occurrence record describing an observation made by one or more people.
LITERATURE
	An occurrence record based on literature alone.
LIVING_SPECIMEN
	An occurrence record describing a living specimen, e.g.
MACHINE_OBSERVATION
	An occurrence record describing an observation made by a machine.
MATERIAL_SAMPLE
	An occurrence record based on samples taken from other specimens or the environment.
OBSERVATION
	An occurrence record describing an observation.
PRESERVED_SPECIMEN
		An occurrence record describing a preserved specimen.
UNKNOWN
*/
public enum BasisOfRecord {

	PreservedSpecimen, FossilSpecimen, LivingSpecimen, HumanObservation, 
	MachineObservation, Observation, Literature, MaterialSample, Unknown 
	
}
