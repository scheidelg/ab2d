package gov.cms.ab2d.eventlogger.eventloggers.sql;

import gov.cms.ab2d.eventlogger.EventLoggingException;
import gov.cms.ab2d.eventlogger.LoggableEvent;
import gov.cms.ab2d.eventlogger.events.ApiResponseEvent;
import gov.cms.ab2d.eventlogger.utils.UtilMethods;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class ApiResponseEventMapper extends SqlEventMapper {
    private NamedParameterJdbcTemplate template;

    ApiResponseEventMapper(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @Override
    public void log(LoggableEvent event) {
        if (event.getClass() != ApiResponseEvent.class) {
            throw new EventLoggingException("Used " + event.getClass().toString() + " instead of " + ApiResponseEvent.class.toString());
        }
        ApiResponseEvent be = (ApiResponseEvent) event;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "insert into event_api_response " +
                " (time_of_event, user_id, job_id, response_code, response_string, description, request_id) " +
                " values (:time, :user, :job, :responseCode, :responseString, :description, :requestId)";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("time", UtilMethods.convertToUtc(be.getTimeOfEvent()))
                .addValue("user", be.getUser())
                .addValue("job", be.getJobId())
                .addValue("responseCode", be.getResponseCode())
                .addValue("responseString", be.getResponseString())
                .addValue("description", be.getDescription())
                .addValue("requestId", be.getRequestId());

        template.update(query, parameters, keyHolder);
        event.setId(SqlEventMapper.getIdValue(keyHolder));
    }

    @Override
    public ApiResponseEvent mapRow(ResultSet resultSet, int i) throws SQLException {
        ApiResponseEvent event = new ApiResponseEvent();
        event.setId(resultSet.getLong("id"));
        event.setTimeOfEvent(resultSet.getObject("time_of_event", OffsetDateTime.class));
        event.setUser(resultSet.getString("user_id"));
        event.setJobId(resultSet.getString("job_id"));

        event.setResponseCode(resultSet.getInt("response_code"));
        event.setResponseString(resultSet.getString("response_string"));
        event.setDescription(resultSet.getString("description"));
        event.setRequestId(resultSet.getString("request_id"));
        return event;
    }
}
