package stream;

import utils.Parameters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static utils.Parameters.LIST_SIZE;


/**
 * Created by Chaklader on Mar, 2021
 */
public class ProducerBreakingConditionImpl implements ProducerBreakingCondition {


    private static final Logger LOG = Logger.getLogger(ProducerBreakingConditionImpl.class.getName());



    /**
     * this base condition will check all the breaking conditions and inform the producer
     */
    public boolean checkProducerBreakingConditions(List<Character> characters) {


        boolean isTerminate = characters.size() % LIST_SIZE == 0 && checkIfThresholdAttained(characters);
        boolean isSame = checkIfLastThreeItemsSame(characters);

        return isTerminate || isSame;
    }



    public boolean checkIfThresholdAttained(List<Character> list) {

        int count = 0;
        boolean result = false;

        for (char ch : list) {

            if (
                ch == 'A' || ch == 'B' || ch == 'C' ||
                    ch == 'P' || ch == 'Q' || ch == 'R' ||
                    ch == 'X' || ch == 'Y' || ch == 'Z'
            )

                count++;

            if (count >= Parameters.THRESHOLD) {

                result = true;
                break;
            }
        }

        list.clear();
        return result;
    }



    public boolean checkIfLastThreeItemsSame(List<Character> list) {

        if (list.size() < 100) {

            return false;
        }

        List<Character> tail = list.subList(Math.max(list.size() - 4, 0), list.size());

        Set<Character> set = new HashSet<>(tail);

        boolean bol = set.size() == 1;

        if (bol) {

            LOG.info("Last 3 items of the list is same and hence we will terminate the producer " + tail.toString());
        }

        return bol;
    }
}
