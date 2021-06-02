import java.io.*;
import java.util.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс, отвечающий за работу с коллекцией
 */
public class WorkingWithMainStack implements Serializable{
    /**
     * Строковое описние результатов действия методов
     */
    String result = "";
    static Logger LOGGER;

    /**
     * Объект класса с данными
     */
    Model dataBase;
    public WorkingWithMainStack(){
        dataBase = new Model();
        try {
            LogManager.getLogManager().readConfiguration();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER = Logger.getLogger(WorkingWithMainStack.class.getName());
    }

    /**
     * Метод, создающий коллекцию по данным из изначльного файла
     * @param line - название переменной окружения, в которой содержится ссылка на интересующий нас файл
     * @return возвращает строковое описние результатов действия метода
     * @throws FileNotFoundException
     * @throws NullPointerException
     */



    /**
     * Метод, который добавляет новый элемент в коллекцию (без гененрации нового ID)
     * @return возвращает строковое описние результатов действия метода
     */


    /**
     * Метод, добавляющий команду в историю команд
     * @param arg команда от пользователя
     */
    public void addToHistory(String arg){
        LOGGER.info("Добавление команды в историю");
        for (int i = 0; i < dataBase.history.length; i++){
            if (dataBase.history[i] == null) {
                dataBase.history[i] = arg;
                dataBase.historySpaceCounter = 0;
                break;
            }else if (dataBase.historySpaceCounter == dataBase.history.length - 1) {
                offsetOfHistory();
                dataBase.history[dataBase.history.length - 1] = arg;
                break;
            }else {
                dataBase.historySpaceCounter++;
            }
        }
        LOGGER.info("Команда добавлена");
    }



    /**
     * Метод, сдвигающий элементы массива истории команд, когда тот заполнился
     */
    private void offsetOfHistory(){
        LOGGER.info("Сдвиг массива истории");
        for (int i = 0; i < (dataBase.history.length - 1); i++){
            dataBase.history[i] = dataBase.history[i+1];
        }
        dataBase.history[dataBase.history.length - 1] = null;
        LOGGER.info("История сдвинута");
    }



    /**
     * Метод, удаляющий все элементы из коллекции
     * @return возвращает строковое описние результатов действия метода
     */
    public Reporting clearStack(){
        LOGGER.info("Очистка главной коллекции");
        result = "";
        while (!dataBase.cities.empty()){
            dataBase.cities.pop();
        }
        dataBase.numberOfObjectsInStack = 0;
        result = "Коллекция очищена.";
        addToHistory("clear");
        LOGGER.info(result);
        return new Reporting(0, result);
    }

    /**
     * Метод, который устанавливает ID. Суть его работы - увеличивать соответствующую переменную на 1 каждый раз,
     * когда добавляется новый элемент
     * @return возвращает увеличенный параметр ID
     */
    public long setID(){
        dataBase.id++;
        LOGGER.info("Установлен id: " + dataBase.id);
        return dataBase.id;

    }



    /**
     * Метод, сортирующий коллекцию по значению поля capital
     * @return возвращает строковое описние результатов действия метода
     */
    public Stack<City> filterByCapital(){
        LOGGER.info("Сортировка коллекции по полю capital в рукопашную");
        Stack<City> citiesClone = (Stack<City>) dataBase.cities.clone();
        Stack<City> citiesSpare1 = new Stack<>();
        Stack<City> citiesSpare2 = new Stack<>();
        Stack<City> citiesSpare3 = new Stack<>();
        int capitalCounter = 0;
        int unCapitalCounter = 0;
        int nullCapitalCounter = 0;
        for (int i = 0; i < dataBase.numberOfObjectsInStack; i++){
            try{
                if (citiesClone.peek().getCapital()){
                    citiesSpare1.push(citiesClone.pop());
                    capitalCounter++;
                }else{
                    citiesSpare2.push(citiesClone.pop());
                    unCapitalCounter++;
                }
            }catch (NullPointerException e){
                citiesSpare3.push(citiesClone.pop());
                nullCapitalCounter++;
            }
        }
        for (int i = 0; i < capitalCounter; i++){
            citiesClone.push(citiesSpare1.pop());
        }
        for (int i = 0; i < unCapitalCounter; i++){
            citiesClone.push(citiesSpare2.pop());
        }
        for (int i = 0; i < nullCapitalCounter; i++){
            citiesClone.push(citiesSpare3.pop());
        }
        return citiesClone;
    }

    /**
     * Метод, ищущий максимальный элемент среди элементов коллекции по 2 из 3 параметров:
     * площадь, население и плотность
     * @return возвращает строковое описние результатов действия метода
     */
    public City searchMax(){
        LOGGER.info("Поиск максимального города в коллекции");
        Comparator<City> comparator1 = Comparator.comparing(City::getArea);
        City maxArea = dataBase.cities.stream().max(comparator1).get();
        Comparator<City> comparator2 = Comparator.comparing(City::getPopulation);
        City maxPopulation = dataBase.cities.stream().max(comparator2).get();
        City max = null;
        if (maxArea.getPopulation() >= maxPopulation.getPopulation()){
            max = maxArea;
        }else if (maxPopulation.getArea() > maxArea.getArea()){
            max = maxPopulation;
        }else if (maxArea.getPopulation() / maxArea.getArea() >= maxPopulation.getPopulation() / maxPopulation.getArea()){
            max = maxArea;
        }else{
            max = maxPopulation;
        }
        return max;
    }

    /**
     * Метод, сортирующий коллекцию по возврастанию ID
     */
    public void sort(){
        LOGGER.info("Сортировка");
        /*Comparator<City> comparator = Comparator.comparing(City::getId);
        dataBase.cities.sort(comparator);*/
        dataBase.cities = dataBase.cities.stream().sorted(Comparator.comparing(City::getName)).collect(Collectors.toCollection(Stack::new));
    }

    public void reversedSort(){
        LOGGER.info("Реверснутая сортировка");
        dataBase.cities = dataBase.cities.stream().sorted(Comparator.comparing(City::getName).reversed()).collect(Collectors.toCollection(Stack::new));
    }

    /**
     * Метод, сортирующий коллекцию по значению поля capital
     * @deprecated да, такой метод уже был и, казалось бы, тут все проще,
     * но мне не понравился порядок здешней сортировки
     */
    public void capitalSort(){
        Comparator<City> comparator = Comparator.comparing(City::getCapital).reversed();
        dataBase.cities.sort(comparator);
    }

    /**
     * Метод, проверяющий наличие элемента в коллекции с данным ID
     * @param id -указанный ID
     * @return возвращает строковое описние результатов действия метода
     */
    public boolean checkId(long id){
        Stack<City> citiesClone = (Stack<City>) dataBase.cities.clone();
        for (int i = 0; i < dataBase.numberOfObjectsInStack; i++){
            if (citiesClone.pop().getId() == id){
                return true;
            }
        }
        return false;
    }


    /**
     * Метод, сохраняющий коллекцию в указанный файл
     * @param scanner почему-то непереведенный мною в String название файла, для сохранения
     * @return возвращает строковое описние результатов действия метода
     * @throws FileNotFoundException
     * @throws IOException
     */
    public String save(File scanner) throws FileNotFoundException, IOException{
        LOGGER.info("Сохранение коллекции в файл: " + scanner);
        result = "";
        sort();
        //Iterator<City> iterator = dataBase.cities.iterator();
        Stack<City> citiesClone = (Stack<City>) dataBase.cities.clone();
        String text;
            FileOutputStream file = new FileOutputStream(scanner);
            for (int i = 0; i < dataBase.numberOfObjectsInStack; i++){
                text = citiesClone.peek().getName() + "," + citiesClone.peek().getCoordinates().getX()
                        + "," + citiesClone.peek().getCoordinates().getY() + "," + citiesClone.peek().getArea() + "," + citiesClone.peek().getPopulation()
                        + "," + citiesClone.peek().getMetersAboveSeaLevel() + "," + citiesClone.peek().getTimezone() + "," + citiesClone.peek().getCapital()
                        + "," + citiesClone.peek().getGoverment() + "," + citiesClone.pop().getGoverner().getName() + "\n";
                byte[] buffer = text.getBytes();
                BufferedOutputStream bufOut = new BufferedOutputStream(file, buffer.length);
                bufOut.write(buffer, 0, buffer.length);
            }
            file.close();
        result = "Коллекция сохранена";
        addToHistory("save (Server)");
    return result;
    }

}