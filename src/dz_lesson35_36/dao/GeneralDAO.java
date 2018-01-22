package dz_lesson35_36.dao;

import dz_lesson35_36.exception.BadRequestException;
import dz_lesson35_36.model.IdEntity;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public abstract class GeneralDAO {

    private static final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public static ArrayList<String> readFromFile(String path)throws Exception{
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

    public static void writerInFailBD(String path, StringBuffer content)throws Exception{
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path))){
            bufferedWriter.append(content);
        }catch (IOException e){
            throw new IOException("Can not write to file " + path);
        }
    }

    public  static <T extends IdEntity> void assignmentObjectId(T t)throws Exception{
        if (t == null)
            throw new BadRequestException("User does not exist");

        Random random = new Random();
        t.setId(random.nextInt());
        if (t.getId() < 0){
            t.setId(-1 * t.getId());
        }
    }

    public static DateFormat getFORMAT() {
        return FORMAT;
    }
}
