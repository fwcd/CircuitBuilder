package fwcd.circuitbuilder.model.grid;

import java.util.HashSet;
import java.util.Set;

import fwcd.circuitbuilder.model.grid.cable.CableNetwork;
import fwcd.circuitbuilder.model.grid.timediagram.TimeDiagramModel;

/**
 * An engine that ticks and updates components on a circuit grid.
 */
public class CircuitEngineModel {
	private static final boolean DEBUG_NETWORKS = false;
	private final CircuitListenableGridModel grid;
	private final CircuitNetworksManager networkManager;
	
	private long lastTick = 0;
	private long tickDelay = 80; // ms
	private boolean autoCleanCells = true;
	
	public CircuitEngineModel(CircuitListenableGridModel grid) {
		this.grid = grid;
		
		networkManager = new CircuitNetworksManager(new HashSet<>());
		grid.getAddCableListeners().add(networkManager::onAddCable);
		grid.getRemoveCableListeners().add(networkManager::onRemoveCable);
		grid.getClearListeners().add(networkManager::onClear);
	}
	
	public void update() {
		long now = System.currentTimeMillis();
		long delta = now - lastTick;
		
		if (delta > tickDelay) {
			tick();
			lastTick = now;
		}
		
		getTimeDiagram().onPartialTick((double) delta / tickDelay);
	}
	
	private void tick() {
		// Pre ticking - Updating networks
		
		for (CableNetwork network : getCableNetworks()) {
			network.updateStatus(grid.getInner());
		}
		
		if (DEBUG_NETWORKS) {
			// TODO: Logging
			System.out.println("Networks: [\n" + getCableNetworks().stream().map(Object::toString).reduce((a, b) -> a + "\n" + b).orElse("") + "\n]");
		}
		
		// Main ticking
		
		grid.getInner().forEach1x1((cell, component) -> component.tick(grid.getInner().getNeighbors(cell.getPos())));
		grid.getInner().getLargeComponents().values().forEach(largeComponent -> largeComponent.tick());
		
		// Updating
		
		grid.getInner().forEach1x1((cell, component) -> component.update());
		
		// Cleaning
		
		if (autoCleanCells) {
			grid.getInner().cleanCells();
		}
		
		grid.getChangeListeners().fire();
	}
	
	/**
	 * A set of cable networks.
	 */
	public Set<? extends CableNetwork> getCableNetworks() {
		return networkManager.getNetworks();
	}
	
	public TimeDiagramModel getTimeDiagram() {
		return networkManager.getTimeDiagram();
	}
}
