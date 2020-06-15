/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
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
package org.camunda.spin.impl.json.jackson.format;

import java.lang.reflect.TypeVariable;
import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import static org.camunda.spin.impl.json.jackson.format.TypeHelper.bindingsArePresent;

/**
 * Type detector for lists only.
 * @deprecated Please use {@link ErasedCollectionTypeDetector} instead.
 */
@Deprecated
public class ListJacksonJsonTypeDetector extends AbstractJacksonJsonTypeDetector {

  public boolean canHandle(Object object) {
    return object instanceof List;
  }

  public String detectType(Object object) {
    return constructType(object).toCanonical();
  }

  protected JavaType constructType(Object object) {
    TypeFactory typeFactory = TypeFactory.defaultInstance();

    if (object instanceof List && !((List<?>) object).isEmpty()) {
      List<?> list = (List<?>) object;
      Object firstElement = list.get(0);
      if (bindingsArePresent(list.getClass(), 1)) {
        final JavaType elementType = constructType(firstElement);
        return typeFactory.constructCollectionType(list.getClass(), elementType);
      }
    }
    return typeFactory.constructType(object.getClass());
  }

}
