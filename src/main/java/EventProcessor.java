import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Chaklader on Mar, 2021
 */
public class EventProcessor {


    private final List<Event> events;

    private final int sampleSize;


    public EventProcessor(List<Event> events, int sampleSize) {

        this.events = events;
        this.sampleSize = sampleSize;
    }


    protected String getStringUsingConsumedCharacters() {

        List<Character> chars = events.stream().map(Event::getItem).collect(Collectors.toList());

        return chars.stream()
                   .map(String::valueOf)
                   .collect(Collectors.joining());

    }

    protected String createSample(String s) {

        System.out.println(sampleSize);

        return "EMETN";
    }
}
