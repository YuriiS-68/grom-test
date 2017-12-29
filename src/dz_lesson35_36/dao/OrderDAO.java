package dz_lesson35_36.dao;

import dz_lesson35_36.exception.BadRequestException;
import dz_lesson35_36.model.*;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

public class OrderDAO extends GeneralDAO{

    private static Utils utils = new Utils();
    private static final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public static void bookRoom(long roomId, long userId, long hotelId)throws Exception{
        //проверить есть ли в файлах БД такие данные
        //если есть создать ордер и просетить ему данные по всем полям
        //сохранить в файл БД
        if (roomId == 0 || userId == 0 || hotelId == 0)
            throw new BadRequestException("Invalid incoming data");

        if(checkRoomIdInFiles(utils.getPathRoomDB(), roomId))
            throw new BadRequestException("Room with id " + roomId + " is not exist");

        if (checkIdUserInOrderDB(utils.getPathUserDB(), userId))
            throw new BadRequestException("User with id " + userId + " is not exist");

        if (checkIdHotelInOrderDB(utils.getPathHotelDB(), hotelId))
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
            bufferedWriter.append(Long.toString(order.getId())).append(",");
            bufferedWriter.append(order.getUser().toString()).append(",");
            bufferedWriter.append(order.getRoom().toString()).append(",");
            bufferedWriter.append(dateFrom).append(",");
            bufferedWriter.append(dateTo).append(",");
            bufferedWriter.append(Double.toString(orderCost));
            bufferedWriter.append("\n");
        }catch (IOException e){
            throw new IOException("Can not write to file " + utils.getPathOrderDB());
        }
    }

    public static void cancelReservation(long roomId, long userId)throws Exception{
        if (roomId == 0 || userId == 0)
            throw new BadRequestException("Invalid incoming data");

        if(checkIdRoomInOrderDB(utils.getPathOrderDB(), roomId))
            throw new BadRequestException("Room with id " + roomId + " is not exist");

        if (checkIdUserInOrderDB(utils.getPathOrderDB(), userId))
            throw new BadRequestException("User with id " + userId + " is not exist");

        StringBuffer res = new StringBuffer();

        int index = 0;
        for (Order el : gettingListObjectsFromFile(utils.getPathOrderDB())) {
            if (el != null && el.getUser().getId() != userId && el.getRoom().getId() != roomId) {
                res.append(el);
                res.append("\n");
            }
            index++;
        }

        writerInFailBD(utils.getPathOrderDB(), res);
    }

    private static User findUserById(Long id)throws Exception{
        if (id == null)
            throw new BadRequestException("This does  " + id + " not exist ");

        User user = new User();

        for (Order el : gettingListObjectsFromFile(utils.getPathOrderDB())){
            if (el != null && el.getUser().getId() == id){
                user = el.getUser();
            }
        }
        return user;
    }

    private static Room findRoomById(Long id)throws Exception{
        if (id == null)
            throw new BadRequestException("This does  " + id + " not exist ");

        Room room = new Room();

        for (Order el : gettingListObjectsFromFile(utils.getPathOrderDB())){
            if (el != null && el.getRoom().getId() == id){
                room = el.getRoom();
            }
        }
        return room;
    }

    private static boolean checkRoomIdInFiles(String path, Long id)throws Exception{
        if (path == null || id == 0 )
            throw new BadRequestException("Invalid incoming data");

        for (Order el : gettingListObjectsFromFile(path)){
            if (el != null && el.getRoom().getId() != id){
                return false;
            }
        }
        return true;
    }

    private static boolean checkIdRoomInOrderDB(String path, Long id)throws Exception{
        if (path == null || id == 0 )
            throw new BadRequestException("Invalid incoming data");

        for (Order el : gettingListObjectsFromFile(path)){
            if (el != null && el.getRoom().getId() != id){
                return false;
            }
        }
        return true;
    }

    private static boolean checkIdHotelInOrderDB(String path, Long id)throws Exception{
        if (path == null || id == 0 )
            throw new BadRequestException("Invalid incoming data");

        Room room = new Room();
        for (Order el : gettingListObjectsFromFile(path)){
            if (el != null && el.getRoom().getId() != id){
                room = el.getRoom();
                if (room.getHotel().getId() != id){
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkIdUserInOrderDB(String path, Long id)throws Exception{
        if (path == null || id == 0 )
            throw new BadRequestException("Invalid incoming data");

        for (Order el : gettingListObjectsFromFile(path)){
            if (el != null && el.getUser().getId() != id){
                return false;
            }
        }
        return true;
    }

    private static LinkedList<Order> gettingListObjectsFromFile(String path)throws Exception{
        if(path == null)
            throw new BadRequestException("This path " + path + " is not exists");

        LinkedList<Order> arrays = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            String line;

            while ((line = br.readLine()) != null){
                String[] result = line.split("\n");
                int index = 0;
                for (String el : result){
                    if (el != null){
                        String[] fields = el.split(",");
                        Order order = new Order();
                        order.setId(Long.parseLong(fields[0]));
                        order.setUser(findUserById(Long.parseLong(fields[1])));
                        order.setRoom(findRoomById(Long.parseLong(fields[2])));
                        order.setDateFrom(FORMAT.parse(fields[3]));
                        order.setDateTo(FORMAT.parse(fields[4]));
                        order.setMoneyPaid(Long.parseLong(fields[5]));
                        arrays.add(order);
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
}
