/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.handler;

import java.sql.SQLException;

import org.dom4j.Element;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

import com.meetme.openfire.util.Constants;
import com.meetme.openfire.vo.Meeting;
import com.meetme.openfire.vo.MeetingStatus;

/**
 * Implements the TYPE_IQ {@link Constants.IQ_MEET_ID_NAMESPACE} protocol. Allows users to create
 * a new meeting instance and obtains its ID
 *
 * @author alex
 *
 */
public class IQCreateMeetHandler extends AbstractIQHandler{

	public IQCreateMeetHandler() {
		super(IQCreateMeetHandler.class.getName(), Constants.IQ_MEET_ID_NAMESPACE);
	}

	/* (non-Javadoc)
	 * @see org.jivesoftware.openfire.handler.IQHandler#handleIQ(org.xmpp.packet.IQ)
	 */
	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		IQ reply = IQ.createResultIQ(packet);
		// Check if any of the usernames is null
		String sender = packet.getFrom().getNode();
        if (sender == null) {
            reply.setError(PacketError.Condition.forbidden);
            return reply;
        }
        
		//Creation of the new meeting
		Meeting meeting = new Meeting(((IQ)packet).getChildElement());
		meeting.setOwner(sender);
		meeting.setStatus(MeetingStatus.created);
		try {
			meeting.insert();
		} catch (SQLException e) {
            reply.setError(PacketError.Condition.internal_server_error);
            log.error(e.getMessage(), e);
            return reply;
        }
		
		if (meeting.getId() == null) {
            reply.setError(PacketError.Condition.forbidden);
            log.error("meetId is null after creation");
            return reply;
        }
		Element response = reply.setChildElement(Constants.QUERY_ELEMENT_NAME,
				Constants.IQ_MEET_ID_NAMESPACE);
		response.addAttribute("id", meeting.getId().toString());
		
		return reply;
	}
}
