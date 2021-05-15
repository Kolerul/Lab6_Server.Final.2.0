import java.io.*;


public class Main {
    public static void main(String[] arg){
        try{
            Server server = new Server();
            do{
                server.doServerBuisness();
            }while (true);
        }catch (ClassNotFoundException | IOException e){
            e.printStackTrace();
            System.exit(-1);
        }


    }
}

