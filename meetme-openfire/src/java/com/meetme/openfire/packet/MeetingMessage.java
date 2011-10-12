/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.packet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.dom4j.Element;
import org.dom4j.QName;
import org.jivesoftware.util.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.PacketExtension;
import org.xmpp.util.XMPPConstants;

import com.meetme.openfire.util.Constants;
import com.meetme.openfire.vo.Meeting;
import com.meetme.openfire.vo.MeetingRequest;
import com.meetme.openfire.vo.MeetingRequestStatus;
import com.meetme.openfire.vo.MeetingStatus;

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
public class MeetingMessage extends PacketExtension{
	
	private static final Logger log = LoggerFactory.getLogger(MeetingMessage.class);
	
    private static final SimpleDateFormat UTC_FORMAT = new SimpleDateFormat(
            XMPPConstants.XMPP_DATETIME_FORMAT);
    private static final FastDateFormat FAST_UTC_FORMAT =
            FastDateFormat.getInstance(XMPPConstants.XMPP_DELAY_DATETIME_FORMAT,
            TimeZone.getTimeZone("UTC"));
    
    
    public static final String ACTION_ATTRIBUTE = "action";
    public static final String ID_ATTRIBUTE = "id";
    public static final String MEET_ID_ATTRIBUTE = "meetId";
    public static final String DESCRIPTON_ELEMENT = "description";
    public static final String POSITION_ELEMENT = "position";
    public static final String TIME_ELEMENT = "time";
    public static final String MEET_STATUS_ELEMENT = "meetStatus";
    public static final String STATUS_ELEMENT = "status";
    public static final String ACCEPTED_ELEMENT = "accepted";
    public static final String DENIED_ELEMENT = "denied";
    public static final String UNKNOW_ELEMENT = "unknow";
    public static final String REQUESTS_ELEMENT = "requests";
    public static final String REQUEST_ELEMENT = "request";
    public static final String USER_ATTRIBUTE = "user";
    
	public static void initialize(){
		UTC_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        // Register that MeetPacket uses the jabber:x:data namespace
        registeredExtensions.put(QName.get(Constants.MEET_ELEMENT_NAME, 
        		Constants.MEET_NAMESPACE), MeetingMessage.class);
	}
	
	public static void destroy(){
        registeredExtensions.remove(QName.get(Constants.MEET_ELEMENT_NAME, 
        		Constants.MEET_NAMESPACE));
	}
	
	public MeetingMessage(Element element) {
		super(element);
	}

	public MeetingMessage(Action action) {
		super(Constants.MEET_ELEMENT_NAME, Constants.MEET_NAMESPACE);
		this.setAction(action);
	}
	
