/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.interceptor;

import java.sql.SQLException;

import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.util.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketExtension;

import com.meetme.openfire.packet.Action;
import com.meetme.openfire.packet.MeetmeRequestMessage;
import com.meetme.openfire.util.Constants;
import com.meetme.openfire.vo.MeetingRequest;
import com.meetme.openfire.vo.MeetingRequestStatus;

/**
 * Packet interceptor that process meeting request messages. 
 *
 * @author alex
 */
public class MessageInterceptor implements PacketInterceptor {

	private static final Logger log = LoggerFactory.getLogger(MessageInterceptor.class);
	
	@Override
	public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed)
			throws PacketRejectedException {
		//Intercept incoming and not processed packets
		if (processed || !incoming) {
            return;
        }
		if(packet instanceof Message){
			PacketExtension extension = packet.getExtension(Constants.MEET_ELEMENT_NAME, Constants.MEET_NAMESPACE);
	        if (extension instanceof MeetmeRequestMessage) {
	        	MeetmeRequestMessage message = (MeetmeRequestMessage) extension;
	        	
	        	Action action = message.getAction();
	        	
	        	if(action == null || Action.unknow.equals(action)){
	        		log.error("Unknow action for packet");
	        		log.error("id : " + packet.getID());
	        		log.error("from : " + packet.getFrom().getNode());
		        	log.error("to : " + packet.getTo().getNode());
		        	log.error("meetId : " + message.getMeetId());
		        	
	        		throw new PacketRejectedException("Unknow action");
	        	}
	        	
	        	if(Action.create.equals(action)){
	        		this.interceptCreateMessage(packet, message);
	        	}else if(Action.request.equals(action)){
	        		this.interceptRequestMessage(message);
	        	}else if(Action.deny.equals(action)){
	        		this.interceptDenyMessage(message);
	        	}else if(Action.accept.equals(action)){
	        		this.interceptAcceptMessage(message);
	        	}else if(Action.modify.equals(action)){
	        		this.interceptModifyMessage(message);
	        	}
	        }
		}
    }
	
	/**
	 * The server creates de new meeting request instance and modifies the message for:
	 * 	Create the new meetingRequest instance in database
	 * 	Include the new meeting request Id
	 * 	Modify the action to REQUEST
	 * @param packet
	 * @param message
	 * @throws PacketRejectedException
	 */
	private void interceptCreateMessage(Packet packet, MeetmeRequestMessage message) throws PacketRejectedException {
		if(packet.getTo() == null || packet.getTo().getNode() == null){
			throw new PacketRejectedException("To is null");
		}
		String user = packet.getTo().getNode();
		Long meetId = message.getMeetId();
		if(meetId == null){
			throw new PacketRejectedException("MeetId is null");
		}
		//Initialized the MeetingRequest instance
		MeetingRequest request = new MeetingRequest();
		request.setMeetingId(meetId);
		request.setUser(user);
		request.setStatus(MeetingRequestStatus.created);
		
		//Save it in db to get the meeting request Id
		Long id = null;
		try {
			id = request.insert();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new PacketRejectedException(e);
		}
		
		if(id != null){
			//Inserts the id of the new meetingRequest instance, and modifies the action referenced
			message.setId(id.toString());
			message.setAction(Action.request);
		}
		
	}
	
	/**
	 * @param message
	 */
	private void interceptRequestMessage(MeetmeRequestMessage message) throws PacketRejectedException {
		//TODO Para que sirve????
	}
	
	/**
	 * Intercepts a deny confirmation of a {@link MeetingRequest}.
	 * Updates the content in database instance and forwards the message to the user
	 * @param message
	 */
	private void interceptDenyMessage(MeetmeRequestMessage message) throws PacketRejectedException {
		if(message.getId() == null){
			throw new PacketRejectedException("Request id is null");
		}
		
		Long requestId = message.getId();
		MeetingRequest request = null;
		
		try {
			request = new MeetingRequest(requestId);
		} catch (NotFoundException e) {
			log.error("Meeting request not found", e);
			throw new PacketRejectedException(e);
		} catch (SQLException e){
			log.error(e.getMessage(), e);
			throw new PacketRejectedException(e);
		}
		
		request.setStatus(MeetingRequestStatus.denied);
		message.setStatus(MeetingRequestStatus.denied);
		
		try {
			request.update();
		} catch (SQLException e){
			log.error(e.getMessage(), e);
			throw new PacketRejectedException(e);
		}
	}
	
	/**
	 * Intercepts an accept confirmation of a {@link MeetingRequest}.
	 * Updates the content in database instance and forwards the message to the user
	 * @param message
	 */
	private void interceptAcceptMessage(MeetmeRequestMessage message) throws PacketRejectedException {
		if(message.getId() == null){
			throw new PacketRejectedException("Request id is null");
		}
		
		Long requestId = message.getId();
		MeetingRequest request = null;
		
		try {
			request = new MeetingRequest(requestId);
		} catch (NotFoundException e) {
			log.error("Meeting request not found", e);
			throw new PacketRejectedException(e);
		} catch (SQLException e){
			log.error(e.getMessage(), e);
			throw new PacketRejectedException(e);
		}
		
		request.setStatus(MeetingRequestStatus.accepted);
		message.setStatus(MeetingRequestStatus.accepted);
		
		try {
			request.update();
		} catch (SQLException e){
			log.error(e.getMessage(), e);
			throw new PacketRejectedException(e);
		}
	}
	
	/**
	 * @param message
	 */
	private void interceptModifyMessage(MeetmeRequestMessage message) throws PacketRejectedException {
		//TODO
	}

}
