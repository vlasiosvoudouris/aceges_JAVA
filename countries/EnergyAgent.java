package aceges.countries;

import java.util.Hashtable;
import java.util.Vector;

import org.jfree.data.xy.XYSeries;

import aceges.ACEGESApp;

import ec.util.MersenneTwisterFast;
import sim.engine.SimState;
import sim.engine.Steppable;


public class EnergyAgent implements Steppable
{
	private String nameOfCountry=null;
	public String FIPS=null;
	public MersenneTwisterFast randonGenerator =  null;	
	/** records the data for graphical representation */
	public XYSeries  simulatedDNGasStackedProduction;
	/** records the data for graphical representation */
	public XYSeries  simulatedCrudeOilStackedProduction;
	/** records the data for graphical representation */
	public XYSeries  simulatedCrudeOilProduction;
	/** records the data for graphical representation */
	public XYSeries  simulatedDNGasProduction;
	/** records the data for graphical representation */
	public XYSeries  simulatedCrudeOilDemand;
	/** records the data for graphical representation */
	public XYSeries  simulatedDNGasDemand;
	public  ACEGESApp simModel;
	
	private static final long serialVersionUID = 1L;
	
	public EnergyAgent()
	{
		randonGenerator =  new MersenneTwisterFast();
		
	}
	public void step(SimState state) {}

	public void setName(String name) {
		this.nameOfCountry = name;
	}

	public String getName() {
		return nameOfCountry;
	}
	public String getFIPS() {
		return FIPS;
	}
	public void setFIPS(String fIPS) {
		FIPS = fIPS;
	}
	
	public double roundDecimals(double d) 
	{	
		int ix = (int)(d * 10000.0); // scale it 
		return ((double)ix)/10000.0;
	}
	
	public XYSeries initialiseXYSeriesDNGasStacked(String key)
	{
		simulatedDNGasStackedProduction = new XYSeries(key);
		return simulatedDNGasStackedProduction;
	}
	
	public XYSeries initialiseXYSeriesOilStacked(String key)
	{
		simulatedCrudeOilStackedProduction = new XYSeries(key);
		return simulatedCrudeOilStackedProduction;
	}
	
	public XYSeries initialiseXYSeriesDNGasProduction(String key)
	{
		simulatedDNGasProduction = new XYSeries(key);
		return simulatedDNGasProduction;
	}
	
	public XYSeries initialiseXYSeriesOilProduction(String key)
	{
		simulatedCrudeOilProduction = new XYSeries(key);
		return simulatedCrudeOilProduction;
	}
	
	public XYSeries initialiseXYSeriesOilDemand(String key)
	{
		simulatedCrudeOilDemand = new XYSeries(key);
		return simulatedCrudeOilDemand;
	}
	
	public XYSeries initialiseXYSeriesDNGasDemand(String key)
	{
		simulatedDNGasDemand = new XYSeries(key);
		return simulatedDNGasDemand;
	}
	
}