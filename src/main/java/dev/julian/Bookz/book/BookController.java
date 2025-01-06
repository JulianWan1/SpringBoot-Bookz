package dev.julian.Bookz.book;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository repository;

    BookController(BookRepository repository) {
        this.repository = repository;
    }

    @GetMapping("")
    List<Book> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    Optional<Book> findById(@PathVariable Integer id) {
        return Optional.ofNullable(repository.findById(id)
                .orElseThrow(BookNotFoundException::new));
    }
    
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    Book save(@RequestBody @Valid Book book) {
        return repository.save(book);
    }

    @PutMapping("/{id}")
    Book update(@PathVariable Integer id, @RequestBody @Valid Book book) {
        Optional<Book> existing = repository.findById(id);

        if (existing.isPresent()) {
           Book updatedBook = new Book(existing.get().id(),
                   book.title(),
                   book.description(),
                   existing.get().version());

           return repository.save(updatedBook);
        } else {
            throw new BookNotFoundException();
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable Integer id) {
        repository.deleteById(id);
    }
}
