package com.example.journalApp.Repository;

import com.example.journalApp.Entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    User findByUsername(String username);

    void deleteByUsername(String name);
}

/*
What happens in this interface ??

In the runtime, spring boot injects the implementation of these repositories wherever we are using it, such as
in the service class (where we have used a bean of it).

And the custom methods we have written here are called Query Method DSL, which means the method name is written
in such a way that it becomes a query. The query is obviously created by spring boot in the runtime.

But the catch here is we have to know the syntax of how to write the names of these methods... such as find by
name and email, or order by some value, or get a range of values, or using any less than / greater than on the
data --- these we have to know the syntax of how to write the method names, otherwise it won't work.

And also here we are bounded by its limitation, especially when we need to write some more complex queries. So,
to write complex queries and to control this in our way (so that we have the flexibility of using these)... we can
go for Criteria.

 */