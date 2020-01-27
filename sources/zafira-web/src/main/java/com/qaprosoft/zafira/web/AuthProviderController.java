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
package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.models.dto.AuthProviderType;
import com.qaprosoft.zafira.models.entity.AuthProvider;
import com.qaprosoft.zafira.service.AuthProviderService;
import com.qaprosoft.zafira.service.util.URLResolver;
import com.qaprosoft.zafira.web.documented.AuthProviderDocumentedController;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RequestMapping(path = "api/auth/service", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class AuthProviderController extends AbstractController implements AuthProviderDocumentedController {

    private final AuthProviderService authProviderService;
    private final URLResolver urlResolver;
    private final Mapper mapper;

    public AuthProviderController(AuthProviderService authProviderService, URLResolver urlResolver, Mapper mapper) {
        this.authProviderService = authProviderService;
        this.urlResolver = urlResolver;
        this.mapper = mapper;
    }

    @GetMapping("/default")
    @Override
    public AuthProviderType get() {
        AuthProvider authService = authProviderService.retrieve();
        AuthProviderType authServiceType = mapper.map(authService, AuthProviderType.class);
        authServiceType.setSuccessLoginUrl(urlResolver.buildWebURL() + "login/success");
        return authServiceType;
    }

}
