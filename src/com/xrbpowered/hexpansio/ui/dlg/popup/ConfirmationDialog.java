package com.xrbpowered.hexpansio.ui.dlg.popup;

import com.xrbpowered.hexpansio.ui.ClickButton;

public class ConfirmationDialog extends PopupDialog {

	protected final ClickButton yesButton;
	protected final ClickButton noButton;
	
	public ConfirmationDialog(float height, String title, String[] lines, String yesLabel, String noLabel) {
		super(height, title, lines);
		
		yesButton = new ClickButton(box, yesLabel, 140) {
			@Override
			public void onClick() {
				onEnter();
			}
		};
		yesButton.setLocation(box.getWidth()/2+10, box.getHeight()-yesButton.getHeight()-10);

		noButton = new ClickButton(box, noLabel, 140) {
			@Override
			public void onClick() {
				onCancel();
			}
		};
		noButton.setLocation(box.getWidth()/2-noButton.getWidth()-10, box.getHeight()-yesButton.getHeight()-10);
	}

	public ConfirmationDialog(float height, String title, String message, String yesLabel, String noLabel) {
		this(height, title, message==null ? null : message.split("\\n"), yesLabel, noLabel);
	}

}
