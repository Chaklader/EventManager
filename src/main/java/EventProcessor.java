import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Chaklader on Mar, 2021
 */
public class EventProcessor {


    private final List<Event> events;

    private final int sampleSize;

    private final Random random;


    public EventProcessor(List<Event> events, int sampleSize) {

        this.events = events;

        this.sampleSize = sampleSize;

        this.random = new Random();
    }


    protected String getStringUsingConsumedCharacters() {

        List<Character> chars = events.stream().map(Event::getItem).collect(Collectors.toList());

        return chars.stream()
                   .map(String::valueOf)
                   .collect(Collectors.joining());
    }


    /**
     * create a random sample with sample size using the large consumed string
     */
    protected String createRandomSample(String consumedLargeString) {

        StringBuilder result = new StringBuilder();
        int N = consumedLargeString.length();

        for (int i = 0; i < sampleSize; i++) {

            int index = random.nextInt(N);
            String str = consumedLargeString.substring(index , index + 1);

            result.append(str);
        }

        return result.toString();
    }
}
