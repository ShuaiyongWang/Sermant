/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sermant.discovery.retry;

/**
 * Retry exceptions
 *
 * @author zhouss
 * @since 2022-09-28
 */
public class RetryException extends Exception {
    private final Throwable realEx;

    /**
     * Retry exceptions
     *
     * @param realEx Exceptions that actually need to be thrown
     */
    public RetryException(Throwable realEx) {
        super(realEx.getMessage());
        this.realEx = realEx;
    }

    public Throwable getRealEx() {
        return realEx;
    }
}