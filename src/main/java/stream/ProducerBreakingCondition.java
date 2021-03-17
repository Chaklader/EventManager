package stream;

import java.util.List;



/**
 * Created by Chaklader on Mar, 2021
 */
public interface ProducerBreakingCondition {


    // add the definitions for all the producer breaking conditions below


    boolean checkIfThresholdAttained(List<Character> list);

    boolean checkIfLastThreeItemsSame(List<Character> list);
}
