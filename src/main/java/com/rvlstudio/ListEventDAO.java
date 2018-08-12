package com.rvlstudio;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.ArrayList;

public class ListEventDAO implements EventDAO {
	private ArrayList<Event<?>> events;

	public ListEventDAO() {
		this.events = new ArrayList<>();
	}

	public Event<?> getEvent(UUID id) throws UnknownIdException {
		for(Event<?> e : events) {
			if(e.getId().equals(id)) return e;
		}
		throw new UnknownIdException("ID not found: " + id);
	}

	public ArrayList<Event<?>> getEvents() { return events; }

	public Set<Event<?>> getEvents(Calendar start, Calendar end) {
		HashSet<Event<?>> set = new HashSet<>();
		for(Event<?> e : events) {
			if(e.getCalendar().after(start) && e.getCalendar().before(end)) set.add(e);
		}
		return set;
	}

	public Set<Event<?>> getEvents(String description) {
		HashSet<Event<?>> set = new HashSet<>();
		for(Event<?> e : events) {
			if(e.getDescription().equals(description)) set.add(e);
		}
		return set;
	}

	public void addEvent(Event<?> event) {
		events.add(event);
	}

	public void updateEvent(Event<?> event) {
		for(Event<?> e : events) {
			if(e.getId().equals(event.getId())) {
				e.setDescription(event.getDescription());
				e.setUnit(event.getUnit());
			}
		}
	}

	public void deleteEvent(Event<?> event) {
		events.remove(event);
	}

}