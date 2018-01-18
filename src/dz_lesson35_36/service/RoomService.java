package dz_lesson35_36.service;

import dz_lesson35_36.dao.GeneralDAO;
import dz_lesson35_36.dao.RoomDAO;
import dz_lesson35_36.exception.BadRequestException;
import dz_lesson35_36.model.Filter;
import dz_lesson35_36.model.Room;

import java.util.Collection;
import java.util.LinkedList;

public class RoomService {

    private static RoomDAO roomDAO = new RoomDAO();

    public static Room addRoom(Room room)throws Exception {
        if (room == null)
            throw new BadRequestException("This " + room + " is not exist");

        return roomDAO.addRoom(room);
    }

    public static void deleteRoom(Long idRoom)throws Exception{
        if (idRoom == null)
            throw new BadRequestException("This id " + idRoom + " is not exist.");

    }

    public static Collection findRooms(Filter filter)throws Exception{
        if (filter == null)
            throw new BadRequestException("This filter - " + filter + " does not exist." );

        return roomDAO.findRooms(filter);
    }
}
