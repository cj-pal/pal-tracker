package io.pivotal.pal.tracker;

import io.pivotal.pal.trackerapi.TimeEntry;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private AtomicLong idGenerator = new AtomicLong(1l);
    private Map<Long, TimeEntry> repository = new HashMap<>();

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        timeEntry.setId(idGenerator.getAndIncrement());
        repository.put(timeEntry.getId(), timeEntry);
        return timeEntry;
    }

    @Override
    public TimeEntry find(long id) {
        return this.repository.get(id);
    }

    @Override
    public void delete(long id) {
        repository.remove(id);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        if (repository.containsKey(id)) {
            timeEntry.setId(id);
            repository.put(id, timeEntry);
            return timeEntry;
        }
        throw new IllegalArgumentException("Entry does not exist");
    }

    @Override
    public List<TimeEntry> list() {
        return new ArrayList<>(repository.values());
    }
}
