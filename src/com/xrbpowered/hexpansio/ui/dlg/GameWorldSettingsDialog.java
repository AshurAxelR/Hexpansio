package com.xrbpowered.hexpansio.ui.dlg;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.CheckBox;
import com.xrbpowered.hexpansio.ui.ClickButton;
import com.xrbpowered.hexpansio.ui.OptionBox;
import com.xrbpowered.hexpansio.world.NameGen;
import com.xrbpowered.hexpansio.world.WorldSettings;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.std.text.UITextBox;

public class GameWorldSettingsDialog extends OverlayDialog {

	public static WorldSettings lastUsed = new WorldSettings();
	
	private static Random random = new Random();
	
	private WorldSettings settings;
	
	private final ClickButton createButton;
	private final ClickButton resetButton;
	private final ClickButton cancelButton;
	
	private final CheckBox seedCheckBox;
	private final UITextBox seedText;
	private final ClickButton randomButton;

	private final OptionBox baseHappinessOption;

	private final CheckBox voidCheckBox;
	private final OptionBox voidStartTurnOption;
	private final OptionBox voidStartSourcesOption;
	private final OptionBox voidDistanceOption;
	private final OptionBox voidSpreadSpeedOption;

	public GameWorldSettingsDialog() {
		super(Hexpansio.instance.getBase(), 540, 550, "WORLD SETTINGS");
		
		float y = 60f;
		float cx = box.getWidth() - OptionBox.defaultWidth - 30;
		
		seedCheckBox = new CheckBox(box, "Set world seed") {
			@Override
			public boolean isSelected() {
				return settings.customSeed;
			}
			@Override
			public void onClick() {
				settings.customSeed = !settings.customSeed;
				updateUI();
				repaint();
			}
		};
		seedCheckBox.setLocation(10, y);
		y += seedCheckBox.getHeight()+5;
		
		seedText = new UITextBox(box);
		seedText.setSize(box.getWidth()-20, 40);
		seedText.setLocation(10, y);
		y += seedText.getHeight()+5;
		
		randomButton = new ClickButton(box, "Randomise", 100) {
			@Override
			public void onClick() {
				getBase().resetFocus();
				settings.seedString = randomSeedString();
				seedText.editor.setText(settings.seedString);
				repaint();
			}
		};
		randomButton.setLocation(box.getWidth()-randomButton.getWidth()-10, y);
		y += randomButton.getHeight()+30;

		baseHappinessOption = new OptionBox(box, "Initial base happiness:", new int[] {1, 3, 5, 7}, "%+d") {
			@Override
			protected void selectOption(int value) {
				settings.initialBaseHappiness = value;
			}
		};
		baseHappinessOption.setLocation(cx, y);
		y += baseHappinessOption.getHeight()+30;
		
		voidCheckBox = new CheckBox(box, "The Void") {
			@Override
			public boolean isSelected() {
				return settings.voidEnabled;
			}
			@Override
			public void onClick() {
				settings.voidEnabled = !settings.voidEnabled;
				updateUI();
				repaint();
			}
		};
		voidCheckBox.setLocation(10, y);
		y += voidCheckBox.getHeight()+5;

		voidStartTurnOption = new OptionBox(box, "Void start turn:", new int[] {1, 20, 50, 100}) {
			@Override
			protected void selectOption(int value) {
				settings.voidStartTurn = value;
			}
		};
		voidStartTurnOption.setLocation(cx, y);
		y += voidStartTurnOption.getHeight()+5;

		voidStartSourcesOption = new OptionBox(box, "Number of void sources:", new int[] {1, 2, 3, 4}) {
			@Override
			protected void selectOption(int value) {
				settings.voidStartSources = value;
			}
		};
		voidStartSourcesOption.setLocation(cx, y);
		y += voidStartSourcesOption.getHeight()+5;

		voidDistanceOption = new OptionBox(box, "Distance from cities:", new int[] {8, 16, 24, 32})  {
			@Override
			protected void selectOption(int value) {
				settings.setVoidDistance(value);
			}
			@Override
			protected String formatOption(int value) {
				return String.format("%d-%d", value, value+8);
			}
		};
		voidDistanceOption.setLocation(cx, y);
		y += voidDistanceOption.getHeight()+5;

		voidSpreadSpeedOption = new OptionBox(box, "Spread speed:", new int[] {1, 2, 4, 8}, "x%d") {
			@Override
			protected void selectOption(int value) {
				settings.voidSpreadSpeed = value;
			}
		};
		voidSpreadSpeedOption.setLocation(cx, y);
		y += voidSpreadSpeedOption.getHeight()+5;

		createButton = new ClickButton(box, "CREATE", 100) {
			@Override
			public void onClick() {
				onEnter();
			}
		};
		createButton.setLocation(box.getWidth()-createButton.getWidth()-10, box.getHeight()-createButton.getHeight()-10);

		cancelButton = new ClickButton(box, "Back", 100) {
			@Override
			public void onClick() {
				dismiss();
			}
		};
		cancelButton.setLocation(10, createButton.getY());
		
		resetButton = new ClickButton(box, "Reset", 100) {
			@Override
			public void onClick() {
				uiFromSettings(new WorldSettings());
				repaint();
			}
		};
		resetButton.setLocation(15+cancelButton.getWidth(), createButton.getY());
		
		uiFromSettings(lastUsed.copy());
	}
	
