package dz_lesson35_36.dao;

import dz_lesson35_36.exception.BadRequestException;
import dz_lesson35_36.model.Hotel;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class HotelDAO extends GeneralDAO {

    public static Hotel addHotel(Hotel hotel)throws Exception{
        //проверить по id есть ли такой отель в файле
        //если нет, добавить в файл
        if (hotel == null)
            throw new BadRequestException("This " + hotel + " is not exist");

        if (checkObjectById(utils.getPathHotelDB(), hotel.getId()))
            throw new BadRequestException("Hotel with id " + hotel.getId() + " already exists");

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(utils.getPathHotelDB(), true))){
            bufferedWriter.append(Long.toString(hotel.getId()) + (","));
            bufferedWriter.append(hotel.getCountry() + (","));
            bufferedWriter.append(hotel.getCity() + (","));
            bufferedWriter.append(hotel.getStreet() + (","));
            bufferedWriter.append(hotel.getName() + ("\n"));
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

        if (!checkObjectById(utils.getPathHotelDB(), idHotel))
            throw new BadRequestException("Hotel with id " + idHotel + " does not exist");

        StringBuffer res = new StringBuffer();

        for (Hotel el : gettingListObjectsFromFileHotelDB(readFromFile(utils.getPathHotelDB()))){
            if (el != null && el.getId() != idHotel){
                res.append(Long.toString(el.getId()) + (","));
                res.append(el.getCountry() + (","));
                res.append(el.getCity() + (","));
                res.append(el.getStreet() + (","));
                res.append(el.getName() + ("\n"));
            }
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

        for (Hotel el : gettingListObjectsFromFileHotelDB(readFromFile(utils.getPathHotelDB()))){
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

        for (Hotel el : gettingListObjectsFromFileHotelDB(readFromFile(utils.getPathHotelDB()))){
            if (el != null && el.getCity().equals(city)){
                hotels.add(el);
            }
        }

        if (hotels.size() == 0)
            throw new BadRequestException("Method findHotelByCity did not find any hotels from " + city);

        return hotels;
    }

    private static LinkedList<Hotel> gettingListObjectsFromFileHotelDB(ArrayList<String> arrayList)throws Exception{
        if(arrayList == null)
            throw new BadRequestException("This arrayList " + arrayList + " is not exists");

        LinkedList<Hotel> arrays = new LinkedList<>();

        for (String el : arrayList){
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
        }
        return arrays;
    }

    private static boolean checkObjectById(String path, Long id)throws Exception{
        if (path == null || id == null)
            throw new BadRequestException("Invalid incoming data");

        for (Hotel el : gettingListObjectsFromFileHotelDB(readFromFile(path))) {
            if (el != null && el.getId() == id){
                return true;
            }
        }
        return false;
    }
}

