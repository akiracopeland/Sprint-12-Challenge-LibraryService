package com.bloomtech.library.services;

import com.bloomtech.library.exceptions.CheckableNotFoundException;
import com.bloomtech.library.exceptions.LibraryNotFoundException;
import com.bloomtech.library.exceptions.ResourceExistsException;
import com.bloomtech.library.models.Library;
import com.bloomtech.library.models.checkableTypes.*;
import com.bloomtech.library.repositories.CheckableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@SpringBootTest
public class CheckableServiceTest {

    //TODO: Inject dependencies and mocks

    @Autowired
    CheckableService checkableService;

    @MockBean
    CheckableRepository checkableRepository;

    private List<Checkable> checkables;

    @BeforeEach
    void init() {
        //Initialize test data
        checkables = new ArrayList<>();

        checkables.addAll(
                Arrays.asList(
                        new Media("1-0", "The White Whale", "Melvin H", MediaType.BOOK),
                        new Media("1-1", "The Sorcerer's Quest", "Ana T", MediaType.BOOK),
                        new Media("1-2", "When You're Gone", "Complaining at the Disco", MediaType.MUSIC),
                        new Media("1-3", "Nature Around the World", "DocuSpecialists", MediaType.VIDEO),
                        new ScienceKit("2-0", "Anatomy Model"),
                        new ScienceKit("2-1", "Robotics Kit"),
                        new Ticket("3-0", "Science Museum Tickets"),
                        new Ticket("3-1", "National Park Day Pass")
                )
        );
    }

    @Test
    void getAll_returnsAllCheckables() {
        when(checkableRepository.findAll()).thenReturn(checkables);
        List<Checkable> actualCheckables = checkableService.getAll();
        assertEquals(checkables, actualCheckables);
    }

    @Test
    void getByIsbn_existingCheckable_returnsCorrectCheckable() {
        String isbn = "1-3";

        when(checkableRepository.findByIsbn(isbn)).thenReturn(Optional.ofNullable(checkables.get(3)));

        Checkable actualCheckable = checkableService.getByIsbn(isbn);

        assertEquals(checkables.get(3), actualCheckable);
    }

    @Test
    void getByIsbn_nonExistentIsbn_throwsCheckableNotFoundException() {
        String nonExistentIsbn = "doesntExist";

        when(checkableRepository.findByIsbn(nonExistentIsbn)).thenReturn(Optional.empty());

        assertThrows(CheckableNotFoundException.class, ()-> {
            checkableService.getByIsbn(nonExistentIsbn);
        });

    }

    @Test
    void getByType_existingType_returnsCorrectTypeCheckable() {
        when(checkableRepository.findByType(ScienceKit.class)).thenReturn(Optional.ofNullable(checkables.get(4)));

        Checkable actualCheckable = checkableService.getByType(ScienceKit.class);

        assertEquals(checkables.get(4), actualCheckable);
    }

    @Test
    void getByType_nonExistentType_throwsCheckableNotFoundException() {
        when(checkableRepository.findByType(Class.class)).thenReturn(Optional.empty());

        assertThrows(CheckableNotFoundException.class, ()-> {
            checkableService.getByType(Class.class);
        });

    }

    @Test
    void save_existingCheckable_throwsResourceExistsException() {

        Checkable existingCheckable = new Media("1-0", "The White Whale", "Melvin H", MediaType.BOOK);

        when(checkableRepository.findAll()).thenReturn(checkables);
        assertThrows(ResourceExistsException.class, ()->{
            checkableService.save(existingCheckable);
        });
    }

    @Test
    void save_newCheckable_savesCheckable() {

        Checkable newCheckable = new Media("9-9", "This does not exist", "Bobby Orogon", MediaType.BOOK);

        when(checkableRepository.findAll()).thenReturn(checkables);

        checkableService.save(newCheckable);

        Mockito.verify(checkableRepository).save(newCheckable);

    }
}