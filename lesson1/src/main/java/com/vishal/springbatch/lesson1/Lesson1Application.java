package com.vishal.springbatch.lesson1;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBatchProcessing
public class Lesson1Application {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public JobExecutionDecider decider() {
		return new DeliveryDecider();
	}
	
	@Bean
	public JobExecutionDecider receiptDecider() {
		return new ReceiptDecider();
	}


	@Bean
	public Step packageItemStep() {

		return this.stepBuilderFactory.get("packageItemStep").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				String item = chunkContext.getStepContext().getJobParameters().get("item").toString();
				String date = chunkContext.getStepContext().getJobParameters().get("run.date").toString();

				System.out.println(String.format("The %s has been packed on %s", item, date));
				return RepeatStatus.FINISHED;
			}
		}).build();
	}

	@Bean
	public Step driveToAddressStep() {

		boolean got_last = false;
		return this.stepBuilderFactory.get("driveToAddress").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				if(got_last) {
					throw new RuntimeException("Got lost bro");
				}
				System.out.println("Successfully arrived at the address");
				return RepeatStatus.FINISHED;
			}
		}).build();
	}

	@Bean
	public Step givePackageToCustomerStep() {

		return this.stepBuilderFactory.get("givePackageToCustomer").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

				System.out.println("Successfully delivered package");
				return RepeatStatus.FINISHED;
			}
		}).build();
	}

	@Bean
	public Step storePackageStep() {

		return this.stepBuilderFactory.get("storePackage").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

				System.out.println("Storing the package while customer address is located");
				return RepeatStatus.FINISHED;
			}
		}).build();
	}
	
	@Bean
	public Step leaveAtDoorStep() {
		return this.stepBuilderFactory.get("leaveAtDoor").tasklet(new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("Left at the door step");
				return RepeatStatus.FINISHED;
			}
		}).build();
	}
	
	@Bean
	public Step refundStep() {
		return this.stepBuilderFactory.get("refundStep").tasklet(new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("Refunding customer money");
				return RepeatStatus.FINISHED;
			}
		}).build();
	}
	
	@Bean
	public Step thankCustomerStep() {
		return this.stepBuilderFactory.get("thankCustomerStep").tasklet(new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("Thanking the customer");
				return RepeatStatus.FINISHED;
			}
		}).build();
	}

	@Bean
	public Job deliveryPackageJob() {
		return this.jobBuilderFactory.get("deliveryPackageJob")
										.start(packageItemStep())
										.next(driveToAddressStep())
											.on("FAILED").to(storePackageStep())
										.from(driveToAddressStep())
											.on("*").to(decider())
												.on("PRESENT").to(givePackageToCustomerStep())
													.next(receiptDecider()).on("CORRECT").to(thankCustomerStep())
													.from(receiptDecider()).on("INCORRECT").to(refundStep())
											.from(decider())
												.on("NOT_PRESENT").to(leaveAtDoorStep())
										.end()
										.build();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Lesson1Application.class, args);
	}

}
