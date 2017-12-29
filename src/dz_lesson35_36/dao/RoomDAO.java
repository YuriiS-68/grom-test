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

        if (checkObjectById(utils.getPathRoomDB(), room))
            throw new BadRequestException("Room with id " + room.getId() + " already exists");

        String date = FORMAT.format(room.getDateAvailableFrom());

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(utils.getPathRoomDB(), true))){
            bufferedWriter.append(Long.toString(room.getId())).append(",");
            bufferedWriter.append(Integer.toString(room.getNumberOfGuests())).append(",");
            bufferedWriter.append(Double.toString(room.getPrice())).append(",");
            bufferedWriter.append(Boolean.toString(room.isBreakfastIncluded())).append(",");
            bufferedWriter.append(Boolean.toString(room.isPetsAllowed())).append(",");
            bufferedWriter.append(date).append(",");
            bufferedWriter.append(room.getHotel().toString());
            bufferedWriter.append("\n");
        }catch (IOException e){
            throw new IOException("Can not write to file " + utils.getPathRoomDB());
        }
        return room;
    }

    public static void deleteRoom(Long idRoom)throws Exception{
        if (idRoom == null)
            throw new BadRequestException("This id " + idRoom + " is not exist");

        StringBuffer res = new StringBuffer();

        int index = 0;
        for (Room el : gettingListObjectsFromFile(utils.getPathRoomDB())) {
            if (el != null && el.getId() != idRoom){
                res.append(el);
                res.append("\n");
            }
            index++;
        }

        writerInFailBD(utils.getPathRoomDB(), res);
    }

    public static Collection findRooms(Filter filter)throws Exception{
        if (filter == null)
            throw new BadRequestException("This filter - " + filter + " does not exist." );

        LinkedList<Room> foundRooms = new LinkedList<>();

        for (Room el : gettingListObjectsFromFile(utils.getPathRoomDB())) {
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

    private static LinkedList<Room> gettingListObjectsFromFile(String path)throws Exception{
        if(path == null)
            throw new BadRequestException("This path " + path + " is not exists");

        LinkedList<Room> arrays = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            String line;

            while ((line = br.readLine()) != null){
                String[] result = line.split("\n");
                int index = 0;
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

    private static Hotel findHotelById(Long id)throws Exception{
        if (id == null)
            throw new BadRequestException("This does  " + id + " not exist ");

        int index = 0;
        for (Room el : gettingListObjectsFromFile(utils.getPathHotelDB())) {
            if (el != null && el.getHotel().getId() == id){
                return el.getHotel();
            }
            index++;
        }
        throw new BadRequestException("Hotel with " + id + " no such found.");
    }

    private static boolean checkObjectById(String path, Room room)throws Exception{
        if (path == null || room == null)
            throw new BadRequestException("Invalid incoming data");

        int index = 0;
        for (Room el : gettingListObjectsFromFile(path)) {
            if (el != null && el.getId() == room.getId()){
                return true;
            }
            index++;
        }
        return false;
    }
}
