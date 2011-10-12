/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.handler;

import java.util.ArrayList;
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
import com.meetme.openfire.vo.Meeting;
import com.meetme.openfire.vo.MeetingRequest;
import com.meetme.openfire.vo.MeetingRequestStatus;

/**
 * Implements the TYPE_IQ {@link Constants.IQ_GET_MEETS_NAMESPACE} protocol. Allows users to 
 * obtain his meetings form database.
 * The user can ask about specific time restictions @see {@link IQMeetsTimeType}
 *
 * @author alex
 *
 */
public class IQGetMeetsHandler extends AbstractIQHandler {
	
	public IQGetMeetsHandler() {
		super(IQGetMeetsHandler.class.getName(), Constants.IQ_GET_MEETS_NAMESPACE);
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
        Element response = reply.setChildElement(Constants.QUERY_ELEMENT_NAME,
        		Constants.IQ_GET_MEETS_NAMESPACE);
        IQMeetsMessage meetsContainer = new IQMeetsMessage(Constants.MEETS_ELEMENT_NAME, 
        		Constants.IQ_GET_MEETS_NAMESPACE);
        List<MeetingMessage> meets = new ArrayList<MeetingMessage>();
        List<Meeting> resolvedMeetings = null;
        try {
			resolvedMeetings = this.resolveMeetings(user, type);
		} catch (NotFoundException e) {
			log.error(e.getMessage(), e);
			throw new UnauthorizedException(e);
		}
        
        if(resolvedMeetings != null){
        	for (Meeting meeting : resolvedMeetings) {
        		MeetingMessage meetMessage = this.resolveMeetmeMessage(meeting);
        		meets.add(meetMessage);
			}
        }
        
        //Set the resolved meetings in container
        
        meetsContainer.setMeets(meets);
        response.add(meetsContainer.getElement());
		return reply;
	}
	
	private List<Meeting> resolveMeetings(String user, IQMeetsTimeType type) throws NotFoundException{
		List<Meeting> meetings = null;
		if(IQMeetsTimeType.all.equals(type)){
			meetings = Meeting.findAllEnabledByOwner(user);
		}else if(IQMeetsTimeType.current.equals(type)){
			meetings = Meeting.findCurrentEnabledByOwner(user);
		}else if(IQMeetsTimeType.past.equals(type)){
			meetings = Meeting.findPastEnabledByOwner(user);
		}else if(IQMeetsTimeType.custom.equals(type)){
			//TODO A implementar en futuras fases
			throw new NotFoundException("IQMeetsType not found");
		}else{
			throw new NotFoundException("IQMeetsType not found");
		}
		return meetings;
	}
	
	private MeetingMessage resolveMeetmeMessage(Meeting meeting){
		MeetingMessage message = null;
		if(meeting != null){
			message = MeetingMessage.parseMeeting(meeting);
		
			int accepted = 0;
			int denied = 0;
			int unknow = 0;
			
			List<MeetingRequest> requests = meeting.getRequests();
			if(requests != null){
				for (MeetingRequest request : requests) {
					if(MeetingRequestStatus.accepted.equals(request.getStatus())){
						accepted++;
					}else if(MeetingRequestStatus.denied.equals(request.getStatus())){
						denied++;
					}else{
						unknow++;
					}
				}
			}
			message.setAccepted(accepted);
			message.setDenied(denied);
			message.setUnknow(unknow);
		}
		return message;
	}
}
