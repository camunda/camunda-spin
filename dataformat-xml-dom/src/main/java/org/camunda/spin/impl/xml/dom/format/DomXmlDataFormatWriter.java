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

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.camunda.spin.impl.xml.dom.DomXmlLogger;
import org.camunda.spin.spi.DataFormatWriter;
import org.camunda.spin.xml.SpinXmlElementException;
import org.w3c.dom.Node;

/**
 * A writer for XML DOM.
 *
 * @author Daniel Meyer
 *
 */
public class DomXmlDataFormatWriter implements DataFormatWriter {

  protected static final DomXmlLogger LOG = DomXmlLogger.XML_DOM_LOGGER;

  protected static final String STRIP_SPACE_XSL = "org/camunda/spin/impl/xml/dom/format/strip-space.xsl";

  protected DomXmlDataFormat domXmlDataFormat;

  public DomXmlDataFormatWriter(DomXmlDataFormat domXmlDataFormat) {
    this.domXmlDataFormat = domXmlDataFormat;
  }

  public void writeToWriter(Writer writer, Object input) {
    writeResult(new StreamResult(writer), input);
  }

  protected void writeResult(StreamResult streamResult, Object input) {
    Node node = (Node) input;
    DOMSource domSource = new DOMSource(node);
    try {
      if (domXmlDataFormat.isPrettyPrint()) {
        getFormattingTransformer().transform(domSource, streamResult);
      } else {
        getTransformer().transform(domSource, streamResult);
      }
    } catch (TransformerException e) {
      throw LOG.unableToTransformElement(node, e);
    }
  }

  protected Templates formattingTemplates = null;

  /**
   * Return a {@link Templates} instance for the strip-spaces.xsl stylesheet.
   * The Templates instance is initialized lazily in order to use the configured
   * {@link TransformerFactory} from the {@link DomXmlDataFormat}.
   *
   * @return the templates instance for strip-spaces.xsl.
   */
  protected synchronized Templates getFormattingTemplates() {
    if (null == formattingTemplates) {
      TransformerFactory transformerFactory = domXmlDataFormat.getTransformerFactory();
      try {
        try (InputStream xslIn = Thread.currentThread().getContextClassLoader().getResourceAsStream(STRIP_SPACE_XSL)) {
          Source xslt = new StreamSource(xslIn);
          formattingTemplates = transformerFactory.newTemplates(xslt);
        }
      } catch (TransformerConfigurationException | IOException e) {
        LOG.unableToCreateTransformer(e);
      }
    }
    return formattingTemplates;
  }

  /**
   * Returns a configured transformer to write XML and apply indentation (pretty-print) to the xml.
   *
   * @return the XML configured transformer
   * @throws SpinXmlElementException if no new transformer can be created
   */
  protected Transformer getFormattingTransformer() {
    try {
      Transformer transformer = getFormattingTemplates().newTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      return transformer;
    } catch (TransformerConfigurationException e) {
      throw LOG.unableToCreateTransformer(e);
    }
  }

  /**
   * Returns a configured transformer to write XML as is.
   *
   * @return the XML configured transformer
   * @throws SpinXmlElementException if no new transformer can be created
   */
  protected Transformer getTransformer() {
    TransformerFactory transformerFactory = domXmlDataFormat.getTransformerFactory();
    try {
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      return transformer;
    } catch (TransformerConfigurationException e) {
      throw LOG.unableToCreateTransformer(e);
    }
  }

}
