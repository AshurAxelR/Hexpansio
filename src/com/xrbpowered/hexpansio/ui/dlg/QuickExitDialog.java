package com.xrbpowered.hexpansio.ui.dlg;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.ui.dlg.popup.ConfirmationDialog;

public class QuickExitDialog extends ConfirmationDialog {

	public QuickExitDialog() {
		super(0, "EXIT",
				Hexpansio.getWorld()==null ? "Exit to desktop?" :
					"Exit to desktop?\n" + (Hexpansio.settings.saveOnExit ? "The progress will be saved." : "All unsaved progress will be lost."),
				"EXIT", "STAY");
	}

	@Override
	public void onEnter() {
		if(Hexpansio.settings.saveOnExit)
			Hexpansio.instance.autosave();
		System.exit(0);
	}
}
