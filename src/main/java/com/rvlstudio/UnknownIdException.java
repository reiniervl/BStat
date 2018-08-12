package com.rvlstudio;

import java.util.UUID;

public class UnknownIdException extends RuntimeException {
	public static final long serialVersionUID = UUID.randomUUID().getMostSignificantBits();
	
	public UnknownIdException(String message) {
		super(message);
	}
}