/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.packet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.dom4j.Element;
import org.dom4j.QName;
import org.jivesoftware.util.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.PacketExtension;
import org.xmpp.util.XMPPConstants;

/**
 * Represents a message that could be use for create/modify a meeting.
 * <p/>
 * The message could be of the following types:
 * <ul>
 * <li>create -> Indicates a form to fill out.</li>
 * </ul>
 * <p/>
 * @author alex
 *
 */
public class MeetmeMessage extends PacketExtension{
	
	private static final Logger log = LoggerFactory.getLogger(MeetmeMessage.class);
	
    private static final SimpleDateFormat UTC_FORMAT = new SimpleDateFormat(
            XMPPConstants.XMPP_DELAY_DATETIME_FORMAT);
    private static final FastDateFormat FAST_UTC_FORMAT =
            FastDateFormat.getInstance(XMPPConstants.XMPP_DELAY_DATETIME_FORMAT,
            TimeZone.getTimeZone("UTC"));
    
	/**
     * Element name of the packet extension.
     */
    public static final String ELEMENT_NAME = "meet";

    /**
     * Namespace of the packet extension.
     */
    public static final String NAMESPACE = "meetme:meet";
    
    public static final String ACTION_ATTRIBUTE = "action";
    public static final String ID_ATTRIBUTE = "id";
    public static final String DESCRIPTON_ELEMENT = "description";
    public static final String POSITION_ELEMENT = "position";
    public static final String TIME_ELEMENT = "time";
    public static final String STATUS_ELEMENT = "status";
    
	public static void init(){
		UTC_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        // Register that MeetPacket uses the jabber:x:data namespace
        registeredExtensions.put(QName.get(ELEMENT_NAME, NAMESPACE), MeetmeMessage.class);
	}
	
	 /**
     * Returns the String representation of an Object to be used as a field value.
     *
     * @param object the object to encode.
     * @return the String representation of an Object to be used as a field value.
     */
    static String encode(Object object) {
        if (object instanceof String) {
            return object.toString();
        }
        else if (object instanceof Boolean) {
            return Boolean.TRUE.equals(object) ? "1" : "0";
        }
        else if (object instanceof Date) {
            return FAST_UTC_FORMAT.format((Date) object);
        }
        return object.toString();
    }
    
    /**
     * Returns the Date obtained by parsing the specified date representation. The date
     * representation is expected to be in the UTC GMT+0 format.
     *
     * @param date date representation in the UTC GMT+0 format.
     * @return the Date obtained by parsing the specified date representation.
     * @throws ParseException if an error occurs while parsing the date representation.
     */
    public static Date parseDate(String date) throws ParseException {
        synchronized (UTC_FORMAT) {
            return UTC_FORMAT.parse(date);
        }
    }
    
    /**
     * Returns the string obtained by formating the specified date object. The date
     * representation is expected to be in the UTC GMT+0 format.
     *
     * @param date Date object.
     * @return the Date representation in the UTC GMT+0 format.
     * @throws ParseException if an error occurs while formatting the Date object.
     */
    public static String formatDate(Date date) throws ParseException {
        synchronized (UTC_FORMAT) {
            return UTC_FORMAT.format(date);
        }
    }

    public MeetmeMessage(Element element) {
		super(element);
	}
    
    public MeetmeMessage(Action action) {
        super(ELEMENT_NAME, NAMESPACE);
        // Set the type of the data form
        this.setAction(action);
    }
    
    /**
     * Returns the action of this meet message.
     *
     * @return the message action.
     * @see MeetmeMessage.Action
     */
    public Action getAction() {
        String action = element.elementTextTrim(MeetmeMessage.ACTION_ATTRIBUTE);
        return Action.fromString(action);
    }
    
    /**
     * Sets the action of this message.
     *
     * @param action the message type.
     * @see Action
     */
    public void setAction(Action action) {
        if(element.element(MeetmeMessage.ACTION_ATTRIBUTE) != null){
    		element.remove(element.element(MeetmeMessage.ACTION_ATTRIBUTE));
    	}
        element.addElement(MeetmeMessage.ACTION_ATTRIBUTE, action==null?null:action.toString());
    }
    
