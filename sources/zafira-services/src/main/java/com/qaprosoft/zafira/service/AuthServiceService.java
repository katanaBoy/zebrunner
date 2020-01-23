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

import com.qaprosoft.zafira.dbaccess.persistence.AuthServiceRepository;
import com.qaprosoft.zafira.models.entity.AuthService;
import com.qaprosoft.zafira.service.exception.IllegalOperationException;
import com.qaprosoft.zafira.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.qaprosoft.zafira.service.exception.IllegalOperationException.IllegalOperationErrorDetail.AUTH_SERVICE_CAN_NOT_BE_CREATED;
import static com.qaprosoft.zafira.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.AUTH_SERVICE_NOT_FOUND;

@Service
public class AuthServiceService {

    private static final String ERR_MSG_AUTH_SERVICE_ALREADY_DEFINED = "Auth service with name '%s' already defined";
    private static final String ERR_MSG_AUTH_SERVICE_NOT_FOUND = "Auth service does not exist";

    private final AuthServiceRepository authServiceRepository;

    public AuthServiceService(AuthServiceRepository authServiceRepository) {
        this.authServiceRepository = authServiceRepository;
    }

    @Transactional
    public AuthService create(AuthService authService) {
        AuthService dbAuthService = retrieve();
        if (dbAuthService != null) {
            throw new IllegalOperationException(AUTH_SERVICE_CAN_NOT_BE_CREATED, String.format(ERR_MSG_AUTH_SERVICE_ALREADY_DEFINED, authService.getName()));
        }
        authService.setId(null);
        return authServiceRepository.save(authService);
    }

    @Transactional(readOnly = true)
    public AuthService retrieveById(Long id) {
        return authServiceRepository.findById(id)
                                    .orElseThrow(() -> new ResourceNotFoundException(AUTH_SERVICE_NOT_FOUND, ERR_MSG_AUTH_SERVICE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public AuthService retrieve() {
        List<AuthService> authServices = StreamSupport.stream(authServiceRepository.findAll().spliterator(), false)
                                                      .collect(Collectors.toList());
        return CollectionUtils.isEmpty(authServices) ? null : authServices.get(0);
    }

    @Transactional
    public AuthService update(AuthService authService) {
        AuthService dbAuthService = retrieveById(authService.getId());
        dbAuthService.setName(authService.getName());
        dbAuthService.setClientId(authService.getClientId());
        dbAuthService.setClientSecret(authService.getClientSecret());
        return authServiceRepository.save(dbAuthService);
    }
}
