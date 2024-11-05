package com.kyawgyi.api.service;

import com.kyawgyi.api.dto.MovieDto;
import com.kyawgyi.api.dto.MoviePageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {

    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;

    MovieDto getMovie(Long id);

    List<MovieDto> getAllMovies();

    MovieDto updateMovie(Long id, MovieDto movieDto,MultipartFile file) throws IOException;

    String deleteMovie(Long id) throws IOException;

    MoviePageResponse getAllMoviesWithPagination(Integer pageNo, Integer pageSize);

    MoviePageResponse getAllMoviesWithSorting(Integer pageNo,Integer pageSize,String sortBy,String sortOrder);
}
