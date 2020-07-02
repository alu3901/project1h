package es.fabio.service.impl;

import es.fabio.service.ProductOrderService;
import es.fabio.domain.ProductOrder;
import es.fabio.repository.ProductOrderRepository;
import es.fabio.repository.search.ProductOrderSearchRepository;
import es.fabio.service.dto.ProductOrderDTO;
import es.fabio.service.mapper.ProductOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link ProductOrder}.
 */
@Service
@Transactional
public class ProductOrderServiceImpl implements ProductOrderService {

    private final Logger log = LoggerFactory.getLogger(ProductOrderServiceImpl.class);

    private final ProductOrderRepository productOrderRepository;

    private final ProductOrderMapper productOrderMapper;

    private final ProductOrderSearchRepository productOrderSearchRepository;

    public ProductOrderServiceImpl(ProductOrderRepository productOrderRepository, ProductOrderMapper productOrderMapper, ProductOrderSearchRepository productOrderSearchRepository) {
        this.productOrderRepository = productOrderRepository;
        this.productOrderMapper = productOrderMapper;
        this.productOrderSearchRepository = productOrderSearchRepository;
    }

    @Override
    public ProductOrderDTO save(ProductOrderDTO productOrderDTO) {
        log.debug("Request to save ProductOrder : {}", productOrderDTO);
        ProductOrder productOrder = productOrderMapper.toEntity(productOrderDTO);
        productOrder = productOrderRepository.save(productOrder);
        ProductOrderDTO result = productOrderMapper.toDto(productOrder);
        productOrderSearchRepository.save(productOrder);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductOrderDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductOrders");
        return productOrderRepository.findAll(pageable)
            .map(productOrderMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<ProductOrderDTO> findOne(Long id) {
        log.debug("Request to get ProductOrder : {}", id);
        return productOrderRepository.findById(id)
            .map(productOrderMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProductOrder : {}", id);
        productOrderRepository.deleteById(id);
        productOrderSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductOrderDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProductOrders for query {}", query);
        return productOrderSearchRepository.search(queryStringQuery(query), pageable)
            .map(productOrderMapper::toDto);
    }
}
