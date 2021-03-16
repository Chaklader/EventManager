import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Chaklader on Mar, 2021
 */
public class Event {


//    List<Character> list = new ArrayList<>();

    Integer id;
    Character c;

    Date date;

    public Event() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Event(Character c) {
        this.c = c;
    }

    public Character getC() {
        return c;
    }

    public void setC(Character c) {
        this.c = c;
    }

//    public List<Character> getList() {
//        return list;
//    }
//
//    public void setList(List<Character> list) {
//        this.list = list;
//    }
}
