package processor;

import lombok.extern.slf4j.Slf4j;
import models.Event;

import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Chaklader on Mar, 2021
 */
@Slf4j
public class EventProcessor {


    private final List<Event> events;

    private final int sampleSize;

    private final Random random;


    public EventProcessor(List<Event> events, int sampleSize) {

        this.events = events;

        this.sampleSize = sampleSize;

        this.random = new Random();
    }


    public String getStringUsingConsumedCharacters() {

        List<Character> chars = events.stream().map(Event::getItem).collect(Collectors.toList());

        final var originalInput = chars.stream().map(String::valueOf).collect(Collectors.joining());

        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());

        return encodedString;
    }


    /**
     * create a random sample with sample size using the large
     * consumed string
     */
    public String createRandomSample(String consumedLargeString) {


        byte[] decodedBytes = Base64.getDecoder().decode(consumedLargeString);

        String decodedString = new String(decodedBytes);


        StringBuilder result = new StringBuilder();
        int N = decodedString.length();

        for (int i = 0; i < sampleSize; i++) {

            int index = random.nextInt(N);
            String str = decodedString.substring(index, index + 1);

            result.append(str);
        }

        return result.toString();
    }
}
