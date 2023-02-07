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
package org.camunda.spin.impl.xml.dom.format;

import java.util.Collections;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.camunda.spin.impl.xml.dom.DomXmlAttribute;
import org.camunda.spin.impl.xml.dom.DomXmlElement;
import org.camunda.spin.impl.xml.dom.DomXmlLogger;
import org.camunda.spin.spi.DataFormat;
import org.camunda.spin.xml.SpinXmlAttribute;
import org.camunda.spin.xml.SpinXmlElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * @author Daniel Meyer
 *
 */
public abstract class AbstractDomXmlDataFormat implements DataFormat<SpinXmlElement> {

  protected static final DomXmlLogger LOG = DomXmlLogger.XML_DOM_LOGGER;

  protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
  protected static final String DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";
  protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
  protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
  protected static final String JAXP_ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
  protected static final String JAXP_ACCESS_EXTERNAL_SCHEMA_SYSTEM_PROPERTY = "javax.xml.accessExternalSchema";
  protected static final String JAXP_ACCESS_EXTERNAL_SCHEMA_ALL = "all";

  public static final String XXE_PROPERTY = "xxe-processing";
  public static final String SP_PROPERTY = "secure-processing";

  /** the DocumentBuilderFactory used by the reader */
  protected DocumentBuilderFactory documentBuilderFactory;

  /** the TransformerFactory instance used by the writer */
  protected TransformerFactory transformerFactory;

  protected DomXmlDataFormatReader reader;
  protected DomXmlDataFormatWriter writer;

  protected final String name;

  protected boolean prettyPrint;

  public AbstractDomXmlDataFormat(String name) {
    this(name, defaultDocumentBuilderFactory());
  }

  public AbstractDomXmlDataFormat(String name, Map<String, Object> configurationProperties) {
    this(name, configurableDocumentBuilderFactory(configurationProperties));
  }

  public AbstractDomXmlDataFormat(String name, DocumentBuilderFactory documentBuilderFactory) {
    this(name, documentBuilderFactory, defaultTransformerFactory());
  }

  public AbstractDomXmlDataFormat(String name,
                          DocumentBuilderFactory documentBuilderFactory,
                          TransformerFactory transformerFactory) {
    this.name = name;
    this.documentBuilderFactory = documentBuilderFactory;
    this.prettyPrint = true;

    LOG.usingDocumentBuilderFactory(documentBuilderFactory.getClass().getName());

    this.transformerFactory = transformerFactory;

    init();
  }

  protected void init() {
    this.reader = new DomXmlDataFormatReader(this);
    this.writer = new DomXmlDataFormatWriter(this);
  }

  @Override
  public Class<? extends SpinXmlElement> getWrapperType() {
    return DomXmlElement.class;
  }

  @Override
  public SpinXmlElement createWrapperInstance(Object parameter) {
    return createElementWrapper((Element) parameter);
  }

  @Override
  public String getName() {
    return name;
  }

  public SpinXmlElement createElementWrapper(Element element) {
    return new DomXmlElement(element, this);
  }

  public SpinXmlAttribute createAttributeWrapper(Attr attr) {
    return new DomXmlAttribute(attr, this);
  }

  @Override
  public DomXmlDataFormatReader getReader() {
    return reader;
  }

  @Override
  public DomXmlDataFormatWriter getWriter() {
    return writer;
  }

  public DocumentBuilderFactory getDocumentBuilderFactory() {
    return documentBuilderFactory;
  }

  public TransformerFactory getTransformerFactory() {
    return transformerFactory;
  }

  public void setDocumentBuilderFactory(DocumentBuilderFactory documentBuilderFactory) {
    this.documentBuilderFactory = documentBuilderFactory;
  }

  public void setTransformerFactory(TransformerFactory transformerFactory) {
    this.transformerFactory = transformerFactory;
    this.writer.reloadFormattingTemplates();
  }

  public boolean isPrettyPrint() {
    return prettyPrint;
  }

  public void setPrettyPrint(boolean prettyPrint) {
    this.prettyPrint = prettyPrint;
  }

  public static TransformerFactory defaultTransformerFactory() {
    return TransformerFactory.newInstance();
  }

  public static DocumentBuilderFactory defaultDocumentBuilderFactory() {
    return configurableDocumentBuilderFactory(Collections.emptyMap());
  }

  public static DocumentBuilderFactory configurableDocumentBuilderFactory(
      Map<String,Object> configurationProperties) {

    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    documentBuilderFactory.setNamespaceAware(true);
    LOG.documentBuilderFactoryConfiguration("namespaceAware", "true");

    documentBuilderFactory.setValidating(false);
    LOG.documentBuilderFactoryConfiguration("validating", "false");

    documentBuilderFactory.setIgnoringComments(true);
    LOG.documentBuilderFactoryConfiguration("ignoringComments", "true");

    documentBuilderFactory.setIgnoringElementContentWhitespace(false);
    LOG.documentBuilderFactoryConfiguration("ignoringElementContentWhitespace", "false");

    if ((boolean) configurationProperties.getOrDefault(XXE_PROPERTY, false) == false) {
      disableXxeProcessing(documentBuilderFactory);
    }

    if ((boolean) configurationProperties.getOrDefault(SP_PROPERTY, true) == true) {
      enableSecureProcessing(documentBuilderFactory);
    }

    return documentBuilderFactory;
  }

  /*
   * Configures the DocumentBuilderFactory in a way, that it is protected against
   * XML External Entity Attacks. If the implementing parser does not support one or
   * multiple features, the failed feature is ignored. The parser might not be protected,
   * if the feature assignment fails.
   *
   * @see <a href="https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet">OWASP Information of XXE attacks</a>
   *
   * @param dbf The factory to configure.
   */
  protected static void disableXxeProcessing(DocumentBuilderFactory dbf) {
    try {
      dbf.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
      dbf.setFeature(DISALLOW_DOCTYPE_DECL, true);
      dbf.setFeature(LOAD_EXTERNAL_DTD, false);
      dbf.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);
    } catch (ParserConfigurationException ignored) {
      // ignore
    }
    dbf.setXIncludeAware(false);
    dbf.setExpandEntityReferences(false);
  }

  /*
   * Configures the DocumentBuilderFactory to process XML securely.
   * If the implementing parser does not support one or multiple features,
   * the failed feature is ignored. The parser might not be protected,
   * if the feature assignment fails.
   *
   * @param dbf The factory to configure.
   */
  protected static void enableSecureProcessing(DocumentBuilderFactory dbf) {
    try {
      dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      dbf.setAttribute(JAXP_ACCESS_EXTERNAL_SCHEMA, resolveAccessExternalSchemaProperty());
    } catch (ParserConfigurationException | IllegalArgumentException ignored) {
      // ignored
    }
  }

  /*
   * JAXP allows users to override the default value via system properties and
   * a central properties file (see https://docs.oracle.com/javase/tutorial/jaxp/properties/scope.html).
   * However, both are overridden by an explicit configuration in code, as we apply it.
   * Since we want users to customize the value, we take the system property into account.
   * The properties file is not supported at the moment.
   */
  protected static String resolveAccessExternalSchemaProperty() {
    String systemProperty = System.getProperty(JAXP_ACCESS_EXTERNAL_SCHEMA_SYSTEM_PROPERTY);

    if (systemProperty != null) {
      return systemProperty;
    } else {
      return JAXP_ACCESS_EXTERNAL_SCHEMA_ALL;
    }
  }
}
