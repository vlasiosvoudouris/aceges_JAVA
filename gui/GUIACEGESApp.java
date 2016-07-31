package aceges.gui;

import aceges.countries.CountryGasProducer;
//import aceges.countries.CountryOilConsumer;
import aceges.countries.CountryOilProducer;
//import aceges.countries.EnergyAgent;
import aceges.gui.maps.ColorWorldDNGasPortrayal;
import aceges.gui.maps.ColorWorldPetroleumPortrayal;
import aceges.gui.maps.WorldMap;
import aceges.support.ACEGESModelInitialisationOil;
import aceges.support.ACEGESModelinitialisationGas;
import aceges.support.FileDNGReporter;
import aceges.support.FileOilReporter;


import aceges.utilities.io.SaveOpenACEGESModel;
//import aceges.utilities.mathematica.ConnectingToMathematica;
import aceges.utilities.statistics.graphics.MultiTimeSeriesChartGenerator;
import aceges.utilities.statistics.graphics.StackedTimeSeriesChartGenerator;

import java.awt.Color;
//import java.io.File;
//import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ec.util.*;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.field.network.Network;

import sim.portrayal.Inspector;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.geo.GeomVectorFieldPortrayal;
import sim.portrayal.network.NetworkPortrayal2D;
import sim.portrayal.network.SimpleEdgePortrayal2D;
import sim.portrayal.network.SpatialNetwork2D;
import sim.portrayal.simple.CircledPortrayal2D;
import sim.portrayal.simple.HexagonalPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.gui.SimpleColorMap;
import sim.util.media.chart.HistogramGenerator;
import sim.util.media.chart.TimeSeriesChartGenerator;



import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;




import aceges.ACEGESApp;

/** GUIACEGESApp is the main entry class for an GUI-based computation run of ACEGESpp model. 
 * @author Vlasios Voudouris
 * Created: June, 2009
 */
public class GUIACEGESApp extends GUIState
{	
	public static final int OIL_PRODUCTION = 0;
	public static final int OIL_EXPORT_CAPACITY = 1;
	public static final int OIL_PPNP = 2;
	public static final int OIL_PRODUCTION_GROWTH=3;	
	public static final int DNGAS_PRODUCTION = 0;
	public static final int DNGAS_EXPORT_CAPACITY = 1;
    public static final boolean isApplet= Boolean.FALSE;

	private ACEGESApp modelACEGES;
	public WorldMap worldMap=null;
	private Display2D displayWorldOilMap;
	private Display2D displayWorldDNGasMap;
	private Display2D displayWorldOilNetwork;	
	public String fileMap;	
	public Network worldNetwork = new Network(false);
	private FileDNGReporter myFileDNGasReporter;
	private FileOilReporter myFileOilReported;
	private boolean isCountrySpecificGraphics=true;// To display or NOT the country-specific graphics. 	
	public boolean isTest=false;
	
	/**
	 * 
	 * @param simState
	 * @param fromDataFiles - whether to load from the data files to initialise the ACEGES model
	 * @param fileMap
	 */
	public GUIACEGESApp(SimState simState, boolean fromDataFiles, String fileMap) 
	{
		super(simState);
		this.fileMap=fileMap;
		this.modelACEGES = (ACEGESApp)simState;
		if (fromDataFiles)
		{
			ACEGESModelInitialisationOil acegesFactoryOil= new ACEGESModelInitialisationOil(modelACEGES);
			ACEGESModelinitialisationGas acegesFactoryGas= new ACEGESModelinitialisationGas(modelACEGES);
		}
		if(!isApplet)
		{
			this.myFileOilReported= new FileOilReporter(this);
			this.myFileDNGasReporter = new FileDNGReporter(this);
		}	
	}
	
	public static String getName() { return "The ACEGES model 3.0: Crude Oil & Natural Gas"; }
	
