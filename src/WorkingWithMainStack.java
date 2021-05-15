import java.io.*;
import java.util.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс, отвечающий за работу с коллекцией
 */
public class WorkingWithMainStack {
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
    public Reporting createNewMainStack(String line){
        //C:\Users\europ\OneDrive\Рабочий стол\csv.txt, Lab5, System.getenv("csv")
        result = "";
        try {
            LOGGER.info("Начато создание основной коллекции");
            LOGGER.warning("Возможно отсутствие искомой переменной окружения или необходимого файла");
            dataBase.startingFile = new File(System.getenv(line));
            Scanner scanner = new Scanner(dataBase.startingFile);
            dataBase.dateOfCreation = new Date();
            while (scanner.hasNextLine()) {
                LOGGER.warning("Вероятно найденный файл может быть нераспарсен из-за неправильного формата данных");
                String[] parts = StringManipulation.parseToParts(scanner.nextLine());

                if (parts.length != 10 || StringManipulation.emptyCheck(parts) != 1){
                    continue;
                }

                int capital = StringManipulation.capital(parts[7]);

                Government government = StringManipulation.government(parts[8]);

                dataBase.cities.push(new City(setID(), parts[0], Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Float.parseFloat(parts[5]), Integer.parseInt(parts[6]), capital, government, parts[9]));
                dataBase.numberOfObjectsInStack++;
            }
            scanner.close();

        }catch (NullPointerException e){
            //return "Не существует данной переменной окружения";
            LOGGER.warning("Переменная окружения не была найдена");
            return new Reporting(1, "Не существует данной переменной окружения");
        }catch (FileNotFoundException e){
            //return "Данный файл не удалось найти";
            LOGGER.warning("Файл не был найден");
            return new Reporting(1, "Данный файл не удалось найти");
        }

        result = "Коллекция создана, можно приступать к работе";
        return new Reporting(0, result);
        //return result;
    }

