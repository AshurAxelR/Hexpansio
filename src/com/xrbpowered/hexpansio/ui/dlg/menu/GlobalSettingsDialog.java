package com.xrbpowered.hexpansio.ui.dlg.menu;

import com.xrbpowered.hexpansio.GlobalSettings;
import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.CheckBox;
import com.xrbpowered.hexpansio.ui.ClickButton;
import com.xrbpowered.hexpansio.ui.OptionBox;
import com.xrbpowered.hexpansio.ui.dlg.OverlayDialog;
import com.xrbpowered.zoomui.GraphAssist;

public class GlobalSettingsDialog extends OverlayDialog {

	private GlobalSettings settings;
	
	private final ClickButton applyButton;
	private final ClickButton resetButton;
	private final ClickButton cancelButton;

	private final CheckBox windowedCheckBox;
	private final CheckBox hotkeyTooltipsCheckBox;
	private final CheckBox tutorialCheckBox;
	private final OptionBox uiScaleOption;

	private final CheckBox autosaveCheckBox;
	private final CheckBox saveOnExitCheckBox;

	private final CheckBox confirmHurryCheckBox;
	private final CheckBox openMessageLogCheckBox;
	private final CheckBox warnNextTurnCheckBox;
	private final CheckBox warnUnhappyCheckBox;
	private final CheckBox warnNoBuildingCheckBox;
	
