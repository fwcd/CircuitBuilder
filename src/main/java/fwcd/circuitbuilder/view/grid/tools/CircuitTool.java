package fwcd.circuitbuilder.view.grid.tools;

import java.awt.Graphics2D;
import java.awt.Image;

import fwcd.circuitbuilder.model.grid.CircuitCellModel;
import fwcd.circuitbuilder.model.grid.CircuitGridModel;
import fwcd.circuitbuilder.model.grid.components.Circuit1x1ComponentModel;
import fwcd.circuitbuilder.utils.AbsolutePos;
import fwcd.circuitbuilder.view.utils.PositionedRenderable;
import fwcd.fructose.Option;
import fwcd.fructose.OptionInt;

/**
 * A visual tool used to modify the circuit.
 */
public interface CircuitTool extends PositionedRenderable {
	String getName();
	
	default Option<Image> getImage() { return Option.empty(); }
	
	default void onLeftClick(CircuitGridModel grid, CircuitCellModel cell) {}
	
	default OptionInt getWidth() { return getImage().mapToInt(it -> it.getWidth(null)); }
	
	default OptionInt getHeight() { return getImage().mapToInt(it -> it.getHeight(null)); }
	
	@Override
	default void render(Graphics2D g2d, AbsolutePos pos) {
		getImage().ifPresent(img -> g2d.drawImage(img, pos.getX(), pos.getY(), null));
	}
	
	default void onRightClick(CircuitGridModel grid, CircuitCellModel cell) {
		for (Circuit1x1ComponentModel component : cell.getComponents()) {
			component.toggle();
		}
	}
}
