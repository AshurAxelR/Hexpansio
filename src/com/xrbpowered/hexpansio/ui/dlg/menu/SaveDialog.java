package com.xrbpowered.hexpansio.ui.dlg.menu;

import java.awt.Color;
import java.awt.GradientPaint;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.ClickButton;
import com.xrbpowered.hexpansio.ui.dlg.OverlayDialog;
import com.xrbpowered.hexpansio.ui.dlg.popup.ConfirmationDialog;
import com.xrbpowered.hexpansio.world.Save;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.std.UIListBox;
import com.xrbpowered.zoomui.std.UIListItem;
import com.xrbpowered.zoomui.std.text.UITextBox;

public class SaveDialog extends OverlayDialog {

	public final boolean save;
	public final Save current;
	
	private class FileListItem extends UIListItem {
		public FileListItem(UIListBox list, int index, Object object) {
			super(list, index, object);
			setSize(0, 30);
		}
		@Override
		public void paint(GraphAssist g) {
			Save s = (Save) object;
			boolean sel = (index==list.getSelectedIndex());
			if(sel) {
				g.setPaint(new GradientPaint(0, 0, Res.uiBgBright, 0, getHeight(), Res.uiButtonTop));
				g.fill(this);
				g.border(this, Res.uiBorderLight);
			}
			else if(hover) {
				g.fill(this, Res.uiBgMid);
				g.border(this, Res.uiBorderDark);
			}
			
			g.setColor(Color.WHITE);
			g.setFont(Res.fontBold);
			g.drawString(s.name, 10, getHeight()/2f, GraphAssist.LEFT, GraphAssist.CENTER);
		}
	}

	private UIListBox list;

	private final UITextBox filenameText;

	private final ClickButton acceptButton;
	private final ClickButton deleteButton;
	private final ClickButton closeButton;

	public static SaveDialog load() {
		return new SaveDialog(false, null);
	}
	
	public SaveDialog(final boolean save, final Save currentSave) {
		super(Hexpansio.instance.getBase(), 600, 400+(save ? 80 : 0), save ? "SAVE GAME" : "LOAD GAME");
		this.save = save;
		this.current = currentSave;
		
		if(save) {
			filenameText = new UITextBox(box);
			filenameText.setSize(box.getWidth()-20, 40);
			filenameText.setLocation(10, box.getHeight()-60-40);
		}
		else {
			filenameText = null;
		}
		
		ArrayList<Save> saveList = Save.allSaves(!save);
		
		list = new UIListBox(box, saveList.toArray(new Save[saveList.size()])) {
			@Override
			protected UIListItem createItem(int index, Object object) {
				return new FileListItem(this, index, object);
			}
			@Override
			public void onItemSelected(UIListItem item) {
				if(filenameText!=null) {
					getBase().resetFocus();
					Save s = (Save) item.object;
					filenameText.editor.setText(s==null ? "" : s.name);
					repaint();
				}
			}
		};
		list.setSize(350, 400-60-60);
		list.setLocation(10, 60);
		
		list.select(0);
		
		acceptButton = new ClickButton(box, save ? "SAVE" : "LOAD", 140) {
			@Override
			public boolean isEnabled() {
				return selectedSave()!=null;
			}
			@Override
			public void onClick() {
				onEnter();
			}
		};
		acceptButton.setLocation(box.getWidth()-acceptButton.getWidth()-10, box.getHeight()-acceptButton.getHeight()-10);

		closeButton = new ClickButton(box, "Back", 100) {
			@Override
			public void onClick() {
				dismiss();
			}
		};
		closeButton.setLocation(10, box.getHeight()-closeButton.getHeight()-10);
		
		if(!save) {
			deleteButton = new ClickButton(box, "Delete", 100) {
				@Override
				public boolean isEnabled() {
					return selectedSave()!=null;
				}
				@Override
				public void onClick() {
					final Save s = selectedSave();
					if(s==null)
						return;
					new ConfirmationDialog(0, "DELETE", "Delete the save file?", "DELETE", "CANCEL") {
						@Override
						public void onEnter() {
							dismiss();
							SaveDialog.this.dismiss(false);
							s.delete();
							new SaveDialog(save, currentSave).repaint();
						}
					}.repaint();
				}
			};
			deleteButton.setLocation(15+closeButton.getWidth(), closeButton.getY());
		}
		else {
			deleteButton = null;
		}
		
		if(save) {
			String name = (currentSave==null) ? "NewGame" : currentSave.name;
			boolean found = false;
			for(int i=0; i<list.getNumItems(); i++) {
				if(((Save)list.getItem(i).object).name.equalsIgnoreCase(name)) {
					list.select(i);
					found = true;
					break;
				}
			}
			if(!found) {
				filenameText.editor.setText(name);
				repaint();
			}
		}
	}
	
	private Save selectedSave() {
		return list.getSelectedItem()==null ? null : (Save) list.getSelectedItem().object;
	}

	@Override
	public void onEnter() {
		if(save) {
			getBase().resetFocus();
			String text = filenameText.editor.getText();
			String name = text.trim().replaceAll("[^A-Za-z0-9\\-\\_\\.\\s]", "").replaceAll("\\s+", " ");
			if(!name.isEmpty() && text.equals(name)) {
				final Save s = new Save(new File(Save.saveDirectory, name+".save"));
				if(s.exists() && (Hexpansio.currentSave.name==null || !s.name.equalsIgnoreCase(Hexpansio.currentSave.name))) {
					new ConfirmationDialog(0, "OVERWRITE", "Overwrite the existing save file?", "SAVE", "CANCEL") {
						@Override
						public void onEnter() {
							Hexpansio.instance.saveGame(s);
							dismiss();
							SaveDialog.this.dismiss(false);
						}
					}.repaint();
				}
				else {
					Hexpansio.instance.saveGame(s);
					dismiss(false);
				}
			}
			else {
				filenameText.editor.setText(name);
				repaint();
			}
		}
		else {
			Save s = selectedSave();
			if(s!=null) {
				Hexpansio.instance.loadGame(s);
				dismiss(false);
			}
		}
	}
	
	public void dismiss(boolean back) {
		super.dismiss();
		if(back) new GameMenu().repaint();
	}
	
	@Override
	public void dismiss() {
		dismiss(true);
	}
	
	@Override
	public boolean onMouseDown(float x, float y, Button button, int mods) {
		return true;
	}
	
	@Override
	protected void paintBoxContents(GraphAssist g) {
		int x = 20;
		int y = 50;
		g.setFont(Res.font);
		g.setColor(Color.WHITE);
		g.drawString("Saved games:", x, y);
		
		if(filenameText!=null) {
			g.drawString("Name:", 10, filenameText.getY()-10);
		}

		Save s = selectedSave();
		
		DateFormat df = new SimpleDateFormat("d MMM yyyy, HH:mm");
		
		if(s!=null) {
			x = (int)(list.getX()+list.getWidth()+20);
			y = (int)list.getY()+10;
			g.setFont(Res.font);
			g.setColor(Color.LIGHT_GRAY);
			g.drawString("Last played:", x, y);
			y += 15;
			g.setColor(Color.WHITE);
			g.drawString(df.format(new Date(s.file.lastModified())), x, y);
			
			// TODO save game info
		}
		
		super.paintBoxContents(g);
	}
}
