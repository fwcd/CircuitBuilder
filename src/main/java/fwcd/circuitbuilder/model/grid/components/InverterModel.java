package fwcd.circuitbuilder.model.grid.components;

import java.util.Map;

import fwcd.circuitbuilder.model.grid.CircuitCellModel;
import fwcd.circuitbuilder.model.grid.CircuitItemVisitor;
import fwcd.circuitbuilder.utils.Direction;

public class InverterModel implements Circuit1x1ComponentModel {
	private Direction facing = Direction.RIGHT;
	private boolean nowEmitting = true;
	private boolean soonEmitting = nowEmitting;
	
	@Override
	public void tick(Map<Direction, CircuitCellModel> neighbors) {
		soonEmitting = true;
		
		for (Direction dir : neighbors.keySet()) {
			if (dir.equals(facing.invert())) {
				for (Circuit1x1ComponentModel component : neighbors.get(dir).getComponents()) {
					if (component.isPowered() && component.outputsTowards(facing)) {
						soonEmitting = false;
					}
				}
			}
		}
	}
	
	@Override
	public boolean canConnectFrom(Direction direction) {
		return direction.isAlongAxisOf(facing);
	}
	
	@Override
	public boolean toggle() {
		facing = facing.next();
		return true;
	}
	
	@Override
	public void update() {
		nowEmitting = soonEmitting;
	}
		
	@Override
	public String getName() { return "Inverter"; }
	
	@Override
	public boolean isPowered() { return nowEmitting; }
	
	@Override
	public <T> T accept(CircuitItemVisitor<T> visitor) { return visitor.visitInverter(this); }
	
	public Direction getFacing() { return facing; }
	
	public Direction getImageFacing() { return facing.invert().next(); }
	
	@Override
	public boolean outputsTowards(Direction outputDir) { return outputDir.equals(facing); }
}