	public static Object getInfo()
	{
		return
		"<H1> <CENTER>  The ACEGES Project</CENTER></H2>"+
		"<CENTER>Agent-based Computational Economics of the Global Energy System</CENTER>"+
	//	"<CENTER><H2>This is just a demo of the <b>ACEGES </b>software.</H2></CENTER>"+
		"<p> The overall aim of this proposal is to develop, test and disseminate an agent-based computational laboratory for the systematic experimental study of the global energy system through the mechanism of Energy Scenarios. In particular, our intention is to show how Agent-based Computational Economics (ACE) can be applied to help leaders in government, business and civil society better understand the challenging outlook for energy through controlled computational experiments.</p>"+
"<p><b>Objectives</b>: <ul> " +
"<li>At what rate over time can the oil and gas from geographically dispersed nations be supplied to the marketplace?</li>"+
"<li>How will prices affect the ratio of technically recoverable/economically extractable oil and gas reserves?</li>"+
"<li>What are the spatiotemporal dynamics of spot oil prices, oil futures and oil supply shocks?</li>"+
"<li>What peak oil and gas mean for policy and investment?</li>" +
"<li>What peak oil and gas mean for alternative transport fuels and electricity?</li></ul></p>" +
"<br>For more information, please contact:<br>"+
"Dr. Vlasios Voudouris<br>"+ 
"Email: <a href='mailto:v.voudouris@londonmet.ac.uk'>v.voudouris@londonmet.ac.uk<br></a>"+ 
"<b>Note:</b> THE ACEGES SOFTWARE IS PROVIDED TO YOU 'AS IS,' AND THE AUTHOR MAKES NO EXPRESS OR IMPLIED WARRANTIES WHATSOEVER WITH RESPECT TO ITS FUNCTIONALITY, OPERABILITY, OR USE, INCLUDING, WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR INFRINGEMENT. THE AUTHOR EXPRESSLY DISCLAIM ANY LIABILITY WHATSOEVER FOR ANY DIRECT, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR SPECIAL DAMAGES, INCLUDING, WITHOUT LIMITATION, LOST REVENUES, LOST PROFITS, LOSSES RESULTING FROM BUSINESS INTERRUPTION OR LOSS OF DATA, REGARDLESS OF THE FORM OF ACTION OR LEGAL THEORY UNDER WHICH THE LIABILITY MAY BE ASSERTED, EVEN IF ADVISED OF THE POSSIBILITY OR LIKELIHOOD OF SUCH DAMAGES.  <br><br>"+
"<br><b>Citation:</b> Voudouris, V. (2010), The ACEGES software,  Centre for International Business and Sustainability, London Metropolitan Business School, UK"+
"<br><br><b><CENTER>To be used for teaching and research work at LondonMet. Any other uses is not permitted without written approval by Dr. Vlasios Voudouris</CENTER></b>"+
"<br><br><b><CENTER> Centre for International Business and Sustainability (CIBS)</CENTER></b>"+
"<b><CENTER> London Metropolitan Business School (LMBS)</CENTER></b>";

	}
	
    public static void main(String[] args)
    {
     
   
     SimState simState= new ACEGESApp(System.currentTimeMillis(), null, true); //simulation model 
     GUIACEGESApp guiACEGESApp = new GUIACEGESApp(simState,Boolean.TRUE,"data/worldMap/country.shp");//visualisation of simulation model
     new SaveOpenACEGESModel(guiACEGESApp).doSaveModelAs();
     new SaveOpenACEGESModel(guiACEGESApp).doSaveMapAs();
  
  /*  
    SimState simState = new SaveOpenACEGESModel(null).readACEGESmodel();
   	GUIACEGESApp guiACEGESApp = new GUIACEGESApp(simState,Boolean.FALSE,"data/worldMap/country.shp");
    guiACEGESApp.worldMap= new SaveOpenACEGESModel(null).readACEGESmap();
  */ 
    Console c = new Console(guiACEGESApp);//gui controls   
	c.setBounds(600, 5,500, 300);
	c.setVisible(true);   
    //stops the simulation model
    simState.finish();
 
    }
    
    /**
     * Since the GUIState needs to know when the GUI has been lanched
     * the init method is used to register the visualisations
     * using the c.registerFrame function
     * */
     
