import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            HotelManager repo = new HotelManager("hotel.txt");
            System.out.println("\n=== Все отели (по городам, по звёздам) ===");
            repo.printHotelsSortedByCityAndStars();
            System.out.print("\n=== Поиск по городу ===\nВведите название города: ");
            String city = sc.nextLine();
            repo.printHotelsByCity(city);
            System.out.print("\n=== Поиск городов по названию отеля ===\nВведите название отеля: ");
            String hotel = sc.nextLine();
            repo.printCitiesByHotelName(hotel);
        }
        catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
        finally {
            sc.close();
        }
    }
}
