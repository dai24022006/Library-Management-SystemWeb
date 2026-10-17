package com.library.management.service;

import com.library.management.model.Book;
import com.library.management.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository){

        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks(){

        return bookRepository.findByDeletedFalse();
    }

    public Book getBookByID(Long id){

        return bookRepository.findByIdAndDeletedFalse(id).orElse(null);
    }

    public Book addBook(Book book){

        Optional<Book> oldBook = bookRepository.findByBookIdAndDeletedFalse(book.getBookId());

        if(oldBook.isPresent()){
            return null;
        }

        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book book){
        Book oldBook = bookRepository.findByIdAndDeletedFalse(id).orElse(null);

        if(oldBook == null){
            return null;
        }

        oldBook.setTitle(book.getTitle());
        oldBook.setAuthor(book.getAuthor());
        oldBook.setPublisher(book.getPublisher());
        oldBook.setPublicationYear(book.getPublicationYear());
        oldBook.setCategory(book.getCategory());
        oldBook.setPrice(book.getPrice());
        oldBook.setQuantity(book.getQuantity());
        oldBook.setStatus(book.getStatus());

        return bookRepository.save(oldBook);
    }

    public boolean deleteBook(Long id){
        Book book = bookRepository.findByIdAndDeletedFalse(id).orElse(null);

        if(book == null){
            return false;
        }

        book.setDeleted(true);
        bookRepository.save(book);

        return true;
    }
}