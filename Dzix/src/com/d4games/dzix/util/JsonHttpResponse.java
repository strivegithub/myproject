/*
    Copyright 2011, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package com.d4games.dzix.util;

import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.strategicgains.restexpress.Response;

/**
 * @author toddf
 * @since Dec 20, 2011
 */
public class JsonHttpResponse
{
    private int result = 200;
	private String resultTxt = "OK";
	private Map<String,String> data = new HashMap<String, String>();

	public JsonHttpResponse()
    {
    }
		
	public JsonHttpResponse(Response response, HttpResponseStatus status)
    {
	    super();
	    this.setResult(status.getCode());
	    this.setResultTxt(status.getReasonPhrase());
	    response.setResponseCode(getResult());
    }
	
	public JsonHttpResponse(Response response, String resultTxt)
    {
	    super();
	    this.setResult(HttpResponseStatus.BAD_REQUEST.getCode());
	    this.setResultTxt(resultTxt);
	    response.setResponseCode(result);
    }

	public int getResult() {
		return result;
	}

	public JsonHttpResponse setResult(int result) {
		this.result = result;
		return this;
	}
	
	public String getResultTxt() {
		return resultTxt;
	}

	public JsonHttpResponse setResultTxt(String resultTxt) {
		this.resultTxt = resultTxt;
		return this;
	}

	public Map<String,String> getData() {
		return data;
	}

	public JsonHttpResponse setData(Map<String,String> data) {
		this.data.putAll(data);
		return this;
	}
	
	public JsonHttpResponse setData(String key, String value) {
		this.data.put(key, value);
		return this;
	}
}
