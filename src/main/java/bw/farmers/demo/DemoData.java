package bw.farmers.demo;

import bw.farmers.db.Database;
import bw.farmers.db.FarmerRepository;
import bw.farmers.model.Districts;
import bw.farmers.model.Farmer;
import bw.farmers.model.Gender;
import bw.farmers.model.ReportFilter;
import bw.farmers.model.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/**
 * Fills an empty database with fictional farmers so the admin screens have something to show.
 * Every name, number and address here is made up. These farmers have no login accounts.
 */
public final class DemoData {

    private static final List<String> FIRST_MALE = List.of("Kagiso", "Thabo", "Onkemetse", "Tshepo", "Kabelo",
            "Mpho", "Lesego", "Boitumelo", "Oratile", "Tumelo", "Kgosi", "Neo");
    private static final List<String> FIRST_FEMALE = List.of("Naledi", "Masego", "Dineo", "Kelebogile", "Lorato",
            "Onalenna", "Gaone", "Refilwe", "Tebogo", "Amogelang", "Boipelo", "Keabetswe");
    private static final List<String> LAST = List.of("Molefe", "Sebina", "Kgosi", "Mothibi", "Seretse", "Dintwe",
            "Masire", "Ramotswe", "Phiri", "Mogae", "Tau", "Moremi", "Kebonang", "Sekgoma", "Ntshekisang");

    private DemoData() {
    }

    /** Adds {@code count} farmers if the database has none. Returns how many were added. */
    public static int seedIfEmpty(Database db, int count, long seed) {
        FarmerRepository repo = new FarmerRepository(db);
        if (!repo.find(ReportFilter.ALL).isEmpty()) {
            return 0;
        }
        for (Farmer f : generate(count, seed)) {
            repo.insert(f, null);
        }
        return count;
    }

    public static List<Farmer> generate(int count, long seed) {
        Random r = new Random(seed);
        List<String> districts = Districts.all();
        return java.util.stream.IntStream.range(0, count).mapToObj(i -> {
            Gender g = r.nextBoolean() ? Gender.MALE : Gender.FEMALE;
            String first = pick(r, g == Gender.MALE ? FIRST_MALE : FIRST_FEMALE);
            String last = pick(r, LAST);
            String district = pick(r, districts);
            String sub = pick(r, Districts.subDistrictsOf(district));
            double size = 5 + r.nextInt(96) * 5;           // 5 to 480 ha
            double area = Math.round(size * (0.3 + r.nextDouble() * 0.7));
            Status status = i % 5 == 0 ? Status.PENDING : i % 7 == 0 ? Status.DECLINED : Status.APPROVED;
            return new Farmer(
                    String.format("%09d", 100_000_000 + i * 7919),
                    first, last,
                    String.format("7%07d", 1_000_000 + i * 1237),
                    LocalDate.of(1955 + r.nextInt(48), 1 + r.nextInt(12), 1 + r.nextInt(28)),
                    g,
                    "PO Box " + (100 + r.nextInt(9000)) + ", " + sub,
                    district, sub,
                    (first + "." + last + i).toLowerCase() + "@example.com",
                    size, area, status);
        }).toList();
    }

    private static <T> T pick(Random r, List<T> list) {
        return list.get(r.nextInt(list.size()));
    }
}
