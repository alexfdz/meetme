/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.handler;

import java.util.List;

import org.dom4j.Element;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.util.NotFoundException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

import com.meetme.openfire.packet.IQMeetsMessage;
import com.meetme.openfire.packet.IQMeetsTimeType;
import com.meetme.openfire.packet.MeetingMessage;
import com.meetme.openfire.util.Constants;
import com.meetme.openfire.vo.MeetingRequest;

/**
 * Implements the TYPE_IQ {@link Constants.IQ_GET_MY_REQUESTS_NAMESPACE} protocol.  Allows users to 
 * obtain his meetings requests form database.
 * The user can ask about specific time restictions @see {@link IQMeetsTimeType}
 * 
 * @author alex
 *
 */
public class IQGetMyRequestsHandler extends AbstractIQHandler {

	public IQGetMyRequestsHandler() {
		super(IQGetMyRequestsHandler.class.getName(), Constants.IQ_GET_MY_REQUESTS_NAMESPACE);
	}

	/* (non-Javadoc)
	 * @see org.jivesoftware.openfire.handler.IQHandler#handleIQ(org.xmpp.packet.IQ)
	 */
	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		IQ reply = IQ.createResultIQ(packet);
		// Check if any of the usernames is null
		String user = packet.getFrom().getNode();
        if (user == null) {
            reply.setError(PacketError.Condition.forbidden);
            return reply;
        }
        
        IQMeetsMessage message = new IQMeetsMessage(((IQ)packet).getChildElement());
        IQMeetsTimeType type = message.getType();
        List<MeetingMessage> meets = null;
        try {
        	meets = this.resolveMeetings(user, type);
		} catch (NotFoundException e) {
			log.error(e.getMessage(), e);
			throw new UnauthorizedException(e);
		}
        IQMeetsMessage meetsContainer = new IQMeetsMessage(Constants.MEETS_ELEMENT_NAME, 
        		Constants.IQ_GET_MY_REQUESTS_NAMESPACE);
        Element response = reply.setChildElement(Constants.QUERY_ELEMENT_NAME,
        		Constants.IQ_GET_MY_REQUESTS_NAMESPACE);
        meetsContainer.setMeets(meets);
        response.add(meetsContainer.getElement());
        
		return reply;
	}
	
	private List<MeetingMessage> resolveMeetings(String user, IQMeetsTimeType type) throws NotFoundException{
		List<MeetingMessage> meetings = null;
		if(IQMeetsTimeType.all.equals(type)){
			meetings = MeetingRequest.findAllEnabledByUser(user);
		}else if(IQMeetsTimeType.current.equals(type)){
			meetings = MeetingRequest.findCurrentEnabledByUser(user);
		}else if(IQMeetsTimeType.past.equals(type)){
			meetings = MeetingRequest.findPastEnabledByUser(user);
		}else if(IQMeetsTimeType.custom.equals(type)){
			//TODO A implementar en futuras fases
			throw new NotFoundException("IQMeetsType not found");
		}else{
			throw new NotFoundException("IQMeetsType not found");
		}
		return meetings;
	}

}
