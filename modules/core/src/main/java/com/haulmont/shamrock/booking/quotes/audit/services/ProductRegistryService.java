/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.services;

import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.monaco.unirest.UnirestCommand;
import com.haulmont.shamrock.booking.quotes.audit.model.product.Product;
import com.haulmont.shamrock.booking.quotes.audit.services.dto.product_registry.ProductsResponse;
import kong.unirest.GetRequest;
import kong.unirest.HttpRequest;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;

@Component
public class ProductRegistryService {

    private static final String SERVICE_NAME = "shamrock-product-registry-service";

    @Inject
    private Logger logger;

    public List<Product> getProducts() {
        try {
            ProductsResponse response = new GetProductsCommand().execute();
            if (response.getCode() == ErrorCode.OK.getCode()) {
                return response.getProducts();
            } else {
                logger.error("Fail to call shamrock-product-registry-service. Code: " + response.getCode());
            }
        } catch (Exception ex) {
            logger.error("Fail to call shamrock-product-registry-service.", ex);
        }

        return Collections.emptyList();
    }

    private static class GetProductsCommand extends UnirestCommand<ProductsResponse> {

        GetProductsCommand() {
            super(SERVICE_NAME, ProductsResponse.class);
        }

        @Override
        protected HttpRequest<GetRequest> createRequest(String url, Path path) {
            return get(url, path);
        }

        @Override
        protected Path getPath() {
            return new Path("/v3/products");
        }
    }
}
