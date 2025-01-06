package dev.julian.Bookz.book;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

public record Book(
        @Id
        Integer id,
        @NotEmpty
        String title,
        @NotEmpty
        String description,
        @Version Integer version
){

}
