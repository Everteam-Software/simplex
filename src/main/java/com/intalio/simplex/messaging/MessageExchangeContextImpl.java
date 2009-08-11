/*
 * Simplex, lightweight SimPEL server
 * Copyright (C) 2008-2009  Intalio, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.intalio.simplex.messaging;

import com.intalio.simplex.lifecycle.MessageSender;
import com.intalio.simplex.http.datam.FEJOML;
import static com.intalio.simplex.messaging.RequestUtils.*;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.log4j.Logger;
import org.apache.ode.bpel.iapi.*;
import org.apache.ode.utils.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.wsdl.Fault;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import java.util.HashSet;
import java.util.Set;

public class MessageExchangeContextImpl implements MessageExchangeContext {

    private static final Logger __log = Logger.getLogger(MessageExchangeContextImpl.class);
    private static final Logger __logHttp = Logger.getLogger("com.intalio.simplex.http");

    MessageSender _sender;

    public MessageExchangeContextImpl(MessageSender sender) {
        _sender  = sender;
    }

    public void invokePartnerReliable(PartnerRoleMessageExchange partnerRoleMessageExchange) throws ContextException {
        throw new UnsupportedOperationException("Reliable invocation not supported.");
    }

    public void invokePartnerTransacted(PartnerRoleMessageExchange partnerRoleMessageExchange) throws ContextException {
        throw new UnsupportedOperationException("Transactional invocation not supported.");
    }

    public void invokeRestful(RESTOutMessageExchange restOutMessageExchange) throws ContextException {
        Resource res = restOutMessageExchange.getTargetResource();
        invokeRestful(restOutMessageExchange, res);
    }

    private void invokeRestful(RESTOutMessageExchange restOutMessageExchange, Resource res) throws ContextException {
        ClientConfig cc = new DefaultClientConfig();
        Client c = Client.create(cc);
        c.setFollowRedirects(false); // Handling redirects ourselves, Jersey doesn't set the new location properly
        if (__logHttp.isDebugEnabled()) c.addFilter(new LoggingFilter());

        ClientResponse resp;
        WebResource wr = c.resource(res.getUrl());
        if (restOutMessageExchange.getRequest() != null) {
            Element payload = restOutMessageExchange.getRequest().getMessage();
            String cntType = contentType(stripWrappers(payload));
            WebResource.Builder wrb = wr.type(cntType);
            handleOutHeaders(stripWrappers(payload), wrb);
            if (!"GET".equals(res.getMethod().toUpperCase())) {
                String cnt = FEJOML.fromXML(unwrapToPayload(payload), cntType);
                try {
                    resp = wrb.method(res.getMethod().toUpperCase(), ClientResponse.class, cnt);
                } catch (Exception e) {
                    fail(res.getUrl(), "requestError", e.getCause().getMessage(), restOutMessageExchange);
                    return;
                }
            } else {
                try {
                    resp = wrb.method(res.getMethod().toUpperCase(), ClientResponse.class);
                } catch (Exception e) {
                    fail(res.getUrl(), "requestError", e.getCause().getMessage(), restOutMessageExchange);
                    return;
                }
            }
        } else resp = wr.method(res.getMethod().toUpperCase(), ClientResponse.class);

        if (resp.getStatus() == 204) {
            restOutMessageExchange.replyOneWayOk();
            return;
        }

        if (resp.getStatus() == 302) {
            Resource newTarget = new Resource(resp.getMetadata().getFirst("Location"), res.getContentType(), res.getMethod());
            invokeRestful(restOutMessageExchange, newTarget);
            return;
        }

        String response = resp.getEntity(String.class);

        int responseType = isFaultOrFailure(resp.getStatus());
        if (responseType > 0) {
            faultFromHttpStatus(resp.getStatus(), response, restOutMessageExchange);
            return;
        }
        if (responseType < 0) {
            fail(res.getUrl(), "http" + resp.getStatus(), "Failing with HTTP response code "
                    + resp.getStatus(), restOutMessageExchange);
            return;
        }

        String responseCntType = FEJOML.XML;
        String cntType = resp.getType().toString();
        if (cntType.indexOf(";") > 0) cntType = cntType.split(";")[0];
        if (FEJOML.recognizeType(cntType)) responseCntType = cntType;

        Element responseXML = null;
        if (response != null && response.trim().length() > 0) {
            try {
                responseXML = FEJOML.toXML(response, responseCntType);
            } catch (Exception e) {
                fail(res.getUrl(), "parseError", "Response couldn't be parsed: " + response, restOutMessageExchange);
                return;
            }
        }

        // Prepare the response message
        Document odeMsg = DOMUtils.newDocument();
        Element odeMsgEl = odeMsg.createElementNS(null, "message");
        odeMsg.appendChild(odeMsgEl);
        Element partElmt = odeMsg.createElement("payload");
        odeMsgEl.appendChild(partElmt);
        Element methodElmt = odeMsg.createElement(res.getMethod() + "Response");
        partElmt.appendChild(methodElmt);
        if (responseXML != null)
            methodElmt.appendChild(odeMsg.adoptNode(responseXML));

        // Copy headers
        if (resp.getStatus() == 201 || resp.getStatus() == 302) {
            Element loc = odeMsg.createElement("Location");
            loc.setTextContent(resp.getMetadata().getFirst("Location"));
            withHeaders(methodElmt).appendChild(loc);
        }
        Element status = odeMsg.createElement("Status");
        status.setTextContent(""+resp.getStatus());
        withHeaders(methodElmt).appendChild(status);

        Message responseMsg = restOutMessageExchange.createMessage(null);
        responseMsg.setMessage(odeMsgEl);
        restOutMessageExchange.reply(responseMsg);
    }

    public void cancel(PartnerRoleMessageExchange partnerRoleMessageExchange) throws ContextException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onMyRoleMessageExchangeStateChanged(MyRoleMessageExchange myRoleMessageExchange) throws BpelEngineException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<InvocationStyle> getSupportedInvocationStyle(PartnerRoleChannel partnerRoleChannel, EndpointReference endpointReference) {
        HashSet<InvocationStyle> styles = new HashSet<InvocationStyle>();
        // TODO only unreliable for now, we might want to do transactional at a point
        styles.add(InvocationStyle.UNRELIABLE);
        return styles;
    }

    private void faultFromHttpStatus(int s, String response, RESTOutMessageExchange mex) {
        QName faultName = new QName(null, "http" + s);
        Document odeMsg = DOMUtils.newDocument();
        Element odeMsgEl = odeMsg.createElementNS(null, "message");
        odeMsg.appendChild(odeMsgEl);
        Element partElmt = odeMsg.createElement("payload");
        odeMsgEl.appendChild(partElmt);
        Element methodElmt = odeMsg.createElementNS(faultName.getNamespaceURI(), faultName.getLocalPart());
        partElmt.appendChild(methodElmt);
        methodElmt.setTextContent(response);

        Message responseMsg = mex.createMessage(null);
        responseMsg.setMessage(odeMsgEl);
        mex.replyWithFault(faultName, responseMsg);
    }

    private void fail(String calledUrl, String errElmt, String text, RESTOutMessageExchange mex) {
        Document doc = DOMUtils.newDocument();
        Element failureElmt = doc.createElement(errElmt);
        failureElmt.setTextContent(text);
        String fullMsg = "Request to " + calledUrl + " failed. " + text;
        __log.info(fullMsg);
        mex.replyWithFailure(MessageExchange.FailureType.FORMAT_ERROR, fullMsg, failureElmt);
    }

    /**
     * @param s, the status code to test, must be in [400, 600[
     * @return 1 if fault, -1 if failure, 0 if success
     */
    public static int isFaultOrFailure(int s) {
        if (s < 100 || s >= 600)
            throw new IllegalArgumentException("Status-Code must be in interval [400,600]");

        if (s == 500 || s == 501 || s == 502 || s == 505
                || s == 400 || s == 402 || s == 403 || s == 404 || s == 405 || s == 406
                || s == 409 || s == 410 || s == 412 || s == 413 || s == 414 || s == 415
                || s == 411 || s == 416 || s == 417) {
            return 1;
        } else if (s == 503 || s == 504 || s == 401 || s == 407 || s == 408) {
            return -1;
        } else {
            return 0;
        }
    }

    public void invokePartnerUnreliable(PartnerRoleMessageExchange partnerMex) throws ContextException {
        if (_sender == null) {
            partnerMex.replyWithFailure(MessageExchange.FailureType.ABORTED,
                    "No sender configured, can't send the message.", partnerMex.getRequest().getMessage());
            __log.warn("No sender configured, can't send the message:\n"
                    + DOMUtils.domToString(partnerMex.getRequest().getMessage()));
        }

        Operation invokedOp = partnerMex.getPortType().getOperation(partnerMex.getOperationName(), null, null);
        try {
            // We're placing ourselves in the doc/lit case for now, assuming a single part with a single root element
            Node response = _sender.send(partnerMex.getPortType().getQName().getLocalPart(),
                    invokedOp.getName(), unwrapToPayload(partnerMex.getRequest().getMessage()));

            if (invokedOp.getOutput() != null) {
                Document responseDoc = DOMUtils.newDocument();
                Element messageElmt = responseDoc.createElement("message");
                responseDoc.appendChild(messageElmt);
                // Pretty hard to get the part name huh?
                String partName = (String) invokedOp.getOutput().getMessage().getParts().keySet().iterator().next();
                Element partElmt = responseDoc.createElement(partName);
                messageElmt.appendChild(partElmt);
                // TODO same thing, simpel only wrapping
                QName elmtName = ((Part)invokedOp.getOutput().getMessage().getParts().values().iterator().next()).getElementName();
                Element partRootElmt = responseDoc.createElementNS(elmtName.getNamespaceURI(), elmtName.getLocalPart());
                partElmt.appendChild(partRootElmt);
                if (response != null) partRootElmt.appendChild(responseDoc.importNode(response, true));

                Message responseMsg = partnerMex.createMessage(invokedOp.getOutput().getMessage().getQName());
                responseMsg.setMessage(messageElmt);
                partnerMex.reply(responseMsg);
            } else {
                partnerMex.replyOneWayOk();
            }
        } catch (RuntimeException re) {
            __log.warn("The service called threw a runtime exception:\n"
                    + DOMUtils.domToString(partnerMex.getRequest().getMessage()), re);
            // Runtimes are considered failures
            partnerMex.replyWithFailure(MessageExchange.FailureType.COMMUNICATION_ERROR,
                    "The service called threw a runtime exception: " + re.toString(),
                    partnerMex.getRequest().getMessage());
        } catch (Exception e) {
            __log.warn("The service called threw a checked exception:\n"
                    + DOMUtils.domToString(partnerMex.getRequest().getMessage()), e);
            // checked exceptions are considered faults
            Fault fault = invokedOp.getFault(e.getClass().getName());
            Message faultMsg = partnerMex.createMessage(fault.getMessage().getQName());
            Document faultDoc = DOMUtils.newDocument();
            Element faultElmt = faultDoc.createElement("exception");
            faultElmt.setTextContent(e.getMessage());
            faultMsg.setMessage(faultElmt);
            partnerMex.replyWithFault(new QName(
                    partnerMex.getPortType().getQName().getNamespaceURI(), fault.getName()), faultMsg);
        }
    }

}
