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

package com.huaweicloud.sermant.router.config.handler;

import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.config.common.SafeConstructor;

import org.yaml.snakeyaml.Yaml;

/**
 * 配置处理器
 *
 * @author provenceee
 * @since 2022-08-09
 */
public abstract class AbstractConfigHandler implements AbstractHandler {
    /**
     * yaml
     */
    protected final Yaml yaml;

    /**
     * 构造方法
     */
    public AbstractConfigHandler() {
        this.yaml = new Yaml(new SafeConstructor(null));
    }

    /**
     * 是否需要处理
     *
     * @param key 配置key
     * @param content 配置内容
     * @return 是否需要处理
     */
    @Override
    public boolean shouldHandle(String key, String content) {
        return RouterConstant.MATCH_KIND_LIST.stream().anyMatch(content::contains);
    }
}