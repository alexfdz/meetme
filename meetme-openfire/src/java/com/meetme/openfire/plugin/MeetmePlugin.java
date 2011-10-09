/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */

package com.meetme.openfire.plugin;

import java.io.File;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meetme.openfire.handler.IQCreateMeetHandler;
import com.meetme.openfire.interceptor.MessageInterceptor;
import com.meetme.openfire.packet.MeetmeMessage;
import com.meetme.openfire.util.Constants;

/**
 * Plugin initializes managers for packing intercept and request managing
 * @author alex
 */
public class MeetmePlugin implements Plugin {
	
	private static final Logger log = LoggerFactory.getLogger(MeetmePlugin.class);
	
    private static PluginManager pluginManager;
    
    /**
     * Packet interceptor that process meeting request messages.
     */
    private MessageInterceptor messageInterceptor;
    
    /**
     * Implements the TYPE_IQ {@link Constants.IQ_MEET_ID_NAMESPACE} protocol. Allows users to create
     * a new meeting instance and obtains its ID
     */
    private IQCreateMeetHandler iqMeetHandler;
    
    public MeetmePlugin() {
    }

	/* (non-Javadoc)
	 * @see org.jivesoftware.openfire.container.Plugin#initializePlugin(org.jivesoftware.openfire.container.PluginManager, java.io.File)
	 */
	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		log.info("Starting Meetme Plugin");
        System.out.println("Starting Meetme Plugin");

        pluginManager = manager;
        
        //Create the message interceptor
        messageInterceptor = new MessageInterceptor();
		InterceptorManager.getInstance().addInterceptor(messageInterceptor);
        
        //JiveGlobals.setProperty("provider.user.className", "com.meetme.openfire.user.UserProvider");
		
        //Create Meetme message extension
        MeetmeMessage.init();
        
        //Init IQ messages handlers
        XMPPServer server = XMPPServer.getInstance();
        // Register the iqMeetHandler features in disco.
        server.getIQDiscoInfoHandler().addServerFeature(Constants.IQ_MEET_ID_NAMESPACE);
        // Add an IQ handler.
        iqMeetHandler = new IQCreateMeetHandler();
        server.getIQRouter().addHandler(iqMeetHandler);
	}

	/* (non-Javadoc)
	 * @see org.jivesoftware.openfire.container.Plugin#destroyPlugin()
	 */
	@Override
	public void destroyPlugin() {
		// unregister with interceptor manager
		InterceptorManager.getInstance().removeInterceptor(messageInterceptor);
        log.info("Meetme plugin destroyed");
        
        // Remove the iqMeetHandler features in disco.
        XMPPServer server = XMPPServer.getInstance();
        server.getIQDiscoInfoHandler().removeServerFeature(Constants.IQ_MEET_ID_NAMESPACE);
        if (iqMeetHandler != null) {
            server.getIQRouter().removeHandler(iqMeetHandler);
            iqMeetHandler = null;
        }
	}
	
	public static PluginManager getPluginManager() {
        return pluginManager;
    }
	
}
