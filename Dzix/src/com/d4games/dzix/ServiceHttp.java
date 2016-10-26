/*
 * Copyright 2009-2012, Strategic Gains, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.d4games.dzix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strategicgains.restexpress.Format;
import com.strategicgains.restexpress.domain.metadata.ServerMetadata;
import com.strategicgains.restexpress.exception.ExceptionMapping;
import com.strategicgains.restexpress.exception.ServiceException;
import com.strategicgains.restexpress.pipeline.Postprocessor;
import com.strategicgains.restexpress.pipeline.Preprocessor;
import com.strategicgains.restexpress.response.ResponseProcessor;
import com.strategicgains.restexpress.response.ResponseProcessorResolver;
import com.strategicgains.restexpress.route.RouteDeclaration;
import com.strategicgains.restexpress.route.RouteResolver;
import com.strategicgains.restexpress.route.parameterized.ParameterizedRouteBuilder;
import com.strategicgains.restexpress.route.regex.RegexRouteBuilder;
import com.strategicgains.restexpress.serialization.AliasingSerializationProcessor;
import com.strategicgains.restexpress.settings.RouteDefaults;
import com.strategicgains.restexpress.util.Resolver;
import com.d4games.dzix.protocol.DefaultRestAsyncController;
import com.d4games.dzix.protocol.HttpPipelineBuilder;
import com.d4games.dzix.protocol.HttpServerHandler;
import com.d4games.dzix.serialization.ResponseProcessors;

/**
 * Primary entry point to create a RestExpress service. All that's required is a
 * RouteDeclaration. By default: port is 8081, serialization format is JSON,
 * supported formats are JSON and XML.
 * 
 * @author toddf
 */
public class ServiceHttp extends Service {
	private static final Logger log = LoggerFactory.getLogger(ServiceHttp.class);

	private RouteDefaults routeDefaults = new RouteDefaults();

	Map<String, ResponseProcessor> responseProcessors = new HashMap<String, ResponseProcessor>();
	private List<CustomMessageObserver> messageObservers = new ArrayList<CustomMessageObserver>();
	private List<Preprocessor> preprocessors = new ArrayList<Preprocessor>();
	private List<Postprocessor> postprocessors = new ArrayList<Postprocessor>();
	private List<Postprocessor> finallyProcessors = new ArrayList<Postprocessor>();
	private Resolver<ResponseProcessor> responseResolver;
	private ExceptionMapping exceptionMap = new ExceptionMapping();
	private RouteDeclaration routeDeclarations = new RouteDeclaration();

	private static Map<String, RestController> mapController = new HashMap<String, RestController>();

	/**
	 * Create a new RestExpress service. By default, RestExpress uses port 8081.
	 * Supports JSON, and XML, providing JSEND-style wrapped responses. And
	 * displays some messages on System.out. These can be altered with the
	 * setPort(), noJson(), noXml(), noSystemOut(), and useRawResponses() DSL
	 * modifiers, respectively, as needed.
	 * 
	 * <p/>
	 * The default input and output format for messages is JSON. To change that,
	 * use the setDefaultFormat(String) DSL modifier, passing the format to use
	 * by default. Make sure there's a corresponding SerializationProcessor for
	 * that particular format. The Format class has the basics.
	 * 
	 * <p/>
	 * This DSL was created as a thin veneer on Netty functionality. The bind()
	 * method simply builds a Netty pipeline and uses this builder class to
	 * create it. Underneath the covers, RestExpress uses Google GSON for JSON
	 * handling and XStream for XML processing. However, both of those can be
	 * swapped out using the putSerializationProcessor(String,
	 * SerializationProcessor) method, creating your own instance of
	 * SerializationProcessor as necessary.
	 * 
	 * @param routes
	 *            a RouteDeclaration that declares the URL routes that this
	 *            service supports.
	 */
	public ServiceHttp() {
		super();
		setName(DEFAULT_NAME);
		supportJson(true);
		supportXml();
		setProtocol("HTTP");
	}

	public String getBaseUrl() {
		return routeDefaults.getBaseUrl();
	}

	public ServiceHttp setBaseUrl(String baseUrl) {
		routeDefaults.setBaseUrl(baseUrl);
		return this;
	}

	public ServiceHttp putResponseProcessor(String format, ResponseProcessor processor) {
		responseProcessors.put(format, processor);
		return this;
	}

	/* package protected */Map<String, ResponseProcessor> getResponseProcessors() {
		return responseProcessors;
	}

	public Resolver<ResponseProcessor> getResponseResolver() {
		return responseResolver;
	}

	public ServiceHttp setResponseResolver(Resolver<ResponseProcessor> responseResolver) {
		this.responseResolver = responseResolver;
		return this;
	}

	public String getDefaultFormat() {
		return routeDefaults.getDefaultFormat();
	}

	public ServiceHttp setDefaultFormat(String format) {
		if (format == null || format.trim().isEmpty())
			return this;

		routeDefaults.setDefaultFormat(format.trim().toLowerCase());
		return this;
	}

