package es.fabio.service.impl;

import es.fabio.service.ProductCategoryService;
import es.fabio.domain.ProductCategory;
import es.fabio.repository.ProductCategoryRepository;
import es.fabio.repository.search.ProductCategorySearchRepository;
import es.fabio.service.dto.ProductCategoryDTO;
import es.fabio.service.mapper.ProductCategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link ProductCategory}.
 */
@Service
@Transactional
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final Logger log = LoggerFactory.getLogger(ProductCategoryServiceImpl.class);

    private final ProductCategoryRepository productCategoryRepository;

    private final ProductCategoryMapper productCategoryMapper;

    private final ProductCategorySearchRepository productCategorySearchRepository;

    public ProductCategoryServiceImpl(ProductCategoryRepository productCategoryRepository, ProductCategoryMapper productCategoryMapper, ProductCategorySearchRepository productCategorySearchRepository) {
        this.productCategoryRepository = productCategoryRepository;
        this.productCategoryMapper = productCategoryMapper;
        this.productCategorySearchRepository = productCategorySearchRepository;
    }

    @Override
    public ProductCategoryDTO save(ProductCategoryDTO productCategoryDTO) {
        log.debug("Request to save ProductCategory : {}", productCategoryDTO);
        ProductCategory productCategory = productCategoryMapper.toEntity(productCategoryDTO);
        productCategory = productCategoryRepository.save(productCategory);
        ProductCategoryDTO result = productCategoryMapper.toDto(productCategory);
        productCategorySearchRepository.save(productCategory);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCategoryDTO> findAll() {
        log.debug("Request to get all ProductCategories");
        return productCategoryRepository.findAll().stream()
            .map(productCategoryMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<ProductCategoryDTO> findOne(Long id) {
        log.debug("Request to get ProductCategory : {}", id);
        return productCategoryRepository.findById(id)
            .map(productCategoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProductCategory : {}", id);
        productCategoryRepository.deleteById(id);
        productCategorySearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCategoryDTO> search(String query) {
        log.debug("Request to search ProductCategories for query {}", query);
        return StreamSupport
            .stream(productCategorySearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(productCategoryMapper::toDto)
        .collect(Collectors.toList());
    }
}