	private String randomSeedString() {
		return NameGen.generate(random, 2, 3)+" "+NameGen.generate(random, 2, 3);
	}
	
	@Override
	public void onEnter() {
		// TODO create
		lastUsed = settings;
		Hexpansio.instance.newGame(settings);
		dismiss(false);
	}
	
	public void dismiss(boolean back) {
		super.dismiss();
		if(back) new GameMenu().repaint();
	}
	
	@Override
	public void dismiss() {
		dismiss(true);
	}
	
	private void uiFromSettings(WorldSettings w) {
		this.settings = w;
		if(settings.seedString==null || settings.seedString.isEmpty())
			settings.seedString = randomSeedString();
		seedText.editor.setText(settings.seedString);
		
		settings.initialBaseHappiness = baseHappinessOption.findOption(settings.initialBaseHappiness);
		settings.voidStartTurn = voidStartTurnOption.findOption(settings.voidStartTurn);
		settings.voidStartSources = voidStartSourcesOption.findOption(settings.voidStartSources);
		settings.setVoidDistance(voidDistanceOption.findOption(settings.voidMinDistance));
		settings.voidSpreadSpeed = voidSpreadSpeedOption.findOption(settings.voidSpreadSpeed);
		
		updateUI();
	}

	private boolean updateText() {
		String text = seedText.editor.getText();
		if(!text.equals(settings.seedString)) {
			String s = text.trim();
			if(s.length()>32)
				s = s.substring(0, 32);
			if(s.isEmpty())
				s = randomSeedString();
			settings.seedString = s;
			if(text.equals(s))
				return true;
			else {
				getBase().resetFocus();
				seedText.editor.setText(settings.seedString);
				return false;
			}
		}
		return true;
	}
	
	private void updateUI() {
		updateText();
		seedText.setVisible(settings.customSeed);
		randomButton.setVisible(settings.customSeed);
		voidStartTurnOption.setVisible(settings.voidEnabled);
		voidStartSourcesOption.setVisible(settings.voidEnabled);
		voidDistanceOption.setVisible(settings.voidEnabled);
		voidSpreadSpeedOption.setVisible(settings.voidEnabled);
	}

	@Override
	public boolean onMouseDown(float x, float y, Button button, int mods) {
		return true;
	}
	
	@Override
	protected void paintBoxContents(GraphAssist g) {
		g.resetStroke();
		float y = baseHappinessOption.getY()-15;
		g.line(0, y, box.getWidth(), y, Res.uiBorderDark);
		y = baseHappinessOption.getY()+baseHappinessOption.getHeight()+15;
		g.line(0, y, box.getWidth(), y, Res.uiBorderDark);
		
		g.setColor(Color.LIGHT_GRAY);
		g.setFont(Res.font);
		g.drawString("Difficulty rating:", box.getWidth()-10, createButton.getY()-45, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		g.setColor(Color.WHITE);
		g.setFont(Res.fontLarge);
		g.drawString(String.format("%d%%", settings.getDifficultyRating()), box.getWidth()-10, createButton.getY()-20, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		
		super.paintBoxContents(g);
	}
}
