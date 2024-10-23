package com.example.journalApp.Entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Getter
@Setter
@Builder
public class User {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    @NonNull
    private String username;
    // by default, @Indexed won't be taking only unique usernames,
    // we have to configure it inside application.properties file

    @NonNull
    private String password;

    @DBRef
    private List<JournalEntry> journalEntryList = new ArrayList<>();
    // using this annotation, we can connect the journal db with users db.
    // basically here we are creating reference of journal entries in user collection.
    // so the journal entry list is acting like a foreign key of the user collection.

    private List<String> roles;
}
