package es.fabio.repository.search;

import es.fabio.domain.Producto;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Producto} entity.
 */
public interface ProductoSearchRepository extends ElasticsearchRepository<Producto, Long> {
}
