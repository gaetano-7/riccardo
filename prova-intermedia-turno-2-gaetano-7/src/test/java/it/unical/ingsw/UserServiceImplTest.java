package it.unical.ingsw;

import it.unical.ingsw.dao.UserDao;
import it.unical.ingsw.dto.UserConverter;
import it.unical.ingsw.service.EmailService;
import it.unical.ingsw.service.SecurityService;
import it.unical.ingsw.service.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import it.unical.ingsw.dao.UserDao;
import it.unical.ingsw.dto.CreateUserDTO;
import it.unical.ingsw.dto.UserConverter;
import it.unical.ingsw.dto.UserDTO;
import it.unical.ingsw.entities.User;
import it.unical.ingsw.entities.Role;

import it.unical.ingsw.exceptions.UserAlreadyExistsException;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserDao userDao;

    @Mock
    private SecurityService securityService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserConverter converter;

    @InjectMocks
    private UserServiceImpl userService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /*@BeforeEach
    public void setUp() {
        System.out.println("Inizio singolo test");
        userService = new UserServiceImpl(userDao, securityService, emailService, converter);
    }

    @AfterEach
    public void setDown() {
        System.out.println("Fine singolo test");
    }
     */

    @Test
    public void testCreateUser() throws Exception {
        CreateUserDTO createUserDTO = new CreateUserDTO("John Doe", "password", "john@example.com");

        User user = new User(createUserDTO.getName(), createUserDTO.getPassword(), createUserDTO.getEmail());

        when(userDao.getUserByEmail(createUserDTO.getEmail())).thenReturn(null);
        when(converter.createUserDTOtoUser(createUserDTO)).thenReturn(user);
        when(securityService.hash(createUserDTO.getPassword())).thenReturn("hashedPassword");
        when(userDao.save(any(User.class))).thenReturn(user);

        userService.setShouldVerifyEmail(true);

        UserDTO result = userService.createUser(createUserDTO);

        verify(userDao, times(1)).getUserByEmail(createUserDTO.getEmail());
        verify(converter, times(1)).createUserDTOtoUser(createUserDTO);
        verify(securityService, times(1)).hash(createUserDTO.getPassword());
        verify(userDao, times(1)).save(user);
        verify(emailService, never()).sendEmailVerificationEmail(anyString());

        assertNotNull(result);
        assertEquals(createUserDTO.getEmail(), result.getEmail());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getRole(), result.getRole());
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void testCreateUser_UserAlreadyExists() throws Exception {
        CreateUserDTO createUserDTO = new CreateUserDTO("John Doe", "password", "john@example.com");
        User existingUser = new User(createUserDTO.getName(), createUserDTO.getPassword(), createUserDTO.getEmail());

        when(userDao.getUserByEmail(createUserDTO.getEmail())).thenReturn(existingUser);

        userService.createUser(createUserDTO);

    }

    @Test
    public void testFindUserByEmail() {
        // Dati di mock
        String userEmail = "john@example.com";
        User user = new User("John Doe", "password", userEmail);
        UserDTO userDTO = new UserDTO("1", "John Doe", userEmail, null);

        when(userDao.getUserByEmail(userEmail)).thenReturn(user);

        UserDTO result = userService.findUserByEmail(userEmail);

        verify(userDao, times(1)).getUserByEmail(userEmail);
        verify(converter, times(1)).userToUserDTO(user);

        assertNotNull(result);
        assertEquals(userEmail, result.getEmail());
        assertEquals("John Doe", result.getName());
    }

}
