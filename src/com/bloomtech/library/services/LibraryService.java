package com.bloomtech.library.services;

import com.bloomtech.library.exceptions.CheckableNotFoundException;
import com.bloomtech.library.exceptions.LibraryNotFoundException;
import com.bloomtech.library.exceptions.ResourceExistsException;
import com.bloomtech.library.models.*;
import com.bloomtech.library.models.checkableTypes.Checkable;
import com.bloomtech.library.repositories.LibraryRepository;
import com.bloomtech.library.models.CheckableAmount;
import com.bloomtech.library.views.LibraryAvailableCheckouts;
import com.bloomtech.library.views.OverdueCheckout;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private CheckableService checkableService;

    @Autowired
    private LibraryCardService libraryCardService;

    public List<Library> getLibraries() {

        List<Library> libraries = libraryRepository.findAll();

        return libraries;
    }

    public Library getLibraryByName(String name) throws LibraryNotFoundException {

        Optional<Library> library = libraryRepository.findByName(name);

        if (library.isPresent()) {
            return library.get();
        } else {
            throw new LibraryNotFoundException("Library not found");
        }
    }

    public void save(Library library) {
        List<Library> libraries = libraryRepository.findAll();
        if (libraries.stream().filter(p->p.getName().equals(library.getName())).findFirst().isPresent()) {
            throw new ResourceExistsException("Library with name: " + library.getName() + " already exists!");
        }
        libraryRepository.save(library);
    }

    public CheckableAmount getCheckableAmount(String libraryName, String checkableIsbn) {

        Optional<Library> optionalLibrary = libraryRepository.findByName(libraryName);

        if (optionalLibrary.isEmpty()) {
            throw new LibraryNotFoundException("Library not found");
        }

        Library library = optionalLibrary.get();

        Checkable checkable = checkableService.getByIsbn(checkableIsbn);

        List<CheckableAmount> checkableAmounts = library.getCheckables();

        for (CheckableAmount checkableAmount : checkableAmounts) {
            if (checkableAmount.getCheckable().equals(checkable)) {
                return checkableAmount;
            }
        }

        return new CheckableAmount(checkable,0);

    }

    public List<LibraryAvailableCheckouts> getLibrariesWithAvailableCheckout(String isbn) {
        List<LibraryAvailableCheckouts> available = new ArrayList<>();

        List<Library> libraries = libraryRepository.findAll();

        Checkable checkable = checkableService.getByIsbn(isbn);

        for (Library library : libraries) {

            String libraryName = library.getName();
            List<CheckableAmount> checkableAmounts = library.getCheckables();

            for (CheckableAmount checkableAmount : checkableAmounts) {
                if (checkableAmount.getCheckable().equals(checkable)) {
                    available.add(new LibraryAvailableCheckouts(checkableAmount.getAmount(), libraryName));
                }
            }
        }
        return available;
    }

    public List<OverdueCheckout> getOverdueCheckouts(String libraryName) {
        List<OverdueCheckout> overdueCheckouts = new ArrayList<>();

        Optional<Library> optionalLibrary = libraryRepository.findByName(libraryName);

        if (optionalLibrary.isEmpty()) {
            throw new LibraryNotFoundException("Library not found");
        }

        List<LibraryCard> libraryCards = libraryCardService.getLibraryCardsByLibraryName(libraryName);

        for (LibraryCard libraryCard : libraryCards) {
            List<Checkout> checkouts = libraryCard.getCheckouts();

            for (Checkout checkout : checkouts) {

                if (checkout.getDueDate().isBefore(LocalDateTime.now())) {
                    overdueCheckouts.add(new OverdueCheckout(libraryCard.getPatron(), checkout));
                }
            }
        }
        return overdueCheckouts;
    }
}
