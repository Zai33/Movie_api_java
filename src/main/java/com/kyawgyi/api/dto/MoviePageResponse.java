package com.kyawgyi.api.dto;

import com.kyawgyi.api.model.Movie;

import java.util.List;

public record MoviePageResponse(List<MovieDto> movieDtos,
                                Integer pageNo,
                                Integer pageSize,
                                long totalElements,
                                int totalPages,
                                boolean isLast) {

}
