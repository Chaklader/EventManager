package models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Chaklader on Mar, 2021
 */
@Data
@NoArgsConstructor
@Slf4j
public class Event {

    Integer id;

    Character item;

    String timeStamp;


    /**
     * create new event based on the character item and message index
     */
    public static Event createNewEvent(char ch, int messageIndex) {

        Event event = new Event();

        int id = messageIndex + 1;
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());


        event.setItem(ch);
        event.setTimeStamp(timeStamp);
        event.setId(id);

        log.info("created a new event with id : "+ id);
        return event;
    }
}
