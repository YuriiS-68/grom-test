package dz_lesson35_36.dao;

import dz_lesson35_36.exception.BadRequestException;
import dz_lesson35_36.model.Filter;
import dz_lesson35_36.model.Hotel;
import dz_lesson35_36.model.Room;

import java.io.*;
import java.util.*;

public class RoomDAO extends GeneralDAO{

    public static Room addRoom(Room room)throws Exception{
        if (room == null)
            throw new BadRequestException("This " + room + " is not exist");

        if (!checkObjectById(room.getId()))
            throw new BadRequestException("Room with id " + room.getId() + " in file RoomDB already exists.");

        writerToFile(room);

        return room;
    }

    public static void deleteRoom(Long idRoom)throws Exception{
        if (idRoom == null)
            throw new BadRequestException("This id " + idRoom + " is not exist.");

        if (checkObjectById(idRoom))
            throw new BadRequestException("Room with id " + idRoom + " in file RoomDB not found.");

        writerInFailBD(GeneralDAO.getPathRoomDB(), resultForWriting(idRoom));
    }

    public static Collection findRooms(Filter filter)throws Exception{
        if (filter == null)
            throw new BadRequestException("This filter - " + filter + " does not exist." );

        LinkedList<Room> foundRooms = new LinkedList<>();

        for (Room room : gettingListObjectsFromFileRoomDB()){
            if (filterCheck(room, filter)){
                foundRooms.add(room);
            }
            /*if ((filter.getNumberOfGuests() == 0 || room.getNumberOfGuests() == filter.getNumberOfGuests()) && (filter.getPrice() == 0 || room.getPrice() == filter.getPrice())){
                if (filter.getDateAvailableFrom() == null || room.getDateAvailableFrom().compareTo(filter.getDateAvailableFrom()) >= 0) {
                    if (room.isPetsAllowed() == filter.isPetsAllowed() && room.isBreakfastIncluded() == filter.isBreakfastIncluded()) {
                        if ((filter.getCountry() == null || room.getHotel().getCountry().equals(filter.getCountry())) && (filter.getCity() == null || room.getHotel().getCity().equals(filter.getCity()))) {
                            foundRooms.add(room);
                        }
                    }
                }
            }*/
        }
        return foundRooms;
    }

    private static boolean filterCheck(Room room, Filter filter)throws Exception{
        if (room == null || filter == null)
            throw new BadRequestException("Invalid incoming data");

        if (filter.getNumberOfGuests() != 0 && room.getNumberOfGuests() != filter.getNumberOfGuests())
            return false;

        if (filter.getPrice() != 0 && room.getPrice() != filter.getPrice())
            return false;

        if (filter.getDateAvailableFrom() != null && (room.getDateAvailableFrom().compareTo(filter.getDateAvailableFrom()) != 0) && (room.getDateAvailableFrom().compareTo(filter.getDateAvailableFrom()) <= 0))
            return false;

        if (room.isPetsAllowed() != filter.isPetsAllowed() && room.isBreakfastIncluded() != filter.isBreakfastIncluded())
            return false;

        if (filter.getCountry() != null && (!room.getHotel().getCountry().equals(filter.getCountry())))
            return false;

        if (filter.getCity() != null && (!room.getHotel().getCity().equals(filter.getCity())))
            return false;

        return true;
    }

    private static LinkedList<Room> gettingListObjectsFromFileRoomDB()throws Exception{
        LinkedList<Room> arrays = new LinkedList<>();

        int index = 0;
        for (String el : readFromFile(GeneralDAO.getPathRoomDB())){
            if (el != null){
                arrays.add(mapRooms(readFromFile(GeneralDAO.getPathRoomDB()).get(index)));
            }
            index++;
        }
        return arrays;
    }

    private static Room mapRooms(String string)throws Exception{
        if (string == null)
            throw new BadRequestException("String does not exist");

        String[] fields = string.split(",");

        Room room = new Room();
        room.setId(Long.parseLong(fields[0]));
        room.setNumberOfGuests(Integer.parseInt(fields[1]));
        room.setPrice(Double.parseDouble(fields[2]));
        room.setBreakfastIncluded(Boolean.parseBoolean(fields[3]));
        room.setPetsAllowed(Boolean.parseBoolean(fields[4]));
        room.setDateAvailableFrom(FORMAT.parse(fields[5]));
        String idHotel = "";
        for (Character ch : fields[6].toCharArray()) {
            if (ch != null && Character.isDigit(ch)) {
                idHotel += ch;
            }
        }
        room.setHotel(findHotelById(Long.parseLong(idHotel)));

        return room;
    }

    private static LinkedList<Hotel> gettingListObjectsFromFileHotelDB()throws Exception{
        LinkedList<Hotel> arrays = new LinkedList<>();

        int index = 0;
        for (String el : readFromFile(GeneralDAO.getPathHotelDB())){
            if (el != null){
                arrays.add(mapHotels(readFromFile(GeneralDAO.getPathHotelDB()).get(index)));
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

    private static Hotel findHotelById(Long id)throws Exception{
        if (id == null)
            throw new BadRequestException("This does  " + id + " not exist ");

        for (Hotel hotel : gettingListObjectsFromFileHotelDB()){
            if (hotel != null && hotel.getId() == id){
                return hotel;
            }
        }
        throw new BadRequestException("Hotel with " + id + " no such found.");
    }

    private static boolean checkObjectById(Long id)throws Exception{
        if (id == null)
            throw new BadRequestException("Invalid incoming data");

        for (Room el : gettingListObjectsFromFileRoomDB()){
            if (el != null && el.getId() == id){
                return false;
            }
        }
        return true;
    }

    private static void writerToFile(Room room)throws Exception{
        if (room == null)
            throw new BadRequestException("Room does not exist");

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(GeneralDAO.getPathRoomDB(), true))){
            bufferedWriter.append(Long.toString(room.getId()) + (","));
            bufferedWriter.append(Integer.toString(room.getNumberOfGuests()) + (","));
            bufferedWriter.append(Double.toString(room.getPrice()) + (","));
            bufferedWriter.append(Boolean.toString(room.isBreakfastIncluded()) + (","));
            bufferedWriter.append(Boolean.toString(room.isPetsAllowed()) + (","));
            bufferedWriter.append(FORMAT.format(room.getDateAvailableFrom()) + (","));
            bufferedWriter.append(room.getHotel().toString() + ("\n"));
        }catch (IOException e){
            throw new IOException("Can not write to file " + GeneralDAO.getPathRoomDB());
        }
    }

    private static StringBuffer resultForWriting(Long idRoom)throws Exception{
        StringBuffer res = new StringBuffer();

        for (Room el : gettingListObjectsFromFileRoomDB()){
            if (el != null && el.getId() != idRoom){
                res.append(Long.toString(el.getId()) + (","));
                res.append(Integer.toString(el.getNumberOfGuests()) + (","));
                res.append(Double.toString(el.getPrice()) + (","));
                res.append(Boolean.toString(el.isBreakfastIncluded()) + (","));
                res.append(Boolean.toString(el.isPetsAllowed()) + (","));
                res.append(FORMAT.format(el.getDateAvailableFrom()) + (","));
                res.append(el.getHotel().toString() + ("\n"));
            }
        }
        return res;
    }
}
