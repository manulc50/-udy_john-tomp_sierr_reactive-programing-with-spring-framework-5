package guru.springframework.sfgrestbrewery.web.mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

@Component
public class DateMapper {
	
	public OffsetDateTime asOffsetDateTime(LocalDateTime time) {
		if(time != null)
			return OffsetDateTime.of(time.getYear(),time.getMonthValue(),
					time.getDayOfMonth(),time.getHour(),time.getMinute(),
					time.getSecond(),time.getNano(),ZoneOffset.UTC);
		
		return null;
	}
	
	public LocalDateTime asTimestamp(OffsetDateTime offsetDateTime) {
		if(offsetDateTime != null)
			return offsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
		
		return null;
	}

}
