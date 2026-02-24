package com.example.propertyview.repository;

import com.example.propertyview.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {

    interface ValueCount {
        String getValue();

        Long getCount();
    }

    @Query("select h.brand as value, count(h) as count from Hotel h group by h.brand")
    List<ValueCount> countByBrand();

    @Query("select h.address.city as value, count(h) as count from Hotel h group by h.address.city")
    List<ValueCount> countByCity();

    @Query("select h.address.country as value, count(h) as count from Hotel h group by h.address.country")
    List<ValueCount> countByCountry();

    @Query("select a.name as value, count(distinct h.id) as count from Hotel h join h.amenities a group by a.name")
    List<ValueCount> countByAmenity();
}

