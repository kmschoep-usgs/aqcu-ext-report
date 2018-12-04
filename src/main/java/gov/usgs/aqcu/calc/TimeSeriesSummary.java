package gov.usgs.aqcu.calc;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import gov.usgs.aqcu.model.TimeSeriesCorrectedData;
import gov.usgs.aqcu.util.DoubleWithDisplayUtil;

/**
 * Produces a summarized version of the time series including min and max points.
 * 
 * @author 
 */
public class TimeSeriesSummary {
	
	private Temporal startTime;
	private Temporal endTime;
	private List<Qualifier> qualifiers;

	private final Map<OrderingComparators, List<TimeSeriesPoint>> theseExtremesPoints;
	private final Map<OrderingComparators, Map<String, List<TimeSeriesPoint>>> relativeExtremesPoints;

	/**
	 * Default Constructor
	 */
	protected TimeSeriesSummary() {
		this(null, null, null, null, null);
	}

	/**
	 * Construct a TimeSeriesSummary with all of the required parameters.
	 * 
	 * @param startTime The start date & time of the summary period
	 * @param endTime The end date & time of the summary period
	 * @param qualifiers The qualifiers that apply during the summary period
	 * @param thisSummaries The map of time series points to summarize
	 * @param relativeSummaries The map of time series points that are relative to the primary provided set
	 */
	protected TimeSeriesSummary(Temporal startTime, Temporal endTime, List<Qualifier> qualifiers,
			Map<OrderingComparators, List<TimeSeriesPoint>> thisSummaries, 
			Map<OrderingComparators, Map<String, List<TimeSeriesPoint>>> relativeSummaries) {
		
		this.setStartTime(startTime);
		this.setEndTime(endTime);
		this.setQualifiers(qualifiers);
		
		if (null != thisSummaries) {
			this.theseExtremesPoints = Collections.unmodifiableMap(thisSummaries);
		} else {
			this.theseExtremesPoints = Collections.<OrderingComparators, List<TimeSeriesPoint>>emptyMap();
		}

		if (null != relativeSummaries) {
			this.relativeExtremesPoints = Collections.unmodifiableMap(relativeSummaries);
		} else {
			Map<OrderingComparators, Map<String, List<TimeSeriesPoint>>> relative = new LinkedHashMap<>();
			relative.put(OrderingComparators.MAX, Collections.<String, List<TimeSeriesPoint>>emptyMap());
			relative.put(OrderingComparators.MIN, Collections.<String, List<TimeSeriesPoint>>emptyMap());
			this.relativeExtremesPoints = Collections.unmodifiableMap(relative);
		}
	}

	/**
	 * Returns the time series points associated with the provided comparator (min or max).
	 * 
	 * @param comparator The comparator to use to select a series (min or max)
	 * @return The list of ExtremesPoint objects associated with the provided comparator
	 */
	public List<TimeSeriesPoint> get(OrderingComparators comparator) {
		List<TimeSeriesPoint> result = null;
		result = theseExtremesPoints.get(comparator);
		return result;
	}

	/**
	 * Returns the time series points relative to the supplied time series point
	 * for the given comparator.
	 * 
	 * @param comparator The comparator to use to select a series (min or max)
	 * @param getExtremesPointSeries The time series point to get other points relative to
	 * @return The list of ExtremesPoint objects associated with the provided comparator
	 * and relative to the provided ExtremesPoint. 
	 */
	public List<TimeSeriesPoint> getAt(OrderingComparators comparator, String getExtremesPointSeries) {
		List<TimeSeriesPoint> result = null;
		result = relativeExtremesPoints.get(comparator).get(getExtremesPointSeries);
		return result;
	}

