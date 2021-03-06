package fwcd.circuitbuilder.model.grid.components;

import java.util.Map;

import fwcd.circuitbuilder.model.grid.CircuitCellModel;
import fwcd.circuitbuilder.model.grid.CircuitItemVisitor;
import fwcd.circuitbuilder.utils.Direction;

public class ClockModel extends BasicEmitter {
	private int[] tickDelayModes = {5, 1, 20};
	private int tickDelayModeIndex = 0;
	
	private int maxTickDelay = tickDelayModes[tickDelayModeIndex];
	private int tickDelay = 0;
	
	@Override
	public boolean toggle() {
		tickDelayModeIndex = (tickDelayModeIndex + 1) % tickDelayModes.length;
		maxTickDelay = tickDelayModes[tickDelayModeIndex];
		return true;
	}
		
	@Override
	public String getName() { return "Clock"; }
	
	@Override
	public String getSymbol() { return "clk"; }
	
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
	public <T> T accept(CircuitItemVisitor<T> visitor) { return visitor.visitTickingClock(this); }
}
