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
public class IQMeetMessage extends PacketExtension{
	
	private static final Logger log = LoggerFactory.getLogger(IQMeetMessage.class);
	
    private static final SimpleDateFormat UTC_FORMAT = new SimpleDateFormat(
            XMPPConstants.XMPP_DELAY_DATETIME_FORMAT);
    private static final FastDateFormat FAST_UTC_FORMAT =
            FastDateFormat.getInstance(XMPPConstants.XMPP_DELAY_DATETIME_FORMAT,
            TimeZone.getTimeZone("UTC"));
    
    
    public static final String ID_ATTRIBUTE = "id";
    public static final String DESCRIPTON_ELEMENT = "description";
    public static final String POSITION_ELEMENT = "position";
    public static final String TIME_ELEMENT = "time";
    public static final String ACCEPTED_ELEMENT = "accepted";
    public static final String DENIED_ELEMENT = "denied";
    public static final String UNKNOW_ELEMENT = "unknow";
    
	public static void init(){
		UTC_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        // Register that MeetPacket uses the jabber:x:data namespace
        registeredExtensions.put(QName.get(Constants.MEET_ELEMENT_NAME, 
        		Constants.MEET_NAMESPACE), IQMeetMessage.class);
	}
	
	public IQMeetMessage(Element element) {
		super(element);
	}

	public IQMeetMessage() {
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
     * Returns the id of this meet message.
     *
     * @return the id.
     */
    public Long getId() {
    	Long id = null;
        String value = element.elementTextTrim(IQMeetMessage.ID_ATTRIBUTE);
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
    	if(element.element(IQMeetMessage.ID_ATTRIBUTE) != null){
    		element.remove(element.element(IQMeetMessage.ID_ATTRIBUTE));
    	}
        if(id != null){
        	element.addElement(IQMeetMessage.ID_ATTRIBUTE).setText(id);
        }
    }
    
    /**
     * Sets the description of the meet.
     *
     * @param description of the message.
     */
    public void setDescription(String description) {
        // Remove an existing description element.
        if (element.element(IQMeetMessage.DESCRIPTON_ELEMENT) != null) {
            element.remove(element.element(IQMeetMessage.DESCRIPTON_ELEMENT));
        }
        if(description != null){
        	element.addElement(IQMeetMessage.DESCRIPTON_ELEMENT).setText(description);
        }
    }
    
    /**
     * Returns the description  of the meet.
     *
     * @return description of the message.
     */
    public String getDescription() {
        return element.elementTextTrim(IQMeetMessage.DESCRIPTON_ELEMENT);
    }
    
    /**
     * Sets the position of the meet.
     *
     * @param position of the message.
     */
    public void setPosition(String position) {
        // Remove an existing description element.
        if (element.element(IQMeetMessage.POSITION_ELEMENT) != null) {
            element.remove(element.element(IQMeetMessage.POSITION_ELEMENT));
        }
        if(position != null){
        	element.addElement(IQMeetMessage.POSITION_ELEMENT).setText(position);
        }
    }
    
    /**
     * Returns the position of the meet.
     *
     * @return position of the message.
     */
    public String getPosition() {
        return element.elementTextTrim(IQMeetMessage.POSITION_ELEMENT);
    }
    
    /**
     * Returns the time of the meet.
     *
     * @return time of the meet.
     */
    public Date getTime() {
        String time = element.elementTextTrim(IQMeetMessage.TIME_ELEMENT);
        Date result = null;
        if (time != null) {
        	try {
				result = IQMeetMessage.parseDate(time);
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
        if (element.element(IQMeetMessage.TIME_ELEMENT) != null) {
            element.remove(element.element(IQMeetMessage.TIME_ELEMENT));
        }
        if(time != null){
        	 try {
     			value = IQMeetMessage.formatDate(time);
     		} catch (ParseException e) {
     			log.error("Error formatting meetme message date", e);
     		}
             if(value != null){
             	element.addElement(IQMeetMessage.TIME_ELEMENT).setText(value);
             }
        }
    }
    
    /**
     * Returns the accepted requests of this meet message.
     *
     * @return the accepted requests.
     */
    public Integer getAccepted() {
    	Integer count = null;
        String value = element.elementTextTrim(IQMeetMessage.ACCEPTED_ELEMENT);
        if(value != null){
        	count = Integer.parseInt(value);
        }
        return count;
    }
    
    /**
     * Sets the accepted requests of this message.
     *
     * @param the message accepted requests.
     */
    public void setAccepted(Integer count) {
    	if(element.element(IQMeetMessage.ACCEPTED_ELEMENT) != null){
    		element.remove(element.element(IQMeetMessage.ACCEPTED_ELEMENT));
    	}
        if(count != null){
        	element.addElement(IQMeetMessage.ACCEPTED_ELEMENT).setText(count.toString());
        }
    }
    
    /**
     * Returns the denied requests of this meet message.
     *
     * @return the denied requests.
     */
    public Integer getDenied() {
    	Integer count = null;
        String value = element.elementTextTrim(IQMeetMessage.DENIED_ELEMENT);
        if(value != null){
        	count = Integer.parseInt(value);
        }
        return count;
    }
    
    /**
     * Sets the denied requests of this message.
     *
     * @param the message denied requests.
     */
    public void setDenied(Integer count) {
    	if(element.element(IQMeetMessage.DENIED_ELEMENT) != null){
    		element.remove(element.element(IQMeetMessage.DENIED_ELEMENT));
    	}
        if(count != null){
        	element.addElement(IQMeetMessage.DENIED_ELEMENT).setText(count.toString());
        }
    }
    
    /**
     * Returns the unknow requests of this meet message.
     *
     * @return the unknow requests.
     */
    public Integer getUnknow() {
    	Integer count = null;
        String value = element.elementTextTrim(IQMeetMessage.UNKNOW_ELEMENT);
        if(value != null){
        	count = Integer.parseInt(value);
        }
        return count;
    }
    
    /**
     * Sets the unknow requests of this message.
     *
     * @param the message unknow requests.
     */
    public void setUnknow(Integer count) {
    	if(element.element(IQMeetMessage.UNKNOW_ELEMENT) != null){
    		element.remove(element.element(IQMeetMessage.UNKNOW_ELEMENT));
    	}
        if(count != null){
        	element.addElement(IQMeetMessage.UNKNOW_ELEMENT).setText(count.toString());
        }
    }
    
    
    public IQMeetMessage createCopy() {
        return new IQMeetMessage(this.getElement().createCopy());
    }
    
    /**
     * Create a new {@link IQMeetMessage} instance form an {@link Element} contents
     * @param element
     * @return
     */
    public static IQMeetMessage fromElement(Element element){
    	return new IQMeetMessage(element);
    }
    
}
