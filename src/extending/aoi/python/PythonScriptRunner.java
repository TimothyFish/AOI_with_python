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
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import artofillusion.PluginRegistry;
import artofillusion.script.BeanshellScriptEngine;
import artofillusion.script.GroovyScriptEngine;
import artofillusion.script.ObjectScript;
import artofillusion.script.ScriptEngine;
import artofillusion.script.ScriptException;
import artofillusion.script.ScriptOutputWindow;
import artofillusion.script.ScriptRunner;
import artofillusion.script.ToolScript;
import artofillusion.util.SearchlistClassLoader;
import buoy.widget.BStandardDialog;

public class PythonScriptRunner 
{
		  public static final String LANGUAGES[] = {"Python", "Groovy", "BeanShell"};
		  private static SearchlistClassLoader parentLoader;
		  private static PrintStream output;
		  private static final HashMap<String, ScriptEngine> engines = new HashMap<String, ScriptEngine>();
		  private static final String IMPORTS[] = {"artofillusion.*", "artofillusion.image.*", "artofillusion.material.*",
		      "artofillusion.math.*", "artofillusion.object.*", "artofillusion.script.*", "artofillusion.texture.*",
		      "artofillusion.ui.*", "buoy.event.*", "buoy.widget.*"};
		  private static final String PYTHON_IMPORTS[] = {"artofillusion.ApplicationPreferences", 
		  		"artofillusion.ArtOfIllusion", "artofillusion.BevelExtrudeTool", "artofillusion.Camera", 
		  		"artofillusion.CameraFilterDialog", "artofillusion.CompoundImplicitEditorWindow", 
		  		"artofillusion.CreateCameraTool",	"artofillusion.CreateCubeTool", "artofillusion.CreateCurveTool", 
		  		"artofillusion.CreateCylinderTool", "artofillusion.CreateLightTool", "artofillusion.CreatePolygonTool", 
		  		"artofillusion.CreateSphereTool", "artofillusion.CreateSplineMeshTool", "artofillusion.CreateVertexTool", 
		  		"artofillusion.CSGDialog", "artofillusion.CSGEditorWindow", "artofillusion.CurveEditorWindow", 
		  		"artofillusion.CurveViewer", "artofillusion.ExternalObjectEditingWindow", "artofillusion.LayoutWindow", 
		  		"artofillusion.ListChangeListener", "artofillusion.MaterialMappingDialog", "artofillusion.MaterialPreviewer",
		  		"artofillusion.MeshEditorWindow", "artofillusion.MeshViewer", "artofillusion.ModellingApp", 
		  		"artofillusion.ModellingTool", "artofillusion.MoveObjectTool", "artofillusion.MoveScaleRotateMeshTool", 
		  		"artofillusion.MoveScaleRotateObjectTool", "artofillusion.MoveViewTool", "artofillusion.ObjectEditorWindow", 
		  		"artofillusion.ObjectPreviewCanvas", "artofillusion.ObjectPropertiesPanel", "artofillusion.ObjectSet", 
		  		"artofillusion.ObjectTextureDialog", "artofillusion.ObjectViewer", "artofillusion.Plugin", 
		  		"artofillusion.PluginRegistry", "artofillusion.PreferencesWindow", "artofillusion.Property", 
		  		"artofillusion.RecentFiles", "artofillusion.Renderer", "artofillusion.RenderingDialog", 
		  		"artofillusion.RenderingMesh", "artofillusion.RenderingTriangle", "artofillusion.RenderListener", 
		  		"artofillusion.RenderSetupDialog", "artofillusion.ReshapeMeshTool", "artofillusion.RotateMeshTool", 
		  		"artofillusion.RotateObjectTool", "artofillusion.RotateViewTool", "artofillusion.SafeFileOutputStream", 
		  		"artofillusion.ScaleMeshTool", "artofillusion.ScaleObjectTool", "artofillusion.Scene", 
		  		"artofillusion.SceneChangedEvent", "artofillusion.SceneViewer", "artofillusion.ScrollViewTool", 
		  		"artofillusion.SkewMeshTool", "artofillusion.SplineMeshEditorWindow", "artofillusion.SplineMeshViewer", 
		  		"artofillusion.TaperMeshTool", "artofillusion.TextureMappingDialog", "artofillusion.TextureParameter", 
		  		"artofillusion.TexturesAndMaterialsDialog", "artofillusion.ThickenMeshTool", "artofillusion.TitleWindow", 
		  		"artofillusion.TransformDialog", "artofillusion.TransformPointsDialog", "artofillusion.Translator", 
		  		"artofillusion.TriMeshBeveler", "artofillusion.TriMeshEditorWindow", 
		  		"artofillusion.TriMeshSelectionUtilities", "artofillusion.TriMeshSimplifier", "artofillusion.TriMeshViewer", 
		  		"artofillusion.TubeEditorWindow", "artofillusion.TubeViewer", "artofillusion.UndoRecord", 
		  		"artofillusion.UndoStack", "artofillusion.ViewerCanvas", "artofillusion.WireframeMesh", 
		  		"artofillusion.image.BMPEncoder", "artofillusion.image.ComplexImage", "artofillusion.image.ExternalImage", 
		  		"artofillusion.image.HDRDecoder", "artofillusion.image.HDREncoder", "artofillusion.image.HDRImage", 
		  		"artofillusion.image.ImageAverager", "artofillusion.image.ImageDetailsDialog", 
		  		"artofillusion.image.ImageMap", "artofillusion.image.ImageOrColor", "artofillusion.image.ImageOrValue", 
		  		"artofillusion.image.ImageSaver", "artofillusion.image.ImagesDialog", "artofillusion.image.MIPMappedImage", 
		  		"artofillusion.image.SVGImage", "artofillusion.image.TIFFEncoder", "artofillusion.image.filter.BlurFilter", 
		  		"artofillusion.image.filter.BrightnessFilter", "artofillusion.image.filter.DepthOfFieldFilter", 
		  		"artofillusion.image.filter.ExposureFilter", "artofillusion.image.filter.GlowFilter", 
		  		"artofillusion.image.filter.ImageFilter", "artofillusion.image.filter.NoiseReductionFilter", 
		  		"artofillusion.image.filter.OutlineFilter", "artofillusion.image.filter.SaturationFilter", 
		  		"artofillusion.image.filter.TintFilter", "artofillusion.material.LinearMaterialMapping", 
		  		"artofillusion.material.Material", "artofillusion.material.Material3D", 
		  		"artofillusion.material.MaterialMapping", "artofillusion.material.MaterialSpec", 
		  		"artofillusion.material.ProceduralMaterial3D", "artofillusion.material.UniformMaterial", 
		  		"artofillusion.material.UniformMaterialMapping", "artofillusion.math.BoundingBox", 
		  		"artofillusion.math.Cells", "artofillusion.math.CoordinateSystem", "artofillusion.math.FastMath", 
		  		"artofillusion.math.FastRandom", "artofillusion.math.Mat4", "artofillusion.math.Noise", 
		  		"artofillusion.math.PerlinNoise", "artofillusion.math.RGBColor", "artofillusion.math.SimplexNoise", 
		  		"artofillusion.math.SVD", "artofillusion.math.TriangleMath", "artofillusion.math.Vec2", 
		  		"artofillusion.math.Vec3", "artofillusion.object.CompoundImplicitObject", "artofillusion.object.CSGModeller", 
		  		"artofillusion.object.CSGObject", "artofillusion.object.Cube", "artofillusion.object.Curve", 
		  		"artofillusion.object.Cylinder", "artofillusion.object.DirectionalLight", 
		  		"artofillusion.object.ExternalObject", "artofillusion.object.FacetedMesh", 
		  		"artofillusion.object.ImplicitObject", "artofillusion.object.ImplicitSphere", "artofillusion.object.Light", 
		  		"artofillusion.object.Mesh", "artofillusion.object.MeshVertex", "artofillusion.object.NullObject", 
		  		"artofillusion.object.Object3D", "artofillusion.object.ObjectCollection", "artofillusion.object.ObjectInfo", 
		  		"artofillusion.object.ObjectWrapper", "artofillusion.object.PointLight", 
		  		"artofillusion.object.ProceduralDirectionalLight", "artofillusion.object.ProceduralPointLight", 
		  		"artofillusion.object.ReferenceImage", "artofillusion.object.SceneCamera", "artofillusion.object.Sphere", 
		  		"artofillusion.object.SplineMesh", "artofillusion.object.SpotLight", "artofillusion.object.TriangleMesh", 
		  		"artofillusion.object.Tube", "artofillusion.texture.ConstantParameterValue", 
		  		"artofillusion.texture.CylindricalMapping", "artofillusion.texture.FaceParameterValue", 
		  		"artofillusion.texture.FaceVertexParameterValue", "artofillusion.texture.ImageMapTexture", 
		  		"artofillusion.texture.LayeredMapping", "artofillusion.texture.LayeredTexture", 
		  		"artofillusion.texture.LayeredTriangle", "artofillusion.texture.Linear2DTriangle", 
		  		"artofillusion.texture.Linear3DTriangle", "artofillusion.texture.LinearMapping3D", 
		  		"artofillusion.texture.Mapping2D", "artofillusion.texture.Mapping3D", "artofillusion.texture.MoveUVViewTool",
		  		"artofillusion.texture.Nonlinear2DTriangle", "artofillusion.texture.NonlinearMapping2D", 
		  		"artofillusion.texture.ParameterValue", "artofillusion.texture.ProceduralTexture2D",
		  		"artofillusion.texture.ProceduralTexture3D", "artofillusion.texture.ProjectionMapping", 
		  		"artofillusion.texture.SphericalMapping", "artofillusion.texture.Texture", "artofillusion.texture.Texture2D", 
		  		"artofillusion.texture.Texture3D", "artofillusion.texture.TextureMapping", 
		  		"artofillusion.texture.TextureSpec", "artofillusion.texture.UniformMapping", 
		  		"artofillusion.texture.UniformTexture", "artofillusion.texture.UniformTriangle", 
		  		"artofillusion.texture.UVMappedTriangle", "artofillusion.texture.UVMapping", 
		  		"artofillusion.texture.UVMappingViewer", "artofillusion.texture.UVMappingWindow", 
		  		"artofillusion.texture.UVWMappedTriangle", "artofillusion.texture.VertexParameterValue", 
		  		"artofillusion.ui.ActionProcessor", "artofillusion.ui.AutoScroller", "artofillusion.ui.ColorChooser", 
		  		"artofillusion.ui.ComponentsDialog", "artofillusion.ui.Compound3DManipulator",
		  		"artofillusion.ui.DefaultDockableWidget", "artofillusion.ui.DefaultToolButton", 
		  		"artofillusion.ui.EditingTool", "artofillusion.ui.EditingWindow", "artofillusion.ui.FloatingDialog", 
		  		"artofillusion.ui.GenericTool", "artofillusion.ui.GenericTreeElement", "artofillusion.ui.ImageFileChooser", 
		  		"artofillusion.ui.InfiniteDragListener", "artofillusion.ui.Manipulator",
		  		"artofillusion.ui.MeshEditController", "artofillusion.ui.MeshEditingTool", "artofillusion.ui.MessageDialog", 
		  		"artofillusion.ui.NinePointManipulator", "artofillusion.ui.ObjectTreeElement", 
		  		"artofillusion.ui.PanelDialog", "artofillusion.ui.PopupMenuManager", "artofillusion.ui.PropertyEditor", 
		  		"artofillusion.ui.Spacer", "artofillusion.ui.ThemeManager", "artofillusion.ui.ToolButton",
		  		"artofillusion.ui.ToolButtonWidget", "artofillusion.ui.ToolPalette", "artofillusion.ui.Translate", 
		  		"artofillusion.ui.TreeElement", "artofillusion.ui.TreeList", "artofillusion.ui.UIUtilities", 
		  		"artofillusion.ui.ValueChecker", "artofillusion.ui.ValueField", "artofillusion.ui.ValueSelector", 
		  		"artofillusion.ui.ValueSlider",
		  	  "buoy.event.CellValueChangedEvent", "buoy.event.CommandEvent", "buoy.event.DocumentLinkEvent", 
		  	  "buoy.event.EventProcessor", "buoy.event.EventSource", "buoy.event.FocusGainedEvent", 
		  	  "buoy.event.FocusLostEvent", "buoy.event.KeyPressedEvent", "buoy.event.KeyReleasedEvent", 
		  	  "buoy.event.KeyTypedEvent", "buoy.event.MouseClickedEvent", "buoy.event.MouseDraggedEvent", 
		  	  "buoy.event.MouseEnteredEvent", "buoy.event.MouseExitedEvent", "buoy.event.MouseMovedEvent", 
		  	  "buoy.event.MousePressedEvent", "buoy.event.MouseReleasedEvent", "buoy.event.MouseScrolledEvent", 
		  	  "buoy.event.RepaintEvent", "buoy.event.SelectionChangedEvent", "buoy.event.ToolTipEvent", 
		  	  "buoy.event.ValueChangedEvent", "buoy.event.WidgetEvent", "buoy.event.WidgetFocusEvent", 
		  	  "buoy.event.WidgetKeyEvent", "buoy.event.WidgetMouseEvent", "buoy.event.WidgetWindowEvent", 
		  	  "buoy.event.WindowActivatedEvent", "buoy.event.WindowClosingEvent", "buoy.event.WindowDeactivatedEvent", 
		  	  "buoy.event.WindowDeiconifiedEvent", "buoy.event.WindowIconifiedEvent", "buoy.event.WindowResizedEvent",
		  		"buoy.widget.AWTWidget", "buoy.widget.BButton", "buoy.widget.BCheckBox", "buoy.widget.BCheckBoxMenuItem", 
		  		"buoy.widget.BColorChooser", "buoy.widget.BComboBox", "buoy.widget.BDialog", "buoy.widget.BDocumentViewer", 
		  		"buoy.widget.BFileChooser", "buoy.widget.BFrame", "buoy.widget.BLabel", "buoy.widget.BList", 
		  		"buoy.widget.BMenu", "buoy.widget.BMenuBar", "buoy.widget.BMenuItem", "buoy.widget.BorderContainer", 
		  		"buoy.widget.BOutline", "buoy.widget.BPasswordField", "buoy.widget.BPopupMenu", "buoy.widget.BProgressBar", 
		  		"buoy.widget.BRadioButton", "buoy.widget.BRadioButtonMenuItem", "buoy.widget.BScrollBar", 
		  		"buoy.widget.BScrollPane", "buoy.widget.BSeparator", "buoy.widget.BSlider", "buoy.widget.BSpinner", 
		  		"buoy.widget.BSplitPane", "buoy.widget.BStandardDialog", "buoy.widget.BTabbedPane", "buoy.widget.BTable", 
		  		"buoy.widget.BTextArea", "buoy.widget.BTextField", "buoy.widget.BToolBar", "buoy.widget.BToolTip", 
		  		"buoy.widget.BTree", "buoy.widget.BuoyComponent", "buoy.widget.BWindow", "buoy.widget.ColumnContainer", 
		  		"buoy.widget.CustomWidget", "buoy.widget.ExplicitContainer", "buoy.widget.FormContainer", 
		  		"buoy.widget.GridContainer", "buoy.widget.LayoutInfo", "buoy.widget.MenuWidget", 
		  		"buoy.widget.OverlayContainer", "buoy.widget.RadioButtonGroup", "buoy.widget.RowContainer", 
		  		"buoy.widget.Shortcut", "buoy.widget.TextWidget", "buoy.widget.Widget", "buoy.widget.WidgetContainer", 
		  		"buoy.widget.WindowWidget"};
		  

