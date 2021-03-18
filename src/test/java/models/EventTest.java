package models;


import org.junit.jupiter.api.Test;
import utils.Parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Created by Chaklader on Mar, 2021
 */
class EventTest {


    @Test
    public void whenCreateEventWithCharacterAndIndex_matchesCreatedNewEvent() {

        Event event = Event.createNewEvent('A', 1);

        assertEquals('A', event.getItem());
        assertEquals(1, event.getId());

        boolean isDateFormatMatched = Parameters.DATE_TIME_FORMAT.matcher(event.getTimeStamp()).matches();
        assertTrue(isDateFormatMatched);
    }

}