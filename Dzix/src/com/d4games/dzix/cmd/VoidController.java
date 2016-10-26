package com.d4games.dzix.cmd;

import com.d4games.dzix.RestController;
import com.d4games.dzix.model.DzixHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;

/**
 * @author toddf
 * @since Aug 31, 2010
 */
public class VoidController implements RestController {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(VoidController.class);

    // POST
    public Object create(Request request, Response response) {
        return new VoidCommand(request, response).execute();
    }

    // GET
    public Object read(Request request, Response response) {
        return new VoidCommand(request, response).execute();
    }

    // PUT
    public Object update(Request request, Response response) {
        return new VoidCommand(request, response).execute();
    }

    // DELETE
    public Object delete(Request request, Response response) {
        return new VoidCommand(request, response).execute();
    }

    public class VoidCommand extends DzixHttpCommand {

        public VoidCommand(Request request, Response response) {
            super("Void", request, response);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected DzixHttpResponse run() {
            return makeResponseNotImplemented();
        }
    }
}
