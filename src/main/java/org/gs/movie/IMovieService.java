package org.gs.movie;

import io.smallrye.mutiny.Uni;

import java.util.List;

public interface IMovieService {
    Uni<List<MovieView>> listAll();
    Uni<MovieView> findById(Long id);
    Uni<MovieView> findByTitle(String title);
    Uni<List<MovieView>> findByCountry(String country);
    Uni<Boolean> deleteById(Long id);
    Uni<MovieView> save(MovieView movieView);
    Uni<MovieView> update(Long id, MovieView movieView);
}
