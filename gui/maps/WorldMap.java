package aceges.gui.maps;

//import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


//import sim.engine.SimState;
//import sim.engine.Steppable;
import sim.field.geo.GeomVectorField;
import sim.io.geo.ShapeFileImporter;
//import sim.util.gui.SimpleColorMap;
//import aceges.gui.GUIACEGESApp;

public class WorldMap 
{
	
	//private  ShapeFileImporter importer = new ShapeFileImporter();
	public GeomVectorField worldMap = new GeomVectorField();
	private boolean fromJar=false;
	
	
	public WorldMap(String fileName)
	{
		try {			
			  // this line allows us to replace the standard MasonGeometry with our 
			  // own subclass of MasonGeometry; see OilFieldGeomWrapper.java for more info. 
             // Note: this line MUST occur prior to ingesting the data
			if(fileName.isEmpty())
			{
				fileName="worldMap/country.shp";
			}
			URL bldgGeometry=null;
			bldgGeometry = WorldMap.class.getResource(fileName);

			if(fromJar)
			{
			   try {
					bldgGeometry = new URL(bldgGeometry.getPath());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			else
			{
				File file=  new File(fileName);
				try {
					bldgGeometry = file.toURI().toURL();
				} catch (MalformedURLException e) {
					System.err.println("Error in reading the shale file");
					e.printStackTrace();
				}
			}
			ShapeFileImporter.read(bldgGeometry, worldMap);
			//importer.ingest("worldMap/country.shp", WorldMap.class, worldMap, null);
		} catch (FileNotFoundException e) {
			Logger.getLogger(WorldMap.class.getName()).log(Level.SEVERE, null, e);
			
		}
	}

}
