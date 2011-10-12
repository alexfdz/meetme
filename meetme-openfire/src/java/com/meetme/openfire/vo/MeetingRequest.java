/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.vo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.database.JiveID;
import org.jivesoftware.database.SequenceManager;
import org.jivesoftware.util.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meetme.openfire.packet.MeetingMessage;
import com.meetme.openfire.util.Constants;

/**
 * Meeting request of a {@link Meeting}.
 * 
 * Persistence of request status between the owner and the requested user.
 * 
 * @author alex
 *
 */
@JiveID(Constants.MEETQUERY_JID)
public class MeetingRequest {
	
	//TODO: Añadir columna para fecha de actualizacion
	
	private static final Logger log = LoggerFactory.getLogger(Meeting.class);
	
	private static final String INSERT =
            "INSERT INTO ofMeetingRequest(id, meet_id, requested_user, status)" +
                    " VALUES (?,?,?,?)";
	private static final String UPDATE =
            "UPDATE ofMeetingRequest SET meet_id=?, requested_user=?," +
            " status=? WHERE id=?";
	private static final String LOAD =
            "SELECT meet_id, requested_user, status" +
                    " FROM ofMeetingRequest WHERE id=?";
	private static final String EXIST =
            "SELECT id FROM ofMeetingRequest WHERE meet_id=? and requested_user=?";
    private static final String DELETE =
            "DELETE from ofMeetingRequest WHERE id=?";
    private static final String FIND_BY_USER =
            "SELECT meet.id, meet.owner, meet.description, meet.position, meet.start_time, meet.status, req.id, req.status " +
            "from ofMeeting as meet, ofMeetingRequest as req WHERE req.meet_id = meet.id AND " +
            "req.requested_user = ? AND meet.status=?";
    private static final String FIND_CURRENT_BY_USER =
            "SELECT meet.id, meet.owner, meet.description, meet.position, meet.start_time, meet.status, req.id, req.status " +
            "from ofMeeting as meet, ofMeetingRequest as req WHERE req.meet_id = meet.id AND " +
            "req.requested_user = ? AND meet.status=? and (meet.start_time >= NOW() or meet.start_time IS NULL)";
    private static final String FIND_PAST_BY_USER =
            "SELECT meet.id, meet.owner, meet.description, meet.position, meet.start_time, meet.status, req.id, req.status " +
            "from ofMeeting as meet, ofMeetingRequest as req WHERE req.meet_id = meet.id AND " +
            "req.requested_user = ? AND meet.status=? and meet.start_time IS NOT NULL and meet.start_time < NOW() ";
    
	/**
	 * Unique id of the request
	 */
	private Long id;
	/**
	 * User requested
	 */
	private String user;
	
	/**
	 * The meeting
	 */
	private Meeting meeting;
	
	/**
	 * The meeting
	 */
	private Long meetingId;
	
	/**
	 * Status of the request
	 */
	private MeetingRequestStatus status;
	
	/**
	 * 
	 */
	public MeetingRequest() {
		
	}
	
	/**
     * Loads an existing meeting request based on its ID.
     *
     * @param id the meeting request ID.
     * @throws NotFoundException if the meeting request does not exist or could not be loaded.
     * not exist or could not be loaded.
     */
    public MeetingRequest(long id) throws NotFoundException, SQLException {
        this.id = id;
        load();
    }
    
    /**
     * Loads an existing meeting request based on its ID.
     * 
     * @param id the meeting request ID.
     * @param meeting
     * @throws NotFoundException if the meeting request does not exist or
     *  could not be loaded.
     */
    public MeetingRequest(long id, Meeting meeting) throws NotFoundException, SQLException {
        this.id = id;
        this.meeting = meeting;
        load();
    }
    
    /**
     * Inserts a new meeting request into the database.
     * @return the Id of the new instance
     */
    public Long insert() throws SQLException {
        this.id = SequenceManager.nextID(this);
        Connection con = null;
        boolean abortTransaction = false;
        try {
            con = DbConnectionManager.getTransactionConnection();
            PreparedStatement pstmt = con.prepareStatement(INSERT);
            pstmt.setLong(1, id);
            pstmt.setLong(2, meetingId);
            pstmt.setString(3, user);
            pstmt.setInt(4, status.getCode());
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
        log.debug("MeetingRequest created id:" + this.id);
        return this.id;
    }
    
    /**
     * Loads a meeting request from the database.
     *
     * @throws NotFoundException if the meeting request could not be loaded.
     */
    public void load() throws NotFoundException, SQLException {
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
            this.meetingId = rs.getLong(1);
            this.user = rs.getString(2);
            this.status = MeetingRequestStatus.fromInt(rs.getInt(3));
            
            rs.close();
            pstmt.close();
        }
        catch (SQLException sqle) {
            log.error(sqle.getMessage(), sqle);
            throw sqle;
        }
        finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
    }
    
