import java.io.Serializable;
import java.time.LocalDate;

/**
 * Класс, объекты которого являются основным элементоми коллекции.
 */
public class City implements Serializable {
    /**
     * Поле ID ( у каждого элемента оно должно быть уникально)
     */
    private long id;
    /**
     * Поле имени города
     */
    private String name;
    /**
     * Поле, являющееся координатами города
     */
    private Coordinates coordinates;
    /**
     * Дата создания города, как объекта программы
     */
    private LocalDate creationDate;
    /**
     * Поля плошади города и его часового пояса
     */
    private Integer area, timezone;
    /**
     * Поле количества населения города
     */
    private int population;
    /**
     * Поле высоты над уровнем моря города
     */
    private float metersAboveSeaLevel;
    /**
     * Поле обозначающее, является ли город столицей
     */
    private Boolean capital;
    /**
     * Поле, обозначающее тип государственного строя
     */
    private Government government;
    /**
     * Поле, обозначающее правителя
     */
    private Human governer;

    public City(long id, String name, Float x, Float y, Integer area, int population, float metersAboveSeaLevel,
                Integer timezone, int capital, Government government, String governerName){
        this.id = id;
        this.name = name;
        this.coordinates = new Coordinates(x, y);
        this.area = area;
        this.population = population;
        this.metersAboveSeaLevel = metersAboveSeaLevel;
        this.timezone = timezone;
        if (capital == -1){
            this.capital = null;
        }else if (capital == 0){
            this.capital = false;
        }else if (capital == 1){
            this.capital = true;
        }
        this.government = government;
        this.governer = new Human(governerName);
        creationDate = LocalDate.now();
    }

    public City(String name, Float x, Float y, Integer area, int population, float metersAboveSeaLevel,
                Integer timezone, int capital, Government government, String governerName){
        this.name = name;
        this.coordinates = new Coordinates(x, y);
        this.area = area;
        this.population = population;
        this.metersAboveSeaLevel = metersAboveSeaLevel;
        this.timezone = timezone;
        if (capital == -1){
            this.capital = null;
        }else if (capital == 0){
            this.capital = false;
        }else if (capital == 1){
            this.capital = true;
        }
        this.government = government;
        this.governer = new Human(governerName);
        creationDate = LocalDate.now();
    }

    public City(long id, City city){
        this.id = id;
        this.name = city.name;
        this.coordinates = city.coordinates;
        this.area = city.area;
        this.population = city.population;
        this.metersAboveSeaLevel = city.metersAboveSeaLevel;
        this.timezone = city.timezone;
        this.capital = city.getCapital();
        this.government = city.government;
        this.governer = city.getGoverner();
        creationDate = LocalDate.now();
    }

    public String getName() {
        return name;
    }

    public Boolean getCapital() {
        return capital;
    }

    public Government getGoverment() {
        return government;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public float getMetersAboveSeaLevel() {
        return metersAboveSeaLevel;
    }

    public Human getGoverner() {
        return governer;
    }

    public int getPopulation() {
        return population;
    }

    public Integer getArea() {
        return area;
    }

    public Integer getTimezone() {
        return timezone;
    }

    public long getId() {
        return id;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    @Override
    public String toString() {
        return "ID: " + id +
                ", Название: " + name +
                ", Координата X: " + coordinates.getX() +
                ", Координата Y: " + coordinates.getY() +
                ", Площадь: " + area +
                ", Население: " + population +
                ", Высота над уровнем моря: " + metersAboveSeaLevel +
                ", Часовой пояс: " + timezone +
                ", Является столицей: " + capital +
                ", Тип правительства: " + government +
                ", Имя правителя: " + governer.getName() +
                ", Дата создания: " + creationDate;
    }
}