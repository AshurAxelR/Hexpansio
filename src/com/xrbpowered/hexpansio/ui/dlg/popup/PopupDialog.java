package com.xrbpowered.hexpansio.ui.dlg.popup;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.ui.dlg.OverlayDialog;
import com.xrbpowered.zoomui.GraphAssist;

public abstract class PopupDialog extends OverlayDialog {

	protected String[] lines;

	public PopupDialog(float height, String title, String[] lines) {
		super(Hexpansio.instance.getBase(), 400, height>0 ? height : lines.length*15+120, title);
		this.lines = lines;
	}

	public PopupDialog(float height, String title, String message) {
		this(height, title, message==null ? null : message.split("\\n"));
	}

	public void onCancel() {
		dismiss();
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
