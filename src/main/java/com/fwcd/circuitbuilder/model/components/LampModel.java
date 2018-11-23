package com.fwcd.circuitbuilder.model.components;

import com.fwcd.circuitbuilder.model.CircuitItemVisitor;

public class LampModel extends BasicReceiver {	
	@Override
	public String getName() { return "Lamp"; }
	
	@Override
	public void accept(CircuitItemVisitor visitor) { visitor.visitLamp(this); }
}
