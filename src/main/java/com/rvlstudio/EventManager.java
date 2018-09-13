package com.rvlstudio;

import java.util.Observable;
import java.util.Calendar;
import java.util.ArrayList;

public class EventManager extends Observable {
	private ArrayList<Event<?>> events;

	private static EventDAO dao = null;
	
	public EventManager() {
		EventManager.dao = SqlEventDAO.getDAO();
		events = new ArrayList<>();
	}

	public void addEvent(Event<?> event) {
		dao.addEvent(event);
		notifyObservers("ADD");
	}

	public void deleteEvent(Event<?> event) {
		dao.deleteEvent(event);
		notifyObservers("DELETE");
	}

	public void updateEvent(Event<?> event) {
		dao.updateEvent(event);
		notifyObservers("UPDATE");
	}
	
	public void setListToMonth() {
		Calendar start = Calendar.getInstance();
		start.set(Calendar.DAY_OF_MONTH, 0);
		start.set(Calendar.HOUR, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);

		Calendar end = Calendar.getInstance();
		end.set(Calendar.DAY_OF_MONTH, end.getMaximum(Calendar.DAY_OF_MONTH));
		end.set(Calendar.HOUR, 23);
		end.set(Calendar.MINUTE, 59);
		end.set(Calendar.SECOND, 59);

		events = new ArrayList<Event<?>>(dao.getEvents(start, end));
		notifyObservers("MONTH");
	}
	
	public void setListToWeek() {
		Calendar start = Calendar.getInstance();
		start.set(Calendar.DAY_OF_WEEK, 0);
		start.set(Calendar.HOUR, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);

		Calendar end = Calendar.getInstance();
		end.set(Calendar.DAY_OF_WEEK, 7);
		end.set(Calendar.HOUR, 23);
		end.set(Calendar.MINUTE, 59);
		end.set(Calendar.SECOND, 59);

		events = new ArrayList<Event<?>>(dao.getEvents(start, end));
		notifyObservers("WEEK");
	}
	
	public void setListToWeek(Calendar week) {
		Calendar start = (Calendar)week.clone();
		start.set(Calendar.DAY_OF_WEEK, 0);
		start.set(Calendar.HOUR, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);

		Calendar end = week;
		end.set(Calendar.DAY_OF_WEEK, 7);
		end.set(Calendar.HOUR, 23);
		end.set(Calendar.MINUTE, 59);
		end.set(Calendar.SECOND, 59);

		events = new ArrayList<Event<?>>(dao.getEvents(start, end));
		notifyObservers("WEEK");
	}

	public void setListToDay() {
		Calendar start = Calendar.getInstance();
		start.set(Calendar.HOUR, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);

		Calendar end = Calendar.getInstance();
		end.set(Calendar.HOUR, 23);
		end.set(Calendar.MINUTE, 59);
		end.set(Calendar.SECOND, 59);

		events = new ArrayList<Event<?>>(dao.getEvents(start, end));
		notifyObservers("DAY");
	}

	public void setListToDay(Calendar day) {
		Calendar start = (Calendar)day.clone();
		start.set(Calendar.HOUR, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);

		Calendar end = day;
		end.set(Calendar.HOUR, 23);
		end.set(Calendar.MINUTE, 59);
		end.set(Calendar.SECOND, 59);

		events = new ArrayList<Event<?>>(dao.getEvents(start, end));
		notifyObservers("DAY");
	}

	public ArrayList<Event<?>> getEvents() { return events; }
}