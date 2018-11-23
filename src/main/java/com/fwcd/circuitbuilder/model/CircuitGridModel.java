package com.fwcd.circuitbuilder.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.fwcd.circuitbuilder.model.components.Circuit1x1ComponentModel;
import com.fwcd.circuitbuilder.model.components.CircuitLargeComponentModel;
import com.fwcd.circuitbuilder.model.components.InputComponentModel;
import com.fwcd.circuitbuilder.model.components.OutputComponentModel;
import com.fwcd.circuitbuilder.utils.ConcurrentMultiKeyHashMap;
import com.fwcd.circuitbuilder.utils.Direction;
import com.fwcd.circuitbuilder.utils.MultiKeyMap;
import com.fwcd.circuitbuilder.utils.RelativePos;
import com.fwcd.fructose.ListenerList;

/**
 * A 2D-grid of circuit components.
 */
public class CircuitGridModel {
	private final Map<RelativePos, CircuitCellModel> cells = new HashMap<>();
	private final MultiKeyMap<RelativePos, CircuitLargeComponentModel> largeComponents = new ConcurrentMultiKeyHashMap<>();
	private final ListenerList changeListeners = new ListenerList();
	
	/**
	 * Removes empty cells from the registered cells.<br><br>
	 * 
	 * (They will be re-initialized once they are needed again)
	 */
	public void cleanCells() {
		for (RelativePos key : cells.keySet()) {
			if (cells.containsKey(key) && cells.get(key).isEmpty()) {
				cells.remove(key);
			}
		}
	}
	
	public boolean isCellEmpty(RelativePos pos) {
		return cells.containsKey(pos) ? cells.get(pos).isEmpty() : true;
	}
	
	public CircuitCellModel getCell(RelativePos pos) {
		cells.putIfAbsent(pos, new CircuitCellModel(pos));
		return cells.get(pos);
	}
	
	public Map<Direction, CircuitCellModel> getNeighbors(RelativePos pos) {
		Map<Direction, CircuitCellModel> neighbors = new HashMap<>();
		
		for (Direction dir : Direction.values()) {
			neighbors.put(dir, getCell(pos.follow(dir)));
		}
		
		return neighbors;
	}
	
	public void clearCell(RelativePos pos) {
		if (!isCellEmpty(pos) && !cells.get(pos).containsUnremovableComponents()) {
			cells.remove(pos);
		}
		
		if (largeComponents.containsKey(pos)) {
			for (RelativePos subPos : largeComponents.getAllMappings(pos)) {
				cells.remove(subPos);
			}
			
			largeComponents.removeAllMappings(pos);
		}
	}
	
	public void putLarge(CircuitLargeComponentModel component, RelativePos pos) {
		for (InputComponentModel input : component.getInputs()) {
			RelativePos inputPos = new RelativePos(pos.add(input.getDeltaPos()));
			put(input, inputPos);
		}

		for (OutputComponentModel output : component.getOutputs()) {
			RelativePos outputPos = new RelativePos(pos.add(output.getDeltaPos()));
			put(output, outputPos);
		}
		
		largeComponents.put(pos, component, component.getOccupiedPositions(pos));
	}
	
	public void put(Circuit1x1ComponentModel component, RelativePos pos) {
		getCell(pos).place(component);
		
		Map<Direction, CircuitCellModel> neighbors = getNeighbors(pos);
		component.onPlace(neighbors);
		
		for (CircuitCellModel cell : neighbors.values()) {
			if (!cell.isEmpty()) {
				for (Circuit1x1ComponentModel neighborComponent : cell.getComponents()) {
					neighborComponent.onPlace(getNeighbors(cell.getPos()));
				}
			}
		}
		
		changeListeners.fire();
	}
	
	public void forEach1x1(BiConsumer<CircuitCellModel, Circuit1x1ComponentModel> consumer) {
		for (CircuitCellModel cell : cells.values()) {
			cell.getComponent().forEach(component -> consumer.accept(cell, component));
		}
	}
	
	ListenerList getChangeListeners() { return changeListeners; }
	
	Collection<? extends CircuitLargeComponentModel> getLargeComponents() { return largeComponents.values(); }
}