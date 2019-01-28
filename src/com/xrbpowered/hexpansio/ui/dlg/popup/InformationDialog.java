package com.xrbpowered.hexpansio.ui.dlg.popup;

import com.xrbpowered.hexpansio.ui.ClickButton;

public class InformationDialog extends PopupDialog {

	protected final ClickButton okButton;
	
	public InformationDialog(float height, String title, String[] lines, String okLabel) {
		super(height, title, lines);
		
		okButton = new ClickButton(box, okLabel, 140) {
			@Override
			public void onClick() {
				onEnter();
			}
		};
		okButton.setLocation(box.getWidth()/2-okButton.getWidth()/2, box.getHeight()-okButton.getHeight()-10);
	}

	public InformationDialog(float height, String title, String message, String okLabel) {
		this(height, title, message==null ? null : message.split("\\n"), okLabel);
	}

	public InformationDialog(boolean done, String message) {
		this(0, done ? "DONE" : "CAN'T DO", message==null ? null : message.split("\\n"), "OK");
	}

}
