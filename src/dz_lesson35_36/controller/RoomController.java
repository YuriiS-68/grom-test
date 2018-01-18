package dz_lesson35_36.controller;

import dz_lesson35_36.exception.BadRequestException;
import dz_lesson35_36.model.Filter;
import dz_lesson35_36.model.Room;
import dz_lesson35_36.service.RoomService;

import java.util.Collection;

public class RoomController {

    private static RoomService roomService = new RoomService();

    public static Room addRoom(Room room)throws Exception {
        if (room == null)
            throw new BadRequestException("This " + room + " is not exist");

        return roomService.addRoom(room);
    }

    public static void deleteRoom(Long idRoom)throws Exception{
        if (idRoom == null)
            throw new BadRequestException("This id " + idRoom + " is not exist.");

    }

    public static Collection findRooms(Filter filter)throws Exception{
        if (filter == null)
            throw new BadRequestException("This filter - " + filter + " does not exist." );

        return roomService.findRooms(filter);
    }
}
