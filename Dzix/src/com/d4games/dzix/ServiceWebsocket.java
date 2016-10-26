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

import org.jboss.netty.channel.ChannelPipelineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.d4games.dzix.cmd.WsBinaryCommand;
import com.d4games.dzix.cmd.WsTextCommand;
import com.d4games.dzix.protocol.WebsocketPipelineBuilder;

/**
 * Primary entry point to create a RestExpress service. All that's required is a
 * RouteDeclaration. By default: port is 8081, serialization format is JSON,
 * supported formats are JSON and XML.
 * 
 * @author toddf
 */
public class ServiceWebsocket extends Service {
	private static final Logger log = LoggerFactory.getLogger(ServiceWebsocket.class);

	private Class<?> classWsTextCommand = WsTextCommand.class;
	private Class<?> classWsBinaryCommand = WsBinaryCommand.class;

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
	public ServiceWebsocket() {
		super();
		setName(DEFAULT_NAME);
		setProtocol("WS");
	}

	protected ChannelPipelineFactory CreateChannelPipelineFactory() {
		ChannelPipelineFactory pf = new WebsocketPipelineBuilder(this.UseSsl(), classWsTextCommand,
				classWsBinaryCommand);
		return pf;
	}

	public ServiceWebsocket setClassWsTextCommand(Class<?> classWsTextCommand) {
		try {
			WsTextCommand.getConstructor(classWsTextCommand);
		} catch (NoSuchMethodException | SecurityException e) {
			log.error("classWsTextCommand is not WsTextCommand", e);
		}
		this.classWsTextCommand = classWsTextCommand;
		return this;
	}

	public ServiceWebsocket setClassWsBinaryCommand(Class<?> classWsBinaryCommand) {
		try {
			WsBinaryCommand.getConstructor(classWsBinaryCommand);
		} catch (NoSuchMethodException | SecurityException e) {
			log.error("classWsBinaryCommand is not WsBinaryCommand", e);
		}
		this.classWsBinaryCommand = classWsBinaryCommand;
		return this;
	}
}
