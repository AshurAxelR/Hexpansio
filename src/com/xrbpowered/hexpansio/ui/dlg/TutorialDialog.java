package com.xrbpowered.hexpansio.ui.dlg;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.ui.ClickButton;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class TutorialDialog extends PinDialog {

	public static final String[] steps = {
		"Click or press Q, E, A, S, D\nto select action modes.",
		"LMB performs an action\nRMB pans the map."
	};
	
	private final ClickButton nextButton;
	
	public int step; 
	
	public TutorialDialog(UIContainer parent, int step) {
		super(parent, 250, 130, "WELCOME");
		this.step = step;
		
		nextButton = new ClickButton(this, null, 100) {
			@Override
			public boolean isHot() {
				return true;
			}
			@Override
			protected void paintLabel(GraphAssist g, boolean enabled) {
				paintLabel(g, enabled, TutorialDialog.this.step==steps.length-1 ? "DONE" : "Next");
			}
			@Override
			public void onClick() {
				next();
			}
		};
		nextButton.setLocation(getWidth()/2-nextButton.getWidth()/2, getHeight()-nextButton.getHeight()-10);
	}
	
	public void next() {
		if(step==steps.length-1)
			getParent().removeChild(this);
		else
			step++;
		repaint();
	}
	
	@Override
	public void layout() {
		setLocation(
				Hexpansio.instance.getWidth()/2f-getWidth()/2f,
				Hexpansio.instance.bottom.getY()-getHeight()-20);
		super.layout();
	}

	@Override
	protected void paintContents(GraphAssist g) {
		int y = 60;
		for(String s : steps[step].split("\\n")) {
			g.drawString(s, getWidth()/2f, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
			y += 15;
		}
		super.paintContents(g);
	}
	
	public static void startTutorial(UIContainer parent) {
		new TutorialDialog(parent, 0);
	}
	
}
