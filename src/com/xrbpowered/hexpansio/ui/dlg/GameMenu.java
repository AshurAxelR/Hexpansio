package com.xrbpowered.hexpansio.ui.dlg;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.FrameButton;

public class GameMenu extends OverlayDialog {

	private static final int buttonWidth = 240;
	private static final int buttonHeight = 45;
	
	private final FrameButton resumeButton;
	private final FrameButton saveButton;
	private final FrameButton loadButton;
	private final FrameButton newButton;
	private final FrameButton exitButton;

	public GameMenu() {
		super(Hexpansio.instance.getBase(), buttonWidth+80, buttonHeight*5+140, null);
		
		int y = 60;
		resumeButton = new FrameButton(box, "RESUME", buttonWidth, buttonHeight, Res.fontLarge) {
			@Override
			public void onClick() {
				dismiss();
			}
		};
		resumeButton.setLocation(40, y);
		y += resumeButton.getHeight() + 5;

		saveButton = new FrameButton(box, "SAVE GAME", buttonWidth, buttonHeight, Res.fontLarge) {
			@Override
			public void onClick() {
				Hexpansio.instance.saveGame();
				dismiss();
			}
		};
		saveButton.setLocation(40, y);
		y += saveButton.getHeight() + 5;
		
		loadButton = new FrameButton(box, "RELOAD GAME", buttonWidth, buttonHeight, Res.fontLarge) {
			@Override
			public boolean isEnabled() {
				return Hexpansio.instance.getWorld().save.exists();
			}
			@Override
			public void onClick() {
				if(!isEnabled())
					return;
				new ConfirmationDialog(0, "RELOAD GAME", "Reload last save?", "RELOAD", "CANCEL") {
					@Override
					public void onEnter() {
						Hexpansio.instance.loadGame();
						dismiss();
						GameMenu.this.dismiss();
					}
				}.repaint();
			}
		};
		loadButton.setLocation(40, y);
		y += loadButton.getHeight() + 5;

		newButton = new FrameButton(box, "START NEW", buttonWidth, buttonHeight, Res.fontLarge) {
			@Override
			public void onClick() {
				new ConfirmationDialog(0, "NEW GAME", "Reset all progress and start new world?", "START", "CANCEL") {
					@Override
					public void onEnter() {
						Hexpansio.instance.newGame();
						dismiss();
						GameMenu.this.dismiss();
					}
				}.repaint();
			}
		};
		newButton.setLocation(40, y);
		y += newButton.getHeight() + 5;

		exitButton = new FrameButton(box, "EXIT", buttonWidth, buttonHeight, Res.fontLarge) {
			@Override
			public void onClick() {
				new QuickExitDialog().repaint();
			}
		};
		exitButton.setLocation(40, y);
	}
	
	@Override
	public void onEnter() {
	}

}
