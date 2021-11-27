package com.vishal.springbatch.lesson1;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class FlowersSelectionStepExecutionListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		System.out.println("Executing before step logic");
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		System.out.println("Executing after step logic");
		String flowerTypes = stepExecution.getJobParameters().getString("type");
		return flowerTypes.equalsIgnoreCase("roses") ? new ExitStatus("TRIM_REQUIRED") : new ExitStatus("NO_TRIM_REQUIRED");
	}

}
