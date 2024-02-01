package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;
	@Mock
	private UserService userService;
	@Mock
	private MovieRepository movieRepository;
	@Mock
	private ScoreRepository scoreRepository;

	private Long nonExistingMovieId;
	private MovieEntity movie;
	private UserEntity user;
	private ScoreEntity score;
	private ScoreDTO scoreDTO;

	@BeforeEach
	void setUp() throws Exception {
		nonExistingMovieId = 2L;

		user = UserFactory.createUserEntity();
		movie = MovieFactory.createMovieEntity();
		score = ScoreFactory.createScoreEntity();
		scoreDTO = new ScoreDTO(score);
	}

	@Test
	public void saveScoreShouldReturnMovieDTO() {

		Mockito.when(userService.authenticated()).thenReturn(user);

		Mockito.when(movieRepository.findById(scoreDTO.getMovieId())).thenReturn(Optional.of(movie));

		Mockito.when(scoreRepository.saveAndFlush(any())).thenReturn(score);

		Mockito.when(movieRepository.save(any())).thenReturn(movie);

		MovieDTO result = service.saveScore(scoreDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(movie.getId(), result.getId());
		Assertions.assertEquals(movie.getTitle(), result.getTitle());
		Assertions.assertEquals(movie.getScore(), result.getScore());
		Assertions.assertEquals(movie.getCount(), result.getCount());
		Assertions.assertEquals(movie.getImage(), result.getImage());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {

		Mockito.when(userService.authenticated()).thenReturn(user);
		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(scoreDTO);
		});
	}
}
