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

import static org.junit.Assert.*;
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

    @Mock
    private UserDTO userDTO;

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
        UserDTO userDTO = new UserDTO("n","n","n",new Role("n","n"));
        User user = new User(createUserDTO.getName(), createUserDTO.getPassword(), createUserDTO.getEmail());

        when(userDao.getUserByEmail(anyString())).thenReturn(null);
        when(converter.createUserDTOtoUser(any())).thenReturn(user);
        when(securityService.hash(anyString())).thenReturn("hashedPassword");
        when(userDao.save(any())).thenReturn(user);
        when(converter.userToUserDTO(any())).thenReturn(userDTO);

        UserDTO result = userService.createUser(createUserDTO);

        assertEquals(userDTO,result);

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

    @Test
    public void finduserbyemailTest()
    {
        User us = new User("n","n","n");
        UserDTO userDTO = new UserDTO("n","n","n",new Role("n","n"));
        when(userDao.getUserByEmail(anyString())).thenReturn(us);
        when(converter.userToUserDTO(any())).thenReturn(userDTO);

        UserDTO userDTO1 = userService.findUserByEmail(userDTO.getEmail());

        assertEquals(userDTO,userDTO1);
    }

    @Test
    public void cuTestC()
    {
        userDao.getUserByEmail(anyString());
        converter.createUserDTOtoUser(any());

        verify(userDao,times(1)).getUserByEmail(anyString());
        verify(converter,times(1)).createUserDTOtoUser(any());
    }

    @Test
    public void ex() throws Exception {
        //when(securityService.hash(anyString())).thenThrow(Exception.class);
        doThrow(Exception.class).when(securityService).hash(anyString());


        Exception Ex = assertThrows(Exception.class, () -> {
            securityService.hash(anyString());
        });

        assertNotNull(Ex);

    }

    @Test
    public void getTest()
    {
        doThrow(RuntimeException.class).when(userDTO).getName();

        Exception ex = assertThrows(RuntimeException.class, ()-> {userDTO.getName();} );

        assertNotNull(ex);
    }


}

