/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.packet;

import java.util.List;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.PacketExtension;

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
public class IQMeetsMessage extends PacketExtension{
	
	private static final Logger log = LoggerFactory.getLogger(IQMeetsMessage.class);
    
    
    public static final String TYPE_ATTRIBUTE = "type";

    public IQMeetsMessage(Element element) {
		super(element);
	}
    
    public IQMeetsMessage(IQMeetsType type) {
        super(Constants.MEETS_ELEMENT_NAME, Constants.IQ_GET_MEETS_NAMESPACE);
        this.setType(type);
    }
    
    public IQMeetsMessage() {
        super(Constants.MEETS_ELEMENT_NAME, Constants.IQ_GET_MEETS_NAMESPACE);
    }
    
    /**
     * Sets a list of {@link IQMeetMessage}
     * @param meets
     */
    public void setMeets(List<IQMeetMessage> meets){
    	if(meets != null){
    		for (IQMeetMessage meet : meets) {
    			this.element.add(meet.getElement());
    		}
    	}
    }
    
    /**
     * Returns the type of this iq message.
     *
     * @return the message type.
     * @see IQMeetsType
     */
    public IQMeetsType getType() {
        String type = element.elementTextTrim(IQMeetsMessage.TYPE_ATTRIBUTE);
        return IQMeetsType.fromString(type);
    }
    
    /**
     * Sets the type of this message.
     *
     * @param type the message type.
     * @see IQMeetsType
     */
    public void setType(IQMeetsType type) {
        if(element.element(IQMeetsMessage.TYPE_ATTRIBUTE) != null){
    		element.remove(element.element(IQMeetsMessage.TYPE_ATTRIBUTE));
    	}
        if(type != null){
        	element.addElement(IQMeetsMessage.TYPE_ATTRIBUTE).setText(type.getCode().toString());
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
