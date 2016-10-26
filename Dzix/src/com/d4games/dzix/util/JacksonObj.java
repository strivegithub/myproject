package com.d4games.dzix.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JacksonObj {
	private final Logger log = LoggerFactory.getLogger(JacksonObj.class);
	private ObjectMapper mapper = new ObjectMapper();
	private JsonNode rootNode = null;

	public JacksonObj() {
		this(false);
	}

	public JacksonObj(boolean rootArrayNode) {
		if (rootArrayNode)
			rootNode = mapper.createArrayNode();
		else
			rootNode = mapper.createObjectNode();
	}

	public JacksonObj(byte[] src) {
		rootNode = createJsonNode(src);
	}

	public JacksonObj(String src) {
		rootNode = createJsonNode(src);
	}

	public ObjectMapper getObjectMapper() {
		return mapper;
	}

	public ObjectNode getObj() {
		return (ObjectNode) rootNode;
	}

	public ArrayNode getArray() {
		return (ArrayNode) rootNode;
	}

	public String getText(JsonNode node) {
		return node.textValue();
	}

	public ObjectNode createObjectNode(String json) {
		try {
			return (ObjectNode) mapper.readTree(json);
		} catch (Exception e) {
			return null;
		}
	}

	public JsonNode createJsonNode(String json) {
		try {
			return mapper.readTree(json);
		} catch (Exception e) {
			return null;
		}
	}

	public JsonNode createJsonNode(byte[] json) {
		try {
			return mapper.readTree(json);
		} catch (Exception e) {
			return null;
		}
	}

	public ObjectNode addChildObj(String name) {
		ObjectNode node = mapper.createObjectNode();
		if (rootNode.isArray()) {
			getArray().add(node);
			return node;
		} else {
			getObj().put(name, node);
			return node;
		}
	}

	public String toString() {
		return toString(rootNode);
	}

	public String toString(JsonNode node) {
		try {
			return mapper.writeValueAsString(node);
		} catch (JsonProcessingException e) {
			log.error("Exception", e);
		}
		return null;
	}

	public Map<String, Object> getMap() {
		return getMap(rootNode);
	}

	public Map<String, Object> getMap(JsonNode node) {
		try {
			return mapper.treeToValue(node, Map.class);
		} catch (JsonProcessingException e) {
			log.error("Exception", e);
		}
		return null;
	}
}
