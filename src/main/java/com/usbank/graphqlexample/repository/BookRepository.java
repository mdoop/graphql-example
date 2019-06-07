package com.usbank.graphqlexample.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.usbank.graphqlexample.model.Book;

public interface BookRepository extends JpaRepository<Book, String> {

}
