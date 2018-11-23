package com.fwcd.circuitbuilder.items.components;

import java.awt.Image;

import javax.swing.Icon;

import com.fwcd.circuitbuilder.utils.Direction;
import com.fwcd.fructose.swing.ResourceImage;

public class Lever extends SimpleEmitter {
	private static final ResourceImage IMAGE_ON = new ResourceImage("/leverOn.png");
	private static final ResourceImage IMAGE_OFF = new ResourceImage("/leverOff.png");
	
	private boolean isPowered = false;
	
	@Override
	public boolean isPowered() {
		return isPowered;
	}

	@Override
	public boolean outputsTowards(Direction outputDir) {
		return true;
	}

	@Override
	public CircuitComponent copy() {
		return new Lever();
	}

	@Override
	public void onRightClick() {
		isPowered = !isPowered;
	}

	@Override
	public Icon getIcon() {
		return IMAGE_ON.getAsIcon();
	}

	@Override
	protected Image getEnabledImage() {
		return IMAGE_ON.get();
	}

	@Override
	protected Image getDisabledImage() {
		return IMAGE_OFF.get();
	}
}