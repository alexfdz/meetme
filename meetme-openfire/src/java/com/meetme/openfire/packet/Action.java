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
	unknow(-1),

	/**
	 * User try to create a new meeting request
	 */
	create(0),
	
	/**
	 * User accepts the meeting
	 */
	accept(1),

	/**
	 * User denies the meeting
	 */
	deny(2), 
	
	/**
	 * User try to modify the meeting
	 */
	modify(3);

	public Integer code;

	private Action(int c) {
		code = c;
	}

	public Integer getCode() {
		return code;
	}
	
	/**
	 * Obtains the {@link Action} value of a string
	 * representation
	 * @param code
	 * @return the resolved {@link Action} object
	 */
	public static Action fromString(String code) {
		Integer codeInt = null;
		try {
			codeInt = Integer.parseInt(code);
		} catch (NumberFormatException e) {
			return unknow;
		}
		return Action.fromInt(codeInt);
	}

	/**
	 * Obtains the {@link Action} value of an int
	 * representation
	 * @param code
	 * @return the resolved {@link Action} object
	 */
	public static Action fromInt(Integer code) {
		Action result = unknow;
		switch (code) {
		case 0:
			result = create;
			break;
		case 1:
			result = accept;
			break;
		case 2:
			result = deny;
			break;
		case 3:
			result = modify;
			break;
		}
		return result;
	}
}
