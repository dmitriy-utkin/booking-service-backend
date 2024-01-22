package ru.example.booking.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.example.booking.dao.Hotel;
import ru.example.booking.dto.defaults.HotelFilter;

public interface HotelSpecification {

    static Specification<Hotel> withFilter(HotelFilter filter) {
        return Specification.where(byHotelId(filter.getId()))
                .and(byHotelName(filter.getName()))
                .and(byHotelHeadline(filter.getHeadline()))
                .and(byHotelCity(filter.getCity()))
                .and(byHotelAddress(filter.getAddress()))
                .and(byDistanceHotelToCentre(filter.getDistance()))
                .and(byHotelRating(filter.getRating()))
                .and(byHotelNumberOfRatings(filter.getNumberOfRatings()));
    }

    static Specification<Hotel> byHotelNumberOfRatings(Integer numberOfRatings) {
        return (root, query, cb) -> {
            if (numberOfRatings == null) {
                return null;
            }
            return cb.greaterThanOrEqualTo(root.get(Hotel.Fields.numberOfRatings), numberOfRatings);
        };
    }

    static Specification<Hotel> byHotelRating(Float rating) {
        return (root, query, cb) -> {
            if (rating == null) {
                return null;
            }
            return cb.greaterThanOrEqualTo(root.get(Hotel.Fields.rating), rating);
        };
    }

    static Specification<Hotel> byDistanceHotelToCentre(Float distance) {
        return (root, query, cb) -> {
            if (distance == null) {
                return null;
            }
            return cb.lessThanOrEqualTo(root.get(Hotel.Fields.distance), distance);
        };
    }

    static Specification<Hotel> byHotelAddress(String address) {
        return (root, query, cb) -> {
            if (address == null) {
                return null;
            }
            return cb.equal(root.get(Hotel.Fields.address), address);
        };
    }

    static Specification<Hotel> byHotelCity(String city) {
        return (root, query, cb) -> {
            if (city == null) {
                return null;
            }
            return cb.equal(root.get(Hotel.Fields.city), city);
        };
    }

    static Specification<Hotel> byHotelHeadline(String headline) {
        return (root, query, cb) -> {
            if (headline == null) {
                return null;
            }
            return cb.equal(root.get(Hotel.Fields.headline), headline);
        };
    }

    static Specification<Hotel> byHotelName(String name) {
        return (root, query, cb) -> {
            if (name == null) {
                return null;
            }
            return cb.equal(root.get(Hotel.Fields.name), name);
        };
    }

    static Specification<Hotel> byHotelId(Long id) {
        return (root, query, cb) -> {
            if (id == null) {
                return null;
            }
            return cb.equal(root.get(Hotel.Fields.id), id);
        };
    }




}
