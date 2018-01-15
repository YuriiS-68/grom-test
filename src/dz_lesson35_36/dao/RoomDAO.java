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

        if (checkObjectById(utils.getPathRoomDB(), room.getId()))
            throw new BadRequestException("Room with id " + room.getId() + " in file RoomDB already exists.");

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(utils.getPathRoomDB(), true))){
            bufferedWriter.append(Long.toString(room.getId()) + (","));
            bufferedWriter.append(Integer.toString(room.getNumberOfGuests()) + (","));
            bufferedWriter.append(Double.toString(room.getPrice()) + (","));
            bufferedWriter.append(Boolean.toString(room.isBreakfastIncluded()) + (","));
            bufferedWriter.append(Boolean.toString(room.isPetsAllowed()) + (","));
            bufferedWriter.append(FORMAT.format(room.getDateAvailableFrom()) + (","));
            bufferedWriter.append(room.getHotel().toString() + ("\n"));
        }catch (IOException e){
            throw new IOException("Can not write to file " + utils.getPathRoomDB());
        }
        return room;
    }

    public static void deleteRoom(Long idRoom)throws Exception{
        if (idRoom == null)
            throw new BadRequestException("This id " + idRoom + " is not exist.");

        if (!checkObjectById(utils.getPathRoomDB(), idRoom))
            throw new BadRequestException("Room with id " + idRoom + " in file RoomDB not found.");

        StringBuffer res = new StringBuffer();

        for (Room el : gettingListObjectsFromFileRoomDB(readFromFile(utils.getPathRoomDB()))){
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

        writerInFailBD(utils.getPathRoomDB(), res);
    }

    public static Collection findRooms(Filter filter)throws Exception{
        if (filter == null)
            throw new BadRequestException("This filter - " + filter + " does not exist." );

        LinkedList<Room> foundRooms = new LinkedList<>();

        for (Room room : gettingListObjectsFromFileRoomDB(readFromFile(utils.getPathRoomDB()))){
            if ((room.getNumberOfGuests() == filter.getNumberOfGuests()) || filter.getNumberOfGuests() == 0 && room.getPrice() == filter.getPrice() || filter.getPrice() == 0){
                if (room.getDateAvailableFrom().compareTo(filter.getDateAvailableFrom()) >= 0 || filter.getDateAvailableFrom() == null) {
                    if (room.isPetsAllowed() == filter.isPetsAllowed() && room.isBreakfastIncluded() == filter.isBreakfastIncluded()) {
                        if (room.getHotel().getCountry().equals(filter.getCountry()) || filter.getCountry() == null && room.getHotel().getCity().equals(filter.getCity()) || filter.getCity() == null) {
                            foundRooms.add(room);
                        }
                    }
                }
            }
        }
        return foundRooms;
    }

    private static LinkedList<Room> gettingListObjectsFromFileRoomDB(ArrayList<String> arrayList)throws Exception{
        if(arrayList == null)
            throw new BadRequestException("This " + arrayList + " is not exists");

        LinkedList<Room> arrays = new LinkedList<>();

        for (String el : arrayList){
            if (el != null){
                String[] fields = el.split(",");
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
                System.out.println("Room id - " + room.getId() + "; number of guests - " + room.getNumberOfGuests() + "; Country - " + room.getHotel().getCountry());
                arrays.add(room);
            }
        }
        return arrays;
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

    private static Hotel findHotelById(Long id)throws Exception{
        if (id == null)
            throw new BadRequestException("This does  " + id + " not exist ");

        for (Hotel hotel : gettingListObjectsFromFileHotelDB(readFromFile(utils.getPathHotelDB()))){
            if (hotel != null && hotel.getId() == id){
                return hotel;
            }
        }
        throw new BadRequestException("Hotel with " + id + " no such found.");
    }

    private static boolean checkObjectById(String path, Long id)throws Exception{
        if (path == null || id == null)
            throw new BadRequestException("Invalid incoming data");

        for (Room el : gettingListObjectsFromFileRoomDB(readFromFile(path))){
            if (el != null && el.getId() == id){
                return true;
            }
        }
        return false;
    }
}