    public void init (Controller c)
    {
    	super.init(c);
    	//**** Prepare the data for the graphics
    	if(worldMap==null)
    	{	
    		worldMap = new WorldMap(fileMap); 
    	}
     	this.modelACEGES.resetModelParameters();
    	this.modelACEGES.updatedSimStateOilDNGas();  	
    	
    	//**** prepare the graphics
    	this.prepareCrudeOilGraphics(c);
    	this.prepareDNGasGraphics(c);
   	
    	if(isCountrySpecificGraphics)
		{
    		//this.prepareCountrySpecificGraphicsOilGas(c);  
    		this.prepareCombinedCountrySpecificGraphicsOilGas(c);
		}    	
    	if(isTest)
    	{
    		this.prepareTestGraphics(c);
    	}
    }  
    
    private void prepareTestGraphics(Controller c) 
    {
    	  MultiTimeSeriesChartGenerator oilgasProduction = new MultiTimeSeriesChartGenerator();
    	  oilgasProduction.setTitle("Test Graphic");
    	  //oilgasProduction.setXAxisLabel("Simulated Year");    	  
    	  oilgasProduction.addSeries(modelACEGES.testGraphicTotalProductionOil, null, 0);
    	  oilgasProduction.addSeries(modelACEGES.testGraphicTotalDemandOil,null, 0);
    	  oilgasProduction.addSeries(modelACEGES.testGraphicTotalProductionGas,null, 1);
    	  oilgasProduction.addSeries(modelACEGES.testGraphicTotalDemandGas,null, 1);
    	  c.registerFrame(oilgasProduction.createFrame(this));	
	}

    private void prepareCombinedCountrySpecificGraphicsOilGas(Controller c) 
    {
         //Collections.sort(agentsOil);
    	 
    	 Iterator<CountryOilProducer> itr = this.modelACEGES.oilAgentList.iterator();
    	 CountryGasProducer element2=null;
    	 MultiTimeSeriesChartGenerator oilProduction=null;
    	 CountryOilProducer element=null; 
    	 JFrame oilProdFrame=null;
         while (itr.hasNext()) 
	     {
        	oilProduction = new MultiTimeSeriesChartGenerator();
            element = (CountryOilProducer) itr.next();  
		    //prepare the visualisations such as those in sim.until.media.chart		      	   
			oilProduction.setTitle(element.getName() + " Crude Oil & DNGas");
			//oilProduction.setXAxisLabel("Simulated Year");
			//oilProduction.addSeries(element.initialiseXYSeriesOilDemand("Crude Oil Demand"), null,0);//production
			element.initialiseXYSeriesOilDemand("Crude Oil Demand");
			oilProduction.addSeries(element.initialiseXYSeriesOilProduction("Crude Oil Production"), null,0);//production	
	    	Iterator<CountryGasProducer> itr2 =  this.modelACEGES.gasAgentList.iterator();
	    	while (itr2.hasNext()) 
			{
				element2 = (CountryGasProducer) itr2.next();
				if (element2.getFIPS().equalsIgnoreCase(element.getFIPS()))
				{
					oilProduction.addSeries(element2.initialiseXYSeriesDNGasProduction("DNGas Production"), null,1);//production
					element2.initialiseXYSeriesDNGasDemand("DNGas Demand");					
				}				
							
			}
	        oilProdFrame = oilProduction.createFrame(this);
		    oilProdFrame.setVisible(false);		

			if(isCountrySpecificGraphics && element.getCurrentOilProduction()>0)
			{
				c.registerFrame(oilProdFrame);	
			}
			
		 }
		
	}
    
    
    
