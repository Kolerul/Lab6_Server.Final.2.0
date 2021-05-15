import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class Server{
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private int port;
    private Command input;
    private WorkingWithMainStack presenter;
    private Reporting report;
    static Logger LOGGER;


    public Server() throws IOException {
        port = 4444;
        LogManager.getLogManager().readConfiguration();
        LOGGER = Logger.getLogger(Server.class.getName());
        try {
            serverSocket = new ServerSocket(port);
        }catch (BindException e){
            LOGGER.warning("Порт уже занят, поменяй");
            System.exit(0);
        }

        presenter = new WorkingWithMainStack();

        Thread scannerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //System.err.println("Я жив!");
                LOGGER.log(Level.INFO, "Создается второй поток");
                Scanner scanner = new Scanner(System.in);
                do {
                    String line = scanner.nextLine().trim();
                    if (StringManipulation.commandWithArgumentsCheck(line, "save")) {
                        File file;
                        try {
                            file = new File(StringManipulation.returnLastWords(line));
                        }catch (ArrayIndexOutOfBoundsException e){
                            file = presenter.dataBase.startingFile;
                        }
                        try {
                            try {
                                presenter.save(file);
                                //System.err.println("Данные сохранены");
                            }catch (NullPointerException e){
                                presenter.save(presenter.dataBase.defaultFile);
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if(line.equals("exit")){
                        break;
                    }
                }while (true) ;
                //System.err.println("Я умер!");
                LOGGER.log(Level.INFO, "Второй поток завершает работу, а вместе с ним и вся программа");
                System.exit(0);
            }
        });

        scannerThread.start();

    }

    public void doServerBuisness() throws IOException, ClassNotFoundException {
        //System.out.println("Сервер запущен!");
        LOGGER.info("Запуск основного метода сервера");
        try {
            clientSocket = serverSocket.accept();

            //System.out.println("Клиент подключился");
            LOGGER.info("Клиент подключился");
            try {
                do {
                    inputStream = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                    LOGGER.info("Создан канал входа");
                    input = (Command) inputStream.readObject();
                    //System.out.println("inputStream создан");
                    outputStream = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                    LOGGER.info("Создан канал выхода");
                    //System.out.println("outputStream создан");

                    //System.out.println(input.toString());
                    LOGGER.info("Получена команда от клиента");

                    commandProcess(input);

                    //System.out.println(report.toString());

                    outputStream.writeObject(report);
                    outputStream.flush();
                    LOGGER.info("Отправлен ответ клиенту");
                    //System.out.println("Все норм");
                } while (true);
            } finally {
                clientSocket.close();
            }
       /* }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            LOGGER.warning("Непредвиденная ошибка, программа закрывается");
            System.exit(-1);*/
        } catch (SocketException | EOFException e){
            //System.out.println(input.toString());
            try{
                File file = new File(presenter.dataBase.startingFile.getAbsolutePath());
                presenter.save(file);
            }catch (NullPointerException ex){
                File file = presenter.dataBase.defaultFile;
                presenter.save(file);
            }

            //System.out.println("Соединение разорвано. Данные сохранены в файл");
            LOGGER.info("Соединение разорвано. Данные сохранены в файл");
            presenter.clearStack();
            presenter = new WorkingWithMainStack();
            return;
        }

    }

    private void commandProcess(Command command){
        LOGGER.info("Начата обработка команды от клиента");
        if (command.number == 0){
            report = presenter.createNewMainStack(command.name);
        }else if (command.number == 1){
            report = presenter.showHelp();
       }else if (command.number == 2){
            report = presenter.showInfo();
        }else if (command.number == 3){
            report = presenter.showMainStack();
        }else if (command.number == 4){
            report = presenter.addElement(command.name, command.x, command.y, command.area, command.population, command.metersAboveSeaLevel,
                    command.timezone, command.capital, command.government, command.governorName);
        }else if (command.number == 5){
            report = presenter.updateElementById(command.id, command.name, command.x, command.y, command.area, command.population, command.metersAboveSeaLevel,
                    command.timezone, command.capital, command.government, command.governorName);
        }else if (command.number == 6){
            report = presenter.removeElementById(command.id);
        }else if (command.number == 7){
            report = presenter.clearStack();
        }else if (command.number == 8){
            //presenter.executeScript(command.name, presenter);
        }else if (command.number == 10){
            report = presenter.removeFirst();
        }else if (command.number == 11){
            report = presenter.addIfMax(command.name, command.x, command.y, command.area, command.population, command.metersAboveSeaLevel,
                    command.timezone, command.capital, command.government, command.governorName);
        }else if (command.number == 12){
            report = presenter.showHistory();
        }else if (command.number == 13){
            report = presenter.idGrouper();
        }else if (command.number == 14){
            report = presenter.searchByName(command.name);
        }else if (command.number == 15){
            report = presenter.showCapitalStack();
        }
    }

}