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

package org.camunda.spin.impl.xml.dom.query;

import java.util.Map;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.camunda.spin.SpinList;
import org.camunda.spin.impl.SpinListImpl;
import org.camunda.spin.impl.xml.dom.DomXmlAttributeIterable;
import org.camunda.spin.impl.xml.dom.DomXmlElement;
import org.camunda.spin.impl.xml.dom.DomXmlElementIterable;
import org.camunda.spin.impl.xml.dom.DomXmlLogger;
import org.camunda.spin.impl.xml.dom.format.DomXmlDataFormat;
import org.camunda.spin.xml.SpinXPathQuery;
import org.camunda.spin.xml.SpinXmlAttribute;
import org.camunda.spin.xml.SpinXmlElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Sebastian Menski
 */
public class DomXPathQuery extends SpinXPathQuery {

  private static final DomXmlLogger LOG = DomXmlLogger.XML_DOM_LOGGER;

  private static final String ROOT_NODE_EXPRESSION = "/";

  protected final DomXmlElement domElement;
  protected final XPath query;
  protected final String expression;
  protected final DomXmlDataFormat dataFormat;
  protected DomXPathNamespaceResolver resolver;

  public DomXPathQuery(DomXmlElement domElement, XPath query, String expression, DomXmlDataFormat dataFormat) {
    this.domElement = domElement;
    this.query = query;
    this.expression = expression;
    this.dataFormat = dataFormat;
    this.resolver = new DomXPathNamespaceResolver(this.domElement);

    this.query.setNamespaceContext(this.resolver);
  }

  public SpinXmlElement element() {
    if (ROOT_NODE_EXPRESSION.equals(expression)) {
      // throw an exception for '/' expression
      throw LOG.unableToFindElementWithXPathExpression(expression);
    }

    try {
      Element element = (Element) query.evaluate(expression, domElement.unwrap(), XPathConstants.NODE);
      if (element == null) {
        // throw an exception if element doesn't exist
        throw LOG.unableToFindElementWithXPathExpression(expression);
      }
      return dataFormat.createElementWrapper(element);
    } catch (XPathExpressionException e) {
      throw LOG.unableToEvaluateXPathExpressionOnElement(domElement, e);
    } catch (ClassCastException e) {
      throw LOG.unableToCastXPathResultTo(Element.class, e);
    }
  }

  public SpinList<SpinXmlElement> elementList() {
    try {
      NodeList nodeList = (NodeList) query.evaluate(expression, domElement.unwrap(), XPathConstants.NODESET);
      if (nodeList == null || nodeList.getLength() == 0) {
        // throw an exception if elements don't exist
        throw LOG.unableToFindElementWithXPathExpression(expression);
      }
      return new SpinListImpl<SpinXmlElement>(new DomXmlElementIterable(nodeList, dataFormat));
    } catch (XPathExpressionException e) {
      throw LOG.unableToEvaluateXPathExpressionOnElement(domElement, e);
    } catch (ClassCastException e) {
      throw LOG.unableToCastXPathResultTo(NodeList.class, e);
    }
  }

  public SpinXmlAttribute attribute() {
    if (ROOT_NODE_EXPRESSION.equals(expression)) {
      // throw an exception for '/' expression
      throw LOG.unableToFindElementWithXPathExpression(expression);
    }

    try {
      Attr attribute = (Attr) query.evaluate(expression, domElement.unwrap(), XPathConstants.NODE);
      if (attribute == null) {
        // throw an exception if attribute doesn't exist
        throw LOG.unableToFindElementWithXPathExpression(expression);
      }
      return dataFormat.createAttributeWrapper(attribute);
    } catch (XPathExpressionException e) {
      throw LOG.unableToEvaluateXPathExpressionOnElement(domElement, e);
    } catch (ClassCastException e) {
      throw LOG.unableToCastXPathResultTo(Attr.class, e);
    }
  }

  public SpinList<SpinXmlAttribute> attributeList() {
    try {
      NodeList nodeList = (NodeList) query.evaluate(expression, domElement.unwrap(), XPathConstants.NODESET);
      if (nodeList == null || nodeList.getLength() == 0) {
        // throw an exception if attributes don't exist
        throw LOG.unableToFindElementWithXPathExpression(expression);
      }
      return new SpinListImpl<SpinXmlAttribute>(new DomXmlAttributeIterable(nodeList, dataFormat));
    } catch (XPathExpressionException e) {
      throw LOG.unableToEvaluateXPathExpressionOnElement(domElement, e);
    } catch (ClassCastException e) {
      throw LOG.unableToCastXPathResultTo(NodeList.class, e);
    }
  }

  public String string() {
    if (ROOT_NODE_EXPRESSION.equals(expression)) {
      // throw an exception for '/' expression
      throw LOG.unableToFindElementWithXPathExpression(expression);
    }

    try {
      return (String) query.evaluate(expression, domElement.unwrap(), XPathConstants.STRING);
    } catch (XPathExpressionException e) {
      throw LOG.unableToEvaluateXPathExpressionOnElement(domElement, e);
    } catch (ClassCastException e) {
      throw LOG.unableToCastXPathResultTo(String.class, e);
    }
  }

  public Double number() {
    if (ROOT_NODE_EXPRESSION.equals(expression)) {
      // throw an exception for '/' expression
      throw LOG.unableToFindElementWithXPathExpression(expression);
    }

    try {
      return (Double) query.evaluate(expression, domElement.unwrap(), XPathConstants.NUMBER);
    } catch (XPathExpressionException e) {
      throw LOG.unableToEvaluateXPathExpressionOnElement(domElement, e);
    } catch (ClassCastException e) {
      throw LOG.unableToCastXPathResultTo(Double.class, e);
    }
  }

  public Boolean bool() {
    if (ROOT_NODE_EXPRESSION.equals(expression)) {
      // throw an exception for '/' expression
      throw LOG.unableToFindElementWithXPathExpression(expression);
    }

    try {
      return (Boolean) query.evaluate(expression, domElement.unwrap(), XPathConstants.BOOLEAN);
    } catch (XPathExpressionException e) {
      throw LOG.unableToEvaluateXPathExpressionOnElement(domElement, e);
    } catch (ClassCastException e) {
      throw LOG.unableToCastXPathResultTo(Boolean.class, e);
    }
  }

  public SpinXPathQuery ns(String prefix, String namespace) {
    resolver.setNamespace(prefix, namespace);
    query.setNamespaceContext(resolver);
    return this;
  }

  public SpinXPathQuery ns(Map<String, String> namespaces) {
    resolver.setNamespaces(namespaces);
    query.setNamespaceContext(resolver);
    return this;
  }

}
