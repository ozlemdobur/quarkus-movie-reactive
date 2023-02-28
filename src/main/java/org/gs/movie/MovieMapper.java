package org.gs.movie;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MovieMapper{
    static MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);

    MovieView movieEntityToMovieView(MovieEntity movieEntity);
    List<MovieView> movieEntityToMovieViews(List<MovieEntity> movieEntity);
    MovieEntity movieViewToMovieEntity(MovieView movieView);

}