    /**
     * Returns the id of this meet message.
     *
     * @return the id.
     */
    public String getId() {
        return element.elementTextTrim(MeetmeMessage.ID_ATTRIBUTE);
    }
    
    /**
     * Sets the id of this message.
     *
     * @param the message id.
     */
    public void setId(String id) {
    	if(element.element(MeetmeMessage.ID_ATTRIBUTE) != null){
    		element.remove(element.element(MeetmeMessage.ID_ATTRIBUTE));
    	}
        element.addElement(MeetmeMessage.ID_ATTRIBUTE, id);
      //message.setID(node.getNodeID() + "__" + recipientJID.toBareJID() + "__" + StringUtils.randomString(5));
    }
    
    /**
     * Sets the description of the meet.
     *
     * @param description of the message.
     */
    public void setDescription(String description) {
        // Remove an existing description element.
        if (element.element(MeetmeMessage.DESCRIPTON_ELEMENT) != null) {
            element.remove(element.element(MeetmeMessage.DESCRIPTON_ELEMENT));
        }
        element.addElement(MeetmeMessage.DESCRIPTON_ELEMENT).setText(description);
    }
    
    /**
     * Returns the description  of the meet.
     *
     * @return description of the message.
     */
    public String getDescription() {
        return element.elementTextTrim(MeetmeMessage.DESCRIPTON_ELEMENT);
    }
    
    /**
     * Sets the position of the meet.
     *
     * @param position of the message.
     */
    public void setPosition(String position) {
        // Remove an existing description element.
        if (element.element(MeetmeMessage.POSITION_ELEMENT) != null) {
            element.remove(element.element(MeetmeMessage.POSITION_ELEMENT));
        }
        element.addElement(MeetmeMessage.POSITION_ELEMENT).setText(position);
    }
    
    /**
     * Returns the position of the meet.
     *
     * @return position of the message.
     */
    public String getPosition() {
        return element.elementTextTrim(MeetmeMessage.POSITION_ELEMENT);
    }
    
    /**
     * Returns the status of the meet.
     *
     * @return position of the message.
     */
    public Status getStatus() {
        String status = element.elementTextTrim(MeetmeMessage.STATUS_ELEMENT);
        return Status.fromString(status);
    }
    
    /**
     * Sets the status of the meet.
     *
     * @param status of the message.
     */
    public void setStatus(Status status) {
        // Remove an existing description element.
        if (element.element(MeetmeMessage.STATUS_ELEMENT) != null) {
            element.remove(element.element(MeetmeMessage.STATUS_ELEMENT));
        }
        element.addElement(MeetmeMessage.STATUS_ELEMENT).setText(status==null?null:status.toString());
    }
    
    /**
     * Returns the time of the meet.
     *
     * @return time of the meet.
     */
    public Date getTime() {
        String time = element.elementTextTrim(MeetmeMessage.TIME_ELEMENT);
        Date result = null;
        if (time != null) {
        	try {
				result = MeetmeMessage.parseDate(time);
			} catch (ParseException e) {
				log.error("Error parsing meetme message date", e);
			}
        }
        return result;
    }
    
    /**
     * Sets the time of the meet.
     *
     * @param time of the meet.
     */
    public void setTime(Date time) {
        // Remove an existing description element.
    	String value = null;
        if (element.element(MeetmeMessage.TIME_ELEMENT) != null) {
            element.remove(element.element(MeetmeMessage.TIME_ELEMENT));
        }
        try {
			value = MeetmeMessage.formatDate(time);
		} catch (ParseException e) {
			log.error("Error formatting meetme message date", e);
		}
        element.addElement(MeetmeMessage.TIME_ELEMENT).setText(value);
    }
    
    
    public MeetmeMessage createCopy() {
        return new MeetmeMessage(this.getElement().createCopy());
    }
    
}
