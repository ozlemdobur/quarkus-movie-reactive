package org.gs.movie;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.groups.UniOnItem;
import org.hibernate.annotations.NotFound;
import org.hibernate.cache.NoCacheRegionFactoryAvailableException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.util.List;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

@ApplicationScoped
public class MovieService {

    @Inject
    private MovieRepository movieRepository;

    private MovieMapper movieMapper = MovieMapper.INSTANCE;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Uni<List<MovieView>> listAll() {
        return movieRepository.listAll().map(p -> movieMapper.movieEntityToMovieViews(p));
    }

   public Uni<MovieView> findById(Long id){
        return movieRepository.findById(id)
                .onItem().ifNull().failWith(new WebApplicationException("Not Found", 404))
                .map(movieMapper::movieEntityToMovieView);
   }

/*   public Uni<MovieView> findByTitle(String title){
        return movieRepository.find("title", title).singleResult().onFailure(NR.class)
                .onItem().ifNull().failWith(new WebApplicationException("Not Found", 404))
                .map(p->movieMapper.movieEntityToMovieView(p));
   }*/

   public Uni<List<MovieView>> findByCountry(String country){
        return movieRepository.findByCountry(country)
                .onItem().ifNull().failWith(new WebApplicationException("Not Content", 204))
                .map(p->movieMapper.movieEntityToMovieViews(p));

   }
    @ReactiveTransactional
    public Uni<Boolean> deleteById(Long id) {
        return movieRepository.deleteById(id);
    }
    @ReactiveTransactional
    public Uni<MovieView> save(MovieView movieView) {
        MovieEntity movieEntity= movieMapper.movieViewToMovieEntity(movieView);
        Uni<MovieEntity> uniMovieEntity = movieRepository.persist(movieEntity);
        return uniMovieEntity.map(p->movieMapper.movieEntityToMovieView(p));
    }

    @ReactiveTransactional
    public Uni<MovieView> update(Long id, MovieView movieView) {
        if (id == null) {
            throw new WebApplicationException("Bad Request.Id should be notnull", 400);
        }
        if(id!= movieView.getId()) {
            throw new WebApplicationException("Conflict", 409);
        }
/*
        UniOnItem<MovieEntity> movieViewUniOnItem = movieRepository.findById(id).onItem();
        movieViewUniOnItem.ifNull().failWith(new WebApplicationException("Id is not found", 400));*/

        Uni<MovieEntity> entity = movieRepository.findById(id);
        return entity.onItem().ifNotNull()
                .invoke(movieEntity1 -> movieEntity1.setTitle(movieView.getTitle()))
                .invoke(movieEntity1 -> movieEntity1.setCountry(movieView.getCountry()))
                .invoke(movieEntity1 -> movieEntity1.setDescription(movieView.getDescription()))
                .invoke(movieEntity1 -> movieRepository.persist(movieEntity1))
                .map(newMovieEntity-> movieMapper.movieEntityToMovieView(newMovieEntity));

    }

}
