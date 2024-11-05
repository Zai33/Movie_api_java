package com.kyawgyi.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Movie  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 200)
    @NotBlank(message = "Please provide movie title!")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie director!")
    private String director;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie studio!")
    private String studio;

    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> movieCast;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie poster!")
    private String poster;
}
