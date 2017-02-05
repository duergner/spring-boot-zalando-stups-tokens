/**
 * Copyright (C) 2017 Zalando SE (http://tech.zalando.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zalando.secrets.spring;

import org.zalando.secrets.Authorization;
import org.zalando.stups.tokens.AccessToken;

import lombok.Builder;
import lombok.Getter;

@Getter
class AccessTokenDto extends AccessToken implements Authorization {

    private final String identfier;
    private final String headerValue;

    @Builder
    private AccessTokenDto(String secret, String type, String identfier) {
        super(secret, type, -1, null);
        this.identfier = identfier;
        this.headerValue = type + " " + secret;
    }

    @Override
    public String getSecret() {
        return getToken();
    }

}
