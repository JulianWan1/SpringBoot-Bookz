package dev.julian.Bookz;

import dev.julian.Bookz.book.Book;
import dev.julian.Bookz.book.BookController;
import dev.julian.Bookz.book.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.StringTemplate.STR;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
