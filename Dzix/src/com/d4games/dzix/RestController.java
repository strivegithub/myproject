package com.d4games.dzix;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;

public interface RestController {
	
	// POST
	public Object create(Request request, Response response);

	// GET
	public Object read(Request request, Response response);

	// PUT
	public Object update(Request request, Response response);

	// DELETE
	public Object delete(Request request, Response response);
}
