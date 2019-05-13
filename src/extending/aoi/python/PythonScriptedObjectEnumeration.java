package extending.aoi.python;

import java.util.Enumeration;

import artofillusion.Scene;
import artofillusion.object.ObjectInfo;
import artofillusion.script.ScriptedObjectController;
import artofillusion.*;
import artofillusion.object.*;
import java.util.*;


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
