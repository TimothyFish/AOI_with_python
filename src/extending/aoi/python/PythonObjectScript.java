package extending.aoi.python;

import artofillusion.script.ScriptException;
import artofillusion.script.ScriptedObjectController;

/** This interface represents a parsed Object script. */
public interface PythonObjectScript {

	public void execute(PythonScriptedObjectController pythonScriptedObjectController) throws ScriptException;

}
