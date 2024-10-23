package com.example.journalApp.Controller;

import com.example.journalApp.Entity.JournalEntry;
import com.example.journalApp.Services.JournalEntryServices;
import com.example.journalApp.Services.UserServices;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {


    @Autowired
    private JournalEntryServices journalEntryServices;
    @Autowired
    private UserServices userServices;

    @GetMapping("{username}/all-entries")
    public ResponseEntity<List<JournalEntry>> getAllJournalEntriesOfUser(@PathVariable String username) {
        List<JournalEntry> journalEntries = journalEntryServices.getAllEntries(username);
        if (journalEntries != null && !journalEntries.isEmpty()) {
            return new ResponseEntity<>(journalEntries, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("{username}/add-entry")
    public ResponseEntity<JournalEntry> createEntry(@PathVariable String username, @RequestBody JournalEntry newEntry) {
        try {
            journalEntryServices.addNewEntry(newEntry, username);
            return new ResponseEntity<>(newEntry, HttpStatus.CREATED);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("id/{journalId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId journalId) {
        Optional<JournalEntry> entry = journalEntryServices.getEntryById(journalId);

        return entry.map(journalEntry -> new ResponseEntity<>(journalEntry, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("{username}/id/{journalId}")
    public ResponseEntity<JournalEntry> updateJournalEntry(@PathVariable String username,
                                                           @PathVariable ObjectId journalId,
                                                           @RequestBody JournalEntry updatedEntry) {
        try {
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
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("{username}/id/{journalId}")
    public ResponseEntity<?> deleteJournal(@PathVariable ObjectId journalId, @PathVariable String username) {
        // <?> is like a wildcard, which means we can pass any type of data.
        try {
            journalEntryServices.deleteEntryById(journalId, username);
            return new ResponseEntity<>("Entry is deleted with journalId :: " + journalId.toString(),
                                        HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>("There is no journal existing with the passed journal Id.",
                                        HttpStatus.NO_CONTENT);
        }
    }

}
