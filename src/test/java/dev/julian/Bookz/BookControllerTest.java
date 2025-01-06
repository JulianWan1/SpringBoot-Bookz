package dev.julian.Bookz;

import dev.julian.Bookz.book.Book;
import dev.julian.Bookz.book.BookController;
import dev.julian.Bookz.book.BookRepository;
import dev.julian.Bookz.book.BookNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    // Create a mock MVC for testing the app as the real server will not be spun up
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BookRepository repository;

    List<Book> books = new ArrayList<>();

    // Create some initial posts for the test
    @BeforeEach
    void setUp() {
        books = List.of(
                new dev.julian.Bookz.book.Book(1, "Hello World Book", "A new book for beginners", null),
                new dev.julian.Bookz.book.Book(2, "Intermediate Book", "Next chapter of the Hello World Book", null)
        );
    }

    // Create the REST API tests
    @Test
    void shouldFindAllBooks() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id":1,
                        "title":"Hello World Book",
                        "description":"A new book for beginners",
                        "version": null
                    },
                    {
                        "id":2,
                        "title":"Intermediate Book",
                        "description":"Next chapter of the Hello World Book",
                        "version": null
                    }
                ]
                """;

        when(repository.findAll()).thenReturn(books);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void shouldFindPostWhenGivenValidId() throws Exception {
        when(repository.findById(1)).thenReturn(Optional.of(books.getFirst()));

        Book book = new Book(1, "Hello World Book", "A new book for beginners", null);
        String json = STR."""
                {
                    "id":\{book.id()},
                    "title":"\{book.title()}",
                    "description":"\{book.description()}",
                    "version": null
                }
                """;

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void shouldNotFindWhenGivenInvalidId() throws Exception {
        when(repository.findById(999)).thenThrow(BookNotFoundException.class);

        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewBookWhenGivenValidBook() throws Exception {
        Book book = new Book(3, "LARPing for dummies", "Learn to live your fantasies, in reality!", null);

        String json = STR."""
                {
                    "id":\{book.id()},
                    "title":"\{book.title()}",
                    "description":"\{book.description()}",
                    "version": null
                }
                """;

        when(repository.save(book)).thenReturn(book);

        mockMvc.perform(post("/api/books")
                .contentType("application/json")
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().json(json));
    }

    @Test
    void shouldNotCreateBookWhenBookIsInvalid() throws Exception {
        Book book = new Book(3, "", "", null);

        String json = STR."""
                {
                    "id":\{book.id()},
                    "title":"\{book.title()}",
                    "description":"\{book.description()}",
                    "version": null
                }
                """;

        when(repository.save(book)).thenReturn(book);

        mockMvc.perform(post("/api/books")
                .contentType("application/json")
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdatePostWhenGivenValidBook() throws Exception {
        Book updated = new Book(1,"Hello World V2","Updated version", null);

        when(repository.findById(1)).thenReturn(Optional.of(books.getFirst()));
        when(repository.save(updated)).thenReturn(updated);

        String requestBody = STR."""
                {
                    "id":\{updated.id()},
                    "title":"\{updated.title()}",
                    "description":"\{updated.description()}",
                    "version": \{updated.version()}
                }
                """;

        mockMvc.perform(put("/api/books/1")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk());

    }

    @Test
    void shouldDeleteBookWhenGivenValidId() throws Exception {
        doNothing().when(repository).deleteById(1);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());

        verify(repository, times(1)).deleteById(1);
    }
}
