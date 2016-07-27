package gr.uoa.di.madgik.searchlibrary.operatorlibrary;

import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents and unary operator class that takes single result set as input and
 * returns result set as output
 * 
 * @author john.gerbesiotis - DI NKUA
 */
public class UnaryOp {
	/**
	 * The logger used by the class
	 */
	private Logger log = LoggerFactory.getLogger(UnaryOp.class.getName());

	/**
	 * The parameters which are passes in each {@link Unary} constructor.
	 */
	private static final Class<?>[] operatorConstructorParameterTypes = { URI.class, Map.class, StatsContainer.class };
	private static final Class<?>[] operatorConstructorParameterTypes2 = { URI.class, Map.class, StatsContainer.class, Long.class, TimeUnit.class };

	private Unary unaryOpeator;

	/**
	 * Creates a new {@link UnaryOp} with the default timeout for the writer
	 * 
	 * @param operatorType
	 *            The predefined type of the operator
	 * @param inLocator
	 *            The input locator
	 * @param operatorParameters
	 *            The parameters for the specific operator
	 * @param stats
	 *            statistics
	 * @throws Exception 
	 */
	public UnaryOp(String operatorType, URI inLocator, Map<String, String> operatorParameters, StatsContainer stats) throws Exception {
		@SuppressWarnings("unchecked")
		Class<? extends Unary> UnaryClass = (Class<? extends Unary>) Class.forName(operatorType);
		if (UnaryClass == null) {
			log.error("Could not find opearator with " + operatorType + " operatorType.");
			throw new Exception("Could not find operator with operatorType " + operatorType);
		}

		try {
			Constructor<Unary> operatorConstructor = (Constructor<Unary>) UnaryClass.getConstructor(operatorConstructorParameterTypes);
			unaryOpeator = operatorConstructor.newInstance(inLocator, operatorParameters, stats);
		} catch (Exception e) {
			log.error("Error when instanciating the data source.", e);
			throw new Exception("Error when instanciating the data source", e);
		}

	}

	public UnaryOp(String operatorType, URI inLocator, HashMap<String, String> operatorParameters, StatsContainer stats) throws Exception {
		this(operatorType, inLocator, (Map<String, String>) operatorParameters, stats);
	}

	/**
	 * Creates a new {@link UnaryOp} with configurable timeout for the writer
	 * 
	 * @param operatorType
	 *            The predefined type of the operator
	 * @param inLocator
	 *            The input locator
	 * @param operatorParameters
	 *            The parameters for the specific operator
	 * @param stats
	 *            statistics
	 * @param timeout
	 *            The timeout to be used for the writer
	 * @param timeUnit
	 *            The time unit of the timeout used
	 * @throws Exception 
	 */
	public UnaryOp(String operatorType, URI inLocator, Map<String, String> operatorParameters, StatsContainer stats, long timeout, TimeUnit timeUnit) throws Exception {
		@SuppressWarnings("unchecked")
		Class<? extends Unary> UnaryClass = (Class<? extends Unary>) Class.forName(operatorType);
		if (UnaryClass == null) {
			log.error("Could not find opearator with " + operatorType + " operatorType.");
			throw new Exception("Could not find operator with operatorType " + operatorType);
		}

		try {
			Constructor<Unary> operatorConstructor = (Constructor<Unary>) UnaryClass.getConstructor(operatorConstructorParameterTypes2);
			unaryOpeator = operatorConstructor.newInstance(inLocator, operatorParameters, timeout, timeUnit, stats);
		} catch (Exception e) {
			log.error("Error when instanciating the data source.", e);
			throw new Exception("Error when instanciating the data source", e);
		}
	}

	public UnaryOp(String operatorType, URI inLocator, HashMap<String, String> operatorParameters, StatsContainer stats, long timeout, TimeUnit timeUnit) throws Exception {
		this(operatorType, inLocator, (Map<String, String>) operatorParameters, stats, timeout, timeUnit);
	}

	/**
	 * Starts the operator and returns the output locator
	 * 
	 * @return the output locator of the operator compute method
	 * @throws Exception
	 */
	public URI compute() throws Exception {
		return unaryOpeator.compute();
	}
}
