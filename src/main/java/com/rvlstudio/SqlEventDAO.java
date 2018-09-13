package com.rvlstudio;

import java.sql.*;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet;

public class SqlEventDAO implements EventDAO {
	private static String conStr = "jdbc:sqlite:events.db";
	private static SqlEventDAO dao = null;

	private static Connection getConnection() {
		Connection con = null;
		try { con = DriverManager.getConnection(conStr); } 
		catch(SQLException e) { System.out.println(e); }
		return con;
	}

	public static SqlEventDAO getDAO() {
		if(dao == null) dao = new SqlEventDAO();
		return dao;
	}

	
	public static SqlEventDAO getDAO(String path) {
		if(dao != null) throw new RuntimeException("DAO already exists at path: " + conStr);
		conStr = "jdbc:sqlite:" + path;
		return getDAO();
	}

	private SqlEventDAO() {
		try(Connection con = getConnection()) {
			con.createStatement().execute("CREATE TABLE IF NOT EXISTS events(uuid VARCHAR PRIMARY KEY, description VARCHAR, value VARCHAR, calendar INTEGER, unit VARCHAR)");
		} catch(SQLException e) {
			System.out.println("Error database init: " + e.getMessage());
		}
	}

	private Event<?> eventFromRow(ResultSet rs) throws SQLException {
		Unit unit = Unit.fromString(rs.getString("unit"));
		String description = rs.getString("description");
		UUID uuid = UUID.fromString(rs.getString("uuid"));
		String value = rs.getString("value");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(rs.getLong("calendar"));
		if(unit.equals(Unit.MILILITER) || unit.equals(Unit.GRAM)) return new Event<Integer>(Integer.parseInt(value), description, cal, unit, uuid);
		else if(unit.equals(Unit.LITER) || unit.equals(Unit.KILO)) return new Event<Double>(Double.parseDouble(value), description, cal, unit, uuid);
		else return new Event<String>(value, description, cal, unit, uuid);
	}

	@Override
	public Event<?> getEvent(UUID id) throws UnknownIdException {
		try(Connection con = getConnection()) {
			ResultSet rs = con.createStatement().executeQuery("SELECT * FROM events WHERE uuid=" + id.toString());
			if(rs.next()) {	return eventFromRow(rs); }
			throw new UnknownIdException("id not found " + id);
		} catch(SQLException e) {
			System.out.println("Error retreiving event: " + e.getMessage());
		}
		return null;
	}

	@Override
	public Set<Event<?>> getEvents(Calendar start, Calendar end) {
		long s = start.getTimeInMillis();
		long e = end.getTimeInMillis();
		
		HashSet<Event<?>> set = new HashSet<>();
		try(Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM events WHERE calendar > ? AND calendar < ?")) {
				stmt.setLong(1, s);
				stmt.setLong(2, e);
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) { set.add(eventFromRow(rs));	}
		} catch(SQLException exc) {
			System.out.println("Error retreiving all events: " + exc.getMessage());
		}
		return set;
	}

	@Override
	public Set<Event<?>> getEvents(String description) {
		HashSet<Event<?>> set = new HashSet<>();
		try(Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM events WHERE where description=?")) {
				stmt.setString(1, description);
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) { set.add(eventFromRow(rs)); }
		} catch(SQLException exc) {
			System.out.println("Error retreiving all events: " + exc.getMessage());
		}
		return set;
	}

	public Set<Event<?>> getEvents() {
		HashSet<Event<?>> set = new HashSet<>();
		try(Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM events")) {
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) { set.add(eventFromRow(rs)); }
		} catch(SQLException exc) {
			System.out.println("Error retreiving all events: " + exc.getMessage());
		}
		return set;
	}

	@Override
	public void addEvent(Event<?> event) {
		try(Connection con = getConnection(); 
			PreparedStatement stmt = con.prepareStatement(
				"INSERT INTO events(description, value, calendar, unit, uuid) VALUES(?,?,?,?,?)")) {
					stmt.setString(1, event.getDescription());
					stmt.setString(2, event.getValue().toString());
					stmt.setLong(3, event.getCalendar().getTimeInMillis());
					stmt.setString(4, event.getUnit().toString());
					stmt.setString(5, event.getId().toString());
					stmt.executeUpdate();
		} catch(SQLException e) {
			System.out.println("Error adding event: " + e.getMessage());
		}
	}

	@Override
	public void updateEvent(Event<?> event) {
		try(Connection con = getConnection(); 
			PreparedStatement stmt = con.prepareStatement(
				"UPDATE events SET description=?, value=?, calendar=?, unit=? WHERE uuid=?")) {
					stmt.setString(1, event.getDescription());
					stmt.setString(2, event.getValue().toString());
					stmt.setLong(3, event.getCalendar().getTimeInMillis());
					stmt.setString(4, event.getUnit().toString());
					stmt.setString(5, event.getId().toString());
					stmt.executeUpdate();
		} catch(SQLException e) {
			System.out.println("Error updating event: " + e.getMessage());
		}
		
	}

	@Override
	public void deleteEvent(Event<?> event) {
		try(Connection con = getConnection(); PreparedStatement stmnt = con.prepareStatement("DELETE FROM events WHERE uuid=?")) {
			stmnt.setString(1, event.getId().toString());
			stmnt.executeUpdate();
		} catch(SQLException e) {
			System.out.println("Error deleting event " + e.getMessage());
		}
	}
}