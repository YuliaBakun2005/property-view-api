package com.example.propertyview.repository;

import com.example.propertyview.entity.Hotel;
import com.example.propertyview.repository.projection.ValueCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {

    @Query("select h.brand as value, count(h) as count from Hotel h group by h.brand")
    List<ValueCountProjection> countByBrand();

    @Query("select h.address.city as value, count(h) as count from Hotel h group by h.address.city")
    List<ValueCountProjection> countByCity();

    @Query("select h.address.country as value, count(h) as count from Hotel h group by h.address.country")
    List<ValueCountProjection> countByCountry();

    @Query("select a.name as value, count(distinct h.id) as count from Hotel h join h.amenities a group by a.name")
    List<ValueCountProjection> countByAmenity();
}

