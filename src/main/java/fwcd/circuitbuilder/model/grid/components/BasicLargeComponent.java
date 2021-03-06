package fwcd.circuitbuilder.model.grid.components;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import fwcd.circuitbuilder.utils.Direction;
import fwcd.circuitbuilder.utils.Directioned;
import fwcd.circuitbuilder.utils.RelativePos;

/**
 * A basic large component implementation that
 * uses a fixed number of inputs/outputs
 * and a boolean function to route a signal.
 */
public abstract class BasicLargeComponent implements CircuitLargeComponentModel {
	private final List<InputComponentModel> inputs;
	private final List<OutputComponentModel> outputs;
	private final int inputCount;
	private final int outputCount;
	
	private final int outputYOffset;
	private final int rows;
	private final int cols;
	
	public BasicLargeComponent(int inputCount, int outputCount) {
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		
		Collection<RelativePos> ioPositions = Stream.concat(
			IntStream.range(0, inputCount)
				.mapToObj(this::getInputPosition)
				.map(Directioned::getPos),
			IntStream.range(0, outputCount)
				.mapToObj(this::getOutputPosition)
				.map(Directioned::getPos)
		).collect(Collectors.toSet());
		RelativePos topLeft = ioPositions.stream()
			.reduce(RelativePos::min)
			.orElse(new RelativePos(0, 0));
		RelativePos bottomRight = ioPositions.stream()
			.reduce(RelativePos::max)
			.orElse(new RelativePos(0, 0));
		
		rows = (bottomRight.getY() - topLeft.getY()) + 1;
		cols = (bottomRight.getX() - topLeft.getX()) + 1;
		
		// The vertical offset needed to center the output cells
		outputYOffset = ((2 * Math.max(inputCount, outputCount) - 1) - outputCount) / 2;
		
		inputs = IntStream.range(0, inputCount)
			.mapToObj(this::getInputPosition)
			.map(pos -> new InputComponentModel(pos.getPos(), pos.getDirection().orElse(Direction.LEFT)))
			.collect(Collectors.toList());
		
		outputs = IntStream.range(0, outputCount)
			.mapToObj(this::getOutputPosition)
			.map(pos -> new OutputComponentModel(pos.getPos(), pos.getDirection().orElse(Direction.RIGHT)))
			.collect(Collectors.toList());
	}
	
	/**
	 * Fetches the relative position delta for an
	 * input component index. This method is required
	 * to be idempotent.
	 */
	protected Directioned<RelativePos> getInputPosition(int index) {
		return new Directioned<>(new RelativePos(0, index * 2));
	}
	
	/**
	 * Fetches the relative position delta for an
	 * output component index. This method is required
	 * to be idempotent.
	 */
	protected Directioned<RelativePos> getOutputPosition(int index) {
		return new Directioned<>(new RelativePos(1, index * 2 + outputYOffset));
	}
	
	protected abstract boolean[] compute(boolean... inputs);
	
	@Override
	public List<RelativePos> getOccupiedPositions(RelativePos topLeft) {
		return IntStream.range(0, rows)
			.boxed()
			.flatMap(y -> IntStream.range(0, cols)
				.boxed()
				.map(x -> new RelativePos(topLeft.add(x, y))))
			.collect(Collectors.toList());
	}
	
	@Override
	public void tick() {
		boolean[] boolInputs = new boolean[inputCount];
		
		for (int i = 0; i < inputCount; i++) {
			boolInputs[i] = inputs.get(i).isPowered();
		}
		
		boolean[] boolOutputs = compute(boolInputs);
		
		if (boolOutputs.length != outputCount) {
			throw new RuntimeException("compute() can't return more output booleans than there are outputs in this large component!");
		}
		
		for (int i = 0; i < outputCount; i++) {
			outputs.get(i).setPowered(boolOutputs[i]);
		}
	}
	
	@Override
	public List<InputComponentModel> getInputs() { return inputs; }
	
	@Override
	public List<OutputComponentModel> getOutputs() { return outputs; }
	
	public int getRows() { return rows; }
	
	public int getCols() { return cols; }
	
	@Override
	public String toString() {
		return getName();
	}
}
