/**
    AOI with Python Plugin
    A plugin that didn't make it into the book 
    "Extending Art of Illusion: Scripting for 3D Artists"
    Copyright (C) 2019  Timothy Fish

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
package extending.aoi.python;

import buoy.widget.*;
import buoy.event.*;

import java.awt.*;

/**
 * This class presents a user interface for editing a script.
 */

public class PythonScriptEditor extends BTextArea
{
  /**
   * Create a ScriptEditor.
   *
   * @param text     the text of the script to edit
   */

  public PythonScriptEditor(String text)
  {
    super(text, 25, 80);
    setFont(new Font("Monospaced", Font.PLAIN, 11));
    setTabSize(2);
    addEventLink(KeyTypedEvent.class, this, "keyTyped");
  }

  /**
   * Create a standard WidgetContainer for this editor to go in, which provides scroll bars
   * and draws a border around it.
   */

  public WidgetContainer createContainer()
  {
    BScrollPane sp = new BScrollPane(this, BScrollPane.SCROLLBAR_ALWAYS, BScrollPane.SCROLLBAR_ALWAYS);
    return BOutline.createBevelBorder(sp, false);
  }

  /**
   * Provide auto-indent behavior when the user presses Return.
   */

  @SuppressWarnings("unused")
	private void keyTyped(KeyTypedEvent e)
  {
    if (e.getKeyChar() != '\r' && e.getKeyChar() != '\n')
      return;

    // The user has just pressed Return.  Find any white space characters
    // at the beginning of the previous line, and insert them before this line
    // as well.

    String text = getText();
    int pos = getCaretPosition()-1;
    int endWhiteSpace = pos;
    int startLine = pos;
    boolean extraIndent = (startLine > 0 && text.charAt(startLine-1) == '{');
    while (startLine > 0)
    {
      char c = text.charAt(startLine-1);
      if (c == '\r' || c == '\n')
        break;
      startLine--;
      if (!Character.isWhitespace(c))
        endWhiteSpace = startLine;
    }
    String insert = text.substring(startLine, endWhiteSpace);
    if (extraIndent)
      insert = insert+"\t";
    insert(insert, pos+1);
  }
}