		  /** Get the ScriptEngine for running scripts written in a particular language. */
		  
		  public static ScriptEngine getScriptEngine(String language)
		  {
		    if (!engines.containsKey(language))
		      {
		        if (parentLoader == null)
		        {
		          parentLoader = new SearchlistClassLoader(ScriptRunner.class.getClassLoader());
		          for (ClassLoader plugin : PluginRegistry.getPluginClassLoaders())
		            parentLoader.add(plugin);
		        }
		        ScriptEngine engine;
		        if (language.equals("Groovy"))
		          engine = new GroovyScriptEngine(parentLoader);
		        else if (language.equals("BeanShell"))
		          engine = new BeanshellScriptEngine(parentLoader);
		        else if (language.equals("Python"))
		          engine = new PythonScriptEngine(parentLoader);
		        else
		          throw new IllegalArgumentException("Unknown name for scripting language: "+language);
		        engines.put(language, engine);
		        try
		          {
		            for (String packageName : IMPORTS)
		              engine.addImport(packageName);
		            if(language.equals("Python"))
			            for (String packageName : PYTHON_IMPORTS)
			              engine.addImport(packageName);
		          }
		        catch (Exception e)
		          {
		            e.printStackTrace();
		          }
		        output = new PrintStream(new ScriptOutputWindow());
		        engine.setOutput(output);
		      }
		    return engines.get(language);
		  }
		  
