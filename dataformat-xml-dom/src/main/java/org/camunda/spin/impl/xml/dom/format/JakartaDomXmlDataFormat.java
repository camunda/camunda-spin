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

import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import org.camunda.spin.impl.xml.dom.format.spi.DefaultJakartaJaxBContextProvider;
import org.camunda.spin.impl.xml.dom.format.spi.JakartaJaxBContextProvider;

public class JakartaDomXmlDataFormat extends AbstractDomXmlDataFormat {

  /** the JaxBContextProvider instance used by this writer. */
  protected JakartaJaxBContextProvider jaxBContextProvider;

  protected JakartaDomXmlDataFormatMapper mapper;

  public JakartaDomXmlDataFormat(String name) {
    this(name, defaultDocumentBuilderFactory());
  }

  public JakartaDomXmlDataFormat(String name, Map<String, Object> configurationProperties) {
    this(name, configurableDocumentBuilderFactory(configurationProperties));
  }

  public JakartaDomXmlDataFormat(String name, JakartaJaxBContextProvider contextProvider) {
    this(name, defaultDocumentBuilderFactory(), contextProvider);
  }

  public JakartaDomXmlDataFormat(String name,
                          DocumentBuilderFactory documentBuilderFactory,
                          JakartaJaxBContextProvider contextProvider) {
    this(name, documentBuilderFactory, defaultTransformerFactory(), contextProvider);
  }

  public JakartaDomXmlDataFormat(String name, DocumentBuilderFactory documentBuilderFactory) {
    this(name, documentBuilderFactory, defaultTransformerFactory(), defaultJaxBContextProvider());
  }

  public JakartaDomXmlDataFormat(String name,
                          DocumentBuilderFactory documentBuilderFactory,
                          TransformerFactory transformerFactory,
                          JakartaJaxBContextProvider contextProvider) {
    super(name, documentBuilderFactory, transformerFactory);
    this.jaxBContextProvider = contextProvider;
    this.mapper = new JakartaDomXmlDataFormatMapper(this);
  }

  @Override
  public JakartaDomXmlDataFormatMapper getMapper() {
    return mapper;
  }

  public JakartaJaxBContextProvider getJaxBContextProvider() {
    return jaxBContextProvider;
  }

  public void setJaxBContextProvider(JakartaJaxBContextProvider jaxBContextProvider) {
    this.jaxBContextProvider = jaxBContextProvider;
  }

  public static JakartaJaxBContextProvider defaultJaxBContextProvider() {
    return new DefaultJakartaJaxBContextProvider();
  }

}
