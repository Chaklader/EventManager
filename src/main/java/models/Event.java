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
public final class Event {


    int id;

    char item;

    String timeStamp;


    /**
     * create new event based on the character item and message index
     */
    public static Event createNewEvent(char ch, int msgIndex) {

        Event event = new Event();

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());


        event.setItem(ch);

        event.setTimeStamp(timeStamp);

        event.setId(msgIndex);


        log.info("created a new event with id : " + msgIndex);
        return event;
    }

}
