package com.xrbpowered.hexpansio.ui.dlg;

import com.xrbpowered.hexpansio.Hexpansio;

public class QuickExitDialog extends ConfirmationDialog {

	public QuickExitDialog() {
		super(0, "EXIT",
				"Exit to desktop?\n" + (Hexpansio.saveOnExit ? "The progress will be saved." : "All unsaved progress will be lost."),
				"EXIT", "STAY");
	}

	@Override
	public void onEnter() {
		if(Hexpansio.saveOnExit)
			Hexpansio.instance.saveGame();
		System.exit(0);
	}
}
