package com.d4games.dzix.util;

import java.io.IOException;
import java.util.Map;

import com.d4games.dzix.model.DzixHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonRpc {
	private final Logger log = LoggerFactory.getLogger(JsonRpc.class);
	private ObjectMapper mapper = new ObjectMapper();
	private ArrayNode arrayNode = null;

	public JsonRpc() {
		arrayNode = mapper.createArrayNode();
		arrayNode.add("rpc");
		arrayNode.add(mapper.createObjectNode());
		arrayNode.add(mapper.createObjectNode());
	}

	public JsonRpc(byte[] src) {
		try {
			arrayNode = (ArrayNode) mapper.readTree(src);
			if (arrayNode.size() != 3)
				throw new RuntimeException("Invalid JsonRpc");
		} catch (IOException e) {
			log.error("Exception", e);
		}
	}

	public JsonRpc(String src) {
		try {
			arrayNode = (ArrayNode) mapper.readTree(src);
			if (arrayNode.size() != 3)
				throw new RuntimeException("Invalid JsonRpc");
		} catch (IOException e) {
			log.error("Exception", e);
		}
	}

	public ObjectMapper getObjectMapper() {
		return mapper;
	}

	public String getUri() {
		return arrayNode.get(0).textValue();
	}

	public String GetReplacedUri(String prefix, String replacement) {
		return replacement + getUri().substring(prefix.length());
	}

	public JsonRpc setUri(String uri) {
		arrayNode.remove(0);
		arrayNode.insert(0, uri);
		return this;
	}

	public ObjectNode getBody() {
		return (ObjectNode) arrayNode.get(2);
	}

	public JsonRpc setBody(JsonNode node) {
		arrayNode.remove(2);
		arrayNode.add(node);
		return this;
	}

	public boolean setBody(String json) {
		try {
			setBody((ObjectNode) mapper.readTree(json));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean setBodyAsString(String json) {
		try {
			arrayNode.remove(2);
			arrayNode.add(json);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public JsonNode newJsonNode(String json) {
		try {
			return mapper.readTree(json);
		} catch (Exception e) {
			return null;
		}
	}

	public void setBody(byte[] json) {
		try {
			setBody((ObjectNode) mapper.readTree(json));
		} catch (IOException e) {
			log.error("Exception", e);
		}
	}

	public boolean setBody(DzixHttpResponse dzixResponse) {
		try {
			String json = mapper.writeValueAsString(dzixResponse);
			setBody(json);
			return true;
		} catch (Exception e) {
			log.error("Exception", e);
			return false;
		}
	}

	public void clearBody() {
		getBody().removeAll();
	}

	public ObjectNode getBodyChild(String childname) {
		return (ObjectNode) getBody().path(childname);
	}

	public ObjectNode addBodyChild(String childname) {
		ObjectNode node = mapper.createObjectNode();
		getBody().put(childname, node);
		return node;
	}

	public String toString() {
		try {
			return mapper.writeValueAsString(arrayNode);
		} catch (JsonProcessingException e) {
			log.error("Exception", e);
		}
		return null;
	}

	public String getBodyString() {
		try {
			return mapper.writeValueAsString(getBody());
		} catch (JsonProcessingException e) {
			log.error("Exception", e);
		}
		return null;
	}

	public Map<String, Object> getBodyMap() {
		try {
			return mapper.treeToValue(getBody(), Map.class);
		} catch (JsonProcessingException e) {
			log.error("Exception", e);
		}
		return null;
	}

	public ObjectNode getHeader() {
		return (ObjectNode) arrayNode.get(1);
	}

	public String getHeader(String name) {
		ObjectNode node = getHeader();
		if (node == null)
			return "";
		node = (ObjectNode) node.get(name);
		if (node == null)
			return "";
		return node.textValue();
	}

	public void clearHeader() {
		getHeader().removeAll();
	}
}
