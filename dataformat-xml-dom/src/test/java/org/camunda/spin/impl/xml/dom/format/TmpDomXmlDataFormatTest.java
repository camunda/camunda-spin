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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;

import org.camunda.spin.impl.util.SpinIoUtil;
import org.junit.Test;
import org.w3c.dom.Element;

public class TmpDomXmlDataFormatTest {

  @Test
  public void testPrettyPrintFeatureOnOneline() {
    // given
    DomXmlDataFormat dataFormat = new DomXmlDataFormat("xml");
    DomXmlDataFormatReader reader = dataFormat.getReader();
    DomXmlDataFormatWriter underTest = dataFormat.getWriter();

    String xml = "<order><product>Milk</product><product>Coffee</product></order>";

    String formattedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><order>\n"
        + "  <product>Milk</product>\n"
        + "  <product>Coffee</product>\n"
        + "</order>\n";

    Element input = reader.readInput(SpinIoUtil.stringAsReader(xml));

    // enable pretty-print (the default behaviour)
    StringWriter writer = new StringWriter();
    StringWriter cloneWriter = new StringWriter();

    // when
    underTest.writeToWriter(writer, input);
    Element clone = reader.readInput(SpinIoUtil.stringAsReader(writer.toString()));
    underTest.writeToWriter(cloneWriter, clone);

    // then
    assertThat(writer).hasToString(formattedXml);
    assertThat(cloneWriter).hasToString(writer.toString());
  }

  @Test
  public void testPrettyPrintFeatureOnMultiline() {
    // given
    DomXmlDataFormat dataFormat = new DomXmlDataFormat("xml");
    DomXmlDataFormatReader reader = dataFormat.getReader();
    DomXmlDataFormatWriter underTest = dataFormat.getWriter();

    String xml = "<order>\n"
        + "  <product>Milk</product>\n"
        + "  <product>Coffee</product>\n"
        + "</order>\n";

    String formattedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><order>\n"
        + "  <product>Milk</product>\n"
        + "  <product>Coffee</product>\n"
        + "</order>\n";

    Element input = reader.readInput(SpinIoUtil.stringAsReader(xml));

    // enable pretty-print (the default behaviour)
    StringWriter writer = new StringWriter();
    StringWriter cloneWriter = new StringWriter();

    // when
    underTest.writeToWriter(writer, input);
    Element clone = reader.readInput(SpinIoUtil.stringAsReader(writer.toString()));
    underTest.writeToWriter(cloneWriter, clone);

    // then
    assertThat(writer).hasToString(formattedXml);
    assertThat(cloneWriter).hasToString(writer.toString());
  }

  @Test
  public void testPrettyPrintFeatureWithEmptyLines() {
    // given
    DomXmlDataFormat dataFormat = new DomXmlDataFormat("xml");
    DomXmlDataFormatReader reader = dataFormat.getReader();
    DomXmlDataFormatWriter underTest = dataFormat.getWriter();

    String xml = "<order>\n" +
        "    \n" +
        "  <product>Milk</product>\n" +
        "    \n" +
        "  <product>Coffee</product>\n" +
        "  \n" +
        "</order>";

    String formattedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><order>\n"
        + "  <product>Milk</product>\n"
        + "  <product>Coffee</product>\n"
        + "</order>\n";

    Element input = reader.readInput(SpinIoUtil.stringAsReader(xml));

    // enable pretty-print (the default behaviour)
    StringWriter writer = new StringWriter();
    StringWriter cloneWriter = new StringWriter();

    // when
    underTest.writeToWriter(writer, input);
    Element clone = reader.readInput(SpinIoUtil.stringAsReader(writer.toString()));
    underTest.writeToWriter(cloneWriter, clone);

    // then
    assertThat(writer).hasToString(formattedXml);
    assertThat(cloneWriter).hasToString(writer.toString());
  }

}