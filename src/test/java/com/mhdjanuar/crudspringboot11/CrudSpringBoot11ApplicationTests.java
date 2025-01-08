package com.mhdjanuar.crudspringboot11;

import com.mhdjanuar.crudspringboot11.dto.BookCreateRequestDTO;
import com.mhdjanuar.crudspringboot11.dto.bookUpdateRequestDTO;
import com.mhdjanuar.crudspringboot11.exception.ResourceNotFoundException;
import com.mhdjanuar.crudspringboot11.impl.BookServiceImpl;
import com.mhdjanuar.crudspringboot11.repository.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.mhdjanuar.crudspringboot11.domain.Book;

import java.util.Optional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CrudSpringBoot11ApplicationTests {

}
