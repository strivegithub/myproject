package com.d4games.dzix.protocol;

import java.lang.reflect.Constructor;

import com.d4games.dzix.RestController;
import com.d4games.dzix.model.DzixHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.Observer;

import com.netflix.hystrix.HystrixCommand;
import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;

public class DefaultRestAsyncController implements RestController {
	private static final Logger log = LoggerFactory.getLogger(DefaultRestAsyncController.class);
	private Class<?> classHttpCommand;

	// POST
	public Object create(Request request, Response response) {
		return processAsync(request, response);
	}

	// GET
	public Object read(Request request, Response response) {
		return processAsync(request, response);
	}

	// PUT
	public Object update(Request request, Response response) {
		return processAsync(request, response);
	}

	// DELETE
	public Object delete(Request request, Response response) {
		return processAsync(request, response);
	}

	private Object processAsync(final Request request, final Response response) {
		try {
			Constructor<?> constructor = getCmdConstructor();
			HystrixCommand<?> instance = (HystrixCommand<?>) constructor.newInstance(request, response);
			Observable<?> fResult = (Observable<?>) instance.observe();
			fResult.subscribe(new Observer<Object>() {

				@Override
				public void onCompleted() {
					log.debug("completed : {}", request.getUrl());
				}

				@Override
				public void onError(Throwable e) {
					log.error(e.toString(), e.getCause());
					HttpServerHandler hsh = (HttpServerHandler) request.getAttachment("hsh");
					hsh.processResponse(request, response, true,
							new DzixHttpResponse(response, HttpResponseStatus.GONE.getCode(), e.getCause().getClass()
									.getSimpleName()));
				}

				@Override
				public void onNext(Object result) {
					HttpServerHandler hsh = (HttpServerHandler) request.getAttachment("hsh");
					hsh.processResponse(request, response, true, result);
				}
			});

			// new WsTextCommand(handshakerUri, handshakerUri, ctx,
			// frame).execute();
		} catch (Throwable e) {
			log.error("Exception", e);
			return new DzixHttpResponse(response, HttpResponseStatus.NOT_IMPLEMENTED.getCode(), e.getCause().getClass()
					.getSimpleName());
		} finally {
		}
		return this;
	}

	private Constructor<?> getCmdConstructor() throws NoSuchMethodException {
		Class<?>[] constructorParameterTypes = { Request.class, Response.class };
		Constructor<?> constructor = classHttpCommand.getConstructor(constructorParameterTypes);
		return constructor;
	}

	public boolean isValid() {
		try {
			Constructor<?> constructor = getCmdConstructor();
		} catch (NoSuchMethodException e) {
			return false;
		}
		return true;
	}

	public DefaultRestAsyncController(Class<?> classHttpCommand) {
		this.classHttpCommand = classHttpCommand;
	}
}
