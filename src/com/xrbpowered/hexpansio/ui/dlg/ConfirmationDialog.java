package com.xrbpowered.hexpansio.ui.dlg;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.ui.FrameButton;
import com.xrbpowered.zoomui.GraphAssist;

public class ConfirmationDialog extends OverlayDialog {

	protected final FrameButton yesButton;
	protected final FrameButton noButton;
	
	protected String[] lines;

	public ConfirmationDialog(float height, String title, String[] lines, String yesLabel, String noLabel) {
		super(Hexpansio.instance.getBase(), 400, height>0 ? height : lines.length*15+120, title);
		this.lines = lines;
		
		yesButton = new FrameButton(box, yesLabel, 140) {
			@Override
			public void onClick() {
				onEnter();
			}
		};
		yesButton.setLocation(box.getWidth()/2+10, box.getHeight()-yesButton.getHeight()-10);

		noButton = new FrameButton(box, noLabel, 140) {
			@Override
			public void onClick() {
				dismiss();
			}
		};
		noButton.setLocation(box.getWidth()/2-noButton.getWidth()-10, box.getHeight()-yesButton.getHeight()-10);
	}

	public ConfirmationDialog(float height, String title, String message, String yesLabel, String noLabel) {
		this(height, title, message==null ? null : message.split("\\n"), yesLabel, noLabel);
	}

	@Override
	protected void paintBoxContents(GraphAssist g) {
		if(lines!=null) {
			int y = 60;
			for(String s : lines) {
				g.drawString(s, box.getWidth()/2f, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
				y += 15;
			}
		}
		super.paintBoxContents(g);
	}

}
