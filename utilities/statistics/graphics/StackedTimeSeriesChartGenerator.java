package aceges.utilities.statistics.graphics;

import java.awt.Component;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.data.general.AbstractSeriesDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import sim.util.media.chart.ChartGenerator;
import sim.util.media.chart.SeriesAttributes;
import sim.util.media.chart.TimeSeriesAttributes;

public class StackedTimeSeriesChartGenerator extends ChartGenerator {
	protected XYSeriesCollection dataset;

	protected ArrayList stoppables = new ArrayList();




	DatasetChangeEvent updateEvent;

	public void update() {
		if (updateEvent == null)
			updateEvent = new DatasetChangeEvent(chart.getPlot(), null);
		chart.getPlot().datasetChanged(updateEvent);
	}

	public void removeSeries(int index) {
		// stop the inspector....
		Object tmpObj = stoppables.remove(index);
		if ((tmpObj != null) && (tmpObj instanceof SeriesChangeListener))
			((SeriesChangeListener) tmpObj).seriesChanged(new SeriesChangeEvent(this));

		// remove from the dataset. This is easier done in some JFreeChart plots
		// than others, dang coders
		dataset.removeSeries(index);

		// remove the attribute
		seriesAttributes.remove(index);

		// shift all the seriesAttributes' indices down so they know where they
		// are
		Component[] c = seriesAttributes.getComponents();
		for (int i = index; i < c.length; i++) // do for just the components >=
		// index in the seriesAttributes
		{
			if (i >= index) {
				SeriesAttributes csa = (SeriesAttributes) (c[i]);
				csa.setSeriesIndex(csa.getSeriesIndex() - 1);
				csa.rebuildGraphicsDefinitions();
			}
		}
		revalidate();
	}

	public void moveSeries(int index, boolean up) {
		java.util.List allSeries = dataset.getSeries();
		int count = allSeries.size();

		if ((index > 0 && up) || (index < count - 1 && !up)) // it's not the
		
		{
			// this requires removing everything from the dataset and
			// resinserting, duh
			ArrayList items = new ArrayList(allSeries);
			dataset.removeAllSeries();

			int delta = up ? -1 : 1;
			// now rearrange
			items.add(index + delta, items.remove(index));

			// rebuild the dataset
			for (int i = 0; i < count; i++)
				dataset.addSeries(((XYSeries) (items.get(i))));

			// adjust the seriesAttributes' indices
			Component[] c = seriesAttributes.getComponents();
			SeriesAttributes csa;
			(csa = (SeriesAttributes) c[index]).setSeriesIndex(index + delta);
			csa.rebuildGraphicsDefinitions();
			(csa = (SeriesAttributes) c[index + delta]).setSeriesIndex(index);
			csa.rebuildGraphicsDefinitions();

			seriesAttributes.remove(index + delta);
			// seriesAttributes.add((SeriesAttributes)(c[index+delta]), index);
			seriesAttributes.add(csa, index);

			revalidate();

			// adjust the stoppables, too
			stoppables.add(index + delta, stoppables.remove(index));
		}

	}

	protected void buildChart() {
		
		dataset = new XYSeriesCollection();
		chart = ChartFactory.createXYAreaChart("Untitled Chart", "Year", "Untitled Y Axis", dataset,
				PlotOrientation.VERTICAL, false, true, false);
		chart.setAntiAlias(true);
		chartPanel = new ChartPanel(chart, true);
		chartPanel.setPreferredSize(new java.awt.Dimension(640, 480));
		chartPanel.setMinimumDrawHeight(10);
		chartPanel.setMaximumDrawHeight(2000);
		chartPanel.setMinimumDrawWidth(20);
		chartPanel.setMaximumDrawWidth(2000);
		chartHolder.getViewport().setView(chartPanel);		
	}

	public int addSeries(final XYSeries series, final org.jfree.data.general.SeriesChangeListener stopper) {
		int i = dataset.getSeriesCount();
		dataset.addSeries(series);
		TimeSeriesAttributes csa = new TimeSeriesAttributes(this, series, i, null);
		seriesAttributes.add(csa);
		stoppables.add(stopper);
		revalidate();
		return i;
	}
	


	public XYPlot getPlot() {
		return (XYPlot) chart.getPlot();
	}

	

}
