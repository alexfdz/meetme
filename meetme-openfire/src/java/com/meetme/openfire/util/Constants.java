/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.util;

import com.meetme.openfire.handler.IQCreateMeetHandler;

public class Constants {

	public static final int MEET_JID = 75;
    public static final int MEETQUERY_JID = 76;
    
    /**
     * Namespace for IQ Create Meet message @see {@link IQCreateMeetHandler}
     */
    public static final String IQ_MEET_ID_NAMESPACE = "jabber:iq:meetId";
    
    /**
     * Namespace for IQ Get Meets message @see {@link IQGetMeetsHandler}
     */
    public static final String IQ_GET_MEETS_NAMESPACE = "jabber:iq:getMeets";
    
    /**
     * Namespace for IQ Get Meets message @see {@link IQGetMeetsHandler}
     */
    public static final String IQ_GET_MEET_REQUESTS_NAMESPACE = "jabber:iq:getMeetRequests";
    
    /**
     * Element name of the meet message packet extension.
     */
    public static final String MEET_ELEMENT_NAME = "meet";
    
    /**
     * Element name of the meet message packet extension.
     */
    public static final String MEETS_ELEMENT_NAME = "meets";
    
    /**
     * Element name of the query packet extension.
     */
    public static final String QUERY_ELEMENT_NAME = "query";

    /**
     * Namespace of the meet message packet extension.
     */
    public static final String MEET_NAMESPACE = "meetme:meet";
}
