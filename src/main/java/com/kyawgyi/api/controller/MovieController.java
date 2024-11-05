package com.kyawgyi.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyawgyi.api.dto.MovieDto;
import com.kyawgyi.api.dto.MoviePageResponse;
import com.kyawgyi.api.exceptions.EmptyFileException;
import com.kyawgyi.api.service.MovieService;
import com.kyawgyi.api.utils.AppConstant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
public class    MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("/add-movie")
    public ResponseEntity<MovieDto> addMovieHandler(@RequestPart MultipartFile file,
                                                    @RequestPart String movieDto) throws IOException {
        MovieDto dto = convertToMovieDto(movieDto);
        return new ResponseEntity<>(movieService.addMovie(dto,file), HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieHandler(@PathVariable long id) {
        return new ResponseEntity<>(movieService.getMovie(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieDto>> getAllMovieHandler() {
        return new ResponseEntity<>(movieService.getAllMovies(), HttpStatus.OK);
    };

    @PutMapping("/update/{id}")
    public ResponseEntity<MovieDto> updateMovieHandler(@PathVariable long id,
                                                       @RequestPart MultipartFile file,
                                                       @RequestPart String movieDtoObj) throws IOException, EmptyFileException {
        if(file.isEmpty()){
            throw new EmptyFileException("File is empty! Please send another fiel!");
        }
        MovieDto movieDto = convertToMovieDto(movieDtoObj);
        return ResponseEntity.ok(movieService.updateMovie(id, movieDto, file));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable long id) throws IOException {
        return ResponseEntity.ok(movieService.deleteMovie(id));
    }

    @GetMapping("/allMoviePage")
    public ResponseEntity<MoviePageResponse> getMoviesWithPagination(
            @RequestParam(defaultValue = AppConstant.PAGE_NUMBER , required = false) Integer page,
            @RequestParam(defaultValue = AppConstant.PAGE_SIZE , required = false) Integer pageSize
            ){
        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(page,pageSize));
    }

    @GetMapping("/allMoviePageSort")
    public ResponseEntity<MoviePageResponse> getMoviesWithPaginationAndSort(
            @RequestParam(defaultValue = AppConstant.PAGE_NUMBER , required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstant.PAGE_SIZE , required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstant.SORT_BY , required = false) String sortBy,
            @RequestParam(defaultValue = AppConstant.SORT_ORDER , required = false) String sortOrder
            ){
        return ResponseEntity.ok(movieService.getAllMoviesWithSorting(pageNumber,pageSize,sortBy,sortOrder));
    }

    private MovieDto convertToMovieDto(String movieDtoObject) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
       return objectMapper.readValue(movieDtoObject,MovieDto.class);
    }

}
