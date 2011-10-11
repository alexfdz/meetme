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
import com.meetme.openfire.handler.IQGetMeetsHandler;
import com.meetme.openfire.interceptor.MessageInterceptor;
import com.meetme.openfire.packet.MeetmeRequestMessage;
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
    private IQCreateMeetHandler iqCreateMeetHandler;
    
    /**
     * Implements the TYPE_IQ {@link Constants.IQ_GET_MEETS_NAMESPACE} protocol. Allows users to create
     * a new meeting instance and obtains its ID
     */
    private IQGetMeetsHandler iqGetMeetsHandler;
    
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
        MeetmeRequestMessage.init();
        
        //Init IQ messages handlers
        XMPPServer server = XMPPServer.getInstance();
        // Register the IQs features in disco.
        server.getIQDiscoInfoHandler().addServerFeature(Constants.IQ_MEET_ID_NAMESPACE);
        server.getIQDiscoInfoHandler().addServerFeature(Constants.IQ_GET_MEETS_NAMESPACE);
        // Add IQs handlers.
        iqCreateMeetHandler = new IQCreateMeetHandler();
        iqGetMeetsHandler = new IQGetMeetsHandler();
        server.getIQRouter().addHandler(iqCreateMeetHandler);
        server.getIQRouter().addHandler(iqGetMeetsHandler);
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
        server.getIQDiscoInfoHandler().removeServerFeature(Constants.IQ_GET_MEETS_NAMESPACE);
        
        server.getIQRouter().removeHandler(iqCreateMeetHandler);
        server.getIQRouter().removeHandler(iqGetMeetsHandler);
        
        iqCreateMeetHandler = null;
        iqGetMeetsHandler = null;
	}
	
	public static PluginManager getPluginManager() {
        return pluginManager;
    }
	
}
