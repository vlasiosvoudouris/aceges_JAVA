package aceges.utilities.io;

import java.awt.FileDialog;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.JFrame;
import aceges.ACEGESApp;
import aceges.gui.GUIACEGESApp;
import aceges.gui.maps.WorldMap;


public class SaveOpenACEGESModel 
{
	
	private GUIACEGESApp guiModel;
	private String file;
	private String fileMap;

	public SaveOpenACEGESModel(GUIACEGESApp guiModel)
	{
		this.guiModel=guiModel;
		file ="src/aceges/utilities/io/ACEGESmodelInitialisation.ACEGES";	
		fileMap ="src/aceges/utilities/io/ACEGESmap.ACEGES";	
		
	//	System.out.println(file);
	}
	
	/** Lets the user checkpoint out an ACEGES simulation to a file with a given name. 
	 * @throws IOException */
    public void doSaveModelAs() 
    {
   	
      try
      {
    	FileOutputStream stream = new FileOutputStream(file); 
    	 GZIPOutputStream g = 
             new GZIPOutputStream(
                 new BufferedOutputStream(stream));

         ObjectOutputStream s = 
             new ObjectOutputStream(g);
             
         s.writeObject(guiModel.getModelACEGES());
       
         s.flush();
         g.finish();  // need to force out the gzip stream AND manually flush it.  Java's annoying.  Took a while to find this bug...
         g.flush();
         stream.flush();
         stream.close();
      }
      catch (IOException e) {
			e.printStackTrace();
			
		}
    }
    
    /** Lets the user checkpoint out an ACEGES simulation to a file with a given name. 
	 * @throws IOException */
    public void doSaveMapAs() 
    {
   	
      try
      {
    	FileOutputStream stream = new FileOutputStream(fileMap); 
    	 GZIPOutputStream g = 
             new GZIPOutputStream(
                 new BufferedOutputStream(stream));

         ObjectOutputStream s = 
             new ObjectOutputStream(g);
             
         s.writeObject(guiModel.worldMap);
       
         s.flush();
         g.finish();  // need to force out the gzip stream AND manually flush it.  Java's annoying.  Took a while to find this bug...
         g.flush();
         stream.flush();
         stream.close();
      }
      catch (IOException e) {
			e.printStackTrace();
			
		}
    }
    public ACEGESApp readACEGESmodel() 
    {
    	ACEGESApp acegesModel=null;
    	try
    	{
    		//FileInputStream stream = new FileInputStream(file);
    		InputStream stream2= getClass().getResourceAsStream("ACEGESmodelInitialisation.ACEGES");
    		GZIPInputStream g = 
                new GZIPInputStream(
                    new BufferedInputStream(stream2));

    		ObjectInputStream s = 
                new ObjectInputStream(g);
    		acegesModel=(ACEGESApp) s.readObject();
    		s.close();
    	}
    	catch (IOException e) {
    			e.printStackTrace();
    			
    		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return acegesModel;
    }
    
    public WorldMap readACEGESmap() 
    {
    	WorldMap map=null;
    	try
    	{
    		//FileInputStream stream = new FileInputStream(file);
    		InputStream stream2= getClass().getResourceAsStream("ACEGESmap.ACEGES");
    		GZIPInputStream g = 
                new GZIPInputStream(
                    new BufferedInputStream(stream2));

    		ObjectInputStream s = 
                new ObjectInputStream(g);
    		map=(WorldMap) s.readObject();
    		s.close();
    	}
    	catch (IOException e) {
    			e.printStackTrace();
    			
    		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return map;
    }
    
}