	public GlobalSettingsDialog() {
		super(Hexpansio.instance.getBase(), 460, 470, "SETTINGS");
		
		float y = 60f;
		float cx = box.getWidth() - OptionBox.defaultWidth - 30;

		uiScaleOption = new OptionBox(box, "User interface scaling:", new int[] {0, 100, 125, 150, 175, 200}, "%d%%") {
			@Override
			protected void selectOption(int value) {
				settings.uiScaling = value;
			}
			@Override
			protected String formatOption(int value) {
				return value==0 ? "system" : super.formatOption(value);
			}
		};
		uiScaleOption.setLocation(cx, y);
		y += uiScaleOption.getHeight()+5;

		windowedCheckBox = new CheckBox(box, "Windowed") {
			@Override
			public boolean isSelected() {
				return settings.windowed;
			}
			@Override
			public void onClick() {
				settings.windowed = !settings.windowed;
				repaint();
			}
		};
		windowedCheckBox.setLocation(10, y);
		y += windowedCheckBox.getHeight()+5;

		hotkeyTooltipsCheckBox = new CheckBox(box, "Show hotkeys in tooltips") {
			@Override
			public boolean isSelected() {
				return settings.hotkeyTooltips;
			}
			@Override
			public void onClick() {
				settings.hotkeyTooltips = !settings.hotkeyTooltips;
				repaint();
			}
		};
		hotkeyTooltipsCheckBox.setLocation(10, y);
		y += hotkeyTooltipsCheckBox.getHeight()+5;

		tutorialCheckBox = new CheckBox(box, "Show welcome tips") {
			@Override
			public boolean isSelected() {
				return settings.tutorial;
			}
			@Override
			public void onClick() {
				settings.tutorial = !settings.tutorial;
				repaint();
			}
		};
		tutorialCheckBox.setLocation(10, y);
		y += tutorialCheckBox.getHeight()+30;

		autosaveCheckBox = new CheckBox(box, "Autosave every turn") {
			@Override
			public boolean isSelected() {
				return settings.autosave;
			}
			@Override
			public void onClick() {
				settings.autosave = !settings.autosave;
				repaint();
			}
		};
		autosaveCheckBox.setLocation(10, y);
		y += autosaveCheckBox.getHeight()+5;

		saveOnExitCheckBox = new CheckBox(box, "Autosave on exit") {
			@Override
			public boolean isSelected() {
				return settings.saveOnExit;
			}
			@Override
			public void onClick() {
				settings.saveOnExit = !settings.saveOnExit;
				repaint();
			}
		};
		saveOnExitCheckBox.setLocation(10, y);
		y += saveOnExitCheckBox.getHeight()+30;

		confirmHurryCheckBox = new CheckBox(box, "Confirm hurry production") {
			@Override
			public boolean isSelected() {
				return settings.confirmHurry;
			}
			@Override
			public void onClick() {
				settings.confirmHurry = !settings.confirmHurry;
				repaint();
			}
		};
		confirmHurryCheckBox.setLocation(10, y);
		y += confirmHurryCheckBox.getHeight()+5;

		openMessageLogCheckBox = new CheckBox(box, "Open message log on new turn") {
			@Override
			public boolean isSelected() {
				return settings.openMessageLog;
			}
			@Override
			public void onClick() {
				settings.openMessageLog = !settings.openMessageLog;
				repaint();
			}
		};
		openMessageLogCheckBox.setLocation(10, y);
		y += openMessageLogCheckBox.getHeight()+5;

		warnNextTurnCheckBox = new CheckBox(box, "Confirm ending turn with unresolved problems") {
			@Override
			public boolean isSelected() {
				return settings.warnNextTurn;
			}
			@Override
			public void onClick() {
				settings.warnNextTurn = !settings.warnNextTurn;
				repaint();
			}
		};
		warnNextTurnCheckBox.setLocation(10, y);
		y += warnNextTurnCheckBox.getHeight()+5;

		warnUnhappyCheckBox = new CheckBox(box, "Notify about unhappy cities") {
			@Override
			public boolean isSelected() {
				return settings.warnUnhappy;
			}
			@Override
			public void onClick() {
				settings.warnUnhappy = !settings.warnUnhappy;
				repaint();
			}
		};
		warnUnhappyCheckBox.setLocation(10, y);
		y += warnUnhappyCheckBox.getHeight()+5;

		warnNoBuildingCheckBox = new CheckBox(box, "Always notify about building goods") {
			@Override
			public boolean isSelected() {
				return settings.warnNoBuilding;
			}
			@Override
			public void onClick() {
				settings.warnNoBuilding = !settings.warnNoBuilding;
				repaint();
			}
		};
		warnNoBuildingCheckBox.setLocation(10, y);
		y += warnNoBuildingCheckBox.getHeight()+5;


		applyButton = new ClickButton(box, "APPLY", 100) {
			@Override
			public void onClick() {
				onEnter();
			}
		};
		applyButton.setLocation(box.getWidth()-applyButton.getWidth()-10, box.getHeight()-applyButton.getHeight()-10);

		cancelButton = new ClickButton(box, "Cancel", 100) {
			@Override
			public void onClick() {
				dismiss();
			}
		};
		cancelButton.setLocation(10, applyButton.getY());
		
		resetButton = new ClickButton(box, "Default", 100) {
			@Override
			public void onClick() {
				uiFromSettings(new GlobalSettings());
				repaint();
			}
		};
		resetButton.setLocation(15+cancelButton.getWidth(), applyButton.getY());
		
		uiFromSettings(Hexpansio.settings.copy());
	}

	@Override
	public void onEnter() {
		GlobalSettings prev = Hexpansio.settings;
		Hexpansio.settings = settings;
		GlobalSettings.save(settings);
		if(!settings.apply(prev))
			dismiss();
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
		new GameMenu().repaint();
	}
	
	private void uiFromSettings(GlobalSettings s) {
		this.settings = s;
		settings.uiScaling = uiScaleOption.findOption(settings.uiScaling);
	}
	
	@Override
	public boolean onMouseDown(float x, float y, Button button, int mods) {
		return true;
	}
	
	@Override
	protected void paintBoxContents(GraphAssist g) {
		g.resetStroke();
		float y = autosaveCheckBox.getY()-15;
		g.line(0, y, box.getWidth(), y, Res.uiBorderDark);
		y = saveOnExitCheckBox.getY()+saveOnExitCheckBox.getHeight()+15;
		g.line(0, y, box.getWidth(), y, Res.uiBorderDark);
		
		super.paintBoxContents(g);
	}

}
