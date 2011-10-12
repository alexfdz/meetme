/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */

package com.meetme.openfire.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meetme.openfire.handler.AbstractIQHandler;
import com.meetme.openfire.handler.IQCreateMeetHandler;
import com.meetme.openfire.handler.IQGetMeetRequestsHandler;
import com.meetme.openfire.handler.IQGetMeetsHandler;
import com.meetme.openfire.interceptor.MessageInterceptor;
import com.meetme.openfire.packet.MeetmeRequestMessage;

/**
 * Plugin initializes managers for packing intercept and request managing
 * @author alex
 */
public class MeetmePlugin implements Plugin {
	
	private static final Logger log = LoggerFactory.getLogger(MeetmePlugin.class);
	
    /**
     * Packet interceptor that process meeting request messages.
     */
    private MessageInterceptor messageInterceptor;
    
    /**
     * List of {@link IQHandler} implementations
     */
    private List<AbstractIQHandler> iqHandlers;
    
    public MeetmePlugin() {
    	iqHandlers = new ArrayList<AbstractIQHandler>();
    }

	/* (non-Javadoc)
	 * @see org.jivesoftware.openfire.container.Plugin#initializePlugin(org.jivesoftware.openfire.container.PluginManager, java.io.File)
	 */
	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		log.info("Starting Meetme Plugin");
        System.out.println("Starting Meetme Plugin");

        //Create the message interceptor
        messageInterceptor = new MessageInterceptor();
		InterceptorManager.getInstance().addInterceptor(messageInterceptor);
        
        //Create Meetme message extension
        MeetmeRequestMessage.initialize();
        
        //Init IQ messages handlers
        XMPPServer server = XMPPServer.getInstance();
        
        iqHandlers.add(new IQCreateMeetHandler());
        iqHandlers.add(new IQGetMeetsHandler());
        iqHandlers.add(new IQGetMeetRequestsHandler());
        
        for (AbstractIQHandler iqHandler : iqHandlers) {
        	server.getIQRouter().addHandler(iqHandler);
		}
	}

	/* (non-Javadoc)
	 * @see org.jivesoftware.openfire.container.Plugin#destroyPlugin()
	 */
	@Override
	public void destroyPlugin() {
		// unregister with interceptor manager
		InterceptorManager.getInstance().removeInterceptor(messageInterceptor);
        log.info("Meetme plugin destroyed");
        
        //Remove MeetmeRequestMessage extension
        MeetmeRequestMessage.destroy();
        
        // Remove the iqMeetHandler features in disco.
        XMPPServer server = XMPPServer.getInstance();
        for (AbstractIQHandler iqHandler : iqHandlers) {
        	iqHandler.destroy(server);
		}
        iqHandlers = null;
	}
}
