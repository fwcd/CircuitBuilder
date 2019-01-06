package fwcd.circuitbuilder.model.grid.components;

import fwcd.circuitbuilder.model.grid.CircuitItemVisitor;

/**
 * An NOR logic gate.
 */
public class NorGateModel extends BasicLargeComponent {
	private static final int INPUTS_COUNT = 2;
	private static final int OUTPUTS_COUNT = 1;
	
	public NorGateModel() {
		super(INPUTS_COUNT, OUTPUTS_COUNT);
	}
		
	@Override
	public String getName() { return "NOR"; }
		
	@Override
	public String getSymbol() { return "!>=1"; }
	
	@Override
	public <T> T accept(CircuitItemVisitor<T> visitor) { return visitor.visitNor(this); }
	
	@Override
	protected boolean[] compute(boolean[] inputs) {
		return new boolean[] {!inputs[0] && !inputs[1]};
	}
}
