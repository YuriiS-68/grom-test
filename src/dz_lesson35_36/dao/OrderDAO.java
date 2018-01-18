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

        if(!checkIdRoom(roomId))
            throw new BadRequestException("Room with id " + roomId + " is not exist");

        if (!checkIdUser(userId))
            throw new BadRequestException("User with id " + userId + " is not exist");

        if (!checkIdHotel(hotelId))
            throw new BadRequestException("Hotel with id " + hotelId + " is not exist");

        writerToFile(mapOrder(roomId, userId));
    }

    private static Order mapOrder(long roomId, long userId)throws Exception{
        Order order = new Order();

        assignmentOrderId(order);

        String dateFrom = "23.11.2017";
        String dateTo = "06.12.2017";
        Date dateStart = FORMAT.parse(dateFrom);
        Date dateFinish = FORMAT.parse(dateTo);

        order.setUser(findUserById(userId));
        order.setRoom(findRoomById(roomId));
        order.setDateFrom(FORMAT.parse(dateFrom));
        order.setDateTo(FORMAT.parse(dateTo));

        long difference = dateStart.getTime() - dateFinish.getTime();
        int days = (int)(difference / (24 * 60 * 60 * 1000));
        double orderCost = findRoomById(roomId).getPrice() * days;
        if (orderCost < 0){
            orderCost = -1 * orderCost;
        }

        order.setMoneyPaid(orderCost);

        return order;
    }

    public static void cancelReservation(long roomId, long userId)throws Exception{
        if (roomId == 0 || userId == 0)
            throw new BadRequestException("Invalid incoming data");

        if(!checkIdRoomInOrderDB(roomId))
            throw new BadRequestException("Room with id " + roomId + " is not exist");

        if (!checkIdUserInOrderDB(userId))
            throw new BadRequestException("User with id " + userId + " is not exist");

        writerInFailBD(GeneralDAO.getPathOrderDB(), resultForWriting(roomId, userId));
    }

    private static boolean checkIdRoomInOrderDB(Long id)throws Exception{
        if (id == 0 )
            throw new BadRequestException("Invalid incoming data");

        for (Order el : gettingListObjectsFromFileOrderDB()){
            if (el != null && el.getRoom().getId() == id){
                return true;
            }
        }
        return false;
    }

    private static boolean checkIdUserInOrderDB(Long id)throws Exception{
        if (id == 0 )
            throw new BadRequestException("Invalid incoming data");

        for (Order el : gettingListObjectsFromFileOrderDB()){
            if (el != null && el.getUser().getId() == id){
                return true;
            }
        }
        return false;
    }

    private static LinkedList<Order> gettingListObjectsFromFileOrderDB()throws Exception{
        LinkedList<Order> arrays = new LinkedList<>();

        int index = 0;
        for (String el : readFromFile(GeneralDAO.getPathOrderDB())){
            if (el != null){
                arrays.add(mapOrders(readFromFile(GeneralDAO.getPathOrderDB()).get(index)));
            }
            index++;
        }
        return arrays;
    }

    private static Order mapOrders(String string)throws Exception{
        if (string == null)
            throw new BadRequestException("String does not exist");

        String[] fields = string.split(",");

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

        return order;
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

    private static User findUserById(Long id)throws Exception{
        if (id == null)
            throw new BadRequestException("This does  " + id + " not exist ");

        for (User user : gettingListObjectsFromFileUserDB()){
            if (user != null && user.getId() == id){
                return user;
            }
        }
        throw new BadRequestException("User with " + id + " no such found.");
    }

    private static Room findRoomById(Long id)throws Exception{
        if (id == null)
            throw new BadRequestException("This does  " + id + " not exist ");

        for (Room room : gettingListObjectsFromFileRoomDB()){
            if (room != null && room.getId() == id){
                return room;
            }
        }
        throw new BadRequestException("Room with " + id + " no such found.");
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

    private static LinkedList<User> gettingListObjectsFromFileUserDB()throws Exception{
        LinkedList<User> arrays = new LinkedList<>();

        int index = 0;
        for (String el : readFromFile(GeneralDAO.getPathUserDB())){
            if (el != null){
                arrays.add(mapUsers(readFromFile(GeneralDAO.getPathUserDB()).get(index)));
            }
            index++;
        }
        return arrays;
    }

    private static User mapUsers(String string)throws Exception{
        if (string == null)
            throw new BadRequestException("String does not exist");

        String[] fields = string.split(",");

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
        return user;
    }

    private static boolean checkIdRoom(Long id)throws Exception{
        if (id == 0 )
            throw new BadRequestException("Invalid incoming data");

        for (Room room : gettingListObjectsFromFileRoomDB()){
            if (room != null && room.getId() == id){
                return true;
            }
        }
        return false;
    }

    private static boolean checkIdUser(Long id)throws Exception{
        if (id == 0 )
            throw new BadRequestException("Invalid incoming data");

        for (User user : gettingListObjectsFromFileUserDB()){
            if (user != null && user.getId() == id){
                return true;
            }
        }
        return false;
    }

    private static boolean checkIdHotel(Long id)throws Exception{
        if (id == 0 )
            throw new BadRequestException("Invalid incoming data");

        for (Hotel hotel : gettingListObjectsFromFileHotelDB()){
            if (hotel != null && hotel.getId() == id){
                return true;
            }
        }
        return false;
    }

    private static void assignmentOrderId(Order order)throws Exception{
        if (order == null)
            throw new BadRequestException("User does not exist");

        Random random = new Random();
        order.setId(random.nextInt());
        if (order.getId() < 0){
            order.setId(-1 * order.getId());
        }
    }

    private static void writerToFile(Order order)throws Exception{
        if (order == null)
            throw new BadRequestException("Room does not exist");

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(GeneralDAO.getPathOrderDB(), true))){
            bufferedWriter.append(Long.toString(order.getId()) + (","));
            bufferedWriter.append(order.getUser().toString() + (","));
            bufferedWriter.append(order.getRoom().toString() + (","));
            bufferedWriter.append(FORMAT.format(order.getDateFrom()) + (","));
            bufferedWriter.append(FORMAT.format(order.getDateTo()) + (","));
            bufferedWriter.append(Double.toString(order.getMoneyPaid()) + ("\n"));
        }catch (IOException e){
            throw new IOException("Can not write to file " + GeneralDAO.getPathOrderDB());
        }
    }

    private static StringBuffer resultForWriting(long roomId, long userId)throws Exception{
        StringBuffer res = new StringBuffer();

        int index = 0;
        for (Order el : gettingListObjectsFromFileOrderDB()){
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
        return res;
    }
}
