package fwcd.circuitbuilder.model.grid.components;

import fwcd.circuitbuilder.model.grid.CircuitItemVisitor;

/**
 * An NAND logic gate.
 */
public class NandGateModel extends BasicLargeComponent {
	private static final int INPUT_COUNT = 2;
	private static final int OUTPUT_COUNT = 1;
	
	public NandGateModel() {
		super(INPUT_COUNT, OUTPUT_COUNT);
	}
		
	@Override
	public String getName() { return "NAND"; }
		
	@Override
	public String getSymbol() { return "!&"; }
	
	@Override
	public <T> T accept(CircuitItemVisitor<T> visitor) { return visitor.visitNand(this); }
	
	@Override
	protected boolean[] compute(boolean... inputs) {
		return new boolean[] {!inputs[0] || !inputs[1]};
	}
}
