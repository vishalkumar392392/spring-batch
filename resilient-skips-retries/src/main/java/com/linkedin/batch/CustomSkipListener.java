package com.linkedin.batch;

import org.springframework.batch.core.SkipListener;

public class CustomSkipListener implements SkipListener<Order, TrackedOrder> {

	@Override
	public void onSkipInRead(Throwable t) {

	}

	@Override
	public void onSkipInWrite(TrackedOrder item, Throwable t) {

	}

	@Override
	public void onSkipInProcess(Order item, Throwable t) {
		System.out.println("Skipping processing with id :" + item.getOrderId());
	}

}
