package es.fabio.service;

import es.fabio.service.dto.ProductCategoryDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link es.fabio.domain.ProductCategory}.
 */
public interface ProductCategoryService {

    /**
     * Save a productCategory.
     *
     * @param productCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    ProductCategoryDTO save(ProductCategoryDTO productCategoryDTO);

    /**
     * Get all the productCategories.
     *
     * @return the list of entities.
     */
    List<ProductCategoryDTO> findAll();


    /**
     * Get the "id" productCategory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductCategoryDTO> findOne(Long id);

    /**
     * Delete the "id" productCategory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the productCategory corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<ProductCategoryDTO> search(String query);
}
