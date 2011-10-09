/**
 * Copyright (C) 2011-2011 Meetme Software. All rights reserved.
 */
package com.meetme.openfire.vo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.database.JiveID;
import org.jivesoftware.database.SequenceManager;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.util.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meetme.openfire.util.Constants;

/**
 * Representation of a meeting
 * 
 * @author alex
 *
 */
@JiveID(Constants.MEET_JID)
public class Meeting {
	
	
	//TODO: Pensar en el calculo dinamico del estado del meeting
	
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
    private static final String DELETE =
            "DELETE from ofMeeting WHERE id=?";
    private static final String LOAD_REQUESTS =
            "SELECT id, requested_user, status" +
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
	private Date time;
	
	/**
	 * The status of the meeting. Calculated with the status of the meeting queries
	 */
	private Status status;
	
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
     * Loads an existing meeting based on its ID.
     *
     * @param id the meeting ID.
     * @throws NotFoundException if the meeting does not exist or could not be loaded.
     */
    public Meeting(Long id) throws NotFoundException, UserNotFoundException {
        this.id = id;
        load();
    }
    
    /**
     * Inserts a new meeting into the database.
     */
    public void insert() throws SQLException {
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
            pstmt.setDate(5, time);
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
    }
    
    /**
     * Loads a meeting from the database.
     *
     * @throws NotFoundException if the meeting could not be loaded.
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
            this.owner = rs.getString(1);
            this.description = rs.getString(2);
            this.position = rs.getString(3);
            this.time = rs.getDate(4);
            this.status = Status.fromInt(rs.getInt(5));
            
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
    public void loadRequests() throws NotFoundException, UserNotFoundException {
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
            	request.setUser(UserManager.getInstance().getUser(rs.getString(2)));
                request.setStatus(Status.fromInt(rs.getInt(3)));
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
            pstmt.setDate(4, time);
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

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<MeetingRequest> getRequests() {
		if(requests == null){
			try {
				this.loadRequests();
			} catch (NotFoundException e) {
				log.error(e.getMessage(), e);
			} catch (UserNotFoundException e) {
				log.error(e.getMessage(), e);
			}
		}
		return requests;
	}

	public void setRequests(List<MeetingRequest> requests) {
		this.requests = requests;
	}
	

}
