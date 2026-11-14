package bw.farmers.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Botswana districts and their sub-districts, as used on the registration form. */
public final class Districts {

    private static final Map<String, List<String>> SUB_DISTRICTS = new LinkedHashMap<>();

    static {
        SUB_DISTRICTS.put("Central", List.of("Bobirwa", "Boteti", "Lerala", "Mahalapye", "Mmadinare",
                "Mmaphashalala", "Mogorosi", "Nata", "Orapa", "Paje", "Rakops", "Sebina", "Sefhare",
                "Serowe/Palapye", "Shoshong", "Taupye", "Tonota", "Tutume"));
        SUB_DISTRICTS.put("Chobe", List.of("Kasane"));
        SUB_DISTRICTS.put("Ghanzi", List.of("Charleshill", "Ghanzi"));
        SUB_DISTRICTS.put("Kgalagadi", List.of("Bokspits", "Hukuntsi", "Kang", "Khawa", "Khuis",
                "Kolonkwane", "Makopong", "Middlepits", "Omaweneno", "Struizendam", "Tsabong", "Werda"));
        SUB_DISTRICTS.put("Kgatleng", List.of("Artesia", "Bokaa", "Malotwana", "Mmathubudukwane",
                "Mochudi", "Modipane", "Oodi", "Ramonaka", "Sikwane"));
        SUB_DISTRICTS.put("Kweneng", List.of("Letlhakeng", "Molepolole"));
        SUB_DISTRICTS.put("North-East", List.of("Francistown", "Masunga"));
        SUB_DISTRICTS.put("North-West", List.of("Ngamiland East", "Ngamiland West", "Okavango"));
        SUB_DISTRICTS.put("South-East", List.of("Gaborone", "Mogobane", "Otse", "Ramotswa", "Tlokweng"));
        SUB_DISTRICTS.put("Southern", List.of("Goodhope", "Jwaneng", "Kanye", "Mabutsane", "Moshupa"));
    }

    private Districts() {
    }

    public static List<String> all() {
        return List.copyOf(SUB_DISTRICTS.keySet());
    }

    /** Sub-districts of {@code district}, or an empty list for an unknown district. */
    public static List<String> subDistrictsOf(String district) {
        return SUB_DISTRICTS.getOrDefault(district, List.of());
    }

    public static boolean isValid(String district, String subDistrict) {
        return subDistrictsOf(district).contains(subDistrict);
    }
}