	public MeetingMessage() {
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
     * @see MeetingMessage.Action
     */
    public Action getAction() {
        String action = element.elementTextTrim(MeetingMessage.ACTION_ATTRIBUTE);
        return Action.fromString(action);
    }
    
    /**
     * Sets the action of this message.
     *
     * @param action the message type.
     * @see Action
     */
    public void setAction(Action action) {
        if(element.element(MeetingMessage.ACTION_ATTRIBUTE) != null){
    		element.remove(element.element(MeetingMessage.ACTION_ATTRIBUTE));
    	}
        if(action != null){
        	element.addElement(MeetingMessage.ACTION_ATTRIBUTE).setText(action.getCode().toString());
        }
    }
    
    /**
     * Returns the id of this meet message.
     *
     * @return the id.
     */
    public Long getId() {
    	Long id = null;
        String value = element.elementTextTrim(MeetingMessage.ID_ATTRIBUTE);
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
    	if(element.element(MeetingMessage.ID_ATTRIBUTE) != null){
    		element.remove(element.element(MeetingMessage.ID_ATTRIBUTE));
    	}
        if(id != null){
        	element.addElement(MeetingMessage.ID_ATTRIBUTE).setText(id);
        }
    }
    
    /**
     * Returns the meeting id of this meet message.
     *
     * @return the meeting id.
     */
    public Long getMeetId() {
    	Long id = null;
        String value = element.elementTextTrim(MeetingMessage.MEET_ID_ATTRIBUTE);
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
    	if(element.element(MeetingMessage.MEET_ID_ATTRIBUTE) != null){
    		element.remove(element.element(MeetingMessage.MEET_ID_ATTRIBUTE));
    	}
        if(meetId != null){
        	element.addElement(MeetingMessage.MEET_ID_ATTRIBUTE).setText(meetId);
        }
    }
    
    /**
     * Sets the description of the meet.
     *
     * @param description of the message.
     */
    public void setDescription(String description) {
        // Remove an existing description element.
        if (element.element(MeetingMessage.DESCRIPTON_ELEMENT) != null) {
            element.remove(element.element(MeetingMessage.DESCRIPTON_ELEMENT));
        }
        if(description != null){
        	element.addElement(MeetingMessage.DESCRIPTON_ELEMENT).setText(description);
        }
    }
    
    /**
     * Returns the description  of the meet.
     *
     * @return description of the message.
     */
    public String getDescription() {
        return element.elementTextTrim(MeetingMessage.DESCRIPTON_ELEMENT);
    }
    
    /**
     * Sets the position of the meet.
     *
     * @param position of the message.
     */
    public void setPosition(String position) {
        // Remove an existing description element.
        if (element.element(MeetingMessage.POSITION_ELEMENT) != null) {
            element.remove(element.element(MeetingMessage.POSITION_ELEMENT));
        }
        if(position != null){
        	element.addElement(MeetingMessage.POSITION_ELEMENT).setText(position);
        }
    }
    
    /**
     * Returns the position of the meet.
     *
     * @return position of the message.
     */
    public String getPosition() {
        return element.elementTextTrim(MeetingMessage.POSITION_ELEMENT);
    }
    
    /**
     * Returns the status of the meet request.
     *
     * @return the status of the meet request.
     */
    public MeetingRequestStatus getStatus() {
        String status = element.elementTextTrim(MeetingMessage.STATUS_ELEMENT);
        return MeetingRequestStatus.fromString(status);
    }
    
    /**
     * Sets the status of the meet request.
     *
     * @param status of the meet request.
     */
    public void setStatus(MeetingRequestStatus status) {
        // Remove an existing description element.
        if (element.element(MeetingMessage.STATUS_ELEMENT) != null) {
            element.remove(element.element(MeetingMessage.STATUS_ELEMENT));
        }
        if(status != null){
        	element.addElement(MeetingMessage.STATUS_ELEMENT).setText(status.getCode().toString());
        }
    }
    
    /**
     * Returns the status of the meet.
     *
     * @return status of the meet.
     */
    public MeetingStatus getMeetStatus() {
        String status = element.elementTextTrim(MeetingMessage.MEET_STATUS_ELEMENT);
        return MeetingStatus.fromString(status);
    }
    
    /**
     * Sets the status of the meet.
     *
     * @param status of the message.
     */
    public void setMeetStatus(MeetingStatus status) {
        // Remove an existing description element.
        if (element.element(MeetingMessage.MEET_STATUS_ELEMENT) != null) {
            element.remove(element.element(MeetingMessage.MEET_STATUS_ELEMENT));
        }
        if(status != null){
        	element.addElement(MeetingMessage.MEET_STATUS_ELEMENT).setText(status.getCode().toString());
        }
    }
    
    /**
     * Returns the time of the meet.
     *
     * @return time of the meet.
     */
    public Date getTime() {
        String time = element.elementTextTrim(MeetingMessage.TIME_ELEMENT);
        Date result = null;
        if (time != null) {
        	try {
				result = MeetingMessage.parseDate(time);
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
        if (element.element(MeetingMessage.TIME_ELEMENT) != null) {
            element.remove(element.element(MeetingMessage.TIME_ELEMENT));
        }
        if(time != null){
       	 try {
    			value = MeetingMessage.formatDate(time);
    		} catch (ParseException e) {
    			log.error("Error formatting meetme message date", e);
    		}
            if(value != null){
            	element.addElement(MeetingMessage.TIME_ELEMENT).setText(value);
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
        String value = element.elementTextTrim(MeetingMessage.ACCEPTED_ELEMENT);
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
    	if(element.element(MeetingMessage.ACCEPTED_ELEMENT) != null){
    		element.remove(element.element(MeetingMessage.ACCEPTED_ELEMENT));
    	}
        if(count != null){
        	element.addElement(MeetingMessage.ACCEPTED_ELEMENT).setText(count.toString());
        }
    }
    
    /**
     * Returns the denied requests of this meet message.
     *
     * @return the denied requests.
     */
    public Integer getDenied() {
    	Integer count = null;
        String value = element.elementTextTrim(MeetingMessage.DENIED_ELEMENT);
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
    	if(element.element(MeetingMessage.DENIED_ELEMENT) != null){
    		element.remove(element.element(MeetingMessage.DENIED_ELEMENT));
    	}
        if(count != null){
        	element.addElement(MeetingMessage.DENIED_ELEMENT).setText(count.toString());
        }
    }
    
    /**
     * Adds the meeting requests list to the entity {@link Element}
     * @param requests
     */
    public void setMeetingRequests(List<MeetingRequest> requests){
    	if(requests != null && !requests.isEmpty()){
    		if(element.element(MeetingMessage.REQUESTS_ELEMENT) != null){
        		element.remove(element.element(MeetingMessage.REQUESTS_ELEMENT));
        	}
            Element reqContainerElement = element.addElement(MeetingMessage.REQUESTS_ELEMENT);
            Element reqElement = null;
    		for (MeetingRequest meetingRequest : requests) {
    			reqElement = reqContainerElement.addElement(MeetingMessage.REQUEST_ELEMENT);
    			reqElement.addElement(MeetingMessage.ID_ATTRIBUTE).setText(meetingRequest.getId().toString());
    			reqElement.addElement(MeetingMessage.USER_ATTRIBUTE).setText(meetingRequest.getUser());
    			reqElement.addElement(MeetingMessage.STATUS_ELEMENT).setText(meetingRequest.getStatus().getCode().toString());
			}
    	}
    }
    
    /**
     * Returns the unknow requests of this meet message.
     *
     * @return the unknow requests.
     */
    public Integer getUnknow() {
    	Integer count = null;
        String value = element.elementTextTrim(MeetingMessage.UNKNOW_ELEMENT);
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
    	if(element.element(MeetingMessage.UNKNOW_ELEMENT) != null){
    		element.remove(element.element(MeetingMessage.UNKNOW_ELEMENT));
    	}
        if(count != null){
        	element.addElement(MeetingMessage.UNKNOW_ELEMENT).setText(count.toString());
        }
    }
    
    public MeetingMessage createCopy() {
        return new MeetingMessage(this.getElement().createCopy());
    }
    
    /* (non-Javadoc)
     * @see org.xmpp.packet.PacketExtension#getElement()
     */
    public Element getElement(boolean includeRequests) {
    	if(includeRequests){
    		
    	}
    	return element;
    }
    
    /**
     * Create a new {@link MeetingMessage} instance form an {@link Element} contents
     * @param element
     * @return
     */
    public static MeetingMessage fromElement(Element element){
    	return new MeetingMessage(element);
    }
    
    /**
     * Create a new {@link MeetingMessage} instance form an {@link Meeting} contents
     * @param meeting
     * @return
     */
    public static MeetingMessage parseMeeting(Meeting meeting){
    	MeetingMessage message = null;
    	if(meeting != null){
    		message = new MeetingMessage();
    		message.setDescription(meeting.getDescription());
			message.setMeetId(meeting.getId().toString());
			message.setPosition(meeting.getPosition());
			message.setTime(meeting.getTime());
			message.setMeetStatus(meeting.getStatus());
    	}
    	return message;
    }
    
}
