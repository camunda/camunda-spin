package org.camunda.spin.impl.xml.dom.format;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;
import javax.xml.transform.Templates;
import org.camunda.spin.impl.util.SpinIoUtil;
import org.camunda.spin.xml.SpinXmlElement;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * Test xml transformation in DomXmlDataFormatWriter
 *
 * @author Lars Uffmann, 2020-10-16
 * @author Joern Muehlencord
 */
public class DomXmlDataFormatWriterTest {

  @Test
  public void testFormattedOutputIsDeterministic() {
    // given
    String xml = "<order><product>Milk</product><product>Coffee</product></order>";
    SpinXmlElement xml1 = SpinXmlElement.XML(xml);

    // when
    SpinXmlElement xml2 = SpinXmlElement.XML(xml1.toString());
    SpinXmlElement xml3 = SpinXmlElement.XML(xml2.toString());

    // then
    assertThat(xml2).hasToString(xml1.toString());
    assertThat(xml3).hasToString(xml2.toString());
  }

  @Test
  public void testLazyTemplatesWithoutInit() {
    // given
    DomXmlDataFormat dataFormat = new DomXmlDataFormat("xml");

    // when
    DomXmlDataFormatWriter underTest = dataFormat.getWriter();

    // then
    assertThat(underTest.formattingTemplates).isNull();
  }

  @Test
  public void testLazyTemplatesInit() {
    // given
    DomXmlDataFormat dataFormat = new DomXmlDataFormat("xml");

    // when
    DomXmlDataFormatWriter underTest = dataFormat.getWriter();
    Templates templates = underTest.getFormattingTemplates();

    // then
    assertThat(underTest.formattingTemplates).isNotNull();
    assertThat(underTest.getFormattingTemplates()).isSameAs(templates);
    assertThat(underTest.getFormattingTemplates()).isSameAs(templates);
  }

  @Test
  public void testPrettyPrintFeature() {
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
    dataFormat.setPrettyPrint(true);
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
  public void testNoPrettyPrintFeature() {
    // given
    DomXmlDataFormat dataFormat = new DomXmlDataFormat("xml");
    DomXmlDataFormatReader reader = dataFormat.getReader();
    DomXmlDataFormatWriter underTest = dataFormat.getWriter();

    String xml = "<order><product>Milk</product><product>Coffee</product></order>";

    String nonformattedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><order><product>Milk</product><product>Coffee</product></order>";

    Element input = reader.readInput(SpinIoUtil.stringAsReader(xml));

    // disable pretty-print
    dataFormat.setPrettyPrint(false);
    StringWriter writer = new StringWriter();
    StringWriter cloneWriter = new StringWriter();

    // when
    underTest.writeToWriter(writer, input);
    Element clone = reader.readInput(SpinIoUtil.stringAsReader(writer.toString()));
    underTest.writeToWriter(cloneWriter, clone);

    // then
    assertThat(writer).hasToString(nonformattedXml);
    assertThat(cloneWriter).hasToString(writer.toString());
  }

}
