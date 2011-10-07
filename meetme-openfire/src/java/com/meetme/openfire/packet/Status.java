/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.packet;


/**
 * Type-safe enumeration to represent the status of the Meet messages.
 * 
 * @author alex
 */
public enum Status {
    /**
     */
	created,
	
    confirmed,

    /**
     */
    deleted,

    /**
     */
    unknow;
    
    public static Status fromString(String name) {
        try {
            return Status.valueOf(name);
        }
        catch (Exception e) {
            return unknow;
        }
    }
}
