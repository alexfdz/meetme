/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.vo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.database.JiveID;
import org.jivesoftware.database.SequenceManager;
import org.jivesoftware.util.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meetme.openfire.handler.IQCreateMeetHandler;
import com.meetme.openfire.packet.MeetingMessage;
import com.meetme.openfire.util.Constants;

/**
 * Representation of a meeting
 * 
 * @author alex
 *
 */
@JiveID(Constants.MEET_JID)
public class Meeting {
	
	//TODO: Añadir columna para fecha de creacion
	
	private static final Logger log = LoggerFactory.getLogger(Meeting.class);
	
	private static final String INSERT =
            "INSERT INTO ofMeeting(id, owner, description, position, " +
                    "start_time, status) VALUES (?,?,?,?,?,?)";
	private static final String UPDATE =
            "UPDATE ofMeeting SET owner=?, description=?, position=?, start_time=?, " +
                    "status=? WHERE id=?";
    private static final String LOAD =
            "SELECT owner, description, position, start_time, status" +
                    " FROM ofMeeting WHERE id=?";
    private static final String FIND_BY_OWNER =
            "SELECT id, owner, description, position, start_time, status" +
                    " FROM ofMeeting WHERE owner=? and status=?";
    private static final String FIND_CURRENT_BY_OWNER =
            "SELECT id, owner, description, position, start_time, status" +
                    " FROM ofMeeting WHERE owner=? and status=? and (start_time >= NOW() or start_time IS NULL)";
    private static final String FIND_PAST_BY_OWNER =
            "SELECT id, owner, description, position, start_time, status" +
                    " FROM ofMeeting WHERE owner=? and status=? and start_time IS NOT NULL and start_time < NOW() ";
    private static final String DELETE =
            "DELETE from ofMeeting WHERE id=?";
    private static final String LOAD_REQUESTS =
            "SELECT id, requested_user, status, updated" +
                    " FROM ofMeetingRequest WHERE meet_id=?";
    
	/**
	 * Unique id of the meeting
	 */
	private Long id;
	/**
	 * Username owner of the meeting
	 */
	private String owner;
	
	/**
	 * Description field of the meeting
	 */
	private String description;
	/**
	 * Geoposition of the meeting in WGS84 format
	 */
	private String position;
	
	/**
	 * The start time of the event in UTC FORMAT
	 */
	private Timestamp time;
	
	/**
	 * The status of the meeting.
	 */
	private MeetingStatus status;
	
	/**
	 * The meeting requests between the owner and requested users.
	 */
	private List<MeetingRequest> requests;
	
	/**
	 * 
	 */
	public Meeting() {
		
	}
	
	/**
	 * Initializes de {@link Meeting} element with the contents of an {@link Element}
	 * @see IQCreateMeetHandler
	 */
	public Meeting(Element element) {
		this(MeetingMessage.fromElement(element));
	}
	
	/**
	 * Initializes de {@link Meeting} element with the contents of an {@link MeetingMessage}
	 */
	public Meeting(MeetingMessage message){
		this.id = message.getId();
		this.description = message.getDescription();
		this.position = message.getPosition();
		if(message.getTime() != null){
			this.time = new Timestamp(message.getTime().getTime());
		}
	}
	
	/**
     * Loads an existing meeting based on its ID.
     *
     * @param id the meeting ID.
     * @throws NotFoundException if the meeting does not exist or could not be loaded.
     */
    public Meeting(Long id) throws NotFoundException {
        this.id = id;
        load();
    }
    
