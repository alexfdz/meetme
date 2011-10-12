/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.handler;

import org.dom4j.Element;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.util.NotFoundException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

import com.meetme.openfire.packet.IQMeetsMessage;
import com.meetme.openfire.packet.MeetingMessage;
import com.meetme.openfire.util.Constants;
import com.meetme.openfire.vo.Meeting;

/**
 * Implements the TYPE_IQ {@link Constants.IQ_GET_MEET_REQUESTS_NAMESPACE} protocol. Allows users to 
 * obtain the info, and meeting requests of a specific meeting instance.
 * 
 * The service checks that the user is the meenting's owner
 *
 * @author alex
 *
 */
public class IQGetMeetRequestsHandler extends AbstractIQHandler {

	public IQGetMeetRequestsHandler() {
		super(IQGetMeetRequestsHandler.class.getName(), Constants.IQ_GET_MEET_REQUESTS_NAMESPACE);
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
        	log.error("User from is null");
            reply.setError(PacketError.Condition.forbidden);
            return reply;
        }
        
        IQMeetsMessage message = new IQMeetsMessage(((IQ)packet).getChildElement());
        Element response = reply.setChildElement(Constants.QUERY_ELEMENT_NAME,
        		Constants.IQ_GET_MEET_REQUESTS_NAMESPACE);

        Long id = message.getId();
        
        if (id == null) {
        	log.error("Meet is null");
            reply.setError(PacketError.Condition.forbidden);
            return reply;
        }
        
        Meeting meeting = null;
        
        try {
			meeting = new Meeting(id);
		} catch (NotFoundException e) {
			log.error(e.getMessage(), e);
            reply.setError(PacketError.Condition.forbidden);
            return reply;
		}
        
        if(!user.equals(meeting.getOwner())){
        	throw new UnauthorizedException("User not match with meeting owner");
        }
        
        MeetingMessage meetingMessage = MeetingMessage.parseMeeting(meeting);
        meetingMessage.setMeetingRequests(meeting.getRequests());
        response.add(meetingMessage.getElement());
		return reply;
	}

}
