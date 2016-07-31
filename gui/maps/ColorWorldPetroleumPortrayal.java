package aceges.gui.maps;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import aceges.countries.CountryOilProducer;
import aceges.countries.EnergyAgent;
import aceges.gui.GUIACEGESApp;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.geo.*; 
import sim.util.geo.MasonGeometry;
import sim.util.gui.*; 

/**
 *  We override GeomPortrayal so we can change the paint color for each voting district based on 
 *  how many agents are currently inside the district.  After setting the paint color, GeomPortrayal
 *  handles drawing in the standard GeoMASON way. 
 *
 */

public class ColorWorldPetroleumPortrayal extends GeomPortrayal {

//	private static final long serialVersionUID = 6026649920581400781L;

	SimpleColorMap colorMap = null; 
	GUIACEGESApp myGUIModel=null;	
	
	static final int OIL_PRODUCTION = 0;
	static final int OIL_EXPORT_CAPACITY = 1;
	static final int OIL_PPNP = 2;
	static final int OIL_PRODUCTION_GROWTH=3;
	static final int NOPRODUCER=1000;
	int type=OIL_PRODUCTION;
	
	public ColorWorldPetroleumPortrayal(SimpleColorMap map, GUIACEGESApp guiAppl, int type) 
	{
		super(true); 
		colorMap = map; 
		this.myGUIModel=guiAppl;
		this.type=type;
		
	}

	
	// check the name of the object (country in shapefile) with the country in the  simulation. 
	// then use the oil prodction to model the color for the paint. 

	public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
    {
    	
    	MasonGeometry gm= (MasonGeometry)object;	
    	double agentAttrib=0.0;

    			switch (type){
    			 case OIL_PRODUCTION:
    				 agentAttrib=getAgentOilProduction(gm.getStringAttribute("FIPS_CNTRY"));
    				 //System.out.println(agentAttrib);
    				 if (agentAttrib>=1)
    				 {	 
    	    			 paint = colorMap.getColor(agentAttrib);
    				 }
    				 else if (agentAttrib>0 && agentAttrib<1)
    					 paint=Color.GRAY;
    				 else
    				 {
    					 paint=Color.GREEN;
    				 }
    				 break;
    			 case OIL_EXPORT_CAPACITY:
    			 
    				 agentAttrib= getAgentOilNetDemand(gm.getStringAttribute("FIPS_CNTRY"));
    				 
    				 if (agentAttrib<0)
    	    			 paint = colorMap.getColor(agentAttrib*(-1));
    				 else
    					 paint=Color.GRAY;
    				 break;
    			 case OIL_PPNP: 
    				 agentAttrib= getAgentOilPPNP(gm.getStringAttribute("FIPS_CNTRY"));    				 
    				 if (agentAttrib>0)
    					 paint=Color.BLACK;
    				 else
    					 paint=Color.ORANGE;
    				 break;
    			 case OIL_PRODUCTION_GROWTH:
    				 agentAttrib= getAgentOilProdGrowth(gm.getStringAttribute("FIPS_CNTRY")); 
    				 if (agentAttrib < NOPRODUCER)
    				 {
    					 paint = getColorMap(-10.0,10.0,Color.BLUE, Color.RED).getColor(agentAttrib*100);
    				 }
    				 else
    				 {
    					paint=Color.GRAY; 
    				 }
    				 break;
    			  default:
    				  paint=Color.PINK; 
    				  break;
    			 } 		
 
        super.draw(object, graphics, info);    
    }

    
     private double getAgentOilProdGrowth(String name) 
     {
    	 ArrayList<CountryOilProducer> agents= this.myGUIModel.getModelACEGES().oilAgentList;
  		Iterator<CountryOilProducer> itr = agents.iterator();
  		while (itr.hasNext()) 
  		{
  			
  		      CountryOilProducer element = (CountryOilProducer) itr.next();  
  		    //  System.out.println(element.getName());
  		      if (name.equalsIgnoreCase("GL") )
  		      {
  		    	  name="DA";
  		      }
  		      if (element.FIPS.equalsIgnoreCase(name) )
  		      {
  		    	 if (element.getCurrentOilProduction()>0.1) 
  		    	 {
  		    	//  System.out.println(Math.log(element.getCurrentOilProduction()+1) - Math.log(element.getCurrentOilProductionHist()+1)); 
  		    	  return Math.log(element.getCurrentOilProduction()+1) - Math.log(element.getCurrentOilProductionHist()+1);
  		    	 }
  		      }
  		}
  		
  		return NOPRODUCER+1;
	}

	private double getAgentOilPPNP(String name) 
     {  	
    	ArrayList<CountryOilProducer> agents= this.myGUIModel.getModelACEGES().oilAgentList;
  		Iterator<CountryOilProducer> itr = agents.iterator();
  		while (itr.hasNext()) 
  		{
  			
  		      CountryOilProducer element = (CountryOilProducer) itr.next();  
  		    //  System.out.println(element.getName());
  		      if (name.equalsIgnoreCase("GL") )
  		      {
  		    	  name="DA";
  		      }
  		      if (element.FIPS.equalsIgnoreCase(name) )
  		      {
  		    	  return element.getPpnp();
  		      }
  		}  		
  		return 0.0;
	}

	private double getAgentOilProduction(String name)
     {
    	 ArrayList<CountryOilProducer> agents= this.myGUIModel.getModelACEGES().oilAgentList;
 		Iterator<CountryOilProducer> itr = agents.iterator();
 		while (itr.hasNext()) 
 		{
 			
 		      CountryOilProducer element = (CountryOilProducer) itr.next();  
 		    //  System.out.println(element.getName());
 		      if (name.equalsIgnoreCase("GL") )
 		      {
 		    	  name="DA";
 		      }
 		      if (element.FIPS.equalsIgnoreCase(name) )
 		      {
 		    	//  System.out.println("=======================");
 		    	 return element.getCurrentOilProduction();
 		    	 //return element.getOilDemand();
 		      }
 		}
 		
 		return 0.0;
 		     
   }
     
   private double getAgentOilNetDemand(String name)
     {
    	 ArrayList<CountryOilProducer> agents= this.myGUIModel.getModelACEGES().oilAgentList;
 		Iterator<CountryOilProducer> itr = agents.iterator();
 		while (itr.hasNext()) 
 		{
 			
 		      CountryOilProducer element = (CountryOilProducer) itr.next();  
 		    //  System.out.println(element.getName());
 		     if (name.equalsIgnoreCase("GL") )
		      {
		    	  name="DA";
		      }
 		      if (element.FIPS.equalsIgnoreCase(name))
 		      {
 		    	//  System.out.println("=======================");
 		    	  return element.getOilDemand() - element.getCurrentOilProduction();
 		      }
 		}
 		
 		return 0.0;
 		     
   }
     
     
     public void updateColorMap()
     {
    	 colorMap= new SimpleColorMap(0.0, 2000.0, Color.BLACK, Color.RED);
     }
     
     public SimpleColorMap getColorMap(double min, double max, Color minColor, Color maxColor)
     {
    	return new SimpleColorMap(min, max, minColor, maxColor);
     }

}

