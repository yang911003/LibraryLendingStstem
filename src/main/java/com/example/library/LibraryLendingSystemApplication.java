package com.example.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class LibraryLendingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryLendingSystemApplication.class, args);
    }
}
