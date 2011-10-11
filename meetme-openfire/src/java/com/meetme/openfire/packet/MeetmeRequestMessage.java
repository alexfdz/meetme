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

import com.meetme.openfire.util.Constants;
import com.meetme.openfire.vo.MeetingRequestStatus;

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
public class MeetmeRequestMessage extends PacketExtension{
	
	private static final Logger log = LoggerFactory.getLogger(MeetmeRequestMessage.class);
	
    private static final SimpleDateFormat UTC_FORMAT = new SimpleDateFormat(
            XMPPConstants.XMPP_DELAY_DATETIME_FORMAT);
    private static final FastDateFormat FAST_UTC_FORMAT =
            FastDateFormat.getInstance(XMPPConstants.XMPP_DELAY_DATETIME_FORMAT,
            TimeZone.getTimeZone("UTC"));
    
    
    public static final String ACTION_ATTRIBUTE = "action";
    public static final String ID_ATTRIBUTE = "id";
    public static final String MEET_ID_ATTRIBUTE = "meetId";
    public static final String DESCRIPTON_ELEMENT = "description";
    public static final String POSITION_ELEMENT = "position";
    public static final String TIME_ELEMENT = "time";
    public static final String STATUS_ELEMENT = "status";
    
	public static void init(){
		UTC_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        // Register that MeetPacket uses the jabber:x:data namespace
        registeredExtensions.put(QName.get(Constants.MEET_ELEMENT_NAME, 
        		Constants.MEET_NAMESPACE), MeetmeRequestMessage.class);
	}
	
	public MeetmeRequestMessage(Element element) {
		super(element);
	}

	public MeetmeRequestMessage(Action action) {
		super(Constants.MEET_ELEMENT_NAME, Constants.MEET_NAMESPACE);
		this.setAction(action);
	}
	
