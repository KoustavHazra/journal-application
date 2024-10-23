package com.example.journalApp.Services;

import com.example.journalApp.Entity.JournalEntry;
import com.example.journalApp.Entity.User;
import com.example.journalApp.Repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryServices {

    // dependency injection
    @Autowired
    private JournalEntryRepository journalEntryRepository;
    @Autowired
    private UserServices userServices;

    // WHEN USER NOT EXISTS -- THAT SCENARIO IS NOT HANDLED !!

    @Transactional
    public void addNewEntry(JournalEntry newEntry, String username) {
        try {
            // saving the new entry in journal collection
            newEntry.setDate(LocalDateTime.now());
            JournalEntry savedEntry = journalEntryRepository.save(newEntry);

            // saving the new entry in the specific user collection
            User user = userServices.findByUsername(username);
            user.getJournalEntryList().add(savedEntry);  // saving the reference of the journal collection inside users
            userServices.saveUserWithoutPasswordEncoding(user);
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException("Error occurred while creating a new entry :: " + e);
        }
    }

    public void addNewEntry(JournalEntry newEntry) {
        journalEntryRepository.save(newEntry);
    }

    public List<JournalEntry> getAllEntries(String username) {
        User user = userServices.findByUsername(username);
        return user.getJournalEntryList();
    }

    public Optional<JournalEntry> getEntryById(ObjectId journalId) {
        return journalEntryRepository.findById(journalId);
    }

    @Transactional
    public boolean deleteEntryById(ObjectId journalId, String username) {
        boolean isRemoved = false;
        try {
            // at first delete from user collection, because it has the ref of the journal collection
            User user = userServices.findByUsername(username);
            isRemoved = user.getJournalEntryList().removeIf(x -> x.getId().equals(journalId));
            if (isRemoved) {
                userServices.saveUserWithoutPasswordEncoding(user);
                // once the ref from user table is deleted, then only delete the actual row
                journalEntryRepository.deleteById(journalId);
            }
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException("Error occurred while deleting an entry :: " + e);
        }
        return isRemoved;
    }

//    public List<JournalEntry> findJournalsByUsername(String username) {
//
//    }

}
