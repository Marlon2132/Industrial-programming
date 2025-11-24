import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.*;

public class HotelManager {
    private final List<Hotel> hotels;

    public HotelManager(String filePath) throws IOException {
        hotels = Files.lines(Paths.get(filePath))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(line -> line.split("\\s+"))
                .filter(parts -> parts.length >= 3)
                .map(parts -> new Hotel(
                        parts[0],
                        parts[1],
                        Integer.parseInt(parts[2])
                ))
                .collect(Collectors.toList());
    }

    public void printHotelsSortedByCityAndStars() {
        Map<String, List<Hotel>> byCity = hotels.stream().collect(Collectors.groupingBy(Hotel::getCity));

        byCity.keySet().stream()
                .sorted()
                .forEach(city -> {
                    System.out.println("Город: " + city);
                    byCity.get(city).stream()
                            .sorted(Comparator.comparingInt(Hotel::getStars).reversed())
                            .forEach(h -> System.out.println("  " + h));
                });
    }

    public void printHotelsByCity(String cityQuery) {
        System.out.println("\nОтели в городе \"" + cityQuery + "\":");
        List<Hotel> result = hotels.stream()
                .filter(h -> h.getCity().equalsIgnoreCase(cityQuery))
                .sorted(Comparator.comparingInt(Hotel::getStars).reversed())
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            System.out.println("  (нет отелей)");
        } else {
            result.forEach(h -> System.out.println("  " + h));
        }
    }

    public void printCitiesByHotelName(String hotelQuery) {
        System.out.println("\nГорода с отелем \"" + hotelQuery + "\":");
        List<String> cities = hotels.stream()
                .filter(h -> h.getName().equalsIgnoreCase(hotelQuery))
                .map(Hotel::getCity)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (cities.isEmpty()) {
            System.out.println("  (не найдено)");
        }
        else {
            cities.forEach(c -> System.out.println("  " + c));
        }
    }
}
