package com.itbt.productservice.service;

import com.itbt.productservice.dto.ProductRequest;
import com.itbt.productservice.dto.ProductResponse;
import com.itbt.productservice.model.Product;
import com.itbt.productservice.repository.ProductRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRespository productRespository;
    public void createProduct(ProductRequest productRequest){
        log.info(" name {} desc {}, price {}", productRequest.getName(), productRequest.getDescription(), productRequest.getPrice());
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
        productRespository.save(product);
        log.info("Product {} is saved successfully!", product.getId());
    }

    public List<ProductResponse> getAllProducts(){
        List<Product> products = productRespository.findAll();
        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
