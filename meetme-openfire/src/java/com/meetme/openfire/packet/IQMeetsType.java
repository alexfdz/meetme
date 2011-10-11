/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.packet;


/**
 * Type-safe enumeration to represent the types of the IQ Meets messages.
 * 
 * @author alex
 */
public enum IQMeetsType {
	/**
	 * Unknow type
	 */
	unknow(-1),
	/**
	 * Past meetings
	 */
	past(0),

	/**
	 * Current meetings
	 */
	current(1),

	/**
	 * All meetings of the user
	 */
	all(2),

	/**
	 * Custom filter of queries
	 */
	custom(3);

	public Integer code;

	private IQMeetsType(int c) {
		code = c;
	}

	public Integer getCode() {
		return code;
	}
	
	/**
	 * Obtains the {@link IQMeetsType} value of a string
	 * representation
	 * @param code
	 * @return the resolved {@link IQMeetsType} object
	 */
	public static IQMeetsType fromString(String code) {
		Integer codeInt = null;
		try {
			codeInt = Integer.parseInt(code);
		} catch (NumberFormatException e) {
			return unknow;
		}
		return IQMeetsType.fromInt(codeInt);
	}

	/**
	 * Obtains the {@link IQMeetsType} value of an int
	 * representation
	 * @param code
	 * @return the resolved {@link IQMeetsType} object
	 */
	public static IQMeetsType fromInt(Integer code) {
		IQMeetsType result = unknow;
		switch (code) {
		case 0:
			result = past;
			break;
		case 1:
			result = current;
			break;
		case 2:
			result = all;
			break;
		case 3:
			result = custom;
			break;
		}
		return result;
	}
}
