package gr.uoa.di.madgik.execution.utils;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterArrayEvaluationFilter;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterComposeFilter;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterDecomposeFilter;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterEmitPayloadFilter;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterExternalFilter;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterFilterBase;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterObjectConvertableFilter;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterObjectReflectableFilter;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterReflectableFromTemplateFilter;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterReflectableToTemplateFilter;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterSerializationFilter;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterXPathFilter;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterXsltFilter;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterXsltFilterFrom1DArray;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterXsltFilterTo1DArray;
import gr.uoa.di.madgik.execution.plan.element.variable.FilteredInOutParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.FilteredInParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.FilteredOutParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleInOutParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleInParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleOutParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IParameter.ParameterDirectionType;
import gr.uoa.di.madgik.execution.plan.element.variable.IParameter.ParameterProcessType;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;

public class ParameterUtils
{
	public static ParameterFilterBase GetFilter(ParameterFilterBase.FilterType TypeOfFilter,int Order,boolean StoreOutput, String StoreOutputVariableName, String FilteredVariableName) throws ExecutionValidationException
	{
		ParameterFilterBase filter=null;
		switch(TypeOfFilter)
		{
			case ObjectConvertable:
			{
				filter=new ParameterObjectConvertableFilter();
				((ParameterObjectConvertableFilter)filter).Order=Order;
				((ParameterObjectConvertableFilter)filter).StoreOutput=StoreOutput;
				((ParameterObjectConvertableFilter)filter).StoreOutputVariableName=StoreOutputVariableName;
				((ParameterObjectConvertableFilter)filter).FilteredVariableName=FilteredVariableName;
				break;
			}
			case ObjectReflectable:
			{
				filter=new ParameterObjectReflectableFilter();
				((ParameterObjectReflectableFilter)filter).Order=Order;
				((ParameterObjectReflectableFilter)filter).StoreOutput=StoreOutput;
				((ParameterObjectReflectableFilter)filter).StoreOutputVariableName=StoreOutputVariableName;
				((ParameterObjectReflectableFilter)filter).FilteredVariableName=FilteredVariableName;
				break;
			}
			case Serialization:
			{
				filter=new ParameterSerializationFilter();
				((ParameterSerializationFilter)filter).Order=Order;
				((ParameterSerializationFilter)filter).StoreOutput=StoreOutput;
				((ParameterSerializationFilter)filter).StoreOutputVariableName=StoreOutputVariableName;
				((ParameterSerializationFilter)filter).FilteredVariableName=FilteredVariableName;
				break;
			}
			case XPath:
			{
				filter=new ParameterXPathFilter();
				((ParameterXPathFilter)filter).Order=Order;
				((ParameterXPathFilter)filter).StoreOutput=StoreOutput;
				((ParameterXPathFilter)filter).StoreOutputVariableName=StoreOutputVariableName;
				((ParameterXPathFilter)filter).FilteredVariableName=FilteredVariableName;
				break;
			}
			case Xslt:
			{
				filter=new ParameterXsltFilter();
				((ParameterXsltFilter)filter).Order=Order;
				((ParameterXsltFilter)filter).StoreOutput=StoreOutput;
				((ParameterXsltFilter)filter).StoreOutputVariableName=StoreOutputVariableName;
				((ParameterXsltFilter)filter).FilteredVariableName=FilteredVariableName;
				break;
			}
			case Emit:
			{
				filter=new ParameterEmitPayloadFilter();
				((ParameterEmitPayloadFilter)filter).Order=Order;
				((ParameterEmitPayloadFilter)filter).EmitVariableName=FilteredVariableName;
				break;
			}
			case XsltT1DoArray:
			{
				filter=new ParameterXsltFilterTo1DArray();
				((ParameterXsltFilterTo1DArray)filter).Order=Order;
				((ParameterXsltFilterTo1DArray)filter).StoreOutput=StoreOutput;
				((ParameterXsltFilterTo1DArray)filter).StoreOutputVariableName=StoreOutputVariableName;
				((ParameterXsltFilterTo1DArray)filter).FilteredVariableName=FilteredVariableName;
				break;
			}
			case XsltFrom1DArray:
			{
				filter=new ParameterXsltFilterFrom1DArray();
				((ParameterXsltFilterFrom1DArray)filter).Order=Order;
				((ParameterXsltFilterFrom1DArray)filter).StoreOutput=StoreOutput;
				((ParameterXsltFilterFrom1DArray)filter).StoreOutputVariableName=StoreOutputVariableName;
				((ParameterXsltFilterFrom1DArray)filter).FilteredVariableName=FilteredVariableName;
				break;
			}
			case ReflectableToTemplate:
			{
				filter=new ParameterReflectableToTemplateFilter();
				((ParameterReflectableToTemplateFilter)filter).Order=Order;
				((ParameterReflectableToTemplateFilter)filter).StoreOutput=StoreOutput;
				((ParameterReflectableToTemplateFilter)filter).StoreOutputVariableName=StoreOutputVariableName;
				((ParameterReflectableToTemplateFilter)filter).FilteredVariableName=FilteredVariableName;
				break;
			}
			case ReflectableFromTemplate:
			{
				filter=new ParameterReflectableFromTemplateFilter();
				((ParameterReflectableFromTemplateFilter)filter).Order=Order;
				((ParameterReflectableFromTemplateFilter)filter).StoreOutput=StoreOutput;
				((ParameterReflectableFromTemplateFilter)filter).StoreOutputVariableName=StoreOutputVariableName;
				((ParameterReflectableFromTemplateFilter)filter).FilteredVariableName=FilteredVariableName;
				break;
			}
			case Decompose:
			{
				filter=new ParameterDecomposeFilter();
				((ParameterDecomposeFilter)filter).Order=Order;
				((ParameterDecomposeFilter)filter).FilteredVariableName=FilteredVariableName;
				break;
			}
			case Compose:
			{
				filter=new ParameterComposeFilter();
				((ParameterComposeFilter)filter).Order=Order;
				((ParameterComposeFilter)filter).StoreOutput=StoreOutput;
				((ParameterComposeFilter)filter).StoreOutputVariableName=StoreOutputVariableName;
				break;
			}
			case External:
			{
				filter=new ParameterExternalFilter();
				filter.Order=Order;
				break;
			}
			default:
			{
				throw new ExecutionValidationException("Unrecognized type found");
			}
		}
		return filter;
	}

