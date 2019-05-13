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

import java.util.Enumeration;

import artofillusion.Scene;
import artofillusion.object.ObjectInfo;


public class PythonScriptedObjectEnumeration implements Enumeration<ObjectInfo> {
  private ObjectInfo next;
  private boolean complete;

  PythonScriptedObjectEnumeration(ObjectInfo obj, boolean interactive, Scene sc)
  {
    new PythonScriptedObjectController(obj, this, interactive, sc);
  }

  /** This is called by the ScriptedObjectController every time a new object is created. */

  public synchronized void addObject(ObjectInfo info)
  {
    while (next != null)
      {
        try
          {
            wait();
          }
        catch (InterruptedException ex)
          {
          }
      }
    next = info;
    notify();
  }

  /** This is called by the ScriptedObjectController once execution is complete. */

  public synchronized void executionComplete()
  {
    complete = true;
    notify();
  }

  /** Determine whether there are more objects to enumerate. */

  @Override
  public synchronized boolean hasMoreElements()
  {
    while (next == null && !complete)
      {
        try
          {
            wait();
          }
        catch (InterruptedException ex)
          {
          }
      }
    return (next != null);
  }

  /** Get the next ObjectInfo, or null if there are no more. */

  @Override
  public synchronized ObjectInfo nextElement()
  {
    while (next == null && !complete)
      {
        try
          {
            wait();
          }
        catch (InterruptedException ex)
          {
          }
      }
    ObjectInfo nextElem = next;
    next = null;
    notify();
    return nextElem;
  }
}
