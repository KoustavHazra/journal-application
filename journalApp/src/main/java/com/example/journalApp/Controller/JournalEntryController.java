package com.example.journalApp.Controller;

import com.example.journalApp.Entity.JournalEntry;
import com.example.journalApp.Entity.User;
import com.example.journalApp.Services.JournalEntryServices;
import com.example.journalApp.Services.UserServices;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {


    @Autowired
    private JournalEntryServices journalEntryServices;
    @Autowired
    private UserServices userServices;

    @GetMapping("all-entries")
    public ResponseEntity<List<JournalEntry>> getAllJournalEntriesOfUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        List<JournalEntry> journalEntries = journalEntryServices.getAllEntries(username);
        if (journalEntries != null && !journalEntries.isEmpty()) {
            return new ResponseEntity<>(journalEntries, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("add-entry")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry newEntry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            journalEntryServices.addNewEntry(newEntry, username);
            return new ResponseEntity<>(newEntry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("id/{journalId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId journalId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        // getting authenticated user
        User user = userServices.findByUsername(username);
        // checking if the journal id passed belongs to the user's journal list
        List<JournalEntry> entryCollection = user.getJournalEntryList().stream().filter(x -> x.getId().equals(journalId)).collect(Collectors.toList());

        if (!entryCollection.isEmpty()) {
            Optional<JournalEntry> entry = journalEntryServices.getEntryById(journalId);
            return new ResponseEntity<>(entry.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("id/{journalId}")
    public ResponseEntity<JournalEntry> updateJournalEntry(@PathVariable ObjectId journalId,
                                                           @RequestBody JournalEntry updatedEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        // getting authenticated user
        User user = userServices.findByUsername(username);
        // checking if the journal id passed belongs to the user's journal list
        List<JournalEntry> entryCollection = user.getJournalEntryList().stream().filter(x -> x.getId().equals(journalId)).collect(Collectors.toList());

        if (!entryCollection.isEmpty()) {
            JournalEntry oldEntry = journalEntryServices.getEntryById(journalId).orElse(null);
            if (oldEntry != null) {
                oldEntry.setTitle(
                        updatedEntry.getTitle() != null && !updatedEntry.getTitle().isEmpty()
                                ? updatedEntry.getTitle() : oldEntry.getTitle()
                );

                oldEntry.setContent(
                        updatedEntry.getContent() != null && !updatedEntry.getContent().isEmpty()
                                ? updatedEntry.getContent() : oldEntry.getContent()
                );

                if (!Objects.equals(oldEntry.getDate(), LocalDateTime.now())) {
                    oldEntry.setDate(LocalDateTime.now());
                }
            }

            journalEntryServices.addNewEntry(oldEntry);
            return new ResponseEntity<>(oldEntry, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("id/{journalId}")
    public ResponseEntity<?> deleteJournal(@PathVariable ObjectId journalId) {
        // <?> is like a wildcard, which means we can pass any type of data.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        boolean isRemoved = journalEntryServices.deleteEntryById(journalId, username);
        if (isRemoved) {
            return new ResponseEntity<>("Entry is deleted with journalId :: " + journalId.toString(),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>("There is no journal existing with the passed journal Id.",
                    HttpStatus.NO_CONTENT);
        }
    }

}
