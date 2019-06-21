/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 */

package org.wso2.carbon.identity.rest.api.server.challenge.v1.core;

import org.wso2.carbon.identity.rest.api.server.challenge.v1.dto.ErrorDTO;

public class Error extends ErrorDTO {

    public static class Builder {
        private String code;
        private String message;
        private String description;

        public Builder() {

        }

        public Builder withCode(String code) {
            this.code = code;
            return this;

        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;

        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Error build() {
            Error error = new Error();
            error.setCode(this.code);
            error.setMessage(this.message);
            error.setDescription(this.description);
            return error;
        }
    }
}
