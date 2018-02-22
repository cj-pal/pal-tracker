package io.pivotal.pal.tracker;

import io.pivotal.pal.trackerapi.TimeEntry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

@Component
public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate jdbcTemplate;
    private static final String INSERT_ENTRY =
            "INSERT INTO time_entries (project_id, user_id, date, hours) VALUES (?, ?, ?, ?)";
    private static final String FIND_BY_ID = "SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id = ?";
    private static final String UPDATE_BY_ID = "UPDATE time_entries SET project_id = ?, user_id = ?, date = ?, hours = ? WHERE id = ?";
    private static final String DELETE_BY_ID = "DELETE from time_entries WHERE id = ?";
    private static final String LIST_ALL = "SELECT * FROM time_entries";

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ENTRY, RETURN_GENERATED_KEYS);

            preparedStatement.setLong(1, timeEntry.getProjectId());
            preparedStatement.setLong(2, timeEntry.getUserId());
            preparedStatement.setDate(3, Date.valueOf(timeEntry.getDate()));
            preparedStatement.setInt(4, timeEntry.getHours());

            return preparedStatement;
        }, generatedKeyHolder);

        return find(generatedKeyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long id) {

        return jdbcTemplate.query(
                FIND_BY_ID,
                new Object[]{id},
                extractor);
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update(DELETE_BY_ID, id);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        TimeEntry entryFound = find(id);
        if (entryFound != null) {
            jdbcTemplate.update(UPDATE_BY_ID, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours(), id);
            return find(id);
        }

        return create(timeEntry);
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query(LIST_ALL, mapper);
    }


    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );

    private final ResultSetExtractor<TimeEntry> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;
}
