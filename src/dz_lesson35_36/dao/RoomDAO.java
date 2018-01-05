package dz_lesson35_36.dao;

import dz_lesson35_36.exception.BadRequestException;
import dz_lesson35_36.model.Filter;
import dz_lesson35_36.model.Hotel;
import dz_lesson35_36.model.Room;
import dz_lesson35_36.model.Utils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class RoomDAO extends GeneralDAO{

    private static Utils utils = new Utils();
    private static final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");

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

        for (Room el : gettingListObjectsFromFileRoomDB(utils.getPathRoomDB())) {
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

        for (Room el : gettingListObjectsFromFileRoomDB(utils.getPathRoomDB())) {
            if (el.getNumberOfGuests() == filter.getNumberOfGuests() || filter.getNumberOfGuests() == 0 && el.getPrice() == filter.getPrice() || filter.getPrice() == 0){
                if (el.getDateAvailableFrom().compareTo(filter.getDateAvailableFrom()) >= 0 || filter.getDateAvailableFrom() == null) {
                    if (el.isPetsAllowed() == filter.isPetsAllowed() && el.isBreakfastIncluded() == filter.isBreakfastIncluded()) {
                        if (el.getHotel().getCountry().equals(filter.getCountry()) || filter.getCountry() == null && el.getHotel().getCity().equals(filter.getCity()) || filter.getCity() == null) {
                            foundRooms.add(el);
                        }
                    }
                }
            }
        }
        return foundRooms;
    }

    private static LinkedList<Room> gettingListObjectsFromFileRoomDB(String path)throws Exception{
        if(path == null)
            throw new BadRequestException("This path " + path + " is not exists");

        LinkedList<Room> arrays = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            String line;

            while ((line = br.readLine()) != null){
                String[] result = line.split("\n");
                for (String el : result){
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
                        arrays.add(room);
                    }
                }
            }
        }catch (FileNotFoundException e){
            throw new FileNotFoundException("File does not exist");
        } catch (IOException e) {
            throw new IOException("Reading from file " + path + " failed");
        }
        return arrays;
    }

    private static LinkedList<Hotel> gettingListObjectsFromFileHotelDB(String path)throws Exception{
        if(path == null)
            throw new BadRequestException("This path " + path + " is not exists");

        LinkedList<Hotel> arrays = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            String line;

            while ((line = br.readLine()) != null){
                String[] result = line.split("\n");
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
                }
            }
        }catch (FileNotFoundException e){
            throw new FileNotFoundException("File does not exist");
        } catch (IOException e) {
            throw new IOException("Reading from file " + path + " failed");
        }
        return arrays;
    }

    private static Hotel findHotelById(Long id)throws Exception{
        if (id == null)
            throw new BadRequestException("This does  " + id + " not exist ");

        for (Hotel hotel : gettingListObjectsFromFileHotelDB(utils.getPathHotelDB())) {
            if (hotel != null && hotel.getId() == id){
                return hotel;
            }
        }
        throw new BadRequestException("Hotel with " + id + " no such found.");
    }

    private static boolean checkObjectById(String path, Long id)throws Exception{
        if (path == null || id == null)
            throw new BadRequestException("Invalid incoming data");

        for (Room el : gettingListObjectsFromFileRoomDB(path)) {
            if (el != null && el.getId() == id){
                return true;
            }
        }
        return false;
    }
}