	/**
	 * Tell RestExpress to support JSON in routes, incoming and outgoing. By
	 * default RestExpress supports JSON and is the default.
	 * 
	 * @param isDefault
	 *            true to make JSON the default format.
	 * @return the RestExpress instance.
	 */
	public ServiceHttp supportJson(boolean isDefault) {
		if (!getResponseProcessors().containsKey(Format.JSON)) {
			responseProcessors.put(Format.JSON, ResponseProcessors.json());
		}

		if (isDefault) {
			setDefaultFormat(Format.JSON);
		}

		return this;
	}

	/**
	 * Tell RestExpress to support JSON in routes, incoming and outgoing. By
	 * default RestExpress supports JSON and is the default.
	 * 
	 * @return the RestExpress instance.
	 */
	public ServiceHttp supportJson() {
		return supportJson(false);
	}

	/**
	 * Tell RestExpress to not support JSON in routes, incoming or outgoing.
	 * Client must call setDefaultFormat(String) to set the default format to
	 * something else.
	 * 
	 * @return the RestExpress instance.
	 */
	public ServiceHttp noJson() {
		responseProcessors.remove(Format.JSON);
		return this;
	}

	/**
	 * Tell RestExpress to support XML in routes, incoming and outgoing. By
	 * default RestExpress supports XML.
	 * 
	 * @param isDefault
	 *            true to make XML the default format.
	 * @return the RestExpress instance.
	 */
	public ServiceHttp supportXml(boolean isDefault) {
		if (!getResponseProcessors().containsKey(Format.XML)) {
			getResponseProcessors().put(Format.XML, ResponseProcessors.xml());
		}

		if (isDefault) {
			setDefaultFormat(Format.XML);
		}

		return this;
	}

	/**
	 * Tell RestExpress to support XML in routes, incoming and outgoing. By
	 * default RestExpress supports XML.
	 * 
	 * @param isDefault
	 *            true to make XML the default format.
	 * @return the RestExpress instance.
	 */
	public ServiceHttp supportXml() {
		return supportXml(false);
	}

	/**
	 * Tell RestExpress to not support XML in routes, incoming or outgoing.
	 * 
	 * @return the RestExpress instance.
	 */
	public ServiceHttp noXml() {
		responseProcessors.remove(Format.XML);
		return this;
	}

	/**
	 * Tell RestExpress to support TXT format specifiers in routes, outgoing
	 * only at present.
	 * 
	 * @param isDefault
	 *            true to make TXT the default format.
	 * @return the RestExpress instance.
	 */
	public ServiceHttp supportTxt(boolean isDefault) {
		if (!getResponseProcessors().containsKey(Format.TXT)) {
			getResponseProcessors().put(Format.TXT, ResponseProcessor.defaultTxtProcessor());
		}

		if (isDefault) {
			setDefaultFormat(Format.TXT);
		}

		return this;
	}

	/**
	 * Tell RestExpress to support TXT format specifier in routes, outgoing only
	 * at present.
	 * 
	 * @return the RestExpress instance.
	 */
	public ServiceHttp supportTxt() {
		return supportTxt(false);
	}

	public ServiceHttp addMessageObserver(CustomMessageObserver observer) {
		if (!messageObservers.contains(observer)) {
			messageObservers.add(observer);
		}

		return this;
	}

	public List<CustomMessageObserver> getMessageObservers() {
		return Collections.unmodifiableList(messageObservers);
	}

	/**
	 * Add a Preprocessor instance that gets called before an incoming message
	 * gets processed. Preprocessors get called in the order in which they are
	 * added. To break out of the chain, simply throw an exception.
	 * 
	 * @param processor
	 * @return
	 */
	public ServiceHttp addPreprocessor(Preprocessor processor) {
		if (!preprocessors.contains(processor)) {
			preprocessors.add(processor);
		}

		return this;
	}

	public List<Preprocessor> getPreprocessors() {
		return Collections.unmodifiableList(preprocessors);
	}

	/**
	 * Add a Postprocessor instance that gets called after an incoming message
	 * is processed. A Postprocessor is useful for augmenting or transforming
	 * the results of a controller or adding headers, etc. Postprocessors get
	 * called in the order in which they are added. Note however, they do NOT
	 * get called in the case of an exception or error within the route.
	 * 
	 * @param processor
	 * @return
	 */
	public ServiceHttp addPostprocessor(Postprocessor processor) {
		if (!postprocessors.contains(processor)) {
			postprocessors.add(processor);
		}

		return this;
	}

	public List<Postprocessor> getPostprocessors() {
		return Collections.unmodifiableList(postprocessors);
	}

	/**
	 * Add a Postprocessor instance that gets called in a finally block after
	 * the message is processed. Finally processors are Postprocessor instances
	 * that are guaranteed to run even if an error is thrown from the controller
	 * or somewhere else in the route. A Finally Processor is useful for adding
	 * headers or transforming results even during error conditions. Finally
	 * processors get called in the order in which they are added.
	 * 
	 * If an exception is thrown during finally processor execution, the finally
	 * processors following it are executed after printing a stack trace to the
	 * System.err stream.
	 * 
	 * @param processor
	 * @return RestExpress for method chaining.
	 */
	public ServiceHttp addFinallyProcessor(Postprocessor processor) {
		if (!postprocessors.contains(processor)) {
			postprocessors.add(processor);
		}

		return this;
	}

