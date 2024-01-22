/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.services;

import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.monaco.unirest.UnirestCommand;
import com.haulmont.shamrock.booking.quotes.audit.model.customer.Account;
import com.haulmont.shamrock.booking.quotes.audit.services.dto.customer_profile.ClientResponse;
import kong.unirest.GetRequest;
import kong.unirest.HttpRequest;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Optional;

@Component
public class CustomerProfileService {

    private static final String SERVICE_NAME = "shamrock-customer-profile-service";

    @Inject
    private Logger logger;

    public Optional<Account> getAccountByClient(String clientPid) {
        try {
            ClientResponse response = new GetClientCommand(clientPid).execute();
            if (response.getCode() == ErrorCode.OK.getCode()) {
                return Optional.ofNullable(response.getAccount());
            } else {
                logger.error("Fail to call shamrock-customer-profile-service. Code: " + response.getCode());
            }
        } catch (Exception ex) {
            logger.error("Fail to call shamrock-customer-profile-service. Client pid: " + clientPid, ex);
        }

        return Optional.empty();
    }

    private static class GetClientCommand extends UnirestCommand<ClientResponse> {

        private final String clientPid;

        GetClientCommand(String clientPid) {
            super(SERVICE_NAME, ClientResponse.class);
            this.clientPid = clientPid;
        }

        @Override
        protected HttpRequest<GetRequest> createRequest(String url, Path path) {
            return get(url, path);
        }

        @Override
        protected Path getPath() {
            return new Path("/v3/clients/{client_pid}", Collections.singletonMap("client_pid", clientPid));
        }
    }
}
