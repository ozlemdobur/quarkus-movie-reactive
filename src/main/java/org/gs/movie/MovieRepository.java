package org.gs.movie;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class MovieRepository implements PanacheRepository<MovieEntity> {
    public Uni<List<MovieEntity>> findByCountry(String country) {
        return list("SELECT m FROM MovieEntity m WHERE m.country = ?1 ORDER BY id DESC", country );
    }
}
