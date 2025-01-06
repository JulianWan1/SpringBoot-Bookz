package dev.julian.Bookz.book;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
class BookDataLoader implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(BookDataLoader.class);
    private final ObjectMapper objectMapper;
    private final BookRepository bookRepository;

    public BookDataLoader(ObjectMapper objectMapper, BookRepository bookRepository) {
        this.objectMapper = objectMapper;
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if(bookRepository.count() == 0){
            String BOOKS_JSON = "/data/books.json";
            log.info("Loading posts into database from JSON: {}", BOOKS_JSON);
            try (InputStream inputStream = TypeReference.class.getResourceAsStream(BOOKS_JSON)) {
                Books response = objectMapper.readValue(inputStream, Books.class);
                bookRepository.saveAll(response.books());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read JSON data", e);
            }
        }
    }
}
