package com.linkedin.batch;

import org.springframework.batch.item.ItemProcessor;

public class FreeShippingItemProcessor implements ItemProcessor<TrackedOrder, TrackedOrder> {

	@Override
	public TrackedOrder process(TrackedOrder trackedOrder) throws Exception {

		if(trackedOrder.getCost().intValue() >= 80) {
			trackedOrder.setFreeShipping(true);
		}
		return trackedOrder;
	}

}
