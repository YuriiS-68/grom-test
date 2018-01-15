package dz_lesson35_36.dao;

import dz_lesson35_36.exception.BadRequestException;
import dz_lesson35_36.model.Utils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GeneralDAO <T> {

    public static Utils utils = new Utils();
    private static String pathUserDB = "C:\\Users\\Skorodielov\\Desktop\\UserDB.txt";
    public static final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public static String getPathUserDB() {
        return pathUserDB;
    }

    public static ArrayList<String> readFromFile(String path)throws Exception{
        if(path == null)
            throw new BadRequestException("This path " + path + " does not exists");

        ArrayList<String> arrayList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))){
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
            throw new IOException("Reading from file " + path + " failed");
        }
        return arrayList;
    }

    /*private T findUserById(Long id)throws Exception{
        if (id == null)
            throw new BadRequestException("This does  " + id + " not exist ");

        for (T el : gettingListObjectsFromFileUserDB(readFromFile(utils.getPathUserDB()))){
            if (el != null && el.getId() == id){
                return el;
            }
        }
        throw new BadRequestException("User with " + id + " no such found.");
    }*/

    public static void writerInFailBD(String path, StringBuffer content)throws Exception{
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path))){
            bufferedWriter.append(content);
        }catch (IOException e){
            throw new IOException("Can not write to file " + path);
        }
    }
}
