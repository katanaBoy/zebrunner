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
package com.qaprosoft.zafira.web.documented;

import com.qaprosoft.zafira.models.dto.AuthServiceType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("Auth service API")
public interface AuthServiceDocumentedController {

    @ApiOperation(
            value = "Retrieves default auth service object. ",
            notes = "Returns found auth service or null if internal login supported only",
            nickname = "get",
            httpMethod = "GET",
            response = AuthServiceType.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found auth service or null", response = AuthServiceType.class)
    })
    AuthServiceType get();
}
