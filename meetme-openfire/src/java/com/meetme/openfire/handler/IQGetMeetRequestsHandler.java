/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.handler;

import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

import com.meetme.openfire.util.Constants;

/**
 * Implements the TYPE_IQ {@link Constants.IQ_GET_MEET_REQUESTS_NAMESPACE} protocol. Allows users to 
 * obtain his meetings form database.
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
            reply.setError(PacketError.Condition.forbidden);
            return reply;
        }
        
        //TODO
		return reply;
	}

}
