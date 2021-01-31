package com.example.hibernatenlp.db;

import com.example.hibernatenlp.ProductEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Petar Tahchiev
 * @since 2.2.2
 */
@Repository
public interface ProductRepository extends PagingAndSortingRepository<ProductEntity, Long> {
}
