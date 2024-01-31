package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService service;
	@Mock
	private MovieRepository repository;

	private String movieName;
	private Long existingID, nonExistingId;
	private PageImpl<MovieEntity> page;
	private MovieEntity movie;
	private MovieDTO movieDTO;

	@BeforeEach
	void setUp() throws Exception{
		movieName = "Test Movie";
		existingID = 1L;
		nonExistingId = 2L;

		movie = MovieFactory.createMovieEntity(movieName);
		movieDTO = new MovieDTO(movie);
		page = new PageImpl<>(List.of(movie));

		Mockito.when(repository.searchByTitle(any(), (Pageable) any())).thenReturn(page);

		Mockito.when(repository.findById(existingID)).thenReturn(Optional.of(movie));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
	}

	@Test
	public void findAllShouldReturnPagedMovieDTO() {

		Pageable pageable = PageRequest.of(0, 12);
		String name = "Thor";

		Page<MovieDTO> result = service.findAll(name, pageable);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getSize(), 1);
		Assertions.assertEquals(result.iterator().next().getTitle(), movieName);
	}
	
	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {

		MovieDTO result = service.findById(existingID);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingID);
		Assertions.assertEquals(result.getTitle(), movieName);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			MovieDTO result = service.findById(nonExistingId);
		});
	}
	
	@Test
	public void insertShouldReturnMovieDTO() {
	}
	
	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
	}
}