		  /** Execute a script. */
		  
		  public static void executeScript(String language, String script, Map<String, Object> variables)
		  {
		    try
		      {
		        getScriptEngine(language).executeScript(script, variables);
		      }
		    catch (ScriptException e)
		      {
		        System.out.println("Error in line "+e.getLineNumber()+": "+e.getMessage());
		      }
		  }
		  
		  /** Parse a Tool script. */
		  
		  public static ToolScript parseToolScript(String language, String script) throws Exception
		  {
		    return getScriptEngine(language).createToolScript(script);
		  }
		  
		  /** Parse an Object script. */
		  
		  public static ObjectScript parseObjectScript(String language, String script) throws Exception
		  {
		    return getScriptEngine(language).createObjectScript(script);
		  }

		  /** Display a dialog showing an exception thrown by a script.  This returns the line number
		      in which the error occurred, or -1 if it could not be determined. */

		  public static int displayError(String language, Exception ex)
		  {
		    if (ex instanceof UndeclaredThrowableException)
		      ex = (Exception) ((UndeclaredThrowableException) ex).getUndeclaredThrowable();
		    String head = "An error occurred while executing the script:";
		    String message = null;
		    int line = -1;
		    try
		    {
		      if (ex instanceof ScriptException)
		      {
		        ScriptException t = (ScriptException) ex;
		        message = t.getMessage();
		        if (t.getLineNumber() > -1)
		          line = t.getLineNumber();
		        ex.printStackTrace(output);
		      }
		      else
		        {
		          message = ex.getMessage();
		          ex.printStackTrace(output);
		        }
		      if (message == null || message.length() == 0)
		        message = ex.toString();
		    }
		    catch (Exception ex2)
		    {
		      ex2.printStackTrace();
		    }
		    ArrayList<String> v = new ArrayList<String>();
		    v.add(head);
		    if (message != null)
		    {
		      if (!message.contains("Inline eval of"))
			  {
		        v.add(message);
			  }
		    }

		    String msg[] = v.toArray(new String [v.size()]);
		    new BStandardDialog("Error", msg, BStandardDialog.ERROR).showMessageDialog(null);
		    return line;
		  }

		  /** Given the name of a file, determine what language it contains based on the extension. */

		  public static String getLanguageForFilename(String filename)
		  {
		    for (String language : LANGUAGES)
		      if (filename.endsWith("."+getScriptEngine(language).getFilenameExtension()))
		        return language;
		    throw new IllegalArgumentException("Filename \""+filename+"\" does not match any recognized scripting language.");
		  }

		  /** Return the standard filename extension to use for a language. */

		  public static String getFilenameExtension(String language)
		  {
		    return getScriptEngine(language).getFilenameExtension();
		  }

}
