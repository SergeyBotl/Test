package entity;

import java.util.List;

public class Hotel {

    private long id;
    private String hotelName;
    private String cityName;
    private List<Room> rooms;

    public Hotel(String hotelName, String cityName, List<Room> rooms) {
        this.hotelName = hotelName;
        this.cityName = cityName;
        this.rooms = rooms;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getHotelName() {
        return hotelName;
    }

    public String getCityName() {
        return cityName;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hotel hotel = (Hotel) o;

        if (id != hotel.id) return false;
        if (!hotelName.equals(hotel.hotelName)) return false;
        return cityName.equals(hotel.cityName);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + hotelName.hashCode();
        result = 31 * result + cityName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Hotel: " +
                "id=" + id +
                ", Name=" + hotelName +
                ", City=" + cityName +
                ", All rooms=" + rooms.size() +
                ", Reserved=" + rooms.stream().filter(r -> r.getUserReservedId()!= -1).count();
    }
}
