package dz_lesson35_36.controller;

import dz_lesson35_36.exception.BadRequestException;
import dz_lesson35_36.service.OrderService;

public class OrderController {

    private static OrderService orderService = new OrderService();

    public static void bookRoom(long roomId, long userId, long hotelId)throws Exception{
        if (roomId == 0 || userId == 0 || hotelId == 0)
            throw new BadRequestException("Invalid incoming data");

    }

    public static void cancelReservation(long roomId, long userId)throws Exception{
        if (roomId == 0 || userId == 0)
            throw new BadRequestException("Invalid incoming data");

    }
}
