package com.example.propertyview.specification;

import com.example.propertyview.entity.Hotel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

/**
 * Helper class providing reusable specifications for hotel searches.
 * Methods return {@code null} when the corresponding filter value is empty,
 * allowing callers to chain them without explicit null checks.
 */
public final class HotelSpecifications {

    private HotelSpecifications() { /* utility */ }

    public static Specification<Hotel> nameLike(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        String pattern = "%" + name.toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern);
    }

    public static Specification<Hotel> brandLike(String brand) {
        if (!StringUtils.hasText(brand)) {
            return null;
        }
        String pattern = "%" + brand.toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("brand")), pattern);
    }

    public static Specification<Hotel> cityLike(String city) {
        if (!StringUtils.hasText(city)) {
            return null;
        }
        String pattern = "%" + city.toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("address").get("city")), pattern);
    }

    public static Specification<Hotel> countryLike(String country) {
        if (!StringUtils.hasText(country)) {
            return null;
        }
        String pattern = "%" + country.toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("address").get("country")), pattern);
    }

    public static Specification<Hotel> amenitiesIn(List<String> amenities) {
        if (amenities == null || amenities.isEmpty()) {
            return null;
        }
        List<String> lowered = amenities.stream()
                .filter(StringUtils::hasText)
                .map(a -> a.toLowerCase(Locale.ROOT))
                .toList();
        if (lowered.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> {
            query.distinct(true);
            var join = root.join("amenities");
            return cb.lower(join.get("name")).in(lowered);
        };
    }
}
