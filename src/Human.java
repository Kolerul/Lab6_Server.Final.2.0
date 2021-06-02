import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Класс, объекты которого являются правителями
 */
public class Human implements Serializable {
    /**
     * Поле имя человека
     */
    private String name;
    /**
     * Поле времени рождения человека, как объекта в программе
     */
    private final ZonedDateTime birthday;

    public Human(String name){
        this.name = name;
        birthday = ZonedDateTime.now();
    }

    public String getName() {
        return name;
    }
}

