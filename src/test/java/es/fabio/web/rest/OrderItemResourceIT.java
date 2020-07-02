package es.fabio.web.rest;

import es.fabio.Application;
import es.fabio.domain.OrderItem;
import es.fabio.domain.Producto;
import es.fabio.domain.ProductOrder;
import es.fabio.repository.OrderItemRepository;
import es.fabio.repository.search.OrderItemSearchRepository;
import es.fabio.service.OrderItemService;
import es.fabio.service.dto.OrderItemDTO;
import es.fabio.service.mapper.OrderItemMapper;
import es.fabio.service.dto.OrderItemCriteria;
import es.fabio.service.OrderItemQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import es.fabio.domain.enumeration.OrderItemStatus;
/**
 * Integration tests for the {@link OrderItemResource} REST controller.
 */
@SpringBootTest(classes = Application.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class OrderItemResourceIT {

    private static final Integer DEFAULT_QUANTITY = 0;
    private static final Integer UPDATED_QUANTITY = 1;
    private static final Integer SMALLER_QUANTITY = 0 - 1;

    private static final BigDecimal DEFAULT_TOTAL_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_TOTAL_PRICE = new BigDecimal(0 - 1);

    private static final OrderItemStatus DEFAULT_STATUS = OrderItemStatus.AVAILABLE;
    private static final OrderItemStatus UPDATED_STATUS = OrderItemStatus.OUT_OF_STOCK;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderItemService orderItemService;

    /**
     * This repository is mocked in the es.fabio.repository.search test package.
     *
     * @see es.fabio.repository.search.OrderItemSearchRepositoryMockConfiguration
     */
    @Autowired
    private OrderItemSearchRepository mockOrderItemSearchRepository;

    @Autowired
    private OrderItemQueryService orderItemQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderItemMockMvc;

    private OrderItem orderItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItem createEntity(EntityManager em) {
        OrderItem orderItem = new OrderItem()
            .quantity(DEFAULT_QUANTITY)
            .totalPrice(DEFAULT_TOTAL_PRICE)
            .status(DEFAULT_STATUS);
        // Add required entity
        Producto producto;
        if (TestUtil.findAll(em, Producto.class).isEmpty()) {
            producto = ProductoResourceIT.createEntity(em);
            em.persist(producto);
            em.flush();
        } else {
            producto = TestUtil.findAll(em, Producto.class).get(0);
        }
        orderItem.setProducto(producto);
        // Add required entity
        ProductOrder productOrder;
        if (TestUtil.findAll(em, ProductOrder.class).isEmpty()) {
            productOrder = ProductOrderResourceIT.createEntity(em);
            em.persist(productOrder);
            em.flush();
        } else {
            productOrder = TestUtil.findAll(em, ProductOrder.class).get(0);
        }
        orderItem.setOrder(productOrder);
        return orderItem;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItem createUpdatedEntity(EntityManager em) {
        OrderItem orderItem = new OrderItem()
            .quantity(UPDATED_QUANTITY)
            .totalPrice(UPDATED_TOTAL_PRICE)
            .status(UPDATED_STATUS);
        // Add required entity
        Producto producto;
        if (TestUtil.findAll(em, Producto.class).isEmpty()) {
            producto = ProductoResourceIT.createUpdatedEntity(em);
            em.persist(producto);
            em.flush();
        } else {
            producto = TestUtil.findAll(em, Producto.class).get(0);
        }
        orderItem.setProducto(producto);
        // Add required entity
        ProductOrder productOrder;
        if (TestUtil.findAll(em, ProductOrder.class).isEmpty()) {
            productOrder = ProductOrderResourceIT.createUpdatedEntity(em);
            em.persist(productOrder);
            em.flush();
        } else {
            productOrder = TestUtil.findAll(em, ProductOrder.class).get(0);
        }
        orderItem.setOrder(productOrder);
        return orderItem;
    }

    @BeforeEach
    public void initTest() {
        orderItem = createEntity(em);
    }

    @Test
    @Transactional
    public void createOrderItem() throws Exception {
        int databaseSizeBeforeCreate = orderItemRepository.findAll().size();
        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);
        restOrderItemMockMvc.perform(post("/api/order-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderItemDTO)))
            .andExpect(status().isCreated());

        // Validate the OrderItem in the database
        List<OrderItem> orderItemList = orderItemRepository.findAll();
        assertThat(orderItemList).hasSize(databaseSizeBeforeCreate + 1);
        OrderItem testOrderItem = orderItemList.get(orderItemList.size() - 1);
        assertThat(testOrderItem.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testOrderItem.getTotalPrice()).isEqualTo(DEFAULT_TOTAL_PRICE);
        assertThat(testOrderItem.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the OrderItem in Elasticsearch
        verify(mockOrderItemSearchRepository, times(1)).save(testOrderItem);
    }

    @Test
    @Transactional
    public void createOrderItemWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = orderItemRepository.findAll().size();

        // Create the OrderItem with an existing ID
        orderItem.setId(1L);
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderItemMockMvc.perform(post("/api/order-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OrderItem in the database
        List<OrderItem> orderItemList = orderItemRepository.findAll();
        assertThat(orderItemList).hasSize(databaseSizeBeforeCreate);

        // Validate the OrderItem in Elasticsearch
        verify(mockOrderItemSearchRepository, times(0)).save(orderItem);
    }


    @Test
    @Transactional
    public void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderItemRepository.findAll().size();
        // set the field null
        orderItem.setQuantity(null);

        // Create the OrderItem, which fails.
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);


        restOrderItemMockMvc.perform(post("/api/order-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderItemDTO)))
            .andExpect(status().isBadRequest());

        List<OrderItem> orderItemList = orderItemRepository.findAll();
        assertThat(orderItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTotalPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderItemRepository.findAll().size();
        // set the field null
        orderItem.setTotalPrice(null);

        // Create the OrderItem, which fails.
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);


        restOrderItemMockMvc.perform(post("/api/order-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderItemDTO)))
            .andExpect(status().isBadRequest());

        List<OrderItem> orderItemList = orderItemRepository.findAll();
        assertThat(orderItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderItemRepository.findAll().size();
        // set the field null
        orderItem.setStatus(null);

        // Create the OrderItem, which fails.
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);


        restOrderItemMockMvc.perform(post("/api/order-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderItemDTO)))
            .andExpect(status().isBadRequest());

        List<OrderItem> orderItemList = orderItemRepository.findAll();
        assertThat(orderItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOrderItems() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList
        restOrderItemMockMvc.perform(get("/api/order-items?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(DEFAULT_TOTAL_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    @Transactional
    public void getOrderItem() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get the orderItem
        restOrderItemMockMvc.perform(get("/api/order-items/{id}", orderItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderItem.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.totalPrice").value(DEFAULT_TOTAL_PRICE.intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }


    @Test
    @Transactional
    public void getOrderItemsByIdFiltering() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        Long id = orderItem.getId();

        defaultOrderItemShouldBeFound("id.equals=" + id);
        defaultOrderItemShouldNotBeFound("id.notEquals=" + id);

        defaultOrderItemShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOrderItemShouldNotBeFound("id.greaterThan=" + id);

        defaultOrderItemShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOrderItemShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllOrderItemsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where quantity equals to DEFAULT_QUANTITY
        defaultOrderItemShouldBeFound("quantity.equals=" + DEFAULT_QUANTITY);

        // Get all the orderItemList where quantity equals to UPDATED_QUANTITY
        defaultOrderItemShouldNotBeFound("quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    public void getAllOrderItemsByQuantityIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where quantity not equals to DEFAULT_QUANTITY
        defaultOrderItemShouldNotBeFound("quantity.notEquals=" + DEFAULT_QUANTITY);

        // Get all the orderItemList where quantity not equals to UPDATED_QUANTITY
        defaultOrderItemShouldBeFound("quantity.notEquals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    public void getAllOrderItemsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where quantity in DEFAULT_QUANTITY or UPDATED_QUANTITY
        defaultOrderItemShouldBeFound("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY);

        // Get all the orderItemList where quantity equals to UPDATED_QUANTITY
        defaultOrderItemShouldNotBeFound("quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    public void getAllOrderItemsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where quantity is not null
        defaultOrderItemShouldBeFound("quantity.specified=true");

        // Get all the orderItemList where quantity is null
        defaultOrderItemShouldNotBeFound("quantity.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrderItemsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where quantity is greater than or equal to DEFAULT_QUANTITY
        defaultOrderItemShouldBeFound("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the orderItemList where quantity is greater than or equal to UPDATED_QUANTITY
        defaultOrderItemShouldNotBeFound("quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    public void getAllOrderItemsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where quantity is less than or equal to DEFAULT_QUANTITY
        defaultOrderItemShouldBeFound("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the orderItemList where quantity is less than or equal to SMALLER_QUANTITY
        defaultOrderItemShouldNotBeFound("quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    public void getAllOrderItemsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where quantity is less than DEFAULT_QUANTITY
        defaultOrderItemShouldNotBeFound("quantity.lessThan=" + DEFAULT_QUANTITY);

        // Get all the orderItemList where quantity is less than UPDATED_QUANTITY
        defaultOrderItemShouldBeFound("quantity.lessThan=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    public void getAllOrderItemsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where quantity is greater than DEFAULT_QUANTITY
        defaultOrderItemShouldNotBeFound("quantity.greaterThan=" + DEFAULT_QUANTITY);

        // Get all the orderItemList where quantity is greater than SMALLER_QUANTITY
        defaultOrderItemShouldBeFound("quantity.greaterThan=" + SMALLER_QUANTITY);
    }


    @Test
    @Transactional
    public void getAllOrderItemsByTotalPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where totalPrice equals to DEFAULT_TOTAL_PRICE
        defaultOrderItemShouldBeFound("totalPrice.equals=" + DEFAULT_TOTAL_PRICE);

        // Get all the orderItemList where totalPrice equals to UPDATED_TOTAL_PRICE
        defaultOrderItemShouldNotBeFound("totalPrice.equals=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    @Transactional
    public void getAllOrderItemsByTotalPriceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where totalPrice not equals to DEFAULT_TOTAL_PRICE
        defaultOrderItemShouldNotBeFound("totalPrice.notEquals=" + DEFAULT_TOTAL_PRICE);

        // Get all the orderItemList where totalPrice not equals to UPDATED_TOTAL_PRICE
        defaultOrderItemShouldBeFound("totalPrice.notEquals=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    @Transactional
    public void getAllOrderItemsByTotalPriceIsInShouldWork() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where totalPrice in DEFAULT_TOTAL_PRICE or UPDATED_TOTAL_PRICE
        defaultOrderItemShouldBeFound("totalPrice.in=" + DEFAULT_TOTAL_PRICE + "," + UPDATED_TOTAL_PRICE);

        // Get all the orderItemList where totalPrice equals to UPDATED_TOTAL_PRICE
        defaultOrderItemShouldNotBeFound("totalPrice.in=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    @Transactional
    public void getAllOrderItemsByTotalPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where totalPrice is not null
        defaultOrderItemShouldBeFound("totalPrice.specified=true");

        // Get all the orderItemList where totalPrice is null
        defaultOrderItemShouldNotBeFound("totalPrice.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrderItemsByTotalPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where totalPrice is greater than or equal to DEFAULT_TOTAL_PRICE
        defaultOrderItemShouldBeFound("totalPrice.greaterThanOrEqual=" + DEFAULT_TOTAL_PRICE);

        // Get all the orderItemList where totalPrice is greater than or equal to UPDATED_TOTAL_PRICE
        defaultOrderItemShouldNotBeFound("totalPrice.greaterThanOrEqual=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    @Transactional
    public void getAllOrderItemsByTotalPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where totalPrice is less than or equal to DEFAULT_TOTAL_PRICE
        defaultOrderItemShouldBeFound("totalPrice.lessThanOrEqual=" + DEFAULT_TOTAL_PRICE);

        // Get all the orderItemList where totalPrice is less than or equal to SMALLER_TOTAL_PRICE
        defaultOrderItemShouldNotBeFound("totalPrice.lessThanOrEqual=" + SMALLER_TOTAL_PRICE);
    }

    @Test
    @Transactional
    public void getAllOrderItemsByTotalPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where totalPrice is less than DEFAULT_TOTAL_PRICE
        defaultOrderItemShouldNotBeFound("totalPrice.lessThan=" + DEFAULT_TOTAL_PRICE);

        // Get all the orderItemList where totalPrice is less than UPDATED_TOTAL_PRICE
        defaultOrderItemShouldBeFound("totalPrice.lessThan=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    @Transactional
    public void getAllOrderItemsByTotalPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where totalPrice is greater than DEFAULT_TOTAL_PRICE
        defaultOrderItemShouldNotBeFound("totalPrice.greaterThan=" + DEFAULT_TOTAL_PRICE);

        // Get all the orderItemList where totalPrice is greater than SMALLER_TOTAL_PRICE
        defaultOrderItemShouldBeFound("totalPrice.greaterThan=" + SMALLER_TOTAL_PRICE);
    }


    @Test
    @Transactional
    public void getAllOrderItemsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where status equals to DEFAULT_STATUS
        defaultOrderItemShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the orderItemList where status equals to UPDATED_STATUS
        defaultOrderItemShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllOrderItemsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where status not equals to DEFAULT_STATUS
        defaultOrderItemShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the orderItemList where status not equals to UPDATED_STATUS
        defaultOrderItemShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllOrderItemsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultOrderItemShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the orderItemList where status equals to UPDATED_STATUS
        defaultOrderItemShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllOrderItemsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where status is not null
        defaultOrderItemShouldBeFound("status.specified=true");

        // Get all the orderItemList where status is null
        defaultOrderItemShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrderItemsByProductoIsEqualToSomething() throws Exception {
        // Get already existing entity
        Producto producto = orderItem.getProducto();
        orderItemRepository.saveAndFlush(orderItem);
        Long productoId = producto.getId();

        // Get all the orderItemList where producto equals to productoId
        defaultOrderItemShouldBeFound("productoId.equals=" + productoId);

        // Get all the orderItemList where producto equals to productoId + 1
        defaultOrderItemShouldNotBeFound("productoId.equals=" + (productoId + 1));
    }


    @Test
    @Transactional
    public void getAllOrderItemsByOrderIsEqualToSomething() throws Exception {
        // Get already existing entity
        ProductOrder order = orderItem.getOrder();
        orderItemRepository.saveAndFlush(orderItem);
        Long orderId = order.getId();

        // Get all the orderItemList where order equals to orderId
        defaultOrderItemShouldBeFound("orderId.equals=" + orderId);

        // Get all the orderItemList where order equals to orderId + 1
        defaultOrderItemShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrderItemShouldBeFound(String filter) throws Exception {
        restOrderItemMockMvc.perform(get("/api/order-items?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(DEFAULT_TOTAL_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restOrderItemMockMvc.perform(get("/api/order-items/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrderItemShouldNotBeFound(String filter) throws Exception {
        restOrderItemMockMvc.perform(get("/api/order-items?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrderItemMockMvc.perform(get("/api/order-items/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingOrderItem() throws Exception {
        // Get the orderItem
        restOrderItemMockMvc.perform(get("/api/order-items/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrderItem() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        int databaseSizeBeforeUpdate = orderItemRepository.findAll().size();

        // Update the orderItem
        OrderItem updatedOrderItem = orderItemRepository.findById(orderItem.getId()).get();
        // Disconnect from session so that the updates on updatedOrderItem are not directly saved in db
        em.detach(updatedOrderItem);
        updatedOrderItem
            .quantity(UPDATED_QUANTITY)
            .totalPrice(UPDATED_TOTAL_PRICE)
            .status(UPDATED_STATUS);
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(updatedOrderItem);

        restOrderItemMockMvc.perform(put("/api/order-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderItemDTO)))
            .andExpect(status().isOk());

        // Validate the OrderItem in the database
        List<OrderItem> orderItemList = orderItemRepository.findAll();
        assertThat(orderItemList).hasSize(databaseSizeBeforeUpdate);
        OrderItem testOrderItem = orderItemList.get(orderItemList.size() - 1);
        assertThat(testOrderItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testOrderItem.getTotalPrice()).isEqualTo(UPDATED_TOTAL_PRICE);
        assertThat(testOrderItem.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the OrderItem in Elasticsearch
        verify(mockOrderItemSearchRepository, times(1)).save(testOrderItem);
    }

    @Test
    @Transactional
    public void updateNonExistingOrderItem() throws Exception {
        int databaseSizeBeforeUpdate = orderItemRepository.findAll().size();

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderItemMockMvc.perform(put("/api/order-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OrderItem in the database
        List<OrderItem> orderItemList = orderItemRepository.findAll();
        assertThat(orderItemList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrderItem in Elasticsearch
        verify(mockOrderItemSearchRepository, times(0)).save(orderItem);
    }

    @Test
    @Transactional
    public void deleteOrderItem() throws Exception {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);

        int databaseSizeBeforeDelete = orderItemRepository.findAll().size();

        // Delete the orderItem
        restOrderItemMockMvc.perform(delete("/api/order-items/{id}", orderItem.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OrderItem> orderItemList = orderItemRepository.findAll();
        assertThat(orderItemList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the OrderItem in Elasticsearch
        verify(mockOrderItemSearchRepository, times(1)).deleteById(orderItem.getId());
    }

    @Test
    @Transactional
    public void searchOrderItem() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem);
        when(mockOrderItemSearchRepository.search(queryStringQuery("id:" + orderItem.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(orderItem), PageRequest.of(0, 1), 1));

        // Search the orderItem
        restOrderItemMockMvc.perform(get("/api/_search/order-items?query=id:" + orderItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(DEFAULT_TOTAL_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