	public List<Postprocessor> getFinallyProcessors() {
		return Collections.unmodifiableList(finallyProcessors);
	}

	/**
	 * 
	 * @param elementName
	 * @param theClass
	 * @return
	 */
	public ServiceHttp alias(String elementName, Class<?> theClass) {
		routeDefaults.addXmlAlias(elementName, theClass);
		return this;
	}

	public <T extends Exception, U extends ServiceException> ServiceHttp mapException(Class<T> from, Class<U> to) {
		exceptionMap.map(from, to);
		return this;
	}

	public ServiceHttp setExceptionMap(ExceptionMapping mapping) {
		this.exceptionMap = mapping;
		return this;
	}

	protected ChannelPipelineFactory CreateChannelPipelineFactory() {
		// Set up the event pipeline factory.
		HttpServerHandler requestHandler = new HttpServerHandler(createRouteResolver(),
				createResponseProcessorResolver());

		// Add MessageObservers to the request handler here, if desired...
		requestHandler.addMessageObserver(messageObservers.toArray(new CustomMessageObserver[0]));

		requestHandler.setExceptionMap(exceptionMap);

		// Add pre/post processors to the request handler here...
		addPreprocessors(requestHandler);
		addPostprocessors(requestHandler);
		addFinallyProcessors(requestHandler);

		ExecutionHandler executionHandler = null;
		if (getExecutorThreadCount() > 0) {
			executionHandler = new ExecutionHandler(new OrderedMemoryAwareThreadPoolExecutor(getExecutorThreadCount(),
					0, 0));
		}

		ChannelPipelineFactory pf = new HttpPipelineBuilder(this.UseSsl(), getUserAgent(), getDzixDecoderName(), getDzixEncoderName(), getDzixNoneCryptoIpList())
                .addRequestHandler(requestHandler)
				.setExecutionHandler(executionHandler);

		return pf;
	}

	/**
	 * @return
	 */
	private RouteResolver createRouteResolver() {
		return new RouteResolver(routeDeclarations.createRouteMapping(routeDefaults));
	}

	/**
	 * @return
	 */
	public ServerMetadata getRouteMetadata() {
		ServerMetadata m = new ServerMetadata();
		m.setName(getName());
		m.setPort(getPort());
		m.setDefaultFormat(getDefaultFormat());
		m.addAllSupportedFormats(getResponseProcessors().keySet());
		m.addAllRoutes(routeDeclarations.getMetadata());
		return m;
	}

	/**
	 * @return
	 */
	private ResponseProcessorResolver createResponseProcessorResolver() {
		ResponseProcessorResolver resolver = new ResponseProcessorResolver();
		resolver.setDefaultFormat(getDefaultFormat());

		for (Entry<String, ResponseProcessor> entry : getResponseProcessors().entrySet()) {
			if (entry.getKey().equals(Format.XML)) {
				setXmlAliases((AliasingSerializationProcessor) entry.getValue().getSerializer());
			}

			resolver.put(entry.getKey(), entry.getValue());
		}

		return resolver;
	}

	/**
	 * @param processor
	 */
	private void setXmlAliases(AliasingSerializationProcessor processor) {
		routeDefaults.setXmlAliases(processor);
	}

	/**
	 * @param requestHandler
	 */
	private void addPreprocessors(HttpServerHandler requestHandler) {
		for (Preprocessor processor : getPreprocessors()) {
			requestHandler.addPreprocessor(processor);
		}
	}

	/**
	 * @param requestHandler
	 */
	private void addPostprocessors(HttpServerHandler requestHandler) {
		for (Postprocessor processor : getPostprocessors()) {
			requestHandler.addPostprocessor(processor);
		}
	}

	/**
	 * @param requestHandler
	 */
	private void addFinallyProcessors(HttpServerHandler requestHandler) {
		for (Postprocessor processor : getFinallyProcessors()) {
			requestHandler.addFinallyProcessor(processor);
		}
	}

	// SECTION: ROUTE CREATION

	public ParameterizedRouteBuilder uri(String uriPattern, RestController controller) {
		mapController.put(uriPattern, controller);
		log.info("uri {} -> controller {}", uriPattern, controller);
		return routeDeclarations.uri(uriPattern, controller, routeDefaults);
	}

	public ParameterizedRouteBuilder uri(String uriPattern, Class<?> classHttpCommand) {
		DefaultRestAsyncController controller = new DefaultRestAsyncController(classHttpCommand);
		if (!controller.isValid()) {
			log.error("!!! INVALID URI !!! {} -> controller {}", uriPattern, controller);
			throw new RuntimeException("URI Controller is invalid");
		}

		mapController.put(uriPattern, controller);
		log.info("uri {} -> controller {}", uriPattern, classHttpCommand.getName());
		return routeDeclarations.uri(uriPattern, controller, routeDefaults);
	}

	public RegexRouteBuilder regex(String uriPattern, Object controller) {
		return routeDeclarations.regex(uriPattern, controller, routeDefaults);
	}
}
