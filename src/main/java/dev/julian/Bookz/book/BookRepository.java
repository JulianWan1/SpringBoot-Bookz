package dev.julian.Bookz.book;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

public interface BookRepository extends ListCrudRepository<Book,Integer> {
}