    /**
     * Checks if exist another {@link MeetingRequest} for same user about the same
     * {@link Meeting}
     */
    public boolean exist() throws SQLException{
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean result = false;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(EXIST);
            pstmt.setLong(1, this.meetingId);
            pstmt.setString(2, this.user);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = true;
            }
            rs.close();
            pstmt.close();
        }
        catch (SQLException sqle) {
            log.error(sqle.getMessage(), sqle);
            throw sqle;
        }
        finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
        return result;
    }
    
    /**
     * Saves a meeting to the database.
     */
    public void update() throws SQLException{
        Connection con = null;
        boolean abortTransaction = false;
        try {
            con = DbConnectionManager.getTransactionConnection();
            PreparedStatement pstmt = con.prepareStatement(UPDATE);
            pstmt.setLong(1, meeting.getId());
            pstmt.setString(2, user);
            pstmt.setInt(3, status.getCode());
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch (SQLException sqle) {
            abortTransaction = true;
            log.error(sqle.getMessage(), sqle);
            throw sqle;
        }
        finally {
            DbConnectionManager.closeTransactionConnection(con, abortTransaction);
        }
    }

    /**
     * Deletes a meeting request in the database
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
            log.error(sqle.getMessage(), sqle);
            throw sqle;
        }
        finally {
            DbConnectionManager.closeTransactionConnection(con, abortTransaction);
        }
    }

    /**
     * Loads all user meetings from database not deleted for a specific owner.
     *
     * @throws NotFoundException if the meeting could not be loaded.
     */
    public static List<MeetingMessage> findAllEnabledByUser(String username) throws NotFoundException {
        return MeetingRequest.findByUser(username, FIND_BY_USER, MeetingStatus.created);
    }
    
    /**
     * Loads current user meetings from database not deleted for a specific owner.
     *
     * @throws NotFoundException if the meeting could not be loaded.
     */
    public static List<MeetingMessage> findCurrentEnabledByUser(String username) throws NotFoundException {
        return MeetingRequest.findByUser(username, FIND_CURRENT_BY_USER, MeetingStatus.created);
    }
    
    /**
     * Loads past user meetings from database not deleted for a specific owner.
     *
     * @throws NotFoundException if the meeting could not be loaded.
     */
    public static List<MeetingMessage> findPastEnabledByUser(String username) throws NotFoundException {
        return MeetingRequest.findByUser(username, FIND_PAST_BY_USER, MeetingStatus.created);
    }
    
    private static List<MeetingMessage> findByUser(String username, String statement, MeetingStatus status) 
    		throws NotFoundException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<MeetingMessage> meetings = null;
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
    public static List<MeetingMessage> parseResultSet(ResultSet rs) throws SQLException{
    	List<MeetingMessage> messagesList = null;
    	if(rs != null){
    		messagesList = new ArrayList<MeetingMessage>();
            Meeting meeting = null;
            MeetingMessage message = null;
            while(rs.next()){
            	meeting = new Meeting();
            	meeting.setId(rs.getLong(1));
            	meeting.setOwner(rs.getString(2));
            	meeting.setDescription(rs.getString(3));
            	meeting.setPosition(rs.getString(4));
            	meeting.setTime(rs.getTimestamp(5));
            	meeting.setStatus(MeetingStatus.fromInt(rs.getInt(6)));
            	message = MeetingMessage.parseMeeting(meeting);
            	message.setId(rs.getString(7));
            	message.setStatus(MeetingRequestStatus.fromInt(rs.getInt(8)));
                messagesList.add(message);
            }
    	}
    	return messagesList;
    }
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Meeting getMeeting() {
		if(this.meeting == null && this.meetingId != null){
        	try {
				this.meeting = new Meeting(this.meetingId);
			} catch (NotFoundException e) {
				log.error(e.getMessage(), e);
			}
        }
		return meeting;
	}

	public void setMeeting(Meeting meeting) {
		if(meeting != null){
			this.meeting = meeting;
			this.setMeetingId(meeting.getId());
		}
	}

	public MeetingRequestStatus getStatus() {
		return status;
	}

	public void setStatus(MeetingRequestStatus status) {
		this.status = status;
	}

	public Long getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(Long meetingId) {
		this.meetingId = meetingId;
	}

}
