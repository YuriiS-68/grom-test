package dz_lesson35_36.dao;

import dz_lesson35_36.exception.BadRequestException;
import dz_lesson35_36.model.IdEntity;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public abstract class GeneralDAO {

    private static String pathUserDB = "C:\\Users\\Skorodielov\\Desktop\\UserDB.txt";
    private static String pathHotelDB = "C:\\Users\\Skorodielov\\Desktop\\HotelDB.txt";
    private static String pathRoomDB = "C:\\Users\\Skorodielov\\Desktop\\RoomDB.txt";
    private static String pathOrderDB = "C:\\Users\\Skorodielov\\Desktop\\OrderDB.txt";
    private static String pathToDB = "";
    private static final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public static ArrayList<String> readFromFile()throws Exception{
        /*if(path == null)
            throw new BadRequestException("This path " + path + " does not exists");
*/
        ArrayList<String> arrayList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(getPathToDB()))){
            String line;

            while ((line = br.readLine()) != null){
                String[] result = line.split("\n");
                for (String el : result){
                    if (el != null){
                        arrayList.add(el);
                    }
                }
            }
        }catch (FileNotFoundException e){
            throw new FileNotFoundException("File does not exist");
        } catch (IOException e) {
            throw new IOException("Reading from file " + getPathToDB() + " failed");
        }
        return arrayList;
    }

    public static void writerInFailBD(String path, StringBuffer content)throws Exception{
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path))){
            bufferedWriter.append(content);
        }catch (IOException e){
            throw new IOException("Can not write to file " + path);
        }
    }

    /*private LinkedList<T> getObjectsFromDB()throws Exception{
        LinkedList<T> arrays = new LinkedList<>();

        int index = 0;
        for (String el : readFromFile(getPathToDB())){
            if (el != null){
                //arrays.add(mapObjects(readFromFile(getPathToDB()).get(index)));
            }
            index++;
        }
        return arrays;
    }*/

    /*private T mapObjects(String string)throws Exception{
        if (string == null)
            throw new BadRequestException("String does not exist");

        String[] fields = string.split(",");

        T object = (T) new Object();
        object.setId(Long.parseLong(fields[0]));
        object.setUserName(fields[1]);
        object.setPassword(fields[2]);
        object.setCountry(fields[3]);
        if (fields[4].equals("USER")){
            object.setUserType(UserType.USER);
        }else {
            object.setUserType(UserType.ADMIN);
        }
        return object;
    }*/

    /*private static <T> boolean checkObjectById(String path, Long id)throws Exception{
        if (path == null || id == null)
            throw new BadRequestException("Invalid incoming data");

        for (T el : gettingListObjectsFromFileHotelDB()) {
            if (el != null && el.getId() == id){
                return true;
            }
        }
        return false;
    }*/

    public  static <T extends IdEntity> void assignmentObjectId(T t)throws Exception{
        if (t == null)
            throw new BadRequestException("User does not exist");

        Random random = new Random();
        t.setId(random.nextInt());
        if (t.getId() < 0){
            t.setId(-1 * t.getId());
        }
    }

    /*public static <T extends IdEntity> void writerToFile(T t)throws Exception{
        if (t == null)
            throw new BadRequestException("Invalid incoming data");

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(GeneralDAO.getPathUserDB(), true))){
            bufferedWriter.append(Long.toString(t.getId()) + (","));
            bufferedWriter.append(t.getUserName() + (","));
            bufferedWriter.append(t.getPassword() + (","));
            bufferedWriter.append(t.getCountry() + (","));
            bufferedWriter.append(t.getUserType() + ("\n"));
        }catch (IOException e){
            throw new IOException("Can not write to file " + GeneralDAO.getPathUserDB());
        }
    }*/

    public static String getPathUserDB() {
        return pathUserDB;
    }

    public static String getPathHotelDB() {
        return pathHotelDB;
    }

    public static String getPathRoomDB() {
        return pathRoomDB;
    }

    public static String getPathOrderDB() {
        return pathOrderDB;
    }

    public static String getPathToDB() {
        return pathToDB;
    }

    public static DateFormat getFORMAT() {
        return FORMAT;
    }

    public static void setPathToDB(String pathToDB) {
        GeneralDAO.pathToDB = pathToDB;
    }
}