    /**
     * Метод, который добавляет новый элемент в коллекцию (с гененрацией нового ID)
     * @return возвращает строковое описние результатов действия метода
     */
    public Reporting addElement(String name, Float x, Float y, Integer area, int population, float meters, Integer timezone,
                             int capital, Government government, String governorName){
        LOGGER.info("Добавление элемента в коллекцию");
        dataBase.cities.push(new City(setID(), name, x, y, area, population, meters, timezone, capital, government, governorName));
        dataBase.numberOfObjectsInStack++;
        addToHistory("add { element }");
        LOGGER.info("Элемент добавлен");
        return new Reporting(0, "Элемент добавлен");
    }
    /**
     * Метод, который добавляет новый элемент в коллекцию (без гененрации нового ID)
     * @return возвращает строковое описние результатов действия метода
     */
    public String addElement(long id, String name, Float x, Float y, Integer area, int population, float meters, Integer timezone,
                             int capital, Government government, String governorName){
        LOGGER.info("Обновление элемента коллекции");
        dataBase.cities.push(new City(id, name, x, y, area, population, meters, timezone, capital, government, governorName));
        addToHistory("add { element }");
        LOGGER.info("Элемент обновлен");
        return "Элемент обновлен";
    }

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
     * Метод, показывающий массив с историей команд пользователю
     * @return возвращает строковое описние результатов действия метода
     */
    public Reporting showHistory(){
        LOGGER.info("Демонстрация истории");
        result = "";
        for (int i = 0; i < dataBase.history.length; i++){
            if (dataBase.history[i] != null) {
                result = result + "[ " + dataBase.history[i] + " ]";
                if (i != dataBase.history.length - 1){
                    result = result + "\n";
                }
            }
        }
        addToHistory("history");
        LOGGER.info(result);
        return new Reporting(0, result);
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
     * Метод, который ищет и обновляет элемент коллекции по его ID
     * @return возвращает строковое описние результатов действия метода
     */
    public Reporting updateElementById(long id, String name, Float x, Float y, Integer area, int population, float meters, Integer timezone,
                                    int capital, Government government, String governorName){
        result = "";
        LOGGER.info("Обновление элемента коллекции по id");
        try{
            dataBase.cities.stream().filter(city -> city.getId() == id).findFirst().get();
            dataBase.cities = dataBase.cities.stream().filter(city -> city.getId() != id).collect(Collectors.toCollection(Stack::new));
            result = addElement(id, name, x, y, area, population, meters, timezone, capital, government, governorName);
        }catch (NoSuchElementException e){
            result = "Элемент с данным id не удалось найти";
        }

        LOGGER.info(result);
        addToHistory("update id { element }");
        return new Reporting(0, result);
    }


    /**
     * Метод, который ищет и удаляет элемент из коллекции по его ID
     * @param id - ID элемента, который мы собираемся удалить
     * @return возвращает строковое описние результатов действия метода
     */
    public Reporting removeElementById(long id){
        LOGGER.info("Удаление элемента по id");
        result = "";
        try{
            dataBase.cities.stream().filter(city -> city.getId() == id).findFirst().get();
            dataBase.cities = dataBase.cities.stream().filter(city -> city.getId() != id).collect(Collectors.toCollection(Stack::new));
            dataBase.numberOfObjectsInStack--;
            result = "Элемент удален";
        }catch (NoSuchElementException e){
            result = "Элемент с данным id не удалось найти";
        }
        LOGGER.info(result);
        addToHistory("remove_by_id id");
        return new Reporting(0, result);
    }

    /**
     * Метод, который удаляет первый элемент из коллекции
     * @return возвращает строковое описние результатов действия метода
     */
    public Reporting removeFirst(){
        LOGGER.info("Удаление первого элемента коллекции");
        result = "";
        addToHistory("remove_first");
        if (dataBase.numberOfObjectsInStack <= 0){
            result = "Извините, удалять нечего, так как коллекция пуста";
            return new Reporting(0, result);
        }
        reversedSort();
        dataBase.cities.pop();
        dataBase.numberOfObjectsInStack--;
        result = "Первый элемент удален";
        LOGGER.info(result);
        return new Reporting(0, result);
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
    private long setID(){
        dataBase.id++;
        LOGGER.info("Установлен id: " + dataBase.id);
        return dataBase.id;

    }

    /**
     * Метод, который ищет элементы коллекции по заданной подстроке в именах элементов
     * @param line - искомая подстрока
     * @return возвращает строковое описние результатов действия метода
     */
    public Reporting searchByName(String line){
        LOGGER.info("Начат поиск по подстроке в имени");
        result = "";
        try{
            Stack<City> resultStack = dataBase.cities.stream().filter(x -> x.getName().contains(line)).collect(Collectors.toCollection(Stack::new));
            Iterator<City> iterator = resultStack.iterator();
            if (!iterator.hasNext()){
                throw new NoSuchElementException();
            }
            while (iterator.hasNext()){
                result = result + iterator.next();
                if (iterator.hasNext()){
                    result = result + "\n";
                }
            }
        }catch (NoSuchElementException e){
            result = "Ничего не удалось найти";
        }
        LOGGER.info(result);
        addToHistory("filter_contains_name name");
        return new Reporting(0, result);
    }


    public Reporting showCapitalStack(){
        LOGGER.info("Градация по полю capital");
        result = "";
        if (dataBase.numberOfObjectsInStack == 0){
            result = "Коллекция пуста";
        }else{
            Iterator<City> iterator = filterByCapital().iterator();
            while (iterator.hasNext()){
                result = result + iterator.next();
                if (iterator.hasNext()){
                    result = result + "\n";
                }
            }
        }
        addToHistory("print_field_ascending_capital");

        LOGGER.info(result);
        return new Reporting(0, result);
    }

    /**
     * Метод, который демонстрирует коллекцию
     * @return возвращает строковое описние результатов действия метода
     */
    public Reporting showMainStack(){
        LOGGER.info("Демонстрация основной коллекции");
        result = "";
        sort();
        Iterator<City> iterator = dataBase.cities.iterator();

        if (dataBase.numberOfObjectsInStack > 0){

            int i = 0;
            while (iterator.hasNext()){
                result = result + iterator.next().toString();
                if (i != dataBase.numberOfObjectsInStack - 1){
                    result = result + "\n";
                }
                i++;
            }
        }else {
            return new Reporting(0, "Извините, но коллекция пуста");
        }
        LOGGER.info(result);
        addToHistory("show");
        return new Reporting(0, result);
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
     * Метод, сортирующий элементы коллекции по ID группам
     * @return возвращает строковое описние результатов действия метода
     */
    public Reporting idGrouper(){
        LOGGER.info("Сортировка городов по группам по id");
        result = "";
        int mem1 = 1337, mem2 = 1488, mem3 = 228, mem4 = 282, mem5 = 80, mem6 = 47, mem7 = 69;
        Stack<City> citiesClone = (Stack<City>) dataBase.cities.clone();
        int evenId = 0, oddId = 0, memId = 0, randomMax = (int) (Math.random() * 10000), smallerMaxId = 0;

        for (int i = 0; i < dataBase.numberOfObjectsInStack; i++){
            if (citiesClone.peek().getId() % 2 == 0){
                evenId++;
            }else{
                oddId++;
            }
            if (citiesClone.peek().getId() == mem1 || citiesClone.peek().getId() == mem2 || citiesClone.peek().getId() == mem3 ||
                    citiesClone.peek().getId() == mem4 || citiesClone.peek().getId() == mem5 || citiesClone.peek().getId() == mem6 ||
                    citiesClone.peek().getId() == mem7){
                memId++;
            }
            if (citiesClone.pop().getId() <= randomMax){
                smallerMaxId++;
            }
        }
        result = "Четных id в коллекции: " + evenId + "\n" +
                "Нечетных id в коллекции: " + oddId + "\n" +
                "Мемных id в коллекции: " + memId + "\n" +
                "id меньше рандомного числа (" + randomMax + "): " + smallerMaxId;
        LOGGER.info(result);
        addToHistory("group_counting_by_id");
        return new Reporting(0, result);
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
     * Метод, демонстрирующий шпаргалку с коммандами для пользователя
     * @return возвращает строковое описние результатов действия метода
     */
    public Reporting showHelp(){
        result = dataBase.cheatSheet;
        addToHistory("help");
        LOGGER.info("Справка: " + result);
        return new Reporting(0, result);
    }

    /**
     * Метод, демонстрирующий информацию о коллекции
     * @return возвращает строковое описние результатов действия метода
     */
    public Reporting showInfo(){
        result = "Тип коллекции: " + dataBase.cities.getClass() + "\n" + "Время создания коллекции: " + dataBase.dateOfCreation + "\n" +
                "Количество элементов коллекции: " + dataBase.numberOfObjectsInStack;
        addToHistory("info");
        LOGGER.info("Информация по коллекции: " + result);
        return new Reporting(0, result);
    }

    /**
     * Метод, добавляющий элемент в коллекцию, если он больше наибольшего
     * @return возвращает строковое описние результатов действия метода
     */
    public Reporting addIfMax(String name, Float x, Float y, Integer area, int population, float meters, Integer timezone,
                           int capital, Government government, String governorName){
        result = "";
        LOGGER.info("Добавление элемента в коллекцию если он больше максимального");
        City applicant = new City(setID(), name, x, y, area, population, meters, timezone, capital, government, governorName);
        if (dataBase.numberOfObjectsInStack >= 1){
            City max = searchMax();
            int checkCounter = 0;
            if (applicant.getArea() >= max.getArea()){
                checkCounter++;
            }
            if (applicant.getPopulation() >= max.getPopulation()){
                checkCounter++;
            }
            if ((applicant.getPopulation() / applicant.getArea() >= max.getPopulation() / max.getArea())){
                checkCounter++;
            }
            if (checkCounter >=2 ){
                dataBase.cities.push(applicant);
                dataBase.numberOfObjectsInStack++;
                result = "Элемент добавлен";
            }else{
                result = "Элемент не оказался больше наибольшего. Элемент не был добавлен";
                dataBase.id--;
            }
        }else{
            dataBase.cities.push(applicant);
            dataBase.numberOfObjectsInStack++;
            result = "Элемент добавлен";
        }
        addToHistory("add_if_max { element }");
        LOGGER.info(result);
        return new Reporting(0, result);
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