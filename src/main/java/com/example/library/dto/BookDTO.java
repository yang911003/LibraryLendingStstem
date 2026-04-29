package com.example.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private String isbn;
    private String name;
    private String author;
    private String introduction;
    private String publisher;
    private LocalDate publishDate;
    private String category;
    private Integer availableCount;
}
