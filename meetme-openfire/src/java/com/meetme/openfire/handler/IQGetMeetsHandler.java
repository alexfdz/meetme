/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.disco.ServerFeaturesProvider;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.util.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

import com.meetme.openfire.packet.IQMeetMessage;
import com.meetme.openfire.packet.IQMeetsMessage;
import com.meetme.openfire.packet.IQMeetsType;
import com.meetme.openfire.util.Constants;
import com.meetme.openfire.vo.Meeting;
import com.meetme.openfire.vo.MeetingRequest;
import com.meetme.openfire.vo.MeetingRequestStatus;

/**
 * Implements the TYPE_IQ {@link Constants.IQ_GET_MEETS_NAMESPACE} protocol. Allows users to 
 * obtain his meetings form database.
 *
 * @author alex
 *
 */
public class IQGetMeetsHandler extends IQHandler implements ServerFeaturesProvider {

	private static final Logger log = LoggerFactory.getLogger(IQGetMeetsHandler.class);
	
	/**
     * Element name of the packet extension.
     */
    public static final String MEETS_ELEMENT_NAME = "meets";
	
	private IQHandlerInfo info;
	
	public IQGetMeetsHandler() {
		super(IQGetMeetsHandler.class.getName());
		info = new IQHandlerInfo("getMeets", Constants.IQ_GET_MEETS_NAMESPACE);
	}

	@Override
	public void initialize(XMPPServer server) {
        super.initialize(server);
    }
	
	@Override
	public Iterator<String> getFeatures() {
		ArrayList<String> features = new ArrayList<String>();
        features.add(Constants.IQ_GET_MEETS_NAMESPACE);
        return features.iterator();
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
        IQMeetsType type = message.getType();
        Element response = reply.setChildElement(Constants.QUERY_ELEMENT_NAME,
        		Constants.IQ_GET_MEETS_NAMESPACE);
        IQMeetsMessage meetsContainer = new IQMeetsMessage();
        List<IQMeetMessage> meets = new ArrayList<IQMeetMessage>();
        List<Meeting> resolvedMeetings = null;
        try {
			resolvedMeetings = this.resolveMeetings(user, type);
		} catch (NotFoundException e) {
			log.error(e.getMessage(), e);
			throw new UnauthorizedException(e);
		}
        
        if(resolvedMeetings != null){
        	for (Meeting meeting : resolvedMeetings) {
        		IQMeetMessage meetMessage = this.resolveMeetmeMessage(meeting);
        		meets.add(meetMessage);
			}
        }
        
        meetsContainer.setMeets(meets);
        response.add(meetsContainer.getElement());
		return reply;
	}
	
	private List<Meeting> resolveMeetings(String user, IQMeetsType type) throws NotFoundException{
		List<Meeting> meetings = null;
		if(IQMeetsType.all.equals(type)){
			meetings = Meeting.findByUser(user);
		}else if(IQMeetsType.current.equals(type)){
			//TODO coger todos los meetings con time > actual || time == null y estado=activos
			meetings = Meeting.findByUser(user);
		}else if(IQMeetsType.past.equals(type)){
			//TODO coger todos los meetings con time < actual  y estado=activos
			meetings = Meeting.findByUser(user);
		}else if(IQMeetsType.custom.equals(type)){
			//TODO A implementar en futuras fases
			throw new NotFoundException("IQMeetsType not found");
		}else{
			throw new NotFoundException("IQMeetsType not found");
		}
		return meetings;
	}
	
	private IQMeetMessage resolveMeetmeMessage(Meeting meeting){
		IQMeetMessage message = null;
		if(meeting != null){
			message = new IQMeetMessage();
			//Include the basic meeting contents
			message.setDescription(meeting.getDescription());
			message.setId(meeting.getId().toString());
			message.setPosition(meeting.getPosition());
			message.setTime(meeting.getTime());
			//resolve the requests type counts
			
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

	@Override
	public IQHandlerInfo getInfo() {
		return info;
	}

}
