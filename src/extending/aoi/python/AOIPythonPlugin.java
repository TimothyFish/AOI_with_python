package extending.aoi.python;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import artofillusion.ArtOfIllusion;
import artofillusion.LayoutWindow;
import artofillusion.Plugin;
import artofillusion.UndoRecord;
import artofillusion.math.CoordinateSystem;
import artofillusion.object.ObjectInfo;
import artofillusion.script.ScriptedObject;
import artofillusion.ui.ComponentsDialog;
import artofillusion.ui.Translate;
import buoy.widget.BComboBox;
import buoy.widget.BMenu;
import buoy.widget.BMenuItem;
import buoy.widget.BSeparator;
import buoy.widget.BStandardDialog;
import buoy.widget.BTextField;
import buoy.widget.Widget;

public class AOIPythonPlugin  implements Plugin{
	private LayoutWindow layout;

	/**
	 * Constructor
	 */
	public AOIPythonPlugin() {
	}

	@Override
	public void processMessage(int message, Object[] args) {
		switch (message) {
		case Plugin.APPLICATION_STARTING:
			break;
		case Plugin.APPLICATION_STOPPING:
			break;
		case Plugin.SCENE_WINDOW_CREATED:
			layout = (LayoutWindow) args[0];

			BMenu toolsMenu = layout.getToolsMenu();
			// Add menu item to the Tools menu
			// Locate position after "Edit Script..."
			int posEditScript = 0; 
			for(int i = 0; i < toolsMenu.getChildCount(); i++){
				if(toolsMenu.getChild(i) instanceof BSeparator) continue;
				BMenuItem menuIter = null;
				menuIter = (BMenuItem) toolsMenu.getChild(i);
				if(menuIter.getText().equalsIgnoreCase("Edit Script...")){
					posEditScript = i;
					break;
				}
			}
			posEditScript++;

			BMenuItem menuItem1 = Translate.menuItem("Create Python Object...", this, "createPythonObjectMenuAction");
			toolsMenu.add(menuItem1, posEditScript);

			BMenuItem menuItem2 = Translate.menuItem("Edit Python...", this, "editPythonMenuAction");
			toolsMenu.add(menuItem2, posEditScript+1);
			
			break;
		}

	}

	@SuppressWarnings("unused")
	private void createPythonObjectMenuAction() {	
    // Prompt the user to select a name and, optionally, a predefined script.

    BTextField nameField = new BTextField(Translate.text("Script"));
    BComboBox scriptChoice = new BComboBox();
    scriptChoice.add(Translate.text("newScript"));
    String files[] = new File(ArtOfIllusion.OBJECT_SCRIPT_DIRECTORY).list();
    ArrayList<String> scriptNames = new ArrayList<String>();
    if (files != null)
      for (String file : files)
      {
        try
        {
          PythonScriptRunner.getLanguageForFilename(file);
          scriptChoice.add(file.substring(0, file.lastIndexOf(".")));
          scriptNames.add(file);
        }
        catch (IllegalArgumentException ex)
        {
          // This file isn't a known scripting language.
        }
      }
    ComponentsDialog dlg = new ComponentsDialog(layout, Translate.text("newScriptedObject"),
      new Widget [] {nameField, scriptChoice}, new String [] {Translate.text("Name"), Translate.text("Script")});
    if (!dlg.clickedOk())
      return;

    // If they are using a predefined script, load it.

    String scriptText = "";
    String language = PythonScriptRunner.LANGUAGES[0];
    if (scriptChoice.getSelectedIndex() > 0)
    {
      try
      {
        File f = new File(ArtOfIllusion.OBJECT_SCRIPT_DIRECTORY, scriptNames.get(scriptChoice.getSelectedIndex()-1));
        scriptText = ArtOfIllusion.loadFile(f);
        language = PythonScriptRunner.getLanguageForFilename(f.getName());
      }
      catch (IOException ex)
      {
        new BStandardDialog("", new String [] {Translate.text("errorReadingScript"), ex.getMessage() == null ? "" : ex.getMessage()}, BStandardDialog.ERROR).showMessageDialog(layout);
        return;
      }
    }
    ScriptedObject obj = new PythonScriptedObject(scriptText, language);
    ObjectInfo info = new ObjectInfo(obj, new CoordinateSystem(), nameField.getText());
    UndoRecord undo = new UndoRecord(layout, false);
    int sel[] = layout.getSelectedIndices();
    layout.addObject(info, undo);
    undo.addCommand(UndoRecord.SET_SCENE_SELECTION, new Object [] {sel});
    layout.setSelection(layout.getScene().getNumObjects()-1);
    layout.setUndoRecord(undo);
    layout.updateImage();
    layout.editObjectCommand();
	}

	@SuppressWarnings("unused")
	private void editPythonMenuAction() {
		new ExecutePythonWindow(layout);
	  
	}
}
