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
 ******************************************************************************/
package com.qaprosoft.zafira.services.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ExternalSystemException extends ApplicationException {

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public enum IllegalOperationErrorDetail implements ErrorDetail {

        JIRA_ISSUE_CAN_NOT_BE_FOUND(2150);

        private final Integer code;
        private String messageKey;

    }

    public ExternalSystemException(IllegalOperationErrorDetail errorDetail, String message, Throwable cause) {
        super(errorDetail, message, cause);
    }

    public ExternalSystemException(IllegalOperationErrorDetail errorDetail, String message) {
        super(errorDetail, message);
    }

    public ExternalSystemException(String message) {
        super(message);
    }

    public ExternalSystemException(String message, Throwable cause) {
        super(message, cause);
    }

}
