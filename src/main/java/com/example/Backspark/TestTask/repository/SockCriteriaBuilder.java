package com.example.Backspark.TestTask.repository;

import com.example.Backspark.TestTask.controller.payload.FilterSocksPayload;
import com.example.Backspark.TestTask.entity.SockEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class SockCriteriaBuilder {

    public Specification<SockEntity> build(FilterSocksPayload payload) {
        return withSockColor(payload.sockColor())
                .and(withCotton(payload.operation(), payload.cotton()))
                .and(withBetween(payload.betweenFrom(), payload.betweenTo()))
                .and(withSort(payload.sortField(), payload.sortType()));
    }

    private Specification<SockEntity> withSort(String field, String direction) {
        return (root, query, cb) -> {
            if (field == null) {
                return cb.conjunction();
            } else if (direction != null) {
                if (direction.equals("asc")) {
                    query.orderBy(cb.asc(root.get(field)));
                    return cb.conjunction();
                } else if (direction.equals("desc")) {
                    query.orderBy(cb.desc(root.get(field)));
                    return cb.conjunction();
                }
            }
            return cb.conjunction();
        };
    }

    private Specification<SockEntity> withSockColor(String sockColor) {
        return (root, query, cb) -> sockColor == null ? cb.conjunction() :
                cb.equal(root.get("sockColor"), sockColor);
    }

    private Specification<SockEntity> withCotton(String operation, Double cotton) {
        if (operation != null) {
            if (operation.equals("moreThan")) {
                return (root, query, cb) -> cotton == null ? cb.conjunction() :
                        cb.greaterThan(root.get("cotton"), cotton);
            } else if (operation.equals("lessThan")) {
                return (root, query, cb) -> cotton == null ? cb.conjunction() :
                        cb.lessThan(root.get("cotton"), cotton);
            }
        }
        return (root, query, cb) -> cotton == null ? cb.conjunction() :
                cb.equal(root.get("cotton"), cotton);
    }

    private Specification<SockEntity> withBetween(Double betweenFrom, Double betweenTo) {
        if (betweenFrom == null && betweenTo != null) {
            return (root, query, cb) -> cb.between(root.get("cotton"), 0d, betweenTo);
        } else if (betweenFrom != null && betweenTo == null) {
            return (root, query, cb) -> cb.between(root.get("cotton"), betweenFrom, 100d);
        } else if (betweenFrom == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.between(root.get("cotton"), betweenFrom, betweenTo);
    }
}
