/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.handler;

import java.util.ArrayList;
import java.util.Iterator;

import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.disco.ServerFeaturesProvider;
import org.jivesoftware.openfire.handler.IQHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implementation of {@link IQHandler} for common implementations.
 *
 * @author alex
 *
 */
public  abstract class AbstractIQHandler extends IQHandler implements ServerFeaturesProvider {

	protected static final Logger log = LoggerFactory.getLogger(AbstractIQHandler.class);
	
	private IQHandlerInfo info;
	
	public AbstractIQHandler(String moduleName, String namespace) {
		super(moduleName);
		info = new IQHandlerInfo(moduleName, namespace);
	}

	@Override
	public void initialize(XMPPServer server) {
        super.initialize(server);
        server.getIQDiscoInfoHandler().addServerFeature(this.getInfo().getNamespace());
    }
	
	public void destroy(XMPPServer server) {
		super.destroy();
		server.getIQDiscoInfoHandler().removeServerFeature(this.getInfo().getNamespace());
        server.getIQRouter().removeHandler(this);
    }
	
	@Override
	public Iterator<String> getFeatures() {
		ArrayList<String> features = new ArrayList<String>();
        features.add(this.getInfo().getNamespace());
        return features.iterator();
	}

	@Override
	public IQHandlerInfo getInfo() {
		return info;
	}

}
