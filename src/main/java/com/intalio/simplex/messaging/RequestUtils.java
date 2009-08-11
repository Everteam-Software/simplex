package com.intalio.simplex.messaging;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.apache.ode.utils.DOMUtils;

import javax.xml.namespace.QName;

import com.intalio.simplex.http.datam.FEJOML;
import com.sun.jersey.api.client.WebResource;

import java.io.UnsupportedEncodingException;

public class RequestUtils {

    public static Element stripWrappers(Element req) {
        return DOMUtils.getFirstChildElement(DOMUtils.getFirstChildElement(req));
    }

    public static String contentType(Element req) {
        Element ce = DOMUtils.findChildByName(withHeaders(req), new QName(null, "Content_Type"));
        if (ce != null) {
            String ces = ce.getTextContent();
            if (FEJOML.recognizeType(ces)) return ces;
        }
        return FEJOML.XML;
    }

    public static Element withHeaders(Element element) {
        Element res = DOMUtils.findChildByName(element, new QName(null, "headers"));
        if (res == null) {
            Element headers = element.getOwnerDocument().createElement("headers");
            element.appendChild(headers);
            res = headers;
        }
        return res;
    }

    public static void handleOutHeaders(Element msg, WebResource.Builder wr) {
        Element headers = withHeaders(msg);
        if (headers != null) {
            NodeList headerElmts = headers.getChildNodes();
            for (int m = 0; m < headerElmts.getLength(); m++) {
                Node n = headerElmts.item(m);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    if (n.getNodeName().equals("basicAuth")) {
                        Element login = DOMUtils.findChildByName((Element) n, new QName(null, "login"));
                        Element password = DOMUtils.findChildByName((Element) n, new QName(null, "password"));
                        if (login != null && password != null) {
                            // TODO rely on Jersey basic auth once 1.0.2 is released
                            try {
                                byte[] unencoded = (login.getTextContent() + ":" + password.getTextContent()).getBytes("UTF-8");
                                String credString = Base64.encode(unencoded);
                                String authHeader = "Basic " + credString;
                                wr.header("authorization", authHeader);
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else if (!n.getNodeName().equals("Content_Type")) {
                        wr.header(n.getNodeName().replaceAll("_", "-"), n.getTextContent());
                    }
                }
            }
        }
    }

    public static Node unwrapToPayload(Element message) {
        Element root = DOMUtils.getFirstChildElement(DOMUtils.getFirstChildElement(message));
        // TODO this assumption only works with SimPEL, in the general case we could have a NodeList
        // and should therefore send the whole part element
        Node payload;
        if (DOMUtils.getFirstChildElement(root) != null)
            payload = DOMUtils.getFirstChildElement(root);
        else {
            Document doc = DOMUtils.newDocument();
            payload = doc.createTextNode(DOMUtils.getTextContent(root));
        }
        return payload;
    }

}
