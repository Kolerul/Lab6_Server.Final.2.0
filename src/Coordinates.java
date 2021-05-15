/**
 * Класс объекты, которого являются координатами
 */
public class Coordinates{
    /**
     * Поле координата по оси x
     */
    private Float x;
    /**
     * Поле координата по оси y
     */
    private Float y;

    public Coordinates(Float x, Float y){
        this.x = x;
        this.y = y;
    }

    public Float getX() {
        return x;
    }

    public Float getY() {
        return y;
    }
}