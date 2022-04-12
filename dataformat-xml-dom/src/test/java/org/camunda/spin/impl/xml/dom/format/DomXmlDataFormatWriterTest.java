package org.camunda.spin.impl.xml.dom.format;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import org.camunda.spin.DataFormats;
import org.camunda.spin.Spin;
import org.camunda.spin.SpinFactory;
import org.camunda.spin.spi.DataFormat;
import org.camunda.spin.xml.SpinXmlElement;
import org.junit.Test;

/**
 * Test xml transformation in DomXmlDataFormatWriter
 */
public class DomXmlDataFormatWriterTest {

  private final String newLine = System.getProperty("line.separator");
  private final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><order><product>Milk</product><product>Coffee</product></order>";

  private final String formattedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><order>" + newLine
      + "  <product>Milk</product>" + newLine
      + "  <product>Coffee</product>" + newLine
      + "</order>" + newLine;


  // this is what execution.setVariable("test", spinXml); does
  // see https://github.com/camunda/camunda-bpm-platform/blob/master/engine-plugins/spin-plugin/src/main/java/org/camunda/spin/plugin/impl/SpinValueSerializer.java
  private byte[] serializeValue(SpinXmlElement spinXml) throws UnsupportedEncodingException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    OutputStreamWriter outWriter = new OutputStreamWriter(out, "UTF-8");
    BufferedWriter bufferedWriter = new BufferedWriter(outWriter);

    spinXml.writeToWriter(bufferedWriter);
    return out.toByteArray();
  }

  public SpinXmlElement deserializeValue(byte[] serialized, DataFormat<SpinXmlElement> dataFormat)
      throws UnsupportedEncodingException {
    ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
    InputStreamReader inReader = new InputStreamReader(bais, "UTF-8");
    BufferedReader bufferedReader = new BufferedReader(inReader);

    Object wrapper = dataFormat.getReader().readInput(bufferedReader);
    return dataFormat.createWrapperInstance(wrapper);
  }

  /**
   * standard behaviour: an unformatted XML will be formatted stored into a SPIN variable and also returned formatted.
   */
  @Test
  public void testStandardFormatter() throws Exception {
    DataFormat<SpinXmlElement> dataFormat = new DomXmlDataFormat(DataFormats.XML_DATAFORMAT_NAME);

    SpinXmlElement spinXml = SpinFactory.INSTANCE.createSpin(xml, dataFormat);
    byte[] serializedValue = serializeValue(spinXml);

    // assert that there are now new lines in the serialized value:
    assertThat(new String(serializedValue, "UTF-8")).isEqualTo(formattedXml);

    // this is what execution.getVariable("test"); does
    SpinXmlElement spinXmlElement = deserializeValue(serializedValue, dataFormat);
    assertThat(spinXmlElement.toString()).isEqualTo(formattedXml);
  }

  /**
   * behaviour fixed by CAM-13699: an already formatted XML will be formatted stored into a SPIN variable and also
   * returned formatted but no additional blank lines are inserted into the XML.
   */
  @Test
  public void testAlreadyFormattedXml() throws Exception {
    DataFormat<SpinXmlElement> dataFormat = new DomXmlDataFormat(DataFormats.XML_DATAFORMAT_NAME);

    SpinXmlElement spinXml = SpinFactory.INSTANCE.createSpin(formattedXml, dataFormat);
    byte[] serializedValue = serializeValue(spinXml);

    // assert that there are no new lines in the serialized value:
    assertThat(new String(serializedValue, "UTF-8")).isEqualTo(formattedXml);

    // this is what execution.getVariable("test"); does
    SpinXmlElement spinXmlElement = deserializeValue(serializedValue, dataFormat);
    assertThat(spinXmlElement.toString()).isEqualTo(formattedXml);
  }

  /**
   * new feature provided by CAM-13699 - pretty print feature disabled. The XML is stored and returned as is.
   */
  @Test
  public void testDisabledPrettyPrintUnformatted() throws Exception {
    DataFormat<SpinXmlElement> dataFormat = new DomXmlDataFormat(DataFormats.XML_DATAFORMAT_NAME);
    ((DomXmlDataFormat) dataFormat).setPrettyPrint(false);

    SpinXmlElement spinXml = SpinFactory.INSTANCE.createSpin(xml, dataFormat);
    byte[] serializedValue = serializeValue(spinXml);

    // assert that xml has not been formatted
    assertThat(new String(serializedValue, "UTF-8")).isEqualTo(xml);

    // this is what execution.getVariable("test"); does
    SpinXmlElement spinXmlElement = deserializeValue(serializedValue, dataFormat);
    assertThat(spinXmlElement.toString()).isEqualTo(xml);
  }

  /**
   * new feature provided by CAM-13699 - pretty print feature disabled. The XML is stored and returned as is.
   */
  @Test
  public void testDisabledPrettyPrintFormatted() throws Exception {

    String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><order>" + newLine
        + "  <product>Milk</product>" + newLine
        + "  <product>Coffee</product>" + newLine
        + "</order>";

    DataFormat<SpinXmlElement> dataFormat = DataFormats.xml();
    ((DomXmlDataFormat) dataFormat).setPrettyPrint(false);

    SpinXmlElement spinXml = Spin.XML(formattedXml);
    byte[] serializedValue = serializeValue(spinXml);

    // assert that xml has not been formatted
    assertThat(new String(serializedValue, "UTF-8")).isEqualTo(expectedXml);

    // this is what execution.getVariable("test"); does
    SpinXmlElement spinXmlElement = deserializeValue(serializedValue, dataFormat);
    assertThat(spinXmlElement.toString()).isEqualTo(expectedXml);
  }
}
