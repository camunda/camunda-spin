/* Licensed under the Apache License, Version 2.0 (the "License");
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

package org.camunda.spin.impl.json.tree;

import org.camunda.spin.SpinList;
import org.camunda.spin.json.SpinJsonNode;
import org.camunda.spin.json.SpinJsonTreePathQuery;
import org.camunda.spin.logging.SpinLogger;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

/**
 * @author Stefan Hentschel
 */
public class SpinJsonJacksonPathQuery implements SpinJsonTreePathQuery {

  private static final JsonJacksonTreeLogger LOG = SpinLogger.JSON_TREE_LOGGER;

  protected final SpinJsonNode spinJsonNode;
  protected final JsonPath query;
  protected final JsonJacksonTreeDataFormat dataFormat;

  public SpinJsonJacksonPathQuery(SpinJsonJacksonTreeNode spinJsonJacksonTreeNode, JsonPath query, JsonJacksonTreeDataFormat dataFormat) {
    this.spinJsonNode = spinJsonJacksonTreeNode;
    this.query = query;
    this.dataFormat = dataFormat;
  }

  public SpinJsonNode element() {
    try {
      JsonNode node = dataFormat.createJsonNode(query.read(spinJsonNode.toString(), dataFormat.getJsonPathConfiguration()));
      return dataFormat.createWrapperInstance(node);
    } catch(PathNotFoundException pex) {
      throw LOG.unableToEvaluateJsonPathExpressionOnNode(spinJsonNode, pex);
    } catch (ClassCastException cex) {
      throw LOG.unableToCastJsonPathResultTo(SpinJsonNode.class, cex);
    }
  }

  public SpinList<SpinJsonNode> elementList() {
    SpinJsonJacksonTreeNode node = (SpinJsonJacksonTreeNode) element();
    if(node.isArray()) {
      return node.elements();
    } else {
      throw LOG.unableToParseValue(SpinList.class.getSimpleName(), node.getNodeType());
    }
  }

  public String stringValue() {
    SpinJsonJacksonTreeNode node = (SpinJsonJacksonTreeNode) element();
    if(node.isString()) {
      return node.stringValue();
    } else {
      throw LOG.unableToParseValue(String.class.getSimpleName(), node.getNodeType());
    }
  }

  public Number numberValue() {
    SpinJsonJacksonTreeNode node = (SpinJsonJacksonTreeNode) element();
    if(node.isNumber()) {
      return node.numberValue();
    } else {
      throw LOG.unableToParseValue(Number.class.getSimpleName(), node.getNodeType());
    }
  }

  public Boolean boolValue() {
    SpinJsonJacksonTreeNode node = (SpinJsonJacksonTreeNode) element();
    if(node.isBoolean()) {
      return node.boolValue();
    } else {
      throw LOG.unableToParseValue(Boolean.class.getSimpleName(), node.getNodeType());
    }
  }
}