	public MeetmeRequestMessage() {
		super(Constants.MEET_ELEMENT_NAME, Constants.MEET_NAMESPACE);
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

    /**
     * Returns the action of this meet message.
     *
     * @return the message action.
     * @see MeetmeRequestMessage.Action
     */
    public Action getAction() {
        String action = element.elementTextTrim(MeetmeRequestMessage.ACTION_ATTRIBUTE);
        return Action.fromString(action);
    }
    
    /**
     * Sets the action of this message.
     *
     * @param action the message type.
     * @see Action
     */
    public void setAction(Action action) {
        if(element.element(MeetmeRequestMessage.ACTION_ATTRIBUTE) != null){
    		element.remove(element.element(MeetmeRequestMessage.ACTION_ATTRIBUTE));
    	}
        if(action != null){
        	element.addElement(MeetmeRequestMessage.ACTION_ATTRIBUTE).setText(action.getCode().toString());
        }
    }
    
    /**
     * Returns the id of this meet message.
     *
     * @return the id.
     */
    public Long getId() {
    	Long id = null;
        String value = element.elementTextTrim(MeetmeRequestMessage.ID_ATTRIBUTE);
        if(value != null){
        	id = Long.parseLong(value);
        }
        return id;
    }
    
    /**
     * Sets the id of this message.
     *
     * @param the message id.
     */
    public void setId(String id) {
    	if(element.element(MeetmeRequestMessage.ID_ATTRIBUTE) != null){
    		element.remove(element.element(MeetmeRequestMessage.ID_ATTRIBUTE));
    	}
        if(id != null){
        	element.addElement(MeetmeRequestMessage.ID_ATTRIBUTE).setText(id);
        }
    }
    
    /**
     * Returns the meeting id of this meet message.
     *
     * @return the meeting id.
     */
    public Long getMeetId() {
    	Long id = null;
        String value = element.elementTextTrim(MeetmeRequestMessage.MEET_ID_ATTRIBUTE);
        if(value != null){
        	id = Long.parseLong(value);
        }
        return id;
    }
    
    /**
     * Sets the meeting id of this message.
     *
     * @param the meeting id.
     */
    public void setMeetId(String meetId) {
    	if(element.element(MeetmeRequestMessage.MEET_ID_ATTRIBUTE) != null){
    		element.remove(element.element(MeetmeRequestMessage.MEET_ID_ATTRIBUTE));
    	}
        if(meetId != null){
        	element.addElement(MeetmeRequestMessage.MEET_ID_ATTRIBUTE).setText(meetId);
        }
    }
    
    /**
     * Sets the description of the meet.
     *
     * @param description of the message.
     */
    public void setDescription(String description) {
        // Remove an existing description element.
        if (element.element(MeetmeRequestMessage.DESCRIPTON_ELEMENT) != null) {
            element.remove(element.element(MeetmeRequestMessage.DESCRIPTON_ELEMENT));
        }
        if(description != null){
        	element.addElement(MeetmeRequestMessage.DESCRIPTON_ELEMENT).setText(description);
        }
    }
    
    /**
     * Returns the description  of the meet.
     *
     * @return description of the message.
     */
    public String getDescription() {
        return element.elementTextTrim(MeetmeRequestMessage.DESCRIPTON_ELEMENT);
    }
    
    /**
     * Sets the position of the meet.
     *
     * @param position of the message.
     */
    public void setPosition(String position) {
        // Remove an existing description element.
        if (element.element(MeetmeRequestMessage.POSITION_ELEMENT) != null) {
            element.remove(element.element(MeetmeRequestMessage.POSITION_ELEMENT));
        }
        if(position != null){
        	element.addElement(MeetmeRequestMessage.POSITION_ELEMENT).setText(position);
        }
    }
    
    /**
     * Returns the position of the meet.
     *
     * @return position of the message.
     */
    public String getPosition() {
        return element.elementTextTrim(MeetmeRequestMessage.POSITION_ELEMENT);
    }
    
    /**
     * Returns the status of the meet.
     *
     * @return position of the message.
     */
    public MeetingRequestStatus getStatus() {
        String status = element.elementTextTrim(MeetmeRequestMessage.STATUS_ELEMENT);
        return MeetingRequestStatus.fromString(status);
    }
    
    /**
     * Sets the status of the meet.
     *
     * @param status of the message.
     */
    public void setStatus(MeetingRequestStatus status) {
        // Remove an existing description element.
        if (element.element(MeetmeRequestMessage.STATUS_ELEMENT) != null) {
            element.remove(element.element(MeetmeRequestMessage.STATUS_ELEMENT));
        }
        if(status != null){
        	element.addElement(MeetmeRequestMessage.STATUS_ELEMENT).setText(status.getCode().toString());
        }
    }
    
    /**
     * Returns the time of the meet.
     *
     * @return time of the meet.
     */
    public Date getTime() {
        String time = element.elementTextTrim(MeetmeRequestMessage.TIME_ELEMENT);
        Date result = null;
        if (time != null) {
        	try {
				result = MeetmeRequestMessage.parseDate(time);
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
        if (element.element(MeetmeRequestMessage.TIME_ELEMENT) != null) {
            element.remove(element.element(MeetmeRequestMessage.TIME_ELEMENT));
        }
        if(time != null){
       	 try {
    			value = MeetmeRequestMessage.formatDate(time);
    		} catch (ParseException e) {
    			log.error("Error formatting meetme message date", e);
    		}
            if(value != null){
            	element.addElement(MeetmeRequestMessage.TIME_ELEMENT).setText(value);
            }
       }
    }
    
    public MeetmeRequestMessage createCopy() {
        return new MeetmeRequestMessage(this.getElement().createCopy());
    }
    
    /**
     * Create a new {@link MeetmeRequestMessage} instance form an {@link Element} contents
     * @param element
     * @return
     */
    public static MeetmeRequestMessage fromElement(Element element){
    	return new MeetmeRequestMessage(element);
    }
    
}
