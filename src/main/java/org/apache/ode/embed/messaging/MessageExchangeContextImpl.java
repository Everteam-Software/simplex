package org.apache.ode.embed.messaging;

import org.apache.ode.bpel.iapi.*;
import org.apache.ode.embed.MessageSender;
import org.apache.ode.utils.DOMUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.wsdl.Operation;
import javax.wsdl.Fault;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import java.util.Set;
import java.util.HashSet;

public class MessageExchangeContextImpl implements MessageExchangeContext {

    private static final Logger __log = Logger.getLogger(MessageExchangeContextImpl.class);

    MessageSender _sender;

    public MessageExchangeContextImpl(MessageSender sender) {
        _sender  = sender;
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
            Element message = partnerMex.getRequest().getMessage();
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
            Node response = _sender.send(partnerMex.getPortType().getQName().getLocalPart(), invokedOp.getName(), payload);

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

    public void invokePartnerReliable(PartnerRoleMessageExchange partnerRoleMessageExchange) throws ContextException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void invokePartnerTransacted(PartnerRoleMessageExchange partnerRoleMessageExchange) throws ContextException {
        //To change body of implemented methods use File | Settings | File Templates.
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
}