package gr.uoa.di.madgik.workflow.adaptor.hive.utils.wrappers.impl;

import gr.uoa.di.madgik.execution.datatype.DataTypeResultSet;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.ParameterProcessingPlanElement;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterExternalFilter;
import gr.uoa.di.madgik.execution.plan.element.variable.FilteredInParameter;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.LocatorElevationFilter;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.wrappers.FunctionalityWrapper;

import java.util.UUID;

/**
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public abstract class ProcessingWrapper extends FunctionalityWrapper {

	protected ParameterProcessingPlanElement elevationElement = null;
	protected NamedDataType elevatedLocator = null;

	@Override
	public void addVariablesToPlan(ExecutionPlan plan) throws Exception {
		if (elevatedLocator != null) {
			plan.Variables.Add(elevatedLocator);
		}
	}

	@Override
	public IPlanElement[] constructPlanElements() throws ExecutionValidationException, ExecutionSerializationException, Exception {
		if (elevationElement == null) {
			return new IPlanElement[] {};
		} else {
			return new IPlanElement[] { elevationElement };
		}
	}

	public void elevate() {
		elevationElement = new ParameterProcessingPlanElement();

		ParameterExternalFilter elevationFilter = new ParameterExternalFilter();
		elevationFilter.Order = 0;
		elevationFilter.TokenMapping.clear();
		elevationFilter.ExternalFilter = new LocatorElevationFilter();
		((LocatorElevationFilter) elevationFilter.ExternalFilter).LocatorVariableName = this.getOutputVariable().Name;
		elevatedLocator = new NamedDataType();
		elevatedLocator.IsAvailable = false;
		elevatedLocator.Name = UUID.randomUUID().toString();
		elevatedLocator.Token = elevatedLocator.Name;
		elevatedLocator.Value = new DataTypeResultSet();
		((LocatorElevationFilter) elevationFilter.ExternalFilter).ElevatedLocatorVariableName = elevatedLocator.Name;
		((LocatorElevationFilter) elevationFilter.ExternalFilter).StoreOutput = true;

		FilteredInParameter procParam = new FilteredInParameter();
		procParam.Filters.add(elevationFilter);
		elevationElement.Parameters.add(procParam);
	}

	public IPlanElement getElevationElement() {
		return elevationElement;
	}

	public NamedDataType getElevationVariable() {
		return elevatedLocator;
	}
}
