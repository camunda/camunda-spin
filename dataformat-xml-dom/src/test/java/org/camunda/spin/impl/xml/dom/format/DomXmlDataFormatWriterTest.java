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
    String xml = "<order><product>Milk</product><product>Coffee</product></order>";
    SpinXmlElement xml1 = SpinXmlElement.XML(xml);
    SpinXmlElement xml2 = SpinXmlElement.XML(xml1.toString());
    assertThat(xml2.toString()).isEqualTo(xml1.toString());
    SpinXmlElement xml3 = SpinXmlElement.XML(xml2.toString());
    assertThat(xml3.toString()).isEqualTo(xml2.toString());
  }

  @Test
  public void testLazyTemplatesInit() {
    DomXmlDataFormat dataFormat = new DomXmlDataFormat("xml");
    DomXmlDataFormatWriter underTest = dataFormat.getWriter();
    assertThat(underTest.formattingTemplates).isNull();
    Templates templates = underTest.getFormattingTemplates();
    assertThat(underTest.formattingTemplates).isNotNull();
    assertThat(underTest.getFormattingTemplates()).isSameAs(templates);
    assertThat(underTest.getFormattingTemplates()).isSameAs(templates);
  }

  @Test
  public void testPrettyPrintFeature() {
    DomXmlDataFormat dataFormat = new DomXmlDataFormat("xml");
    DomXmlDataFormatReader reader = dataFormat.getReader();
    DomXmlDataFormatWriter underTest = dataFormat.getWriter();

    String xml = "<order><product>Milk</product><product>Coffee</product></order>";

    String formattedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><order>\n"
        + "  <product>Milk</product>\n"
        + "  <product>Coffee</product>\n"
        + "</order>\n";

    String nonformattedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><order><product>Milk</product><product>Coffee</product></order>";

    Element input = reader.readInput(SpinIoUtil.stringAsReader(xml));

    // enable pretty-print (the default behaviour)
    dataFormat.setPrettyPrint(true);
    StringWriter writer = new StringWriter();
    underTest.writeToWriter(writer, input);

    assertThat(writer.toString()).isEqualTo(formattedXml);

    Element clone = reader.readInput(SpinIoUtil.stringAsReader(writer.toString()));
    StringWriter cloneWriter = new StringWriter();
    underTest.writeToWriter(cloneWriter, clone);

    assertThat(cloneWriter.toString()).isEqualTo(writer.toString());


    // disable pretty-print
    dataFormat.setPrettyPrint(false);
    writer = new StringWriter();
    underTest.writeToWriter(writer, input);

    assertThat(writer.toString()).isEqualTo(nonformattedXml);

    clone = reader.readInput(SpinIoUtil.stringAsReader(writer.toString()));
    cloneWriter = new StringWriter();
    underTest.writeToWriter(cloneWriter, clone);

    assertThat(cloneWriter.toString()).isEqualTo(writer.toString());
  }

}
