var ROOT = "http://localhost:3434";

/*
function fail(msg) {
    throw msg;
}

function assert(msg, cond) {
    var realCond = cond === undefined ? msg : cond;
    if (!realCond) fail(msg);
    else realCond;
}

*/

var jz = Packages.com.sun.jersey.api.client;
var reqUtils = Packages.com.intalio.simplex.embed.messaging.RequestUtils;
var fejoml = Packages.com.intalio.simplex.http.datam.FEJOML;

/* Builds an HTTP request calling the provided url and method using the payload. Both method and payload are
 * optional (defaulting on GET with an empty payload). Redirects are automatically followed. Returns an object
 * with status and payload values.
 */
function request(url, method, payload) {
    var cc = new jz.config.DefaultClientConfig();
    var c = jz.Client.create(cc);
    c.setFollowRedirects(false); // Handling redirects ourselves, Jersey doesn't set the new location properly

    if (!method) method = "GET";

    var resp = null;
    if (payload) {
        var wr = c.resource(url);
        if (typeof payload == "xml") {
            // Unwrap the DOM
            payload = e4xToDOM(payload);
            var cntType = reqUtils.contentType(payload);
            var wrb = wr.type(cntType);
            reqUtils.handleOutHeaders(payload, wrb);
            var cnt = fejoml.fromXML(payload, cntType);
        } else {
            var wrb = wr.type("text/plain");
            var cnt = payload;
        }

        print(method + " " + cnt);
        if (method.toUpperCase() != "GET") {
            resp = wrb.method(method.toUpperCase(), jz.ClientResponse, cnt);
        } else {
            resp = wrb.method(method.toUpperCase(), jz.ClientResponse);
        }
    } else {
        resp = wrb.method(method.toUpperCase(), jz.ClientResponse);
    }

    if (resp.getStatus() == 302) {
        resp = request(resp.getMetadata().getFirst("Location"), method, payload);
    } else if (resp.getStatus() >= 400) {
        throw "Request error, return code: " + resp.getStatus();
    }

    var respPayload = resp.getEntity(java.lang.String);
    try {
        respPayload = new XML(respPayload = new XML(respPayload.substring(39)));
    } catch(err) {
        respPayload = new String(respPayload.toString()); // Converting the Java string back to JS
    };


    return {status: resp.getStatus(), payload: respPayload};
}

function e4xToDOM(node) {
    return org.mozilla.javascript.xmlimpl.XMLLibImpl.toDomNode(node);
}