    /**
     * Inserts a new meeting into the database.
     * @return the Id of the new instance
     * @throws SQLException
     */
    public Long insert() throws SQLException {
        this.id = SequenceManager.nextID(this);
        Connection con = null;
        boolean abortTransaction = false;
        try {
            con = DbConnectionManager.getTransactionConnection();
            PreparedStatement pstmt = con.prepareStatement(INSERT);
            pstmt.setLong(1, id);
            pstmt.setString(2, owner);
            pstmt.setString(3, description);
            pstmt.setString(4, position);
            pstmt.setTimestamp(5, time);
            pstmt.setInt(6, status.getCode());
            
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch (SQLException sqle) {
            abortTransaction = true;
            throw sqle;
        }
        finally {
            DbConnectionManager.closeTransactionConnection(con, abortTransaction);
        }
        log.debug("Meeting created id:" + this.id);
        return this.id;
    }
    
    /**
     * Loads a meeting from the database.
     *
     * @throws NotFoundException if the meeting could not be loaded.
     */
    public void load() throws NotFoundException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOAD);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if (!rs.next()) {
                throw new NotFoundException("Meeting not found: " + id);
            }
            this.owner = rs.getString(1);
            this.description = rs.getString(2);
            this.position = rs.getString(3);
            this.time = rs.getTimestamp(4);
            this.status = MeetingStatus.fromInt(rs.getInt(5));
            rs.close();
            pstmt.close();
        }
        catch (SQLException sqle) {
            log.error(sqle.getMessage(), sqle);
        }
        finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
    }
    
    /**
     * Loads meeting requests from the database.
     *
     * @throws NotFoundException if the meeting could not be loaded.
     */
    public void loadRequests() throws NotFoundException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
        	if(this.id == null){
        		return;
        	}
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOAD_REQUESTS);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            
            this.requests = new ArrayList<MeetingRequest>();
            MeetingRequest request = null;
            while(rs.next()){
            	request = new MeetingRequest();
            	request.setId(rs.getLong(1));
            	request.setUser(rs.getString(2));
                request.setStatus(MeetingRequestStatus.fromInt(rs.getInt(3)));
                request.setUpdated(rs.getTimestamp(4));
                request.setMeeting(this);
                request.setMeetingId(this.id);
                this.requests.add(request);
            }
            
            rs.close();
            pstmt.close();
        }
        catch (SQLException sqle) {
            log.error(sqle.getMessage(), sqle);
        }
        finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
    }
    
    /**
     * Saves a meeting to the database.
     */
    public void update() {
        Connection con = null;
        boolean abortTransaction = false;
        try {
            con = DbConnectionManager.getTransactionConnection();
            PreparedStatement pstmt = con.prepareStatement(UPDATE);
            pstmt.setString(1, owner);
            pstmt.setString(2, description);
            pstmt.setString(3, position);
            pstmt.setTimestamp(4, time);
            pstmt.setInt(5, status.getCode());
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch (SQLException sqle) {
            abortTransaction = true;
            log.error(sqle.getMessage(), sqle);
        }
        finally {
            DbConnectionManager.closeTransactionConnection(con, abortTransaction);
        }
    }
    
    /**
     * Deletes a meeting in the database
     * @throws SQLException
     */
    public void delete() throws SQLException {
        Connection con = null;
        boolean abortTransaction = false;
        try {
            con = DbConnectionManager.getTransactionConnection();
            PreparedStatement pstmt = con.prepareStatement(DELETE);
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch (SQLException sqle) {
            abortTransaction = true;
            throw sqle;
        }
        finally {
            DbConnectionManager.closeTransactionConnection(con, abortTransaction);
        }
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public MeetingStatus getStatus() {
		return status;
	}

	public void setStatus(MeetingStatus status) {
		this.status = status;
	}
	
	public List<MeetingRequest> getRequests() {
		if(requests == null){
			try {
				this.loadRequests();
			} catch (NotFoundException e) {
				log.error(e.getMessage(), e);
			}
		}
		return requests;
	}

	public void setRequests(List<MeetingRequest> requests) {
		this.requests = requests;
	}
	
	/**
     * Loads all user meetings from database not deleted for a specific owner.
     *
     * @throws NotFoundException if the meeting could not be loaded.
     */
    public static List<Meeting> findAllEnabledByOwner(String owner) throws NotFoundException {
        return Meeting.findByOwner(owner, FIND_BY_OWNER, MeetingStatus.created);
    }
    
    /**
     * Loads current user meetings from database not deleted for a specific owner.
     *
     * @throws NotFoundException if the meeting could not be loaded.
     */
    public static List<Meeting> findCurrentEnabledByOwner(String owner) throws NotFoundException {
        return Meeting.findByOwner(owner, FIND_CURRENT_BY_OWNER, MeetingStatus.created);
    }
    
    /**
     * Loads past user meetings from database not deleted for a specific owner.
     *
     * @throws NotFoundException if the meeting could not be loaded.
     */
    public static List<Meeting> findPastEnabledByOwner(String owner) throws NotFoundException {
        return Meeting.findByOwner(owner, FIND_PAST_BY_OWNER, MeetingStatus.created);
    }
    
    private static List<Meeting> findByOwner(String username, String statement, MeetingStatus status) 
    		throws NotFoundException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Meeting> meetings = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(statement);
            pstmt.setString(1, username);
            pstmt.setInt(2, status.getCode());
            rs = pstmt.executeQuery();
            meetings = parseResultSet(rs);
            rs.close();
            pstmt.close();
        }
        catch (SQLException sqle) {
            log.error(sqle.getMessage(), sqle);
        }
        finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
        return meetings;
    }
    
    /**
     * Parses a {@link ResultSet} containing the result of a {@link PreparedStatement} 
     * with order parameters: id, owner, description, position, start_time, status
     * @param rs
     * @return
     * @throws SQLException
     */
    public static List<Meeting> parseResultSet(ResultSet rs) throws SQLException{
    	List<Meeting> meetings = null;
    	if(rs != null){
    		meetings = new ArrayList<Meeting>();
            Meeting meeting = null;
            while(rs.next()){
            	meeting = new Meeting();
            	meeting.setId(rs.getLong(1));
            	meeting.setOwner(rs.getString(2));
            	meeting.setDescription(rs.getString(3));
            	meeting.setPosition(rs.getString(4));
            	meeting.setTime(rs.getTimestamp(5));
            	meeting.setStatus(MeetingStatus.fromInt(rs.getInt(6)));
                meetings.add(meeting);
            }
    	}
    	return meetings;
    }
    
	

}
