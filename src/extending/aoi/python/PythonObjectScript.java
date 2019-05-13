package extending.aoi.python;

import artofillusion.script.ScriptException;

/** This interface represents a parsed Object script. */
public interface PythonObjectScript {

	public void execute(PythonScriptedObjectController pythonScriptedObjectController) throws ScriptException;

}
