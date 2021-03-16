import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by Chaklader on Mar, 2021
 */
@Data
@NoArgsConstructor
public class Event {

    Integer id;

    Character item;

    Date date;

    public Event(Character item) {

        this.item = item;
    }
}
