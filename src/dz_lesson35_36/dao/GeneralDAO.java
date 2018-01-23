package dz_lesson35_36.dao;

import dz_lesson35_36.exception.BadRequestException;
import dz_lesson35_36.model.IdEntity;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public abstract class GeneralDAO {

    private static String pathDB = "";

    private static final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    static ArrayList<String> readFromFile()throws Exception{
        ArrayList<String> arrayList = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(new FileReader(pathDB))){
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
            throw new IOException("Reading from file " + pathDB + " failed");
        }
        return arrayList;
    }

    static void writerInFailBD(String path, StringBuffer content)throws Exception{
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path))){
            bufferedWriter.append(content);
        }catch (IOException e){
            throw new IOException("Can not write to file " + path);
        }
    }

    /*<T extends IdEntity> findObjectById(T t)throws Exception{
        if (id == null)
            throw new BadRequestException("This does  " + id + " not exist ");

        for (T el : gettingListObjects()){
            if (user != null && user.getId() == id){
                return t;
            }
        }
        throw new BadRequestException("User with " + id + " no such found.");
    }*/

    static <T extends IdEntity> void assignmentObjectId(T t)throws Exception{
        if (t == null)
            throw new BadRequestException("User does not exist");

        Random random = new Random();
        t.setId(random.nextInt());
        if (t.getId() < 0){
            t.setId(-1 * t.getId());
        }
    }

    public static void setPathDB(String pathDB) {
        GeneralDAO.pathDB = pathDB;
    }

    public static DateFormat getFORMAT() {
        return FORMAT;
    }
}
