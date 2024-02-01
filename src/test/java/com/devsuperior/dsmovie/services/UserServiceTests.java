package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;
	@Mock
	private UserRepository userRepository;
	@Mock
	private CustomUserUtil userUtil;

	private UserEntity user;
	private UserDetailsProjection userDetails;
	private String existingUsername, nonExistingUsername;


	@BeforeEach
	void setUp() throws Exception{
		existingUsername = "Ana";
		nonExistingUsername = "Caio";

		user = UserFactory.createUserEntity();
	}

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {

		Mockito.when(userUtil.getLoggedUsername()).thenReturn(existingUsername);
		Mockito.when(userRepository.findByUsername(existingUsername)).thenReturn(Optional.of(user));

		UserEntity result = service.authenticated();

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result, user);
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {

		Mockito.when(userUtil.getLoggedUsername()).thenReturn(nonExistingUsername);
		Mockito.when(userRepository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.authenticated();
		});
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {

		List<UserDetailsProjection> expectedUserDetails = UserDetailsFactory.createCustomClientUser(existingUsername);
		Mockito.when(userRepository.searchUserAndRolesByUsername(existingUsername)).thenReturn(expectedUserDetails);

		UserDetails actualUserDetails = service.loadUserByUsername(existingUsername);

		Assertions.assertEquals(expectedUserDetails.get(0).getUsername(), actualUserDetails.getUsername());
		Assertions.assertEquals(expectedUserDetails.get(0).getPassword(), actualUserDetails.getPassword());
		Assertions.assertEquals(1, actualUserDetails.getAuthorities().size());

	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {

		Mockito.when(userRepository.searchUserAndRolesByUsername(nonExistingUsername)).thenReturn(Collections.emptyList());

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.loadUserByUsername(nonExistingUsername);
		});
	}
}
