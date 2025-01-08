package com.mhdjanuar.crudspringboot11.service;

import com.mhdjanuar.crudspringboot11.domain.Book;
import com.mhdjanuar.crudspringboot11.dto.BookCreateRequestDTO;
import com.mhdjanuar.crudspringboot11.dto.BookDetailResponseDTO;
import com.mhdjanuar.crudspringboot11.dto.BookListResponseDTO;
import com.mhdjanuar.crudspringboot11.dto.bookUpdateRequestDTO;
import com.mhdjanuar.crudspringboot11.exception.ResourceNotFoundException;
import com.mhdjanuar.crudspringboot11.impl.BookServiceImpl;
import com.mhdjanuar.crudspringboot11.repository.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BookServiceTests {
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inisialisasi mock

        bookService = new BookServiceImpl();
        // Inject mock bookRepository ke dalam bookService
        ReflectionTestUtils.setField(bookService, "bookRepository", bookRepository);
    }

    @Test
    void testCreateNewBook() {
        // Mock Data
        BookCreateRequestDTO dto = new BookCreateRequestDTO();
        dto.setAuthor("John Doe");
        dto.setTitle("Spring Boot in Action");
        dto.setDescription("A comprehensive guide to Spring Boot");

        // Panggil metode yang diuji
        bookService.createNewBook(dto);

        // Verifikasi bahwa metode save di repository dipanggil dengan entity yang sesuai
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookCaptor.capture());

        Book savedBook = bookCaptor.getValue();
        assertEquals("John Doe", savedBook.getAuthor());
        assertEquals("Spring Boot in Action", savedBook.getTitle());
        assertEquals("A comprehensive guide to Spring Boot", savedBook.getDescription());
    }

    @Test
    void updateBook_success() {
        // Arrange
        Long bookId = 1L;
        bookUpdateRequestDTO dto = new bookUpdateRequestDTO();
        dto.setAuthor("Updated Author");
        dto.setTitle("Updated Title");
        dto.setDescription("Updated Description");

        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setAuthor("Old Author");
        existingBook.setTitle("Old Title");
        existingBook.setDescription("Old Description");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

        // Act
        bookService.updateBook(bookId, dto);

        // Assert
        verify(bookRepository).findById(bookId);
        verify(bookRepository).save(Mockito.argThat(book ->
                book.getAuthor().equals("Updated Author") &&
                        book.getTitle().equals("Updated Title") &&
                        book.getDescription().equals("Updated Description")
        ));
    }

    @Test
    void updateBook_notFound() {
        // Arrange
        Long bookId = 1L;
        bookUpdateRequestDTO dto = new bookUpdateRequestDTO();
        dto.setAuthor("Updated Author");
        dto.setTitle("Updated Title");
        dto.setDescription("Updated Description");

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(bookId, dto));
        verify(bookRepository).findById(bookId);
        verify(bookRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void testFindBookAll_WithTitle() {
        // Mock data
        String searchTitle = "%Java%";
        List<Book> mockBooks = Arrays.asList(
                new Book(1L, "Java Basics", "Author A", "Introduction to Java"),
                new Book(2L, "Advanced Java", "Author B", "Deep dive into Java")
        );

        // Atur perilaku repository mock
        when(bookRepository.findAllByTitleLike(searchTitle)).thenReturn(mockBooks);

        // Panggil metode layanan
        List<BookListResponseDTO> result = bookService.findBookAll("Java");

        // Verifikasi hasil
        assertEquals(2, result.size());
        assertEquals("Java Basics", result.get(0).getTitle());
        assertEquals("Author A", result.get(0).getAuthor());
        assertEquals("Advanced Java", result.get(1).getTitle());
        assertEquals("Author B", result.get(1).getAuthor());

        // Verifikasi bahwa repository dipanggil dengan argumen yang benar
        verify(bookRepository).findAllByTitleLike(searchTitle);
    }

    @Test
    void testFindBookAll_WithoutTitle() {
        // Mock data
        String searchTitle = "%";
        List<Book> mockBooks = Arrays.asList(
                new Book(1L, "Book A", "Author A", "Description A"),
                new Book(2L, "Book B", "Author B", "Description B")
        );

        // Atur perilaku repository mock
        when(bookRepository.findAllByTitleLike(searchTitle)).thenReturn(mockBooks);

        // Panggil metode layanan
        List<BookListResponseDTO> result = bookService.findBookAll("");

        // Verifikasi hasil
        assertEquals(2, result.size());
        assertEquals("Book A", result.get(0).getTitle());
        assertEquals("Book B", result.get(1).getTitle());

        // Verifikasi bahwa repository dipanggil dengan argumen yang benar
        verify(bookRepository).findAllByTitleLike(searchTitle);
    }

    @Test
    void testFindBookDetail_BookExists() {
        // Mock data
        Long bookId = 1L;
        Book mockBook = new Book(bookId, "Java Basics", "Author A", "Introduction to Java");

        // Atur perilaku repository mock
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));

        // Panggil metode layanan
        BookDetailResponseDTO result = bookService.findBookDetail(bookId);

        // Verifikasi hasil
        assertNotNull(result);
        assertEquals("Java Basics", result.getTitle());
        assertEquals("Author A", result.getAuthor());
        assertEquals("Introduction to Java", result.getDescription());

        // Verifikasi bahwa repository dipanggil dengan argumen yang benar
        verify(bookRepository).findById(bookId);
    }

    @Test
    void testFindBookDetail_BookNotFound() {
        // Mock data
        Long bookId = 1L;

        // Atur perilaku repository mock
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Panggil metode layanan dan tangkap exception
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookService.findBookDetail(bookId);
        });

        // Verifikasi pesan exception
        assertEquals("book.not.found", exception.getMessage());

        // Verifikasi bahwa repository dipanggil dengan argumen yang benar
        verify(bookRepository).findById(bookId);
    }

    @Test
    void testDeleteBook_BookExists() {
        // Mock data
        Long bookId = 1L;

        // Panggil metode deleteBook
        bookService.deleteBook(bookId);

        // Verifikasi bahwa repository dipanggil dengan argumen yang benar
        verify(bookRepository).deleteById(bookId);
    }
}
