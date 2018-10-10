package gr.uoa.di.madgik.commons.configuration.parameter.elements.graph;

import gr.uoa.di.madgik.commons.configuration.parameter.IParameter;
import gr.uoa.di.madgik.commons.configuration.parameter.ObjectParameter;
import gr.uoa.di.madgik.commons.configuration.parameter.elements.Argument;
import gr.uoa.di.madgik.commons.configuration.parameter.elements.Method;
import gr.uoa.di.madgik.commons.configuration.utils.ClassWrapper;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class creates a graph representing the dependencies that are marked in the parsed configuration file.
 * The configuration markup allows parameter items to declare that they are dependant from other parameters if
 * they use their values. Typicaly the parameters that can declare such dependencies are those of type
 * {@link gr.uoa.di.madgik.commons.configuration.parameter.IParameter.ParameterType#Object}. Respectivly, parameters can
 * declare that they are generated and so they are dependent on the evaluation of the parameter that creates them.
 * Typically, the parameters that can generate values are the ones of type
 * {@link gr.uoa.di.madgik.commons.configuration.parameter.IParameter.ParameterType#Object}. The dependencies that this
 * graph can resolve are the ones between parameters. It cannot handle dependencies of a parameter of type
 * {@link gr.uoa.di.madgik.commons.configuration.parameter.IParameter.ParameterType#Object} that are generated in a method
 * of the same parameter and are needed in a subsequent method of the same parameter. The graph then resolves the
 * dependencies by evaluating each parameter and generating their values if they are marked as generated or they are
 * of type {@link gr.uoa.di.madgik.commons.configuration.parameter.IParameter.ParameterType#Object} and their evaluation
 * creates instnaces that are either the target of the configuration markup or are used internally to instnatiate
 * other parameters. The dependency resolve process is terinated once all parameters are evaluated. If this process
 * cannot be completed due to either incomplete markup or because of cyclic references, the process stops with error.
 *
 * TODO Handle dependencies of an {@link ObjectParameter} that are generated in a method of the parameter and are needed by a subsequent method of the same parameter
 *
s * @author gpapanikos
 */
public class DependencyGraph
{

	private static Logger logger = Logger.getLogger(DependencyGraph.class.getName());
	private Map<String, GraphElement> Graph = null;
	private Map<String, IParameter> Params = null;

	/**
	 * Creates a new instance
	 *
	 * @param Params the parameters that can be used to retrieve and set values
	 */
	public DependencyGraph(Map<String, IParameter> Params)
	{
		this.Graph = new Hashtable<String, GraphElement>();
		this.Params = Params;
	}

	/**
	 * Creates the graph based on the provided parameters
	 *
	 * @throws java.lang.Exception The graph could not be constructed
	 */
	public void ConstructGraph() throws Exception
	{
		for (Map.Entry<String, IParameter> entry : this.Params.entrySet())
		{
			this.Graph.put(entry.getKey(), new GraphElement(entry.getKey()));
		}
		for (Map.Entry<String, IParameter> entry : this.Params.entrySet())
		{
			if (entry.getValue().GetParameterType().equals(IParameter.ParameterType.Object))
			{
				if (((ObjectParameter) entry.getValue()).GetConstructor() != null)
				{
					for (Argument a : ((ObjectParameter) entry.getValue()).GetConstructor().GetArguments().GetArguments())
					{
						if (!this.Graph.containsKey(a.GetParameterName()))
						{
							throw new Exception("Reference to " + a.GetParameterName() + " not found");
						}
						this.Graph.get(entry.getValue().GetName()).Incoming.add(a.GetParameterName());
					}
				}
				for (Method m : ((ObjectParameter) entry.getValue()).GetMethods())
				{
					for (Argument a : m.GetArguments().GetArguments())
					{
						if (!this.Graph.containsKey(a.GetParameterName()))
						{
							throw new Exception("Reference to " + a.GetParameterName() + " not found");
						}
						this.Graph.get(entry.getValue().GetName()).Incoming.add(a.GetParameterName());
					}
					if (m.GetOutput() != null)
					{
						if (!this.Graph.containsKey(m.GetOutput().GetParameterName()))
						{
							throw new Exception("Reference to " + m.GetOutput().GetParameterName() + " not found");
						}
						this.Graph.get(entry.getValue().GetName()).Outgoing.add(m.GetOutput().GetParameterName());
					}
				}
			}
		}
	}

	/**
	 * Resolved the graph nodes dependencies. This method iterrates over all {@link GraphElement}
	 * nodes and evaluates their values. This process is repeated untill their is no {@link GraphElement}
	 * that has not been succesfully evaluated. A {@link GraphElement} is evaluated only if all the
	 * parameters that the {@link GraphElement} depends on have already been evaluated. If during
	 * the process of a full iteration no {@link GraphElement} has been successfully evaluated, the
	 * process stops
	 * 
	 * @throws java.lang.Exception The dependencies could not be resolved
	 */
	public void ResolveDependencies() throws Exception
	{
		boolean continueLoop = true;
		while (continueLoop)
		{
			continueLoop = false;
			for (Map.Entry<String, GraphElement> entry : this.Graph.entrySet())
			{
				boolean generated = this.Generate(entry.getKey());
				if (!generated)
				{
					continueLoop = true;
				}
			}
		}
	}

	/**
	 * Checks if all {@link GraphElement} entriues have been evaluated
	 *
	 * @return <code>true</code> if all {@link GraphElement} entries are evaluated, <code>false</code> otherwise
	 */
	public Boolean AllChecked()
	{
		for (Map.Entry<String, IParameter> entry : this.Params.entrySet())
		{
			if (!entry.getValue().IsChecked())
			{
				if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Parameter " + entry.getKey() + " not checked");
				return false;
			}
		}
		return true;
	}

	private Boolean Generate(String Name) throws Exception
	{
		if (this.Params.get(Name).IsChecked())
		{
			return true;
		}
		if (!(this.Params.get(Name) instanceof ObjectParameter))
		{
			if (!(this.Params.get(Name).IsGenerated()))
			{
				this.Params.get(Name).Check();
				return true;
			} else
			{
				return false;
			}
		} else
		{
			boolean canGenerate = true;
			for (String incoming : this.Graph.get(Name).Incoming)
			{
				if (!this.Params.get(incoming).IsChecked())
				{
					canGenerate = false;
				}
			}
			if (!canGenerate)
			{
				return false;
			} else
			{
				ClassWrapper wrap = new ClassWrapper(this.Params);
				wrap.Instantiate(((ObjectParameter) this.Params.get(Name)).GetClassName(), ((ObjectParameter) this.Params.get(Name)).GetConstructor());
				List<Method> meths = ((ObjectParameter) this.Params.get(Name)).GetMethods();
				for (Method m : meths)
				{
					wrap.Invoke(m);
				}
				if (((ObjectParameter) this.Params.get(Name)).GetConstructor() == null)
				{
					this.Params.get(Name).SetValue(wrap.ModuleClass);
				} else
				{
					this.Params.get(Name).SetValue(wrap.Instance);
				}
				this.Params.get(Name).Check();
				return true;
			}
		}
	}
}
