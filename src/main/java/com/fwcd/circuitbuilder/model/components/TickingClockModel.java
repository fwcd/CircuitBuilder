package com.fwcd.circuitbuilder.model.components;

import java.util.Map;

import com.fwcd.circuitbuilder.model.CircuitCellModel;
import com.fwcd.circuitbuilder.model.CircuitItemVisitor;
import com.fwcd.circuitbuilder.utils.Direction;

public class TickingClockModel extends BasicEmitter {
	private int[] tickDelayModes = {5, 1, 20};
	private int tickDelayModeIndex = 0;
	
	private int maxTickDelay = tickDelayModes[tickDelayModeIndex];
	private int tickDelay = 0;
	
	@Override
	public void toggle() {
		tickDelayModeIndex = (tickDelayModeIndex + 1) % tickDelayModes.length;
		maxTickDelay = tickDelayModes[tickDelayModeIndex];
	}
	
	@Override
	public void tick(Map<Direction, CircuitCellModel> neighbors) {
		if (tickDelay <= 0) {
			setPowered(!isPowered());
			tickDelay = maxTickDelay;
		} else {
			tickDelay--;
		}
	}
	
	@Override
	public void accept(CircuitItemVisitor visitor) { visitor.visitTickingClock(this); }
}