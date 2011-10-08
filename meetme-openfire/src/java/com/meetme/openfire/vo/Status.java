/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.vo;

/**
 * Type-safe enumeration to represent the status of the Meet messages.
 * 
 * @author alex
 */
public enum Status {
    /**
     */
	created(1),
    /**
     */
    confirmed(2),
    /**
     */
    deleted(3),
    /**
     */
    unknow(-1);
    
    private Integer code;

	private Status(int c) {
		code = c;
	}

	public Integer getCode() {
		return code;
	}
	
	/**
	 * Obtains the {@link Status} value of a string
	 * representation
	 * @param code
	 * @return the resolved {@link Status} object
	 */
	public static Status fromString(String code) {
		Integer codeInt = null;
		try {
			codeInt = Integer.parseInt(code);
		} catch (NumberFormatException e) {
			return unknow;
		}
		return Status.fromInt(codeInt);
	}

	/**
	 * Obtains the {@link Status} value of an int
	 * representation
	 * @param code
	 * @return the resolved {@link Status} object
	 */
	public static Status fromInt(Integer code) {
		Status result = unknow;
		switch (code) {
		case 1:
			result = created;
			break;
		case 2:
			result = confirmed;
			break;
		case 3:
			result = deleted;
			break;
		}
		return result;
	}
}
