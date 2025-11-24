public class Hotel {
    private final String city;
    private final String name;
    private final int stars;

    public Hotel(String city, String name, int stars) {
        this.city = city;
        this.name = name;
        this.stars = stars;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public int getStars() {
        return stars;
    }

    public String toString() {
        return String.format("%s\t%s\t%d", city, name, stars);
    }
}
