package dz_lesson35_36.dao;

import com.sun.xml.internal.stream.writers.WriterUtility;
import dz_lesson35_36.exception.BadRequestException;
import dz_lesson35_36.model.Hotel;

import java.io.*;
import java.util.LinkedList;

public class HotelDAO extends GeneralDAO {

    private static String pathHotelDB = "C:\\Users\\Skorodielov\\Desktop\\HotelDB.txt";

    public static Hotel addHotel(Hotel hotel)throws Exception{
        //проверить по id есть ли такой отель в файле
        //если нет, добавить в файл
        if (hotel == null)
            throw new BadRequestException("This " + hotel + " is not exist");

        if (!checkHotelById(hotel.getId()))
            throw new BadRequestException("Hotel with id " + hotel.getId() + " already exists");

        writerToFile(hotel);

        return hotel;
    }

    public static void deleteHotel(Long idHotel)throws Exception{
        //считать файл
        //разбить на строки по отелям
        //если строка содержит заданный отель, удалить эту строку
        //перезаписать файл
        if (idHotel == null)
            throw new BadRequestException("This id " + idHotel + " is not exist");

        if (checkHotelById(idHotel))
            throw new BadRequestException("Hotel with id " + idHotel + " does not exist");

        writerInFailBD(pathHotelDB, contentForWriting(idHotel));
    }

    public static LinkedList<Hotel> findHotelByName(String name)throws Exception{
        //считать файл
        //разбить сплитом по символу переноса строки и найти нужную строку содержащую имя отеля
        //полученную строку разбить сплитом по запятой
        //получаю массив стрингов
        if (name == null)
            throw new BadRequestException("This name - " + name + " does not exist." );

        LinkedList<Hotel> hotels = new LinkedList<>();

        for (Hotel el : getHotels()){
            if (el != null && el.getName().equals(name)){
                hotels.add(el);
            }
        }

        if (hotels.size() == 0)
            throw new BadRequestException("Method findHotelByName did not find any hotels by name " + name);

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

        for (Hotel el : getHotels()){
            if (el != null && el.getCity().equals(city)){
                hotels.add(el);
            }
        }

        if (hotels.size() == 0)
            throw new BadRequestException("Method findHotelByCity did not find any hotels from " + city);

        return hotels;
    }

    public static Hotel findHotelById(Long id)throws Exception{
        if (id == null)
            throw new BadRequestException("This does  " + id + " not exist ");

        for (Hotel hotel : getHotels()){
            if (hotel != null && hotel.getId() == id){
                return hotel;
            }
        }
        throw new BadRequestException("Hotel with " + id + " no such found.");
    }

    public static boolean checkIdHotel(Long id)throws Exception{
        if (id == 0 )
            throw new BadRequestException("Invalid incoming data");

        for (Hotel hotel : getHotels()){
            if (hotel != null && hotel.getId() == id){
                return true;
            }
        }
        return false;
    }

    private static LinkedList<Hotel> getHotels()throws Exception{
        LinkedList<Hotel> arrays = new LinkedList<>();

        setPathDB(pathHotelDB);

        int index = 0;
        for (String el : readFromFile()){
            if (el != null){
                arrays.add(mapHotels(readFromFile().get(index)));
            }
            index++;
        }
        return arrays;
    }

    private static Hotel mapHotels(String string)throws Exception{
        if (string == null)
            throw new BadRequestException("String does not exist");

        String[] fields = string.split(",");

        Hotel hotel = new Hotel();
        hotel.setId(Long.parseLong(fields[0]));
        hotel.setCountry(fields[1]);
        hotel.setCity(fields[2]);
        hotel.setStreet(fields[3]);
        hotel.setName(fields[4]);

        return hotel;
    }

    private static boolean checkHotelById(Long id)throws Exception{
        if (id == null)
            throw new BadRequestException("Invalid incoming data");

        for (Hotel el : getHotels()) {
            if (el != null && el.getId() == id){
                return false;
            }
        }
        return true;
    }

    private static StringBuffer contentForWriting(Long idHotel)throws Exception{
        StringBuffer res = new StringBuffer();

        for (Hotel el : getHotels()){
            if (el != null && el.getId() != idHotel){
                res.append(el.toString() + ("\n"));

            }
        }
        return res;
    }

    private static void writerToFile(Hotel hotel)throws Exception{
        if (hotel == null)
            throw new BadRequestException("Hotel does not exist");

        //setPathDB(pathHotelDB);
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathHotelDB, true))){
            bufferedWriter.append(hotel.toString() + "\n");
        }catch (IOException e){
            throw new IOException("Can not write to file " + pathHotelDB);
        }
    }
}

