package io.pivotal.pal.tracker;

import io.pivotal.pal.trackerapi.TimeEntry;

import java.util.List;

public interface TimeEntryRepository {
    TimeEntry create(TimeEntry timeEntry);

    TimeEntry find(long id);

    void delete(long id);

    TimeEntry update(long id, TimeEntry timeEntry);

    List<TimeEntry> list();
}
