/**
 * 
 */
package org.gcube.common.mycontainer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * A JUnit {@link Runner} for integration tests in {@link MyContainer}.
 * <p>
 * The runner offer transparencies over standard usese of {@link MyContainer} API in the JUnit framework, including:
 * 
 * <ul>
 * <li><b>lifetime management</b>: the runner will start and stop an instance of the container on a per test suite
 * basis. All methods annotated with {@link Test}, {@link Before}, {@link BeforeClass}, {@link After}, and
 * {@link AfterClass} are guaranteed to occurred within a running container
 * <li><b>deployment</b>: the runner will deploy in the container instance any static field of type {@link Gar} which is
 * annotated with {@link Deployment}
 * <li><b>container injection</b>: the runner will inject the container into any static field of type
 * {@link MyContainer} which is annotated with {@link Inject}
 * <li><b>endpoint injection</b>: the runner will inject the implementation of a service of type <code>T</code> into any
 * static field of type <code>T</code> which is annotated with {@link Named} and qualified with the WSDD name of the
 * service
 * <li><b>endpoint reference injection</b>: the runner will inject a reference to a service endpoint into any static
 * field of type {@link EndpointReferenceType} or {@link URIe} which is annotated with {@link Named} and qualified with
 * the WSDD name of the service.
 * <li><b>service instance reference injection</b>: the runner will inject a reference to a service instance into any static
 * field of type {@link EndpointReferenceType} which is annotated with {@link Reference} and qualified with
 * the WSDD name of the service, the namespace of the service, and the key of the service instance.
 * </ul>
 * 
 * In addition, the runner will surround the logs of each test with their name, for increased readability in test
 * reports and during interactive testing.
 * <p>
 * Usage may be illustrated with the following example:
 * 
 * 
 * <pre>
 * <blockquote>
 *  
 *  @RunWith(MyContainerTestRunner.class)
 *  public class SampleIntegrationTest {
 *  	
 *  	@Deployment
 *  	static Gar serviceGar=new Gar("sample-service").
 *  	addConfigurations("src/main/resources/META-INF").
 *  	addInterfaces("src/main/wsdl");
 *  	
 *  	@Named("acme/sample/stateless")
 * 		static Stateless service;
 * 
 * 		@Named("acme/sample/stateless")
 * 		static EndpointReferenceType address;
 * 	
 * 		@Named("acme/sample/stateless")
 * 		static URI reference;
 * 	
 * 		@Reference(name="acme/sample/stateless",namespace="http://acme.org",key="somekey")
 * 		static EndpointReferenceType instanceReference;
 * 	
 * 		@BeforeClass
 * 		public static void serviceHasStarted() {
 * 	
 * 			//check service is ready
 * 			assertTrue(ServiceContext.getContext().getStatus()==READIED);
 * 		}
 * 	
 * 		@Test
 * 		public void smokeTest() throws Exception {
 * 		
 * 			//call method on implementation
 * 			System.out.println(stateless.about("joe"));
 * 		
 * 		}
 * 	
 * 		@Test
 * 		public void clientSmokeTest() throws Exception {
 * 		
 * 			//call operation via stub
 * 			SampleServicePortType statelessPT = 
 * 				new StatelessServiceAddressingLocator().getStatelessPortTypePort(address);
 * 		
 * 			System.out.println(statelessPT.about("joe"));
 * 		
 * 		}
 * }
 *  
 * </blockquote>
 * </pre>
 * 
 * <p>
 * 
 * @author Fabio Simeoni
 * 
 */
public class MyContainerTestRunner extends BlockJUnit4ClassRunner {

	MyContainer container;
	Field containerField;
	List<Gar> gars = new ArrayList<Gar>();