	private void prepareCrudeOilGraphics(Controller c) 
    {
		
		JFrame prodOilWorldFrameStackedSerries;
		Continuous2D worldCont2D = new Continuous2D(1.0,10,10);	
		
		StackedTimeSeriesChartGenerator  prodOilWorldStackedSerries = new StackedTimeSeriesChartGenerator();
	    prodOilWorldStackedSerries.setTitle("World Crude Oil Production");
	    prodOilWorldStackedSerries.setXAxisLabel("Simulated Year");
	    prodOilWorldStackedSerries.setYAxisLabel("Crude Oil Production (millions barrels)");
		Iterator<CountryOilProducer> itr = this.modelACEGES.oilAgentList.iterator();
		int row=0;
		int column=0;
		while (itr.hasNext()) 
	    {
		      CountryOilProducer element = (CountryOilProducer) itr.next();
		      prodOilWorldStackedSerries.addSeries(element.initialiseXYSeriesOilStacked(element.getName()), null); 
		     
		      worldNetwork.addNode(element);
		      worldCont2D.setObjectLocation(element, new Double2D(row,column));
		      row= row+1;
		      if (row>10)
		     {
		    	 row=0;
		    	 column=column+1;
		     }		     		    		
				 // STOP generation of stacks	
		 }
		    prodOilWorldFrameStackedSerries = prodOilWorldStackedSerries.createFrame(this);
		    prodOilWorldFrameStackedSerries.setVisible(false);
		    c.registerFrame(prodOilWorldFrameStackedSerries);
		
		  if (!isApplet)
		  {  
		   GeomVectorFieldPortrayal worldPortrayalOilPPNP = new GeomVectorFieldPortrayal();
		   GeomVectorFieldPortrayal worldPortrayalOilProduction = new GeomVectorFieldPortrayal();
		   GeomVectorFieldPortrayal worldPortrayalOilExportCapacity = new GeomVectorFieldPortrayal();
		   GeomVectorFieldPortrayal worldPortrayalOilGrowth = new GeomVectorFieldPortrayal();
		   worldPortrayalOilPPNP.setField(worldMap.worldMap);
		   ColorWorldPetroleumPortrayal colorWorldPetroleumPortrayal=new ColorWorldPetroleumPortrayal(new SimpleColorMap(0,1, Color.RED, Color.BLACK),this,OIL_PPNP);
		   worldPortrayalOilPPNP.setPortrayalForNonNull(colorWorldPetroleumPortrayal);
	    	
	    	worldPortrayalOilProduction.setField(worldMap.worldMap);   
		    colorWorldPetroleumPortrayal=new ColorWorldPetroleumPortrayal(new SimpleColorMap(1,10000.0, Color.RED, Color.BLACK),this,OIL_PRODUCTION);
		    CircledPortrayal2D circledPortrayal = new CircledPortrayal2D(colorWorldPetroleumPortrayal);
		    //worldPortrayalOilProduction.setPortrayalForNonNull(circledPortrayal);
		    worldPortrayalOilProduction.setPortrayalForNonNull(colorWorldPetroleumPortrayal);
	       
		    worldPortrayalOilExportCapacity.setField(worldMap.worldMap);
		    colorWorldPetroleumPortrayal=new ColorWorldPetroleumPortrayal(new SimpleColorMap(1,10000.0, Color.RED, Color.BLACK),this,OIL_EXPORT_CAPACITY);
		    worldPortrayalOilExportCapacity.setPortrayalForNonNull(colorWorldPetroleumPortrayal);
		    	
		    worldPortrayalOilGrowth.setField(worldMap.worldMap);
		    colorWorldPetroleumPortrayal=new ColorWorldPetroleumPortrayal(new SimpleColorMap(-7,7, Color.RED, Color.BLACK),this,OIL_PRODUCTION_GROWTH);
		    worldPortrayalOilGrowth.setPortrayalForNonNull(colorWorldPetroleumPortrayal);
		    
		    // add the two portrayals at the same display. 
		    displayWorldOilMap = new Display2D(800, 405, this);
		    displayWorldOilMap.attach(worldPortrayalOilPPNP, "World Map:PPNP");
	    	displayWorldOilMap.attach(worldPortrayalOilExportCapacity, "World Map:Export Capacity");
	    	displayWorldOilMap.attach(worldPortrayalOilGrowth, "World Map:Oil Production Growth");
	    	displayWorldOilMap.attach(worldPortrayalOilProduction, "World Map:Production");
	    	
	    	displayWorldOilMap.setScale(0.7);
	    	displayWorldOilMap.setBackdrop(Color.WHITE);
	    	JFrame displayFrameOil = displayWorldOilMap.createFrame();
	    	displayFrameOil.setTitle("World Crude Oil Map");
	    	c.registerFrame(displayFrameOil);
	        displayFrameOil.setVisible(false);

	        ContinuousPortrayal2D worldSpacePortrayal = new ContinuousPortrayal2D();
	        NetworkPortrayal2D worldNetworkPortrayal  = new NetworkPortrayal2D();
	        worldSpacePortrayal.setField(worldCont2D);
	        worldSpacePortrayal.setPortrayalForAll(new CircledPortrayal2D(new HexagonalPortrayal2D()));
	        worldNetworkPortrayal.setField(new SpatialNetwork2D(worldCont2D,worldNetwork));
	        worldNetworkPortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D());
	        
	        displayWorldOilNetwork = new Display2D(800, 405, this);
	        displayWorldOilNetwork.setClipping(true);
	        displayWorldOilNetwork.attach(worldNetworkPortrayal, "Network");
	        displayWorldOilNetwork.attach(worldSpacePortrayal, "Abstract Space");
	        JFrame displayFrameNetwork = displayWorldOilNetwork.createFrame();
	        displayFrameNetwork.setTitle("World Oil Network");
	        displayFrameNetwork.setVisible(false);
	        //c.registerFrame(displayFrameNetwork);	        
		  }
	}

	private void prepareDNGasGraphics(Controller c)
    {	
		
		StackedTimeSeriesChartGenerator  prodDNGasWorldStackedSerries = new StackedTimeSeriesChartGenerator();
    	prodDNGasWorldStackedSerries .setTitle("World DNGas Production");
 	    prodDNGasWorldStackedSerries .setXAxisLabel("Simulated Year");
 	    prodDNGasWorldStackedSerries .setYAxisLabel("World DNGas Production (billion cubic feet)"); 	     
 	    Iterator<CountryGasProducer> itr =  this.modelACEGES.gasAgentList.iterator();
		
 	    while (itr.hasNext()) 
		{
			CountryGasProducer element = (CountryGasProducer) itr.next();			
			prodDNGasWorldStackedSerries.addSeries(element.initialiseXYSeriesDNGasStacked(element.getName()), null);
		}
		
 	    //register the sereis 
 	   JFrame prodDNGAWorldFrameStackedSerries = prodDNGasWorldStackedSerries.createFrame(this);
 	    prodDNGAWorldFrameStackedSerries.setVisible(false);
	    c.registerFrame(prodDNGAWorldFrameStackedSerries);
		
    	if (!isApplet)
		  {  
			GeomVectorFieldPortrayal worldPortrayalDNGasProduction = new GeomVectorFieldPortrayal();
			GeomVectorFieldPortrayal worldPortrayalDNGasExportCapacity = new GeomVectorFieldPortrayal();		
			WorldMap worldDNGasMap = new WorldMap(fileMap);
		    
			worldPortrayalDNGasProduction.setField(worldMap.worldMap);   
			ColorWorldDNGasPortrayal colorWorldDNGasPortrayal=new ColorWorldDNGasPortrayal(new SimpleColorMap(20,100000.0, Color.RED, Color.BLACK),this,DNGAS_PRODUCTION);
		    worldPortrayalDNGasProduction.setPortrayalForNonNull(colorWorldDNGasPortrayal);
	       
		    worldPortrayalDNGasExportCapacity.setField(worldMap.worldMap);
		    colorWorldDNGasPortrayal=new ColorWorldDNGasPortrayal(new SimpleColorMap(1,10000.0, Color.RED, Color.BLACK),this,DNGAS_EXPORT_CAPACITY);
		    worldPortrayalDNGasExportCapacity.setPortrayalForNonNull(colorWorldDNGasPortrayal);
		    	
		    
		    // add the two portrayals at the same display. 
		    displayWorldDNGasMap = new Display2D(800, 405, this);
		    displayWorldDNGasMap.attach(worldPortrayalDNGasExportCapacity, "World Map:Export Capacity");
	    	
		    displayWorldDNGasMap.attach(worldPortrayalDNGasProduction, "World Map:Production");
	    	
		    displayWorldDNGasMap.setScale(0.7);
		    displayWorldDNGasMap.setBackdrop(Color.WHITE);
		    JFrame displayFrameDNGas = displayWorldDNGasMap.createFrame();
	    	displayFrameDNGas.setTitle("World DNGas Map");
	    	c.registerFrame(displayFrameDNGas);
	    	displayFrameDNGas.setVisible(false);
		  }
    	
    }
     
    private void setupNetworkEdges()
    {
    	Bag countries = this.worldNetwork.getAllNodes();
    	for(int i = 0; i < countries.size(); i++)
    	{
    		Object countryA= countries.get(i);
    		Object countryB= countries.get(i+1);
    		this.worldNetwork.addEdge(countryA, countryB, new Double(0.0));
    		
    	}
   
    	
    }

	public void setupModelUpdaterANDFileReporters() 
	{
		final Steppable modelUpdater = new Steppable() 
		{
			private static final long serialVersionUID = 1L;

			public void step(SimState state) 
			{
				modelACEGES.updatedSimStateOilDNGas();
				modelACEGES.shiftDNGASSiftedValue=0.0;
				modelACEGES.shiftCrudeOilSiftedValue=0.0;		
				if( modelACEGES.whichBaseYear+(int)modelACEGES.schedule.getSteps()>=2150)
				{
					scheduleRepeatingImmediatelyBefore(new Steppable() { public void step(SimState state) {modelACEGES.kill();}});
					
				}
			}	
		};
		this.scheduleRepeatingImmediatelyBefore(modelUpdater);
		if (!isApplet)
		{
			this.scheduleRepeatingImmediatelyAfter(myFileOilReported);
			this.scheduleRepeatingImmediatelyAfter(myFileDNGasReporter);
		}	
	}
	    


	/**
     * this is the first method that is executed once the start button is pressed
     */
	public void start() {
		super.start();
		setupModelUpdaterANDFileReporters();
		if (!isApplet)
		{
			displayWorldOilMap.reset();    // reschedule the displayer
			displayWorldOilMap.repaint();  // redraw the display
			displayWorldDNGasMap.reset();    // reschedule the displayer
			displayWorldDNGasMap.repaint();  // redraw the display
		}
	
	}

	// To load 'serializable' states - Fix it!
	public void load(SimState state) 
	{
		super.load(state);		
	}
	/**
	 * @param modelACEGES the modelACEGES to set
	 */
	public void setModelACEGES(ACEGESApp modelACEGES) {
		this.modelACEGES = modelACEGES;
	}

	/**
	 * @return the modelACEGES
	 */
	public ACEGESApp getModelACEGES() {
		return modelACEGES;
	}

	//this gives the 'model' tab in the consol.
    public Object getSimulationInspectedObject()
    {
    	return state;
    }
    
    public void finish()
    {
    	super.finish();
    	//********* which the line below OFF when you run only GAS!
    	this.modelACEGES.resetModelParameters();
    	if (!isApplet)
    	{
    		myFileOilReported.simulationNumber = myFileOilReported.simulationNumber + 1;
    		myFileDNGasReporter.simulationNumber = myFileDNGasReporter.simulationNumber+1;
    	}  	
    }
	
	public void quit() 
	{
		super.quit();
		if (this.myFileOilReported!=null){myFileOilReported.closeCSVFile();}	
		if (this.myFileDNGasReporter!=null){myFileDNGasReporter.closeCSVFile();}	
		if (this.modelACEGES.rConnection !=null){this.modelACEGES.rConnection.shutDown();}
		//if (this.mathamticaLink!=null){mathamticaLink.shutDown();}
		if (!isApplet){System.exit(0);}
	}

	public Inspector getInspector()
	{
		Inspector i = super.getInspector(); 
		i.setVolatile(true); 
		return i; 
	}
}
