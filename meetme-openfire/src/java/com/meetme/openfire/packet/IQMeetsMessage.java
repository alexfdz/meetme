/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.packet;

import java.util.List;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.PacketExtension;

/**
 * Represents a message of meetings IQs.
 * 
 * @author alex
 *
 */
public class IQMeetsMessage extends PacketExtension{
	
	private static final Logger log = LoggerFactory.getLogger(IQMeetsMessage.class);
    
    
    public static final String TYPE_ATTRIBUTE = "type";
    
    public static final String ID_ATTRIBUTE = "id";

    public IQMeetsMessage(Element element) {
		super(element);
	}
    
    public IQMeetsMessage(String name, String namespace) {
        super(name, namespace);
    }
    
    /**
     * Sets a list of {@link IQMeetMessage}
     * @param meets
     */
    public void setMeets(List<MeetingMessage> meets){
    	if(meets != null){
    		for (MeetingMessage meet : meets) {
    			this.element.add(meet.getElement());
    		}
    	}
    }
    
    /**
     * Returns the type of this iq message.
     *
     * @return the message type.
     * @see IQMeetsTimeType
     */
    public IQMeetsTimeType getType() {
        String type = element.elementTextTrim(IQMeetsMessage.TYPE_ATTRIBUTE);
        return IQMeetsTimeType.fromString(type);
    }
    
    /**
     * Sets the type of this message.
     *
     * @param type the message type.
     * @see IQMeetsTimeType
     */
    public void setType(IQMeetsTimeType type) {
        if(element.element(IQMeetsMessage.TYPE_ATTRIBUTE) != null){
    		element.remove(element.element(IQMeetsMessage.TYPE_ATTRIBUTE));
    	}
        if(type != null){
        	element.addElement(IQMeetsMessage.TYPE_ATTRIBUTE).setText(type.getCode().toString());
        }
    }
    
    /**
     * Returns the id of this meet message.
     *
     * @return the id.
     */
    public Long getId() {
    	Long id = null;
        String value = element.elementTextTrim(IQMeetsMessage.ID_ATTRIBUTE);
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
    	if(element.element(IQMeetsMessage.ID_ATTRIBUTE) != null){
    		element.remove(element.element(IQMeetsMessage.ID_ATTRIBUTE));
    	}
        if(id != null){
        	element.addElement(IQMeetsMessage.ID_ATTRIBUTE).setText(id);
        }
    }
    
    public IQMeetsMessage createCopy() {
        return new IQMeetsMessage(this.getElement().createCopy());
    }
    
    /**
     * Create a new {@link IQMeetsMessage} instance form an {@link Element} contents
     * @param element
     * @return
     */
    public static IQMeetsMessage fromElement(Element element){
    	return new IQMeetsMessage(element);
    }
    
}
