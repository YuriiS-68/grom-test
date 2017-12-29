package dz_lesson35_36.dao;

import dz_lesson35_36.exception.BadRequestException;
import dz_lesson35_36.model.Hotel;
import dz_lesson35_36.model.Utils;

import java.io.*;
import java.util.LinkedList;

public class HotelDAO extends GeneralDAO {

    private static Utils utils = new Utils();
    //private static final String PATH_HOTEL_DB = "C:\\Users\\Skorodielov\\Desktop\\HotelDB.txt";

    public static Hotel addHotel(Hotel hotel)throws Exception{
        //проверить по id есть ли такой отель в файле
        //если нет, добавить в файл
        if (hotel == null)
            throw new BadRequestException("This " + hotel + " is not exist");

        if (checkObjectById(utils.getPathHotelDB(), hotel.getId()))
            throw new BadRequestException("Hotel with id " + hotel.getId() + " already exists");

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(utils.getPathHotelDB(), true))){
            bufferedWriter.append(Long.toString(hotel.getId())).append(",");
            bufferedWriter.append(hotel.getCountry()).append(",");
            bufferedWriter.append(hotel.getCity()).append(",");
            bufferedWriter.append(hotel.getStreet()).append(",");
            bufferedWriter.append(hotel.getName());
            bufferedWriter.append("\n");
        }catch (IOException e){
            throw new IOException("Can not write to file " + utils.getPathHotelDB());
        }
        return hotel;
    }

    public static void deleteHotel(Long idHotel)throws Exception{
        //считать файл
        //разбить на строки по отелям
        //если строка содержит заданный отель, удалить эту строку
        //перезаписать файл
        if (idHotel == null)
            throw new BadRequestException("This id " + idHotel + " is not exist");

        StringBuffer res = new StringBuffer();

        int index = 0;
        for (Hotel el : gettingListObjectsFromFile(utils.getPathHotelDB())) {
            if (el != null && el.getId() != idHotel){
                res.append(el.toString());
                res.append("\n");
            }
            index++;
        }

        writerInFailBD(utils.getPathHotelDB(), res);
    }

    public static LinkedList<Hotel> findHotelByName(String name)throws Exception{
        //считать файл
        //разбить сплитом по символу переноса строки и найти нужную строку содержащую имя отеля
        //полученную строку разбить сплитом по запятой
        //получаю массив стрингов
        if (name == null)
            throw new BadRequestException("This name - " + name + " does not exist." );

        LinkedList<Hotel> hotels = new LinkedList<>();

        for (Hotel el : gettingListObjectsFromFile(utils.getPathHotelDB())){
            if (el != null && el.getName().equals(name)){
                hotels.add(el);
            }
        }
        return hotels;
    }

    public static LinkedList<Hotel> findHotelByCity(String city)throws Exception{
        //считать файл
        //разбить сплитом по символу переноса строки и найти нужную строку содержащую имя отеля
        //полученную строку разбить сплитом по запятой
        //получаю массив стрингов
        if (city == null)
            throw new BadRequestException("This city - " + city + " does not exist." );

        LinkedList<Hotel> hotels = new LinkedList<>();

        for (Hotel el : gettingListObjectsFromFile(utils.getPathHotelDB())){
            if (el != null && el.getCity().equals(city)){
                hotels.add(el);
            }
        }
        return hotels;
    }

    private static LinkedList<Hotel> gettingListObjectsFromFile(String path)throws Exception{
        if(path == null)
            throw new BadRequestException("This path " + path + " is not exists");

        LinkedList<Hotel> arrays = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            String line;

            while ((line = br.readLine()) != null){
                String[] result = line.split("\n");
                int index = 0;
                for (String el : result){
                    if (el != null){
                        String[] fields = el.split(",");
                        Hotel hotel = new Hotel();
                        hotel.setId(Long.parseLong(fields[0]));
                        hotel.setCountry(fields[1]);
                        hotel.setCity(fields[2]);
                        hotel.setStreet(fields[3]);
                        hotel.setName(fields[4]);
                        arrays.add(hotel);
                    }
                    index++;
                }
            }
        }catch (FileNotFoundException e){
            throw new FileNotFoundException("File does not exist");
        } catch (IOException e) {
            throw new IOException("Reading from file " + path + " failed");
        }
        return arrays;
    }

    private static boolean checkObjectById(String path, Long id)throws Exception{
        if (path == null || id == null)
            throw new BadRequestException("Invalid incoming data");

        int index = 0;
        for (Hotel el : gettingListObjectsFromFile(path)) {
            if (el != null && el.getId() == id){
                return true;
            }
            index++;
        }
        return false;
    }

    public static void writerInFailBD(String path, StringBuffer content)throws Exception{
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path))){
            bufferedWriter.append(content);
        }catch (IOException e){
            throw new IOException("Can not write to file " + path);
        }
    }
}

