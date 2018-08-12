package com.rvlstudio;

import java.util.UUID;
import java.util.Calendar;
import java.util.Set;

public interface EventDAO {
	Event<?> getEvent(UUID id);
	Set<Event<?>> getEvents(Calendar start, Calendar end);
	Set<Event<?>> getEvents(String description);
	void addEvent(Event<?> event);
	void updateEvent(Event<?> event);
	void deleteEvent(Event<?> event);
}