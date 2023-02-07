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
package org.camunda.spin.impl.xml.dom;

import static org.camunda.commons.utils.EnsureUtil.ensureNotNull;

import java.util.Iterator;
import org.camunda.spin.impl.xml.dom.format.AbstractDomXmlDataFormat;
import org.camunda.spin.xml.SpinXmlElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Sebastian Menski
 */
public class DomXmlElementIterable implements Iterable<SpinXmlElement> {

  protected final NodeList nodeList;
  protected final AbstractDomXmlDataFormat dataFormat;
  protected final String namespace;
  protected final String name;
  protected boolean validating;

  public DomXmlElementIterable(Element domElement, AbstractDomXmlDataFormat dataFormat) {
    this(domElement.getChildNodes(), dataFormat);
  }

  public DomXmlElementIterable(NodeList nodeList, AbstractDomXmlDataFormat dataFormat) {
    this.nodeList = nodeList;
    this.dataFormat = dataFormat;
    this.namespace = null;
    this.name = null;
    validating = false;
  }

  public DomXmlElementIterable(Element domElement, AbstractDomXmlDataFormat dataFormat, String namespace, String name) {
    this(domElement.getChildNodes(), dataFormat, namespace, name);
  }

  public DomXmlElementIterable(NodeList nodeList, AbstractDomXmlDataFormat dataFormat, String namespace, String name) {
    ensureNotNull("name", name);
    this.nodeList = nodeList;
    this.dataFormat = dataFormat;
    this.namespace = namespace;
    this.name = name;
    validating = true;
  }

  @Override
  public Iterator<SpinXmlElement> iterator() {
    return new DomXmlNodeIterator<SpinXmlElement>() {

      private NodeList childs = nodeList;

      @Override
      protected int getLength() {
        return childs.getLength();
      }

      @Override
      protected SpinXmlElement getCurrent() {
        if (childs != null) {
          Node item = childs.item(index);
          if (item != null && item instanceof Element) {
            SpinXmlElement current = dataFormat.createElementWrapper((Element) item);
            if (!validating || (current.hasNamespace(namespace) && name.equals(current.name()))) {
                return current;
            }
          }
        }
        return null;
      }
    };

  }

}
