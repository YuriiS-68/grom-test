package dz_lesson35_36.dao;

import dz_lesson35_36.exception.BadRequestException;
import dz_lesson35_36.model.*;

import java.io.*;
import java.util.*;

public class OrderDAO extends GeneralDAO{

    public static void bookRoom(long roomId, long userId, long hotelId)throws Exception{
        //проверить есть ли в файлах БД такие данные
        //если есть создать ордер и просетить ему данные по всем полям
        //сохранить в файл БД
        if (roomId == 0 || userId == 0 || hotelId == 0)
            throw new BadRequestException("Invalid incoming data");

        if(!checkIdRoomInRoomDB(utils.getPathRoomDB(), roomId))
            throw new BadRequestException("Room with id " + roomId + " is not exist");

        if (!checkIdUserInUserDB(utils.getPathUserDB(), userId))
            throw new BadRequestException("User with id " + userId + " is not exist");

        if (!checkIdHotelInHotelDB(utils.getPathHotelDB(), hotelId))
            throw new BadRequestException("Hotel with id " + hotelId + " is not exist");

        Order order = new Order();

        String dateFrom = "23.11.2017";
        String dateTo = "06.12.2017";

        Random random = new Random();
        order.setId(random.nextLong() / 1000000000000L);
        if (order.getId() < 0){
            order.setId(-1 * order.getId());
        }

        order.setUser(findUserById(userId));
        order.setRoom(findRoomById(roomId));
        order.setDateFrom(FORMAT.parse(dateFrom));
        order.setDateTo(FORMAT.parse(dateTo));

        Date dateStart = FORMAT.parse(dateFrom);
        Date dateFinish = FORMAT.parse(dateTo);

        long difference = dateStart.getTime() - dateFinish.getTime();
        int days = (int)(difference / (24 * 60 * 60 * 1000));
        double orderCost = findRoomById(roomId).getPrice() * days;
        if (orderCost < 0){
            orderCost = -1 * orderCost;
        }

        order.setMoneyPaid(orderCost);

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(utils.getPathOrderDB(), true))){
            bufferedWriter.append(Long.toString(order.getId()) + (","));
            bufferedWriter.append(order.getUser().toString() + (","));
            bufferedWriter.append(order.getRoom().toString() + (","));
            bufferedWriter.append(dateFrom + (","));
            bufferedWriter.append(dateTo + (","));
            bufferedWriter.append(Double.toString(orderCost) + ("\n"));
        }catch (IOException e){
            throw new IOException("Can not write to file " + utils.getPathOrderDB());
        }
    }

    public static void cancelReservation(long roomId, long userId)throws Exception{
        if (roomId == 0 || userId == 0)
            throw new BadRequestException("Invalid incoming data");

        if(!checkIdRoomInOrderDB(utils.getPathOrderDB(), roomId))
            throw new BadRequestException("Room with id " + roomId + " is not exist");

        if (!checkIdUserInOrderDB(utils.getPathOrderDB(), userId))
            throw new BadRequestException("User with id " + userId + " is not exist");

        StringBuffer res = new StringBuffer();

        int index = 0;
        for (Order el : gettingListObjectsFromFileOrderDB(readFromFile(utils.getPathOrderDB()))){
            if (el != null && el.getUser().getId() == userId && el.getRoom().getId() == roomId) {
                el = null;
            }else {
                if (el != null){
                    res.append(Long.toString(el.getId()) + (","));
                    res.append(el.getUser().toString() + (","));
                    res.append(el.getRoom().toString() + (","));
                    res.append(FORMAT.format(el.getDateFrom()) + (","));
                    res.append(FORMAT.format(el.getDateTo()) + (","));
                    res.append(Double.toString(el.getMoneyPaid()) + ("\n"));
                }
            }
            index++;
        }

        writerInFailBD(utils.getPathOrderDB(), res);
    }

    private static User findUserById(Long id)throws Exception{
        if (id == null)
            throw new BadRequestException("This does  " + id + " not exist ");

        for (User user : gettingListObjectsFromFileUserDB(readFromFile(utils.getPathUserDB()))){
            if (user != null && user.getId() == id){
                return user;
            }
        }
        throw new BadRequestException("User with " + id + " no such found.");
    }

    private static Room findRoomById(Long id)throws Exception{
        if (id == null)
            throw new BadRequestException("This does  " + id + " not exist ");

        for (Room room : gettingListObjectsFromFileRoomDB(readFromFile(utils.getPathRoomDB()))){
            if (room != null && room.getId() == id){
                return room;
            }
        }
        throw new BadRequestException("Room with " + id + " no such found.");
    }

    private static boolean checkIdRoomInOrderDB(String path, Long id)throws Exception{
        if (path == null || id == 0 )
            throw new BadRequestException("Invalid incoming data");

        for (Order el : gettingListObjectsFromFileOrderDB(readFromFile(path))){
            if (el != null && el.getRoom().getId() == id){
                return true;
            }
        }
        return false;
    }

    private static boolean checkIdUserInOrderDB(String path, Long id)throws Exception{
        if (path == null || id == 0 )
            throw new BadRequestException("Invalid incoming data");

        for (Order el : gettingListObjectsFromFileOrderDB(readFromFile(path))){
            if (el != null && el.getUser().getId() == id){
                return true;
            }
        }
        return false;
    }

    private static LinkedList<Order> gettingListObjectsFromFileOrderDB(ArrayList<String> arrayList)throws Exception{
        if(arrayList == null)
            throw new BadRequestException("This " + arrayList + " is not exists");

        LinkedList<Order> arrays = new LinkedList<>();

        for (String el : arrayList){
            if (el != null){
                String[] fields = el.split(",");
                Order order = new Order();
                order.setId(Long.parseLong(fields[0]));
                String idUser = "";
                for (Character ch : fields[1].toCharArray()) {
                    if (ch != null && Character.isDigit(ch)) {
                        idUser += ch;
                    }
                }
                order.setUser(findUserById(Long.parseLong(idUser)));
                String idRoom = "";
                for (Character ch : fields[6].toCharArray()) {
                    if (ch != null && Character.isDigit(ch)) {
                        idRoom += ch;
                    }
                }
                order.setRoom(findRoomById(Long.parseLong(idRoom)));
                order.setDateFrom(FORMAT.parse(fields[17]));
                order.setDateTo(FORMAT.parse(fields[18]));
                order.setMoneyPaid(Double.parseDouble(fields[19]));
                arrays.add(order);
            }
        }
        return arrays;
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

    private static LinkedList<User> gettingListObjectsFromFileUserDB(ArrayList<String> arrayList)throws Exception{
        if(arrayList == null)
            throw new BadRequestException("This arrayList " + arrayList + " does not exists");

        LinkedList<User> arrays = new LinkedList<>();

        for (String el : arrayList){
            if (el != null){
                String[] fields = el.split(",");
                User user = new User();
                user.setId(Long.parseLong(fields[0]));
                user.setUserName(fields[1]);
                user.setPassword(fields[2]);
                user.setCountry(fields[3]);
                if (fields[4].equals("USER")){
                    user.setUserType(UserType.USER);
                }else {
                    user.setUserType(UserType.ADMIN);
                }
                arrays.add(user);
            }
        }
        return arrays;
    }

    private static boolean checkIdRoomInRoomDB(String path, Long id)throws Exception{
        if (path == null || id == 0 )
            throw new BadRequestException("Invalid incoming data");

        for (Room room : gettingListObjectsFromFileRoomDB(readFromFile(path))){
            if (room != null && room.getId() == id){
                return true;
            }
        }
        return false;
    }

    private static boolean checkIdUserInUserDB(String path, Long id)throws Exception{
        if (path == null || id == 0 )
            throw new BadRequestException("Invalid incoming data");

        for (User user : gettingListObjectsFromFileUserDB(readFromFile(path))){
            if (user != null && user.getId() == id){
                return true;
            }
        }
        return false;
    }

    private static boolean checkIdHotelInHotelDB(String path, Long id)throws Exception{
        if (path == null || id == 0 )
            throw new BadRequestException("Invalid incoming data");

        for (Hotel hotel : gettingListObjectsFromFileHotelDB(readFromFile(path))){
            if (hotel != null && hotel.getId() == id){
                return true;
            }
        }
        return false;
    }

    /*private static String gettingOnlyNumericCharacters(String[] arrayLine) {
        String id = "";
        for (Character ch : arrayLine[0].toCharArray()) {
            if (ch != null && Character.isDigit(ch)) {
                id += ch;
            }
        }
        return id;
    }*/
}
