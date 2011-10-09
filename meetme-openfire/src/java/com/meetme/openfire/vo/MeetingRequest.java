/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.vo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.database.JiveID;
import org.jivesoftware.database.SequenceManager;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.util.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final String DELETE =
            "DELETE from ofMeetingRequest WHERE id=?";
    
	/**
	 * Unique id of the request
	 */
	private Long id;
	/**
	 * User requested
	 */
	private User user;
	
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
	private Status status;
	
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
     * @throws UserNotFoundException if the meeting request user does 
     * not exist or could not be loaded.
     */
    public MeetingRequest(long id) throws NotFoundException, UserNotFoundException {
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
     * @throws UserNotFoundException if the meeting request user does 
     * not exist or could not be loaded.
     */
    public MeetingRequest(long id, Meeting meeting) throws NotFoundException, 
    	UserNotFoundException {
        this.id = id;
        this.meeting = meeting;
        load();
    }
    
    /**
     * Inserts a new meeting request into the database.
     */
    public void insert() throws SQLException {
        this.id = SequenceManager.nextID(this);
        Connection con = null;
        boolean abortTransaction = false;
        try {
            con = DbConnectionManager.getTransactionConnection();
            PreparedStatement pstmt = con.prepareStatement(INSERT);
            pstmt.setLong(1, id);
            pstmt.setLong(2, meeting.getId());
            pstmt.setString(3, user.getUsername());
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
    }
    
    /**
     * Loads a meeting request from the database.
     *
     * @throws NotFoundException if the meeting request could not be loaded.
     */
    public void load() throws NotFoundException, UserNotFoundException {
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
            this.user = UserManager.getInstance().getUser(rs.getString(2));
            this.status = Status.fromInt(rs.getInt(3));
            
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
            pstmt.setLong(1, meeting.getId());
            pstmt.setString(2, user.getUsername());
            pstmt.setInt(3, status.getCode());
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Meeting getMeeting() {
		if(this.meeting == null && this.meetingId != null){
        	try {
				this.meeting = new Meeting(this.meetingId);
			} catch (NotFoundException e) {
				log.error(e.getMessage(), e);
			} catch (UserNotFoundException e) {
				log.error(e.getMessage(), e);
			}
        }
		return meeting;
	}

	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Long getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(Long meetingId) {
		this.meetingId = meetingId;
	}

}
