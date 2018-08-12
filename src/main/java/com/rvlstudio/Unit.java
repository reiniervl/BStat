package com.rvlstudio;

public enum Unit {
	MILILITER,
	LITER,
	GRAM,
	KILO,
	CHECK,
	UNKNOWN;

	public static Unit fromString(String unit) {
		if(unit.equals("MILILITER")) return Unit.MILILITER;
		else if(unit.equals("LITER")) return Unit.LITER;
		else if(unit.equals("GRAM")) return Unit.GRAM;
		else if(unit.equals("KILO")) return Unit.KILO;
		else if(unit.equals("CHECK")) return Unit.CHECK;
		return UNKNOWN;
	}
}