/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.vo;

/**
 * Type-safe enumeration to represent the status of the Meeting.
 * 
 * @author alex
 */
public enum MeetingStatus {
    /**
     */
	created(1),
    /**
     */
	deleted(2),
    /**
     */
    unknow(-1);
    
    private Integer code;

	private MeetingStatus(int c) {
		code = c;
	}

	public Integer getCode() {
		return code;
	}
	
	/**
	 * Obtains the {@link MeetingStatus} value of a string
	 * representation
	 * @param code
	 * @return the resolved {@link MeetingStatus} object
	 */
	public static MeetingStatus fromString(String code) {
		Integer codeInt = null;
		try {
			codeInt = Integer.parseInt(code);
		} catch (NumberFormatException e) {
			return unknow;
		}
		return MeetingStatus.fromInt(codeInt);
	}

	/**
	 * Obtains the {@link MeetingStatus} value of an int
	 * representation
	 * @param code
	 * @return the resolved {@link MeetingStatus} object
	 */
	public static MeetingStatus fromInt(Integer code) {
		MeetingStatus result = unknow;
		switch (code) {
		case 1:
			result = created;
			break;
		case 2:
			result = deleted;
			break;
		}
		return result;
	}
}
