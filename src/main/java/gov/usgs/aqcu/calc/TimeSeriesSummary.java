package gov.usgs.aqcu.calc;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;
import com.google.common.base.MoreObjects;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;

import gov.usgs.aqcu.model.ExtremesPoint;
import gov.usgs.aqcu.model.TimeSeriesCorrectedData;

/**
 * Produces a summarized version of the time series including min and max points.
 * 
 * @author 
 */
public class TimeSeriesSummary {
	
	private Temporal startTime;
	private Temporal endTime;
	private List<Qualifier> qualifiers;

	private final Map<OrderingComparators, List<ExtremesPoint>> theseExtremesPoints;
	private final Map<OrderingComparators, Map<String, List<ExtremesPoint>>> relativeExtremesPoints;

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
			Map<OrderingComparators, List<ExtremesPoint>> thisSummaries, 
			Map<OrderingComparators, Map<String, List<ExtremesPoint>>> relativeSummaries) {
		
		this.setStartTime(startTime);
		this.setEndTime(endTime);
		this.setQualifiers(qualifiers);
		
		if (null != thisSummaries) {
			this.theseExtremesPoints = Collections.unmodifiableMap(thisSummaries);
		} else {
			this.theseExtremesPoints = Collections.<OrderingComparators, List<ExtremesPoint>>emptyMap();
		}

		if (null != relativeSummaries) {
			this.relativeExtremesPoints = Collections.unmodifiableMap(relativeSummaries);
		} else {
			Map<OrderingComparators, Map<String, List<ExtremesPoint>>> relative = new LinkedHashMap<>();
			relative.put(OrderingComparators.MAX, Collections.<String, List<ExtremesPoint>>emptyMap());
			relative.put(OrderingComparators.MIN, Collections.<String, List<ExtremesPoint>>emptyMap());
			this.relativeExtremesPoints = Collections.unmodifiableMap(relative);
		}
	}

	/**
	 * Returns the time series points associated with the provided comparator (min or max).
	 * 
	 * @param comparator The comparator to use to select a series (min or max)
	 * @return The list of ExtremesPoint objects associated with the provided comparator
	 */
	public List<ExtremesPoint> get(OrderingComparators comparator) {
		List<ExtremesPoint> result = null;
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
	public List<ExtremesPoint> getAt(OrderingComparators comparator, String getExtremesPointSeries) {
		List<ExtremesPoint> result = null;
		result = relativeExtremesPoints.get(comparator).get(getExtremesPointSeries);
		return result;
	}

	/**
	 * Summarizes a time series based on the supplied parameters and optionally calculates
	 * related points to each keyed summary point.
	 * 
	 * @param timeseries The map of timeseries and identifiers to calculate the summary for
	 * @param seriesIdentifier The specific timeseries identifier to use when calculating summaries
	 * @param calculateRelativeSummary Whether or not relative point summaries should be calculated
	 * @param effectiveInterval The date & time range to calculate the summary within
	 * @param topValuesCount How many of the top values of each summary are kept
	 * @return A list of the calculated time series summaries 
	 */
	public static List<TimeSeriesSummary> calculateSummaries(final Map<String, TimeSeriesCorrectedData> timeseries, final String seriesIdentifier, final boolean calculateRelativeSummary, final int topValuesCount) {
		List<TimeSeriesSummary> result = new ArrayList<>();

		TimeSeriesCorrectedData seriesData = timeseries.get(seriesIdentifier);
		if (0 < topValuesCount && seriesData != null && seriesData.getPoints().size() > 0) {
			Map<OrderingComparators, List<List<ExtremesPoint>>> combinedSeriesSummary = new LinkedHashMap<>();
			Map<OrderingComparators, List<Map<String, List<ExtremesPoint>>>> combinedRelativeSummaries = new LinkedHashMap<>();
			Integer comparatorWinnersSize = null;
			
			//find MIN/MAX (and all other comparator) values, placing results in combinedSeriesSummary
			for (OrderingComparators comparator : OrderingComparators.values()) {
				List<ExtremesPoint> sortedPoints = FluentIterable.from(seriesData.getPoints())
						.toSortedList(new CompareByValue(comparator.getOrder()));

				//filter down to top N GROUPs (by value)
				List<List<ExtremesPoint>> comparatorWinnerGroups = new ArrayList<>();
				List<ExtremesPoint> lastGroup = null;
				for(int i = 0; i < sortedPoints.size() - 1; i++) {
					if(lastGroup == null) {
						lastGroup = new ArrayList<>();
						comparatorWinnerGroups.add(lastGroup);
					}
					
					lastGroup.add(sortedPoints.get(i));
					
					if(i < sortedPoints.size() - 1 && 
							!sortedPoints.get(i).getValue().equals(sortedPoints.get(i+1).getValue())) {
						if(comparatorWinnerGroups.size() >= topValuesCount) {
							break;
						} else {
							lastGroup = new ArrayList<>();
							comparatorWinnerGroups.add(lastGroup);
						}
					}
				}

				combinedSeriesSummary.put(comparator, comparatorWinnerGroups);
				if (null == comparatorWinnersSize) {
					comparatorWinnersSize = comparatorWinnerGroups.size();
				}
				
				if (calculateRelativeSummary) {
					List<Map<String, List<ExtremesPoint>>> winnersRelativeSummaries = new LinkedList<>();
					for (List<ExtremesPoint> currentSetTied : comparatorWinnerGroups) {
						winnersRelativeSummaries.add(calculateRelatedPoints(timeseries, seriesIdentifier, currentSetTied));
					}
					combinedRelativeSummaries.put(comparator, winnersRelativeSummaries);
				}
			}


			for (int i = 0; i < comparatorWinnersSize; i++) {
				Map<OrderingComparators, List<ExtremesPoint>> seriesSummary = new LinkedHashMap<>();
				Map<OrderingComparators, Map<String, List<ExtremesPoint>>> relativeSummaries = new LinkedHashMap<>();
				for (OrderingComparators comparator : combinedSeriesSummary.keySet()) {
					seriesSummary.put(comparator, combinedSeriesSummary.get(comparator).get(i));
					if (calculateRelativeSummary) {
						relativeSummaries.put(comparator, combinedRelativeSummaries.get(comparator).get(i));
					}
				}
				
				result.add(new TimeSeriesSummary(null, null, 
				seriesData.getQualifiers(), seriesSummary, relativeSummaries));
			}
		}

		return result;
	}
	

	private static Map<String, List<ExtremesPoint>> calculateRelatedPoints(Map<String, TimeSeriesCorrectedData> timeseries, String series, List<ExtremesPoint> comparatorWinners) {
		Map<String, List<ExtremesPoint>> relatedExtremesPoints = new LinkedHashMap<>();
		for (String relatedSeries : timeseries.keySet()) {
			if (!relatedSeries.equals(series)) {
				TimeSeriesCorrectedData relatedData = timeseries.get(relatedSeries);
				List<ExtremesPoint> matchingPointsForSeries = new ArrayList<>(); 
				for(ExtremesPoint comparatorWinner : comparatorWinners) {
					int index = Arrays.binarySearch(relatedData.getPoints().toArray(new ExtremesPoint[0]), comparatorWinner, new CompareByTime(Ordering.natural()));
					if (0 <= index && index < relatedData.getPoints().size()) {
						matchingPointsForSeries.add(relatedData.getPoints().get(index));
					}
				}
				
				if(matchingPointsForSeries.size() > 0) {
					relatedExtremesPoints.put(relatedSeries, matchingPointsForSeries);
				}
			}
		}
		return relatedExtremesPoints;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TimeSeriesSummary rhs = (TimeSeriesSummary) obj;
		boolean result1 = Objects.equals(this.theseExtremesPoints, rhs.theseExtremesPoints);
		boolean result2 = Objects.equals(this.relativeExtremesPoints, rhs.relativeExtremesPoints);
		return result1 && result2;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.theseExtremesPoints, this.relativeExtremesPoints);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("points", this.theseExtremesPoints)
				.add("relativePoints", this.relativeExtremesPoints)
				.toString();
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
	
	/**
	 * Compares ExtremesPoint objects by their time values
	 */
	public static class CompareByTime implements Comparator<ExtremesPoint> {

		protected final Ordering order;

		/**
		 * Creates the compare by time comparator with the supplied order
		 * @param order The order to compare
		 */
		public CompareByTime(Ordering order) {
			this.order = order;
		}

		@Override
		public int compare(ExtremesPoint o1, ExtremesPoint o2) {
			try {
				Temporal rawt1 = o1.getTime();
				Temporal rawt2 = o2.getTime();
				
				Temporal t1 = rawt1;
				Temporal t2 = rawt2;
				
				//Any date being compared to a offset date time will have T00:00:00+offset where
				//offset is the offset of the time it's being compared to appended to it.
				if(rawt1 instanceof LocalDate && rawt2 instanceof OffsetDateTime) {
					t1 = copyOffsetAndTime((LocalDate) rawt1, (OffsetDateTime) rawt2);
				} else if(rawt2 instanceof LocalDate && rawt1 instanceof OffsetDateTime) {
					t2 = copyOffsetAndTime((LocalDate) rawt2, (OffsetDateTime) rawt1);
				} 
				
				return order.compare(t1, t2);
				
			} catch (NullPointerException e) {
				if (isTimeNulled(o1)  && isTimeNulled(o2)) {
					return 0;
				} else if (isTimeNulled(o1)) {
					return 1;
				} else {
					return -1;
				}

			}
		}
		private boolean isTimeNulled(ExtremesPoint o) {
			return o == null || o.getTime() == null;
		}
		
		private OffsetDateTime copyOffsetAndTime(LocalDate date, OffsetDateTime timeToCopy) {
			return OffsetDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0, 0, 0, timeToCopy.getOffset());
		}
		
	}

	/**
	 * Compares ExtremesPoint objects by their value
	 */
	public static class CompareByValue implements Comparator<ExtremesPoint> {

		protected final Ordering order;

		/**
		 * Creates the comparator with the supplied order
		 * @param order The order to compare
		 */
		public CompareByValue(Ordering order) {
			this.order = order;
		}

		@Override
		public int compare(ExtremesPoint o1, ExtremesPoint o2) {
			try {
				return order.compare(o1.getValue(), o2.getValue());
			} catch (NullPointerException e) {
				if (isValueNulled(o1) && isValueNulled(o2)) {
					return 0;
				} else if (isValueNulled(o1)) {
					return 1;
				} else {
					return -1;
				}

			}
		}
		private boolean isValueNulled(ExtremesPoint o) {
			return o == null || o.getValue() == null;
		}
		
	}
}
