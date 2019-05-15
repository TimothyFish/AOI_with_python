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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;

import org.python.core.PyCode;
import org.python.util.PythonInterpreter;

import artofillusion.LayoutWindow;
import artofillusion.script.ObjectScript;
import artofillusion.script.ScriptEngine;
import artofillusion.script.ScriptException;
import artofillusion.script.ScriptOutputWindow;
import artofillusion.script.ScriptedObjectController;
import artofillusion.script.ToolScript;
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
/**
 * This ScriptEngine implements the Python scripting language.
 */
public class PythonScriptEngine implements ScriptEngine {
  private ScriptOutputWindow outStream;
  private PythonInterpreter interp;
  private StringBuilder imports;
  private int numImports = 0;
	@SuppressWarnings("unused")
	private PrintWriter thePrintWriter;
	
  public PythonScriptEngine(ClassLoader parent)
  {
  	outStream = new ScriptOutputWindow();
    interp = new PythonInterpreter();
    imports = new StringBuilder();
  }

	@Override
	public String getName() {
		return "Python";
	}

	@Override
	public String getFilenameExtension() {
		return "py";
	}

	@Override
	public void setOutput(PrintStream out) {
		thePrintWriter = new PrintWriter(out, true);
	}

	@Override
	public void addImport(String packageOrClass) throws Exception 
	{  
		  String replaceStr = " import *";
		  if(!(packageOrClass.indexOf(".*")>=0))
		  {
		  	replaceStr = " import " +
		        packageOrClass.substring(packageOrClass.lastIndexOf(".")+1);
		  }
		  String localPackage = new String(packageOrClass.replaceAll("\\.(.\\w*)$", replaceStr));
      imports.append("from ").append(localPackage).append("\n");
      
      numImports++;
	}

	@Override
	public void executeScript(String script, Map<String, Object> variables) throws ScriptException 
	{
    try
    {
      for (Map.Entry<String, Object> entry : variables.entrySet())
		  {
        interp.set(entry.getKey(), entry.getValue());
		  }
      interp.exec(imports.toString()+script);
    }
    catch (Exception e)
    {
      throw new ScriptException(e.getMessage(), -1);
    }
	}

	@Override
	public ToolScript createToolScript(String script) throws ScriptException {
    try
    {
    	String fullScript = imports.toString()+script;
      return new CompiledToolScript(interp.compile(fullScript));
    }
    catch (Exception e)
    {
      throw new ScriptException(e.getMessage(), -1);
    }
	}

	@Override
	public ObjectScript createObjectScript(String script) throws ScriptException {
    try
    {
    	String fullScript = imports.toString()+script;
      return (ObjectScript)new CompiledObjectScript(interp.compile(fullScript));
    }
    catch (Exception e)
    {
      throw new ScriptException(e.getMessage(), -1, e);
    }
	}
	
  /** Inner class used to represent a compiled ToolScript. */

  private class CompiledToolScript implements ToolScript
  {
    private PyCode script;

    public CompiledToolScript(PyCode script)
    {
      this.script = script;
      interp.set("out", outStream);
    }

    @Override
    public void execute(LayoutWindow window) throws ScriptException
    {
      interp.set("window", window);
      try
      {
        interp.exec(script);
      }
      catch (Exception ex)
      {
        int line = -1;
        for (StackTraceElement element : ex.getStackTrace())
        {
          if (element.getClassName().equals(script.getClass().getName())) {
            line = element.getLineNumber()-numImports;
            break;
          }
        }
        throw new ScriptException(ex.getMessage(), line, ex);
      }
    }
  }

  /** Inner class used to represent a compiled ObjectScript. */

  private class CompiledObjectScript implements PythonObjectScript, ObjectScript
  {
    private PyCode script;

    public CompiledObjectScript(PyCode script)
    {
      this.script = script;
      interp.set("out", outStream);
    }

    @Override
    public void execute(PythonScriptedObjectController controller) throws ScriptException
    {
    	interp.set("self", controller);
    	interp.exec(script);
    }

		@Override
		public void execute(ScriptedObjectController controller) throws ScriptException {
		}
  }

}
