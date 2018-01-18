package dz_lesson35_36.controller;

import dz_lesson35_36.exception.BadRequestException;
import dz_lesson35_36.model.Hotel;
import dz_lesson35_36.service.HotelService;

import java.util.LinkedList;

public class HotelController {

    private static HotelService hotelService = new HotelService();

    public static Hotel addHotel(Hotel hotel)throws Exception{
        if (hotel == null)
            throw new BadRequestException("This " + hotel + " is not exist");

        return hotelService.addHotel(hotel);
    }

    public static void deleteHotel(Long idHotel)throws Exception {
        if (idHotel == null)
            throw new BadRequestException("This id " + idHotel + " is not exist");

    }

    public static LinkedList<Hotel> findHotelByName(String name)throws Exception{
        if (name == null)
            throw new BadRequestException("This name - " + name + " does not exist." );

        return hotelService.findHotelByName(name);
    }

    public static LinkedList<Hotel> findHotelByCity(String city)throws Exception{
        if (city == null)
            throw new BadRequestException("This city - " + city + " does not exist." );

        return hotelService.findHotelByName(city);
    }
}