	/**
	 * Summarizes a time series based on the supplied parameters and optionally calculates
	 * related points to each keyed summary point.
	 * 
	 * @param timeseries The map of timeseries and identifiers to calculate the summary for
	 * @param seriesIdentifier The specific timeseries identifier to use when calculating summaries
	 * @return A list of the calculated time series summaries 
	 */
	public static List<TimeSeriesSummary> calculateSummaries(final Map<String, TimeSeriesCorrectedData> timeseries, final String seriesIdentifier) {
		List<TimeSeriesSummary> result = new ArrayList<>();
		Map<OrderingComparators, List<TimeSeriesPoint>> seriesSummary = new LinkedHashMap<>();
		Map<OrderingComparators, Map<String, List<TimeSeriesPoint>>> combinedRelativeSummaries = new LinkedHashMap<>();
		Map<OrderingComparators, Map<String, List<TimeSeriesPoint>>> relativeSummaries = new LinkedHashMap<>();
		TimeSeriesCorrectedData seriesData = timeseries.get(seriesIdentifier);
		List<TimeSeriesPoint> listPoints = seriesData.getPoints();
		TimeSeriesPoint extremePoint = new TimeSeriesPoint();
		for (OrderingComparators comparator : OrderingComparators.values()) {
			if (listPoints.size() > 0) {
				if (comparator.equals(OrderingComparators.MAX)) {
					extremePoint =  Collections.max(listPoints, Comparator.comparing(s -> DoubleWithDisplayUtil.getRoundedValue(s.getValue())));
				} else {
					extremePoint =  Collections.min(listPoints, Comparator.comparing(s -> DoubleWithDisplayUtil.getRoundedValue(s.getValue())));
				}
			}
			final TimeSeriesPoint extremeValue = extremePoint;
			
			List<TimeSeriesPoint> matchPoints = listPoints.stream()
					.filter(point -> DoubleWithDisplayUtil.getRoundedValue(point.getValue()).equals(DoubleWithDisplayUtil.getRoundedValue(extremeValue.getValue())))
					.collect(Collectors.toList());
			seriesSummary.put(comparator, matchPoints);

			Map<String, List<TimeSeriesPoint>> winnersRelativeSummaries = new HashMap<>();
			winnersRelativeSummaries = calculateRelatedPoints(timeseries, seriesIdentifier, matchPoints);
			combinedRelativeSummaries.put(comparator, winnersRelativeSummaries);
	
			relativeSummaries.put(comparator, combinedRelativeSummaries.get(comparator));
			
			
			}
		result.add(new TimeSeriesSummary(null, null, 
				filterQualifiers(seriesSummary, seriesData.getQualifiers()), seriesSummary, relativeSummaries));
		
		return result;
	}

	private static Map<String, List<TimeSeriesPoint>> calculateRelatedPoints(Map<String, TimeSeriesCorrectedData> timeseries, String series, List<TimeSeriesPoint> comparatorWinners) {
		Map<String, List<TimeSeriesPoint>> relatedExtremesPoints = new LinkedHashMap<>();
		for (String relatedSeries : timeseries.keySet()) {
			if (!relatedSeries.equals(series)) {
				TimeSeriesCorrectedData relatedData = timeseries.get(relatedSeries);
				List<TimeSeriesPoint> listRelatedPoints = relatedData.getPoints();
				List<TimeSeriesPoint> matchingPointsForSeries = new ArrayList<>(); 
				for (TimeSeriesPoint comparatorWinner : comparatorWinners) {
					List<TimeSeriesPoint> matchingPointsForTime = listRelatedPoints.stream()
						.filter(point -> point.getTimestamp().getDateTimeOffset().equals(comparatorWinner.getTimestamp().getDateTimeOffset()))
						.collect(Collectors.toList());
					if(matchingPointsForTime.size() > 0) {
						matchingPointsForSeries.addAll(matchingPointsForTime);
					}
				}
				
				if(matchingPointsForSeries.size() > 0) {
					relatedExtremesPoints.put(relatedSeries, matchingPointsForSeries);
				}
			}
		}
		return relatedExtremesPoints;
	}

	public static List<Qualifier> filterQualifiers(Map<OrderingComparators, List<TimeSeriesPoint>> seriesSummaryPoints, List<Qualifier> qualifiers){
		List<TimeSeriesPoint> extremesPoints = new ArrayList<>();
		List<Qualifier> results = new ArrayList<>();
		if (qualifiers != null) {
		extremesPoints.addAll(seriesSummaryPoints.get(OrderingComparators.MAX));
		extremesPoints.addAll(seriesSummaryPoints.get(OrderingComparators.MIN));
			for (TimeSeriesPoint point: extremesPoints) {
				List<Qualifier> pointResult = new ArrayList<>();
				pointResult = qualifiers.stream()
						.filter(s -> !results.contains(s))
						.filter(
								q -> (q.getStartTime().isBefore(point.getTimestamp().getDateTimeOffset()) || q.getStartTime().equals(point.getTimestamp().getDateTimeOffset())) 
								&& (q.getEndTime().isAfter(point.getTimestamp().getDateTimeOffset()) || q.getEndTime().equals(point.getTimestamp().getDateTimeOffset()))
								)
						.collect(Collectors.toList());
				if (!pointResult.isEmpty() && pointResult != null) {
					results.addAll(pointResult);
				}
			}
		}
		return results;
	}
	/**
	 *
	 * @return The start time of the summary period
	 */
	public Temporal getStartTime() {
		return startTime;
	}

	/**
	 *
	 * @param startTime The start time to set for the summary period
	 */
	public void setStartTime(Temporal startTime) {
		this.startTime = startTime;
	}

	/**
	 *
	 * @return The end time of the summary period
	 */
	public Temporal getEndTime() {
		return endTime;
	}

	/**
	 *
	 * @param endTime The end time to set for the summary period
	 */
	public void setEndTime(Temporal endTime) {
		this.endTime = endTime;
	}

	/**
	 *
	 * @return The list of qualifiers applied during the summary period
	 */
	public List<Qualifier> getQualifiers() {
		return qualifiers;
	}

	/**
	 *
	 * @param qualifiers The list of qualifiers to set for the summary period
	 */
	public void setQualifiers(List<Qualifier> qualifiers) {
		this.qualifiers = qualifiers;
	}
	
}
