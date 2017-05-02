package org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables;

import java.util.Arrays;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.LogicType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AlgorithmType;

class Execution{
	private AlgorithmType algorithm;
	private int[] sources;
	private LogicType logic;
	private String parameters;
	
	public LogicType getLogic() {
		return logic;
	}
	public AlgorithmType getAlgorithm() {
		return algorithm;
	}
	
	public int[] getSources() {
		return sources;
	}
	
	public String getParameters() {
		return parameters;
	}
	
	public Execution(AlgorithmType algorithm,  LogicType logic,int[] sources,String parameters) throws Exception {
		super();
		this.algorithm = algorithm;
		this.sources=sources;
		if(sources==null||sources.length==0) throw new Exception("Sources cannot be empty");
		Arrays.sort(this.sources);
		this.logic = logic;
		this.parameters=parameters;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + ((logic == null) ? 0 : logic.hashCode());
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + Arrays.hashCode(sources);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Execution other = (Execution) obj;
		if (algorithm != other.algorithm)
			return false;
		if (logic != other.logic)
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (!Arrays.equals(sources, other.sources))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Execution [algorithm=" + algorithm + ", sources="
				+ Arrays.toString(sources) + ", logic=" + logic
				+ ", parameters=" + parameters + "]";
	}
	
	
	
	
	
}
