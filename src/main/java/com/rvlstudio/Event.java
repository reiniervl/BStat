package com.rvlstudio;

import java.util.*;

public class Event<T> {
	private T value;
	private Unit unit;
	private UUID id;
	private String description;
	private Calendar calendar;

	public Event(T value, String description, Calendar calendar, Unit unit) {
		this.value = value;
		this.description = description;
		this.calendar = calendar;
		this.unit = unit;
		this.id = UUID.randomUUID();
	}

	public Event(T value, String description, Calendar calendar, Unit unit, UUID id) {
		this.value = value;
		this.description = description;
		this.calendar = calendar;
		this.unit = unit;
		this.id = id;
	}

	public T getValue() { return value; }
	public void setValue(T value) { this.value = value; }

	public Unit getUnit() { return unit; }
	public void setUnit(Unit unit) { this.unit = unit; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public Calendar getCalendar() { return calendar; }
	public void setCalendar(Calendar calendar) { this.calendar = calendar; }

	public UUID getId() { return id; }

	@Override
	public int hashCode() { return id.hashCode(); }

	@Override
	public String toString() {
		return description + " value: " + value + "\tat: " + calendar.getTime().toString();
	}
}