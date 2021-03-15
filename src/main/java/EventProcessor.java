import java.util.List;

/**
 * Created by Chaklader on Mar, 2021
 */
public class EventProcessor {

    private List<Event> events;

    int size;

    public EventProcessor(List<Event> events, int size) {
        this.events = events;
        this.size = size;
    }


    protected String createString() {

        String s = "";

        for (Event event : events) {

            String str = event.getC() + "";
            s+= str;
        }

        return s;
    }

    protected String createSample(String s){

        return "EMETN";
    }
}
