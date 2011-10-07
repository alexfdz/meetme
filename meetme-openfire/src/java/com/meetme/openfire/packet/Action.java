/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.packet;

/**
 * Type-safe enumeration to represent the actions of the Meet messages.
 * 
 * @author alex
 */
public enum Action {
    /**
     * Unknow action
     */
	unknow,
	
    /**
     * User try to create a new meeting
     */
    create,
    
    /**
     * Meeting request for an user
     */
    request,

    /**
     * User try to modify the meeting
     */
    modify,

    /**
     * User accepts the meeting
     */
    accept,
    
    /**
     * User denies the meeting
     */
    deny,
    
    /**
     * User respons without confirmation
     */
    noResponse;
    
    public static Action fromString(String name) {
        try {
            return Action.valueOf(name);
        }
        catch (Exception e) {
            return unknow;
        }
    }
}
