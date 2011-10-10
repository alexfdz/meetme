/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.interceptor;

import java.sql.SQLException;

import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketExtension;

import com.meetme.openfire.packet.Action;
import com.meetme.openfire.packet.MeetmeMessage;
import com.meetme.openfire.util.Constants;
import com.meetme.openfire.vo.MeetingRequest;
import com.meetme.openfire.vo.Status;

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
	        if (extension instanceof MeetmeMessage) {
	        	MeetmeMessage message = (MeetmeMessage) extension;
	        	
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
	private void interceptCreateMessage(Packet packet, MeetmeMessage message) throws PacketRejectedException {
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
		request.setStatus(Status.created);
		
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
	private void interceptRequestMessage(MeetmeMessage message) throws PacketRejectedException {
		//TODO Para que sirve????
	}
	
	/**
	 * @param message
	 */
	private void interceptDenyMessage(MeetmeMessage message) throws PacketRejectedException {
		//TODO
//		Modificar el estado de la request en la bd (que hacemos con el estado del meeting)
	}
	
	/**
	 * @param message
	 */
	private void interceptAcceptMessage(MeetmeMessage message) throws PacketRejectedException {
		//TODO
//		Modificar el estado de la request en la bd (que hacemos con el estado del meeting)
	}
	
	/**
	 * @param message
	 */
	private void interceptModifyMessage(MeetmeMessage message) throws PacketRejectedException {
		//TODO
	}

}
