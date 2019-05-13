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

import artofillusion.script.ScriptException;

/** This interface represents a parsed Object script. */
public interface PythonObjectScript {

	public void execute(PythonScriptedObjectController pythonScriptedObjectController) throws ScriptException;

}
