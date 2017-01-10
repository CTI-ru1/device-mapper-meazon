package net.sparkworks.mapper.test;

import net.sparkworks.mapper.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by amaxilatis on 7/10/2016.
 */
public class TestParsing {

    @Test
    public void testParsing() throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("mqtt.log")));
        String line;
        while ((line = br.readLine()) != null) {
            final String topic = line.split(" ")[0];
            final String payload = line.split(" ")[1];
            Utils.ReadingTriple elem = Utils.parseMessage(topic, payload);
            System.out.println(topic + " " + payload);
            if (topic.startsWith("meas")) {
                if (topic.contains("cnrg")) {
                } else if (topic.contains("pwr") || topic.contains("dxi") || topic.contains("pnrg")
                        || topic.contains("rsum") || topic.contains("vmax") || topic.contains("vmin")
                        || topic.contains("izbat") || topic.contains("iztam")
                        || topic.contains("batvlt")) {
                    Assert.assertNull(elem);
                } else {
                    Assert.assertNotNull(elem);
                }
            } else {
                Assert.assertNull(elem);
            }
        }

    }
}
