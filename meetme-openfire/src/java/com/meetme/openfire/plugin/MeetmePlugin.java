/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */

package com.meetme.openfire.plugin;

import java.io.File;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketExtension;

import com.meetme.openfire.packet.MeetmeMessage;

/**
 * @author alex
 *
 */
public class MeetmePlugin implements Plugin, PacketInterceptor {
	
	private static final Logger log = LoggerFactory.getLogger(MeetmePlugin.class);
	
    private static PluginManager pluginManager;
    
    public MeetmePlugin() {
    }

	/* (non-Javadoc)
	 * @see org.jivesoftware.openfire.container.Plugin#initializePlugin(org.jivesoftware.openfire.container.PluginManager, java.io.File)
	 */
	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		log.info("Meetme plugin loaded...");
		InterceptorManager.getInstance().addInterceptor(this);
        pluginManager = manager;
        
        JiveGlobals.setProperty("provider.user.className", "com.meetme.openfire.user.UserProvider");
        MeetmeMessage.init();
	}

	/* (non-Javadoc)
	 * @see org.jivesoftware.openfire.container.Plugin#destroyPlugin()
	 */
	@Override
	public void destroyPlugin() {
		// unregister with interceptor manager
		InterceptorManager.getInstance().removeInterceptor(this);
        log.info("Meetme plugin destroyed");
	}
	
	public static PluginManager getPluginManager() {
        return pluginManager;
    }
	
	/* (non-Javadoc)
	 * @see org.jivesoftware.openfire.interceptor.PacketInterceptor#interceptPacket(org.xmpp.packet.Packet, org.jivesoftware.openfire.session.Session, boolean, boolean)
	 */
	@Override
	public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {
		if (processed || !incoming) {
            return;
        }
		PacketExtension extension = packet.getExtension(MeetmeMessage.ELEMENT_NAME, MeetmeMessage.NAMESPACE);
        if (extension instanceof MeetmeMessage) {
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
