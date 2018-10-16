package gov.usgs.aqcu.calc;

import com.google.common.collect.Ordering;

/**
 * Stores min/max ordering comparators for maps
 * @author
 */
public enum OrderingComparators {

	MAX(Ordering.natural().reverse()),
	MIN(Ordering.natural());
	protected Ordering order;

	private OrderingComparators(Ordering order) {
		this.order = order;
	}

	/**
	 * 
	 * @return The current ordering
	 */
	public Ordering getOrder() {
		return this.order;
	}
}