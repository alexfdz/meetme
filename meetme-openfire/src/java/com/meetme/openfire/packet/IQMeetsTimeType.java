/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.packet;


/**
 * Type-safe enumeration to represent the types of the IQ Meets messages.
 * 
 * @author alex
 */
public enum IQMeetsTimeType {
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

	private IQMeetsTimeType(int c) {
		code = c;
	}

	public Integer getCode() {
		return code;
	}
	
	/**
	 * Obtains the {@link IQMeetsTimeType} value of a string
	 * representation
	 * @param code
	 * @return the resolved {@link IQMeetsTimeType} object
	 */
	public static IQMeetsTimeType fromString(String code) {
		Integer codeInt = null;
		try {
			codeInt = Integer.parseInt(code);
		} catch (NumberFormatException e) {
			return unknow;
		}
		return IQMeetsTimeType.fromInt(codeInt);
	}

	/**
	 * Obtains the {@link IQMeetsTimeType} value of an int
	 * representation
	 * @param code
	 * @return the resolved {@link IQMeetsTimeType} object
	 */
	public static IQMeetsTimeType fromInt(Integer code) {
		IQMeetsTimeType result = unknow;
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
