package gov.usgs.aqcu.calc;

import gov.usgs.aqcu.model.ExtremesPoint;
import gov.usgs.aqcu.model.TimeSeriesCorrectedData;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.time.temporal.Temporal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;

/**
 * Given a list of TimeSeries objects (ordered), this class will allow finding
 * min/max points for any of the TimeSeries, and also allow extracting corresponding
 * points from any of the other TimeSeries.
 * 
 * EG: given TimeSeries A, B, C, D you can...
 * - get the value in D at the same time of the max value of A, B, or C
 * 
 * @author thongsav-usgs
 */
public class MinMaxFinder {
	private static final Logger log = LoggerFactory.getLogger(MinMaxFinder.class);

	private final Map<String, TimeSeriesCorrectedData> timeseries;

	private final MinMaxSummary calculatedSummary;
	
	/**
	 * Constructor for min max finder taking a map of timeseries to use in calculations
	 * 
	 * @param inTimeseries The map of timeseries to compare
	 */
	public MinMaxFinder(final Map<String, TimeSeriesCorrectedData> inTimeseries) {
		log.trace("MinMaxFinder initialized");
		this.timeseries = Collections.unmodifiableMap(inTimeseries);

		//calculate summary
		calculatedSummary = calculateMinMaxSummary(this.timeseries);
	}
	
	/**
	 * 
	 * @return Returns the calculated min-max summary of the input timeseries
	 */
	public MinMaxSummary getCalculatedSummary() {
		return calculatedSummary;
	}
	
	/**
	 * Calculates a min-max summary of all of the provided timeseries
	 * 
	 * @param timeseries The timeseries to calculate the min-max summary against
	 * @return The calculated min-max summary of the provided timeseries
	 */
	public static MinMaxSummary calculateMinMaxSummary(Map<String, TimeSeriesCorrectedData> timeseries) {
		Map<String, TimeSeriesSummary> summaryBySeries = new HashMap<>();
		
		for (String series : timeseries.keySet()) {
			TimeSeriesSummary summary = new TimeSeriesSummary();
			List<TimeSeriesSummary> summaries = TimeSeriesSummary.calculateSummaries(timeseries, series);
			if (null != summaries && !summaries.isEmpty()) {
				summary = summaries.get(0);
			}
			summaryBySeries.put(series, summary);
		}

		return new MinMaxSummary(summaryBySeries);
	}

	protected static enum Units {
		AMERICAN,
		METRIC;
	}
	
	/**
	 * Stores and calculates a min-max summary of all provided timeseries.
	 * A min-max summary includes the mins and maxes of each timeseries and the 
	 * associated values of all other time series at those times. i.e:
	 * 
	 * A Max: 2012-04-17 12:52:52 | 50
	 * Associated Values: B: 23 | C: 10 | D: 33
	 * 
	 * A Min: 2012-03-01 07:56:12 | 3
	 * Associated Values: B: 19 | C: 01 | D: 28
	 * 
	 * B Max: [Date] | [Value]
	 * Associated Values: A: [Value] | C: [Value] | D: [Value]
	 * 
	 * ...
	 */
	public static class MinMaxSummary {

		private final Map<String, TimeSeriesSummary> seriesTimeSeriesPoints;

		/**
		 * Default constructor
		 */
		public MinMaxSummary() {
			this(null);
		}

		/**
		 * Constructor which creates the min-max summary with a specified set of
		 * TimeSeriesSummary objects.
		 * 
		 * @param seriesSummaries The map of TimeSeriesSummary objects to create 
		 * a min-max summary for.
		 */
		public MinMaxSummary(Map<String, TimeSeriesSummary> seriesSummaries) {
			this.seriesTimeSeriesPoints = Collections.unmodifiableMap(seriesSummaries);
		}

		/**
		 * Returns the time series points from the provided timeseries matching
		 * the provided comparator.
		 * 
		 * @param comparator The comparator to use for selecting the point from the specified timeseries
		 * @param getTimeSeriesPointSeries The time series identifier to get the points from
		 * @return
		 */
		public List<ExtremesPoint> get(OrderingComparators comparator, String getTimeSeriesPointSeries) {
			List<ExtremesPoint> result = null;
			result = this.seriesTimeSeriesPoints.get(getTimeSeriesPointSeries).get(comparator);
			return result;
		}

		/**
		 * Returns the time series points relative to the provided time series matching
		 * the provided comparator.
		 * 
		 * @param comparator The min/max comparator to use for selecting the point
		 * @param atTimeSeriesPointSeries The relative time series identifier
		 * @param getTimeSeriesPointSeries The identifier to use for getting the
		 * the points from the specified relative time series.
		 * @return The list of TimeSeriesPoint objects matching the provided parameters
		 */
		public List<ExtremesPoint> getAt(OrderingComparators comparator, String atTimeSeriesPointSeries, String getTimeSeriesPointSeries) {
			List<ExtremesPoint> result = null;
			result = this.seriesTimeSeriesPoints.get(atTimeSeriesPointSeries).getAt(comparator, getTimeSeriesPointSeries);
			return result;
		}
		
		/**
		 *
		 * @param getTimeSeriesPointSeries The timeseries identifier to get the start time for
		 * @return The start time for the specified timeseries
		 */
		public Temporal getStartTime(String getTimeSeriesPointSeries) {
			return this.seriesTimeSeriesPoints.get(getTimeSeriesPointSeries).getStartTime();
		}
		
		/**
		 *
		 * @param getTimeSeriesPointSeries The time series identifier to get the end time for
		 * @return The end time for the specified timeseries
		 */
		public Temporal getEndTime(String getTimeSeriesPointSeries) {
			return this.seriesTimeSeriesPoints.get(getTimeSeriesPointSeries).getEndTime();
		}
		
		/**
		 *
		 * @param getTimeSeriesPointSeries The timeseries identifier to get the qualifiers for
		 * @return The list of qualifiers for the specified timeseries
		 */
		
		public List<Qualifier> getQualifiers(String getTimeSeriesPointSeries) {
			return this.seriesTimeSeriesPoints.get(getTimeSeriesPointSeries).getQualifiers();
		}
		
	}

	/**
	 * A helper function to clean and properly deal with BigDecimal values
	 * 
	 * @param value The BigDecimal value to clean
	 * @return The proper BigDecimal value
	 */
	public static BigDecimal getAs(BigDecimal value) {
		return getAs(value, BigDecimal.ONE);
	}
	
	/**
	 * Multiplies a BigDecimal by the given BigDecimal conversion factor
	 * 
	 * @param value The value to multiply
	 * @param conversionFactor The conversion factor to multiply the value by
	 * @return The modified BigDecimal value
	 */
	public static BigDecimal getAs(BigDecimal value, BigDecimal conversionFactor) {
		BigDecimal result = null;
		
		//TODO, can we safely assume that null is 0.0?
		value = value == null ? BigDecimal.ZERO : value;
		
		result = value.multiply(conversionFactor);
		

		return result;
	}

}
