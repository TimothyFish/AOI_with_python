package extending.aoi.python;

import artofillusion.*;
import artofillusion.animation.Track;
import artofillusion.math.CoordinateSystem;
import artofillusion.object.*;

/** This class mediates interactions between an ObjectScript and the rest of
    the program. */

public class PythonScriptedObjectController
{
  private ObjectInfo info;
  private PythonScriptedObject object;
  private PythonScriptedObjectEnumeration enumeration;
  private Scene scene;
  private boolean preview;

  /** Create a new PythonScriptedObjectController and execute its script. */

  PythonScriptedObjectController(ObjectInfo obj, PythonScriptedObjectEnumeration objectEnum, boolean interactive, Scene sc)
  {
    info = obj;
    Object3D innerObject = obj.getObject();
    while (innerObject instanceof ObjectWrapper)
      innerObject = ((ObjectWrapper) innerObject).getWrappedObject();
    object = (PythonScriptedObject) innerObject;
    enumeration = objectEnum;
    preview = interactive;
    scene = sc;
    object.setUsesTime(false);
    object.setUsesCoords(false);
    new Thread() {
      @Override
      public void run()
      {
        try
          {
        	if(object.getLanguage() == "Python") {
        		PythonObjectScript script = object.getPythonObjectScript();
        		script.execute(PythonScriptedObjectController.this);
        	}
        	enumeration.executionComplete();
          }
        catch (Exception ex)
          {
            enumeration.executionComplete();
            PythonScriptRunner.displayError(object.getLanguage(), ex);
          }
      }
    }.start();
  }
  
  /** Get the coordinate system which defines the scripted object's position in the scene. */

  public final CoordinateSystem getCoordinates()
  {
    object.setUsesCoords(true);
    return info.getCoords();
  }

  /** Get the current time. */

  public final double getTime()
  {
    object.setUsesTime(true);
    return scene.getTime();
  }

  /** Get the scene this object is part of. */

  public final Scene getScene()
  {
    object.setUsesTime(true);
    return scene;
  }

  /** Determine whether the script is currently being executed to create an interactive preview. */

  public final boolean isPreview()
  {
    return preview;
  }

  /** Get the value of a parameter. */

  public final double getParameter(String name) throws IllegalArgumentException
  {
    for (int i = object.getNumParameters()-1; i >= 0; i--)
      if (object.getParameterName(i).equals(name))
        return object.getParameterValue(i);
    throw new IllegalArgumentException("Unknown parameter '"+name+"'");
  }

  /** Add an object to the scripted object. */

  public final void addObject(ObjectInfo info)
  {
    info.tracks = new Track [0];
    if (info.getObject().canSetTexture() && info.getObject().getTextureMapping() == null)
      info.setTexture(object.getTexture(), object.getTextureMapping());
    if (info.getObject().canSetMaterial() && info.getObject().getMaterialMapping() == null)
      info.setMaterial(object.getMaterial(), object.getMaterialMapping());
    enumeration.addObject(info);
  }

  /** Add an object to the scripted object. */

  public final void addObject(Object3D obj, CoordinateSystem coords)
  {
    addObject(new ObjectInfo(obj, coords, ""));
  }
}