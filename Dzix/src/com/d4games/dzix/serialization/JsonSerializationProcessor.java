package com.d4games.dzix.serialization;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strategicgains.restexpress.serialization.json.DefaultJsonProcessor;

/**
 * @author toddf
 * @since Oct 10, 2011
 */
public class JsonSerializationProcessor
extends DefaultJsonProcessor
{

	public JsonSerializationProcessor()
    {
	    super();
    }

	@Override
	protected void initializeMapper(ObjectMapper mapper) {
		// TODO Auto-generated method stub
		super.initializeMapper(mapper);
		mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
		//mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}
}