	/**
	 * Creates an instance for a test class.
	 * 
	 * @param klass the test class
	 * @throws InitializationError if the test class is not well formed
	 */
	public MyContainerTestRunner(Class<?> klass) throws InitializationError {

		super(klass);

		//identifies suite scope
		if (getTestClass().getJavaClass().isAnnotationPresent(Scope.class))
			ScopeProvider.instance.set(getTestClass().getJavaClass().getAnnotation(Scope.class).value());

		//identifies injected fields
		List<Field> fields = new ArrayList<Field>();
		for (FrameworkField ffield : getTestClass().getAnnotatedFields(Inject.class)) {
			Field field = ffield.getField();
			if (field.getType().isAssignableFrom(MyContainer.class))
				fields.add(field);
		}
		// validate
		if (!fields.isEmpty())
			if (fields.size() > 1)
				throw new InitializationError("@Inject placed on too many fields");
			else {
				containerField = fields.get(0);
				if (containerField.getModifiers() != Modifier.STATIC)
					throw new InitializationError("@Inject can only be placed on static fields");
				containerField.setAccessible(true);
			}

		// identifies deployments
		fields = new ArrayList<Field>();
		for (FrameworkField field : getTestClass().getAnnotatedFields(Deployment.class)) {
			if (!field.getField().getType().isAssignableFrom(Gar.class))
				throw new InitializationError("field " + field.getField().getName()
						+ " is annotated with @Gar but its type is invalid");
			else if (!Modifier.isStatic(field.getField().getModifiers()))
				throw new InitializationError("field " + field.getField()
						+ " is annotated with @Gar but it is not static");
			else
				try {
					field.getField().setAccessible(true);
					gars.add(Gar.class.cast(field.getField().get(null)));
				} catch (Throwable t) {
					throw new InitializationError(t);
				}
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		
		String currentScope = ScopeProvider.instance.get();
		
		if (method.getMethod().isAnnotationPresent(Scope.class))
			ScopeProvider.instance.set(method.getAnnotation(Scope.class).value());
		
		System.err.println("\n--- start of [" + method.getName() + "] in scope "+ScopeProvider.instance.get()+"\n");
		
		super.runChild(method, notifier);
		
		System.err.println("\n--- end of [" + method.getName() + "]\n");
		
		ScopeProvider.instance.set(currentScope);
	}

	/** {@inheritDoc} */
	@Override
	protected Statement withBeforeClasses(Statement statement) {

		final Statement defaultStatement = super.withBeforeClasses(statement);

		// add statement that start container, deploys into it, and performs injection before executing default
		// statement
		Statement newstatement = new Statement() {

			public void evaluate() throws Throwable {

				try {

					// start container
					container = new MyContainer(gars.toArray(new Gar[0]));

					container.start();

					// perform injection
					if (containerField != null)
						containerField.set(null, container);

					// inject address, reference, and endpoints
					for (FrameworkField field : getTestClass().getAnnotatedFields(Named.class)) {

						if (!Modifier.isStatic(field.getField().getModifiers()))
							throw new InitializationError("field" + field.getField().getName()
									+ " is annotated with @Named but it is not static");

						Class<?> type = field.getField().getType();
						field.getField().setAccessible(true);
						String name = field.getField().getAnnotation(Named.class).value();

						// reference injection
						if (type.isAssignableFrom(EndpointReferenceType.class))
							field.getField().set(null, container.reference(name));
						else
						// address injection
						if (type.isAssignableFrom(URI.class))
							field.getField().set(null, container.address(name));
						else
							// implementation injection
							field.getField().set(null, container.endpoint(name, type));

					}

					// inject instance references
					for (FrameworkField field : getTestClass().getAnnotatedFields(Reference.class)) {

						if (!Modifier.isStatic(field.getField().getModifiers()))
							throw new InitializationError("field" + field.getField().getName()
									+ " is annotated with @Named but it is not static");

						Class<?> type = field.getField().getType();

						// reference injection
						if (!type.isAssignableFrom(EndpointReferenceType.class))
							throw new InitializationError("field" + field.getField().getName()
									+ " is annotated with @Reference but it is not typed with EndpointReferenceType");

						field.getField().setAccessible(true);

						Reference reference = field.getField().getAnnotation(Reference.class);

						field.getField().set(null,
								container.reference(reference.name(), reference.ns(), reference.key()));

					}
				} catch (Throwable t) {
					Throwable cause = t.getCause();
					if (cause != null) {
						Throwable innerCause = cause.getCause();
						if (innerCause != null)
							innerCause.printStackTrace();
						else
							cause.printStackTrace();
					} else
						t.printStackTrace();
					throw t;
				}

				defaultStatement.evaluate();

			}
		};

		return newstatement;
	}

	/** {@inheritDoc} */
	@Override
	protected Statement withAfterClasses(final Statement statement) {

		final Statement defaultStatement = super.withAfterClasses(statement);
		Statement newstatement = new Statement() {
			public void evaluate() throws Throwable {

				Throwable error = null;

				try {
					defaultStatement.evaluate();
				} catch (Throwable t) {
					error = t;
				}

				if (container != null)
					container.stop();

				if (error != null)
					throw error;
			}
		};

		return newstatement;
	}

}
