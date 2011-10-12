/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.vo;

/**
 * Type-safe enumeration to represent the status of the Meet request messages.
 * 
 * @author alex
 */
public enum MeetingRequestStatus {
    /**
     */
	created(1),
    /**
     */
	accepted(2),
    /**
     */
    denied(3),
    /**
     */
    unknow(-1);
    
    private Integer code;

	private MeetingRequestStatus(int c) {
		code = c;
	}

	public Integer getCode() {
		return code;
	}
	
	/**
	 * Obtains the {@link MeetingRequestStatus} value of a string
	 * representation
	 * @param code
	 * @return the resolved {@link MeetingRequestStatus} object
	 */
	public static MeetingRequestStatus fromString(String code) {
		Integer codeInt = null;
		try {
			codeInt = Integer.parseInt(code);
		} catch (NumberFormatException e) {
			return unknow;
		}
		return MeetingRequestStatus.fromInt(codeInt);
	}

	/**
	 * Obtains the {@link MeetingRequestStatus} value of an int
	 * representation
	 * @param code
	 * @return the resolved {@link MeetingRequestStatus} object
	 */
	public static MeetingRequestStatus fromInt(Integer code) {
		MeetingRequestStatus result = unknow;
		switch (code) {
		case 1:
			result = created;
			break;
		case 2:
			result = accepted;
			break;
		case 3:
			result = denied;
			break;
		}
		return result;
	}
}
