package org.gs.movie;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.groups.UniOnItem;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

import static javax.ws.rs.core.Response.Status.*;

@Path("/movies")
@Produces("application/json")
@Consumes("application/json")
public class MovieResource {

    private IMovieService movieService;

    @Inject
    public MovieResource(IMovieService movieService) {
        this.movieService = movieService;
    }

    @GET
    public Uni<Response> getAll() {
//        Uni<List<MovieView>> movieViews =movieService.listAll();
        return movieService.listAll().map(movie->Response.ok(movie).build());
    }

    @GET
    @Path("{id}")
    public Uni<Response> getById(@PathParam("id") Long id) {
        return movieService.findById(id)
                //.onItem().ifNull().failWith(new WebApplicationException(NOT_FOUND))
                .map(movieView -> Response.ok(movieView).build());
    }

    @GET
    @Path("title/{title}")
    public Uni<Response> getByTitle(@PathParam("title") String title){
        return movieService.findByTitle(title)
                .map(movieView -> Response.ok(movieView).build());
    }

    @GET
    @Path("country/{country}")
    public Uni<Response> getByCountry(@PathParam("country") String country){
        return movieService.findByCountry(country)
                .map(movieViews -> Response.ok(movieViews).build());

    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(Long id) {
        return  movieService.deleteById(id)
                .map(deleted -> deleted
                        ? Response.ok().status(NO_CONTENT).build()
                        : Response.ok().status(NOT_FOUND).build());
    }

    @POST
    public Uni<Response> save(MovieView movieView){
        return  movieService.save(movieView)
                .map(movieView1 -> Response.created(URI.create("/movies/"+movieView1.getId())).entity(movieView1).build());
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(Long id, MovieView movieView) {
        Uni<Response> response = movieService.update(id, movieView)
                .map(movieView1 -> Response.ok(movieView1).build());
        return response;
    }

}
