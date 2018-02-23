package io.pivotal.pal.tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.pal.trackerapi.TimeEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class TimeEntryController {
    public static final String CREATE_ENTRY = "createEntry";
    public static final String DELETE_ENTRY = "deleteEntry";
    public static final String FIND_ENTRY = "findEntry";
    public static final String UPDATE_ENTRY = "updateEntry";
    public static final String LIST_ENTRIES = "listEntries";
    ObjectMapper mapper = new ObjectMapper();


    private final TimeEntryRepository timeEntryRepository;
    private final CounterService counterService;
    private final GaugeService gaugeService;


    @Autowired
    public TimeEntryController(TimeEntryRepository timeEntryRepository, CounterService counterService, GaugeService gaugeService) {
        this.timeEntryRepository = timeEntryRepository;
        this.counterService = counterService;
        this.gaugeService = gaugeService;
    }

    @PostMapping(path = "time-entries", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntry) {
        TimeEntry entry = timeEntryRepository.create(timeEntry);
        counterService.increment(CREATE_ENTRY);
        gaugeService.submit("timeEntries.count", timeEntryRepository.list().size());
        return ResponseEntity.created(URI.create("/time-entries/" + entry.getId())).body(entry);
    }

    @DeleteMapping(path = "time-entries/{id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable long id) {
        TimeEntry entry = timeEntryRepository.find(id);
        timeEntryRepository.delete(id);
        counterService.increment(DELETE_ENTRY);
        gaugeService.submit("timeEntries.count", timeEntryRepository.list().size());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(entry);
    }

    @PutMapping(path = "time-entries/{id}")
    public ResponseEntity<TimeEntry> update(@PathVariable long id, @RequestBody TimeEntry timeEntry) {
        try {
            TimeEntry update = timeEntryRepository.update(id, timeEntry);
            counterService.increment(UPDATE_ENTRY);
            return update != null ?
                    ResponseEntity.ok(update) :
                    ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "time-entries")
    public  ResponseEntity<List<TimeEntry>> list() throws Exception {
        counterService.increment(LIST_ENTRIES);
        return ResponseEntity.ok(timeEntryRepository.list());
    }

    @GetMapping(path = "time-entries/{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable long id) {
        counterService.increment(FIND_ENTRY);
        TimeEntry entry = timeEntryRepository.find(id);
        if (entry != null) {
            return ResponseEntity.ok(timeEntryRepository.find(id));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
