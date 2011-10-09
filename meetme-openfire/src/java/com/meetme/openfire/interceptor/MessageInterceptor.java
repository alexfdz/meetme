/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.interceptor;

import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketExtension;

import com.meetme.openfire.packet.MeetmeMessage;
import com.meetme.openfire.util.Constants;

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
		if (processed || !incoming) {
            return;
        }
		if(packet instanceof Message){
			PacketExtension extension = packet.getExtension(Constants.MEET_ELEMENT_NAME, Constants.MEET_NAMESPACE);
	        if (extension instanceof MeetmeMessage) {
	        	//TODO: Hacer un manager que procese el mensaje, actualize la bbdd y podifique si hace falta el mensaje
	        	MeetmeMessage meetPacket = (MeetmeMessage) extension;
	        	log.info("Meet Message.... ");
	        	log.info("from : " + packet.getFrom().getNode());
	        	log.info("to : " + packet.getTo().getNode());
	        	log.info("type : " + ((Message)packet).getType());
	        	log.info("id : " + meetPacket.getId());
	        	log.info("action : " + meetPacket.getAction());
	        	log.info("status : " + meetPacket.getStatus());
	        	log.info("description : " + meetPacket.getDescription());
	        	log.info("position : " + meetPacket.getPosition());
	        	log.info("time : " + meetPacket.getTime());
	        	log.info(".....................");
	        }
		}
    }

}