	public static ParameterFilterBase GetParameterFilter(Element elem) throws ExecutionSerializationException
	{
		try
		{
			ParameterFilterBase filter=null;
			switch(ParameterFilterBase.FilterType.valueOf(XMLUtils.GetAttribute(elem, "type")))
			{
				case ObjectConvertable:
				{
					filter=new ParameterObjectConvertableFilter();
					break;
				}
				case ObjectReflectable:
				{
					filter=new ParameterObjectReflectableFilter();
					break;
				}
				case Serialization:
				{
					filter=new ParameterSerializationFilter();
					break;
				}
				case XPath:
				{
					filter=new ParameterXPathFilter();
					break;
				}
				case Xslt:
				{
					filter=new ParameterXsltFilter();
					break;
				}
				case Emit:
				{
					filter=new ParameterEmitPayloadFilter();
					break;
				}
				case XsltT1DoArray:
				{
					filter=new ParameterXsltFilterTo1DArray();
					break;
				}
				case XsltFrom1DArray:
				{
					filter=new ParameterXsltFilterFrom1DArray();
					break;
				}
				case ReflectableToTemplate:
				{
					filter=new ParameterReflectableToTemplateFilter();
					break;
				}
				case ReflectableFromTemplate:
				{
					filter=new ParameterReflectableFromTemplateFilter();
					break;
				}
				case Decompose:
				{
					filter=new ParameterDecomposeFilter();
					break;
				}
				case Compose:
				{
					filter=new ParameterComposeFilter();
					break;
				}
				case External:
				{
					filter=new ParameterExternalFilter();
					break;
				}
				case ArrayEvaluation:
				{
					filter=new ParameterArrayEvaluationFilter();
					break;
				}
				default:
				{
					throw new ExecutionSerializationException("Unrecognized parameter filter type");
				}
			}
			filter.FromXML(elem);
			return filter;
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not retrieve parameter filter from provided serialization", ex);
		}
	}
	public static IParameter GetParameter(Element Element) throws ExecutionSerializationException
	{
		try
		{
			IParameter.ParameterDirectionType Direction = ParameterDirectionType.valueOf(XMLUtils.GetAttribute(Element, "direction"));
			IParameter.ParameterProcessType Process = ParameterProcessType.valueOf(XMLUtils.GetAttribute(Element, "process"));
			IParameter param=null;
			if(Direction.equals(ParameterDirectionType.In) && Process.equals(ParameterProcessType.Filter))
			{
				param=new FilteredInParameter();
			}
			else if(Direction.equals(ParameterDirectionType.Out) && Process.equals(ParameterProcessType.Filter))
			{
				param=new FilteredOutParameter();
			}
			else if(Direction.equals(ParameterDirectionType.InOut) && Process.equals(ParameterProcessType.Filter))
			{
				param=new FilteredInOutParameter();
			}
			else if(Direction.equals(ParameterDirectionType.In) && Process.equals(ParameterProcessType.Simple))
			{
				param=new SimpleInParameter();
			}
			else if(Direction.equals(ParameterDirectionType.Out) && Process.equals(ParameterProcessType.Simple))
			{
				param=new SimpleOutParameter();
			}
			else if(Direction.equals(ParameterDirectionType.InOut) && Process.equals(ParameterProcessType.Simple))
			{
				param=new SimpleInOutParameter();
			}
			else
			{
				throw new ExecutionSerializationException("Unrecognized type found");
			}
			param.FromXML(Element);
			return param;
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not retrieve parameter from provided serialization", ex);
		}
	}

