import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chaklader on Mar, 2021
 */
public class Event {


    List<Character> list = new ArrayList<>();

    Character c;

    public Event(Character c) {
        this.c = c;
    }

    public Character getC() {
        return c;
    }

    public void setC(Character c) {
        this.c = c;
    }

    public List<Character> getList() {
        return list;
    }

    public void setList(List<Character> list) {
        this.list = list;
    }
}
