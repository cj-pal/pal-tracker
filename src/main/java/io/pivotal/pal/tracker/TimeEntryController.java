package io.pivotal.pal.tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.pal.trackerapi.TimeEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@RestController
public class TimeEntryController {
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private final TimeEntryRepository timeEntryRepository;

    public TimeEntryController(TimeEntryRepository timeEntryRepository) {
        this.timeEntryRepository = timeEntryRepository;
    }

    @PostMapping(path = "time-entries", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntry) {
        TimeEntry entry = timeEntryRepository.create(timeEntry);
        return ResponseEntity.created(URI.create("/time-entries/" + entry.getId())).body(entry);
    }

    @DeleteMapping(path = "time-entries/{id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable long id) {
        TimeEntry entry = timeEntryRepository.find(id);
        timeEntryRepository.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(entry);
    }

    @PutMapping(path = "time-entries/{id}")
    public ResponseEntity<TimeEntry> update(@PathVariable long id, @RequestBody TimeEntry timeEntry) {
        try {
            TimeEntry update = timeEntryRepository.update(id, timeEntry);
            return update != null ?
                    ResponseEntity.ok(update) :
                    ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "time-entries")
    public  ResponseEntity<List<TimeEntry>> list() throws Exception {
        return ResponseEntity.ok(timeEntryRepository.list());
    }

    @GetMapping(path = "time-entries/{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable long id) {
        TimeEntry entry = timeEntryRepository.find(id);
        if (entry != null) {
            return ResponseEntity.ok(timeEntryRepository.find(id));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
