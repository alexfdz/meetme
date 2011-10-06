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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Packet;

/**
 * @author alex
 *
 */
public class MeetmePlugin implements Plugin, PacketInterceptor {
	
	private static final Logger log = LoggerFactory.getLogger(MeetmePlugin.class);
	//Hook for intercpetorn
    private InterceptorManager interceptorManager;
	
    private static PluginManager pluginManager;

    public MeetmePlugin() {
        interceptorManager = InterceptorManager.getInstance();
    }

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		log.info("Meetme plugin loaded...");
        interceptorManager.addInterceptor(this);
        pluginManager = manager;
	}

	@Override
	public void destroyPlugin() {
		// unregister with interceptor manager
        interceptorManager.removeInterceptor(this);
        log.info("Meetme plugin destroyed");
	}
	
	public static PluginManager getPluginManager() {
        return pluginManager;
    }
	
	@Override
	public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {
        if (processed) {
            return;
        }
        log.info("packet.xml : " + packet.toXML());
    }
}
