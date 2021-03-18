package stream;

import java.util.List;


/**
 * Created by Chaklader on Mar, 2021
 */
public interface ProducerTerminator {


    /*
     * condition: 1
     *
     * if the last produced 1000 characters, if 100 of them matches with the condition,
     * we will terminate producer.
     * */
    boolean checkIfThresholdAttained(List<Character> list);


    /**
     * condition: 2
     * <p>
     * find if this is unique list and the last 4 items of the list is same
     */
    boolean checkIfLastThreeItemsSame(List<Character> list);
}
