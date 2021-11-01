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
import org.camunda.spin.spi.DataFormat;
import org.camunda.spin.xml.SpinXmlElement;
import org.junit.Test;

public class Cam13699Test {

  @Test
  public void testScenarioFromTicket() throws Exception {

    DataFormat<SpinXmlElement> dataFormat = DataFormats.xml();

    // on windows it's /r/n
    String newLine = System.getProperty("line.separator");

    String formattedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><order>" + newLine
        + "  <product>Milk</product>" + newLine
        + "  <product>Coffee</product>" + newLine
        + "</order>" + newLine;

    SpinXmlElement spinXml = Spin.XML(formattedXml);

    // this is what execution.setVariable("test", spinXml); does
    // see https://github.com/camunda/camunda-bpm-platform/blob/master/engine-plugins/spin-plugin/src/main/java/org/camunda/spin/plugin/impl/SpinValueSerializer.java
    byte[] serializedValue = serializeValue(spinXml);

    // assert that there are now new lines in the serialized value:
    assertThat(new String(serializedValue, "UTF-8")).isEqualTo(formattedXml);

    // this is what execution.getVariable("test"); does
    SpinXmlElement spinXmlElement = deserializeValue(serializedValue, dataFormat);
    assertThat(spinXmlElement.toString()).isEqualTo(formattedXml);

  }

  private byte[] serializeValue(SpinXmlElement spinXml) throws UnsupportedEncodingException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    OutputStreamWriter outWriter = new OutputStreamWriter(out, "UTF-8");
    BufferedWriter bufferedWriter = new BufferedWriter(outWriter);

    spinXml.writeToWriter(bufferedWriter);
    return out.toByteArray();
  }

   public SpinXmlElement deserializeValue(byte[] serialized, DataFormat<SpinXmlElement> dataFormat) throws UnsupportedEncodingException {
     ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
     InputStreamReader inReader = new InputStreamReader(bais, "UTF-8");
     BufferedReader bufferedReader = new BufferedReader(inReader);

     Object wrapper = dataFormat.getReader().readInput(bufferedReader);
     return dataFormat.createWrapperInstance(wrapper);
   }


}
