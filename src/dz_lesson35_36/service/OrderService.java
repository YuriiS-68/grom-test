package dz_lesson35_36.service;

import dz_lesson35_36.dao.OrderDAO;
import dz_lesson35_36.exception.BadRequestException;

public class OrderService {

    private static OrderDAO orderDAO = new OrderDAO();

    public static void bookRoom(long roomId, long userId, long hotelId)throws Exception{
        if (roomId == 0 || userId == 0 || hotelId == 0)
            throw new BadRequestException("Invalid incoming data");

    }

    public static void cancelReservation(long roomId, long userId)throws Exception{
        if (roomId == 0 || userId == 0)
            throw new BadRequestException("Invalid incoming data");

    }

}
