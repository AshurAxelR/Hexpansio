package com.xrbpowered.hexpansio.ui.dlg;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.ui.FrameButton;
import com.xrbpowered.zoomui.GraphAssist;

public class QuickExitDialog extends OverlayDialog {

	private final FrameButton yesButton;
	private final FrameButton noButton;

	public QuickExitDialog() {
		super(Hexpansio.instance.getBase(), 400, 130, "EXIT");
		
		yesButton = new FrameButton(box, "EXIT", 140) {
			@Override
			public void onClick() {
				onEnter();
			}
		};
		yesButton.setLocation(box.getWidth()/2+10, box.getHeight()-yesButton.getHeight()-10);

		noButton = new FrameButton(box, "STAY", 140) {
			@Override
			public void onClick() {
				dismiss();
			}
		};
		noButton.setLocation(box.getWidth()/2-noButton.getWidth()-10, box.getHeight()-yesButton.getHeight()-10);
	}

	@Override
	public void onEnter() {
		Hexpansio.instance.saveGame();
		System.exit(0);
	}
	
	@Override
	protected void paintBoxContents(GraphAssist g) {
		g.drawString("Save changes and exit to desktop?", box.getWidth()/2f, 60, GraphAssist.CENTER, GraphAssist.BOTTOM);
		super.paintBoxContents(g);
	}

}
