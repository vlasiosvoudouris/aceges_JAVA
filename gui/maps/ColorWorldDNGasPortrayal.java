package aceges.gui.maps;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

import sim.portrayal.DrawInfo2D;
import sim.portrayal.geo.GeomPortrayal;
//import sim.util.geo.AttributeField;
import sim.util.geo.MasonGeometry;
import sim.util.gui.SimpleColorMap;
import aceges.countries.CountryGasProducer;
import aceges.gui.GUIACEGESApp;

public class ColorWorldDNGasPortrayal extends GeomPortrayal
{
	
	private static final long serialVersionUID = 1L;

	SimpleColorMap colorMap = null; 
	GUIACEGESApp myGUIModel=null;	
	
	static final int DNGAS_PRODUCTION = 0;
	static final int DNGAS_EXPORT_CAPACITY = 1;
	static final int DNGAS_PPNP = 2;
	static final int DNGAS_PRODUCTION_GROWTH=3;
	static final int NOPRODUCER=1000;
	int type=DNGAS_PRODUCTION;
	
	public ColorWorldDNGasPortrayal(SimpleColorMap map, GUIACEGESApp guiAppl, int type) 
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
    			 case DNGAS_PRODUCTION:
    				 agentAttrib=getAgentDNGasProduction(gm.getStringAttribute("FIPS_CNTRY"));
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
    			 case DNGAS_EXPORT_CAPACITY:
    			 
    				 agentAttrib= getAgentDNGasNetDemand(gm.getStringAttribute("FIPS_CNTRY"));
    				 
    				 if (agentAttrib<0)
    	    			 paint = colorMap.getColor(agentAttrib*(-1));
    				 else
    					 paint=Color.GRAY;
    				 break;
    			 case DNGAS_PPNP: 
    				 agentAttrib= getAgentOilPPNP(gm.getStringAttribute("FIPS_CNTRY"));    				 
    				 if (agentAttrib>0)
    					 paint=Color.BLACK;
    				 else
    					 paint=Color.ORANGE;
    				 break;
    			 case DNGAS_PRODUCTION_GROWTH:
    				 agentAttrib= getAgentDNGasProdGrowth(gm.getStringAttribute("FIPS_CNTRY")); 
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
	
    
     private double getAgentDNGasProdGrowth(String name) 
     {
    	 ArrayList<CountryGasProducer> agents= this.myGUIModel.getModelACEGES().gasAgentList;
  		Iterator<CountryGasProducer> itr = agents.iterator();
  		while (itr.hasNext()) 
  		{
  			
  		      CountryGasProducer element = (CountryGasProducer) itr.next();  
  		    //  System.out.println(element.getName());
  		      if (name.equalsIgnoreCase("GL") )
  		      {
  		    	  name="DA";
  		      }
  		      if (element.FIPS.equalsIgnoreCase(name) )
  		      {
  		    	 if (element.getCurrentdnGasProduction()>0.1) 
  		    	 {
  		    	//  System.out.println(Math.log(element.getCurrentOilProduction()+1) - Math.log(element.getCurrentOilProductionHist()+1)); 
  		    	//TO BE DONE 
  		    		 //return Math.log(element.getCurrentdnGasProduction()+1) - Math.log(element.getCurrentGasProductionHist()+1);
  		    	 }
  		      }
  		}
  		
  		return NOPRODUCER+1;
	}

	private double getAgentOilPPNP(String name) 
     {  	
    	ArrayList<CountryGasProducer> agents= this.myGUIModel.getModelACEGES().gasAgentList;
  		Iterator<CountryGasProducer> itr = agents.iterator();
  		while (itr.hasNext()) 
  		{
  			
  		      CountryGasProducer element = (CountryGasProducer) itr.next();  
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

	private double getAgentDNGasProduction(String name)
     {
    	 ArrayList<CountryGasProducer> agents= this.myGUIModel.getModelACEGES().gasAgentList;
 		Iterator<CountryGasProducer> itr = agents.iterator();
 		while (itr.hasNext()) 
 		{
 			
 		      CountryGasProducer element = (CountryGasProducer) itr.next();  
 		    //  System.out.println(element.getName());
 		      if (name.equalsIgnoreCase("GL") )
 		      {
 		    	  name="DA";
 		      }
 		      if (element.FIPS.equalsIgnoreCase(name) )
 		      {
 		    	//  System.out.println("=======================");
 		    	 return element.getCurrentdnGasProduction();
 		    	 //return element.getOilDemand();
 		      }
 		}
 		
 		return 0.0;
 		     
   }
     
   private double getAgentDNGasNetDemand(String name)
     {
    	 ArrayList<CountryGasProducer> agents= this.myGUIModel.getModelACEGES().gasAgentList;
 		Iterator<CountryGasProducer> itr = agents.iterator();
 		while (itr.hasNext()) 
 		{
 			
 		      CountryGasProducer element = (CountryGasProducer) itr.next();  
 		    //  System.out.println(element.getName());
 		     if (name.equalsIgnoreCase("GL") )
		      {
		    	  name="DA";
		      }
 		      if (element.FIPS.equalsIgnoreCase(name))
 		      {
 		    	//  System.out.println("=======================");
 		    	  return element.getGasDemand() - element.getCurrentdnGasProduction();
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
