package com.kyawgyi.api.service;

import com.kyawgyi.api.dto.MovieDto;
import com.kyawgyi.api.dto.MoviePageResponse;
import com.kyawgyi.api.exceptions.FileExitsException;
import com.kyawgyi.api.exceptions.MovieNotFoundException;
import com.kyawgyi.api.model.Movie;
import com.kyawgyi.api.repo.MovieRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private  final MovieRepo movieRepo;
    private final FileService fileService;

    public MovieServiceImpl(MovieRepo movieRepo, FileService fileService) {
        this.movieRepo = movieRepo;
        this.fileService = fileService;
    }

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {

//        upload the file

        if(Files.exists(Paths.get(path+ File.separator + file.getOriginalFilename()))){
            throw new FileExitsException("File already exists! Please another file.");
        }
        String uploadFile = fileService.uploadFile(path,file);

//        set the value of field "poster" as fileName
        movieDto.setPoster(uploadFile);

//        map dto to Movie Object
        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getYear(),
                movieDto.getPoster()
        );

//        save movie object -> saved movie object
       Movie savedMovie = movieRepo.save(movie);

//        generate poster url
        String posterUrl = baseUrl + "/file/" + uploadFile;

//        finally map movie object to Dto object and return it
        MovieDto resp = new MovieDto(
                savedMovie.getId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getYear(),
                savedMovie.getPoster(),
                posterUrl
        );

        return resp;
    }

    @Override
    public MovieDto getMovie(Long id) {

//        to check the data in DB and if exits, fetch data with id
       Movie movie = movieRepo.findById(id).orElseThrow(()->new MovieNotFoundException("Movie not found with id " + id));

//        generate poster url
        String posterUrl = baseUrl + "/file/" + movie.getPoster();

//        map to movie Dto object and return it
        MovieDto resp = new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getYear(),
                movie.getPoster(),
                posterUrl
        );
        return resp;
    }

    @Override
    public List<MovieDto> getAllMovies() {

//        to fetch all data from DB
        List<Movie> movies = movieRepo.findAll();

        List<MovieDto> moviesDto = new ArrayList<>();

//        iterate through the list, generate posterUrl for each movie obj
//        And map to Movie obj
        for(Movie movie : movies){
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto resp = new MovieDto(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getYear(),
                    movie.getPoster(),
                    posterUrl
            );
            moviesDto.add(resp);
        }

        return moviesDto;
    }

    @Override
    public MovieDto updateMovie(Long id, MovieDto movieDto, MultipartFile file) throws IOException {

//        check if exit movie by id
        Movie mv= movieRepo.findById(id).orElseThrow(()->new MovieNotFoundException("Movie not found with id " + id));

//        if file in null, do nothing
//        if file is not null, then delete existing file and upload new
        String fileName = mv.getPoster();
        if(file!=null){
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path,file);
        }

//        set movie Dto poster,
        movieDto.setPoster(fileName);

//        map it to movie object ,then save movie object -> return save movie
        Movie movie = new Movie(
                mv.getId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getYear(),
                movieDto.getPoster()
        );

        Movie updatedMovie = movieRepo.save(movie);

//        generate posterurl
        String posterUrl = baseUrl + "/file/" + fileName;

//        map movie dto and return it
        MovieDto resp = new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getYear(),
                movie.getPoster(),
                posterUrl
        );

        return resp;
    }

    @Override
    public String deleteMovie(Long id) throws IOException {

//        Check if exit
        Movie mv = movieRepo.findById(id).orElseThrow(()->new MovieNotFoundException("Movie not found with id " + id));

//        delete the file associated with the object
        Files.deleteIfExists(Paths.get(path + File.separator + mv.getPoster()));

//        delete movie object
        movieRepo.delete(mv);
        return "Movie deleted with id :" + id ;
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNo, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNo,pageSize);
        Page<Movie> moviePage = movieRepo.findAll(pageable);
        List<Movie> movies = moviePage.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();

        for(Movie movie : movies){
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }

        return new MoviePageResponse(movieDtos,pageNo,pageSize,
                moviePage.getTotalElements(),
                moviePage.getTotalPages(),
                moviePage.isLast());
    }

    @Override
    public MoviePageResponse getAllMoviesWithSorting(Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo,pageSize,sort);
        Page<Movie> moviePage = movieRepo.findAll(pageable);
        List<Movie> movies = moviePage.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();

        for(Movie movie : movies){
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }

        return new MoviePageResponse(movieDtos,pageNo,pageSize,
                moviePage.getTotalElements(),
                moviePage.getTotalPages(),
                moviePage.isLast());
    }
}