	public static IParameter GetSimpleParameter(IParameter.ParameterDirectionType Direction, String VariableName) throws ExecutionValidationException
	{
		IParameter param = null;
		switch (Direction)
		{
			case In:
			{
				param = new SimpleInParameter();
				((SimpleInParameter) param).VariableName = VariableName;
				break;
			}
			case Out:
			{
				param = new SimpleOutParameter();
				((SimpleOutParameter) param).VariableName = VariableName;
				break;
			}
			case InOut:
			{
				param = new SimpleInOutParameter();
				((SimpleInOutParameter) param).VariableName = VariableName;
				break;
			}
			default:
			{
				throw new ExecutionValidationException("Unrecognized type found");
			}
		}
		return param;
	}

	public static IParameter GetFilterParameter(IParameter.ParameterDirectionType Direction, ParameterFilterBase Filter) throws ExecutionValidationException
	{
		List<ParameterFilterBase> filters=new ArrayList<ParameterFilterBase>();
		filters.add(Filter);
		return ParameterUtils.GetFilterParameter(Direction,filters);
	}
	public static IParameter GetFilterParameter(IParameter.ParameterDirectionType Direction, List<ParameterFilterBase> Filters) throws ExecutionValidationException
	{
		IParameter param = null;
		switch (Direction)
		{
			case In:
			{
				param = new FilteredInParameter();
				((FilteredInParameter) param).Filters = Filters;
				break;
			}
			case Out:
			{
				param = new FilteredOutParameter();
				((FilteredOutParameter) param).Filters = Filters;
				break;
			}
			case InOut:
			{
				param = new FilteredInOutParameter();
				((FilteredInOutParameter) param).Filters = Filters;
				break;
			}
			default:
			{
				throw new ExecutionValidationException("Unrecognized type found");
			}
		}
		return param;
	}
}
