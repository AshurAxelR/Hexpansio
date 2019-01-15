package com.xrbpowered.hexpansio.ui.dlg;

import java.util.Random;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.ui.ClickButton;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.zoomui.std.text.UITextBox;

public class CityRenameDialog extends OverlayDialog {

	private static final Random random = new Random();

	public final City city;
	
	private final ClickButton acceptButton;
	private final ClickButton randomButton;
	private final ClickButton cancelButton;
	
	private final UITextBox nameText;

	public CityRenameDialog(final City city) {
		super(Hexpansio.instance.getBase(), 540, 160, "RENAME CITY");
		this.city = city;
		
		nameText = new UITextBox(box);
		nameText.editor.setText(city.name);
		nameText.setSize(box.getWidth()-20, 40);
		nameText.setLocation(10, 50);
		
		acceptButton = new ClickButton(box, "ACCEPT", 100) {
			@Override
			public void onClick() {
				onEnter();
			}
		};
		acceptButton.setLocation(box.getWidth()-acceptButton.getWidth()-10, box.getHeight()-acceptButton.getHeight()-10);

		cancelButton = new ClickButton(box, "Cancel", 100) {
			@Override
			public void onClick() {
				dismiss();
			}
		};
		cancelButton.setLocation(10, acceptButton.getY());
		
		randomButton = new ClickButton(box, "Randomise", 100) {
			@Override
			public void onClick() {
				getBase().resetFocus();
				nameText.editor.setText(City.generateName(city.world, random));
				repaint();
			}
		};
		randomButton.setLocation(15+cancelButton.getWidth(), acceptButton.getY());
	}
	
	@Override
	public void onEnter() {
		getBase().resetFocus();
		String text = nameText.editor.getText();
		StringBuilder sb = new StringBuilder();
		int n = text.length();
		int m = 0;
		char space = '\0';
		boolean start = true;
		for(int i=0; i<n && m<40; i++) {
			char ch = text.charAt(i);
			if(Character.isLetterOrDigit(ch)) {
				if(start || space!='\0') {
					if(!start && space!='\0') {
						sb.append(space);
						m++;
					}
					if(space=='\'')
						sb.append(ch);
					else
						sb.append(Character.toUpperCase(ch));
					space = '\0';
					m++;
					start = false;
				}
				else {
					sb.append(ch);
					m++;
				}
			}
			else if(Character.isWhitespace(ch)) {
				if(space=='\0')
					space = ' ';
			}
			else if(ch=='\'')
				space = '\'';
			else
				space = '-';
		}

		String name = sb.toString();
		if(!name.isEmpty() && name.equals(text)) {
			city.rename(name);
			dismiss();
		}
		else {
			nameText.editor.setText(name);
			repaint();
		}
	}

}
