/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.service;

import com.qaprosoft.zafira.dbaccess.persistence.AuthProviderRepository;
import com.qaprosoft.zafira.models.entity.AuthProvider;
import com.qaprosoft.zafira.service.exception.IllegalOperationException;
import com.qaprosoft.zafira.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.qaprosoft.zafira.service.exception.IllegalOperationException.IllegalOperationErrorDetail.AUTH_PROVIDER_CAN_NOT_BE_CREATED;
import static com.qaprosoft.zafira.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.AUTH_PROVIDER_NOT_FOUND;

@Service
public class AuthProviderService {

    private static final String ERR_MSG_AUTH_PROVIDER_ALREADY_DEFINED = "Auth provider with name '%s' already defined";
    private static final String ERR_MSG_AUTH_PROVIDER_NOT_FOUND = "Auth provider does not exist";

    private final AuthProviderRepository authProviderRepository;

    public AuthProviderService(AuthProviderRepository authProviderRepository) {
        this.authProviderRepository = authProviderRepository;
    }

    @Transactional
    public AuthProvider create(AuthProvider authProvider) {
        AuthProvider dbAuthProvider = retrieve();
        if (dbAuthProvider != null) {
            throw new IllegalOperationException(AUTH_PROVIDER_CAN_NOT_BE_CREATED, String.format(ERR_MSG_AUTH_PROVIDER_ALREADY_DEFINED, authProvider.getName()));
        }
        authProvider.setId(null);
        return authProviderRepository.save(authProvider);
    }

    @Transactional(readOnly = true)
    public AuthProvider retrieveById(Long id) {
        return authProviderRepository.findById(id)
                                     .orElseThrow(() -> new ResourceNotFoundException(AUTH_PROVIDER_NOT_FOUND, ERR_MSG_AUTH_PROVIDER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public AuthProvider retrieve() {
        List<AuthProvider> authProviders = StreamSupport.stream(authProviderRepository.findAll().spliterator(), false)
                                                        .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(authProviders)) {
            throw new ResourceNotFoundException(AUTH_PROVIDER_NOT_FOUND, ERR_MSG_AUTH_PROVIDER_NOT_FOUND);
        }
        return authProviders.get(0);
    }

    @Transactional
    public AuthProvider update(AuthProvider authProvider) {
        AuthProvider dbAuthProvider = retrieveById(authProvider.getId());
        dbAuthProvider.setName(authProvider.getName());
        dbAuthProvider.setClientId(authProvider.getClientId());
        dbAuthProvider.setClientSecret(authProvider.getClientSecret());
        return authProviderRepository.save(dbAuthProvider);
    }
}
