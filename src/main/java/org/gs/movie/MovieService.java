package org.gs.movie;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.groups.UniOnItem;
import org.hibernate.annotations.NotFound;
import org.hibernate.cache.NoCacheRegionFactoryAvailableException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.naming.NotContextException;
import javax.persistence.NoResultException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import java.util.List;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.NOT_FOUND;

@ApplicationScoped
public class MovieService implements IMovieService{
    private MovieRepository movieRepository;

    private MovieMapper movieMapper = MovieMapper.INSTANCE;

    @Inject
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Uni<List<MovieView>> listAll() {
        return movieRepository.listAll().map(p -> movieMapper.movieEntityToMovieViews(p));
    }

   public Uni<MovieView> findById(Long id){
        return movieRepository.findById(id)
                .onItem().ifNull().failWith( new WebApplicationException("Not Found", 404))
                .onItem().transform(movieEntity -> movieMapper.movieEntityToMovieView(movieEntity));
                //.onFailure(NoResultException.class).transform(throwable -> new WebApplicationException("Not Found", 404));

      /* return movieRepository.find("id",id).singleResult()
               .onItem().transform(movieEntity -> movieMapper.movieEntityToMovieView(movieEntity))
               .onFailure(NoResultException.class).transform(throwable -> new WebApplicationException("Not Found", 404));*/
   }



    public Uni<MovieView> findByTitle(String title){

        return movieRepository.find("title", title).singleResult()
                .onItem().transform(movieEntity -> movieMapper.movieEntityToMovieView(movieEntity))
                .onFailure(NoResultException.class).transform(throwable -> new WebApplicationException("Not Found", 404));

    }

   public Uni<List<MovieView>> findByCountry(String country){
        return movieRepository.findByCountry(country)
                //.onItem().ifNull().failWith(new WebApplicationException("404", 404))
                //.invoke(entity->{(entity.isEmpty())?new WebApplicationException("Not found", 404)})
                //.onFailure(NoResultException.class).transform(throwable -> new WebApplicationException("Not Found", 404))
                .onItem().transform(movieEntity -> movieMapper.movieEntityToMovieViews(movieEntity));

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
        if (movieView == null || movieView.getId() == null) {
            throw new WebApplicationException("Not Found", 404);
        }
        if(id!= movieView.getId()) {
            throw new WebApplicationException("Conflict", 409);
        }


        return movieRepository.findById(id)
                //onFailure(NoResultException.class).transform(throwable -> new WebApplicationException("Not Found", 404) )
                .onItem()
                .invoke(movieEntity1 -> movieEntity1.setTitle(movieView.getTitle()))
                .invoke(movieEntity1 -> movieEntity1.setCountry(movieView.getCountry()))
                .invoke(movieEntity1 -> movieEntity1.setDescription(movieView.getDescription()))
                .invoke(movieEntity1 -> movieRepository.persist(movieEntity1))
                .map(movieEntity->movieMapper.movieEntityToMovieView(movieEntity));

    }

}
