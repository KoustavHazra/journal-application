package com.example.journalApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class JournalAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(JournalAppApplication.class, args);
	}

	@Bean
	public PlatformTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
		return new MongoTransactionManager(dbFactory);
	}
}

/*

@EnableTransactionManagement tells spring boot app to look for those methods which have the
@Transaction annotation. Because of this spring will treat those methods as a single operation - no matter
how many multiple operations are being done in that method.
And if any of the operation fails, then spring will revert back all the changes occurred before that error
in that method.

Also, if two users at the same time triggers these methods at the same time, then spring will create two separate
containers for two users to handle operations separately. So that there isn't any mismatch in the data.

But who does all these works... all the commit or rollback ? This is done my the PlatformTransactionManager
interface. Since this is an interface, the implementation is done in another class, which is called as
MongoTransactionManager.

So in our project we have to mention that the implementation of PlatformTransactionManager is being done
in the MongoTransactionManager class.

 */