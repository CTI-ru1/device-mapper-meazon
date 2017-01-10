package net.sparkworks.mapper;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    /**
     * LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(Utils.class);

    private final static Map<String, Double> cnrgMap = new HashMap<>();

    public static ReadingTriple parseMessage(final String topic, final String message) {
        if (topic.startsWith("meas")) {
            final String[] parts = topic.split("/");
            final String gatewayMac = parts[1];
            final String zigbeeMac = parts[2];
            final String sensorType = parts[3];
            LOGGER.info("{gatewayMac:" + gatewayMac + ",zigbeeMac:" + zigbeeMac + ",sensorType:" + sensorType + "}");
            final String device = "meas-" + gatewayMac + "/" + zigbeeMac;
            final String capability = sensorType;
            if (parts.length == 5) {
                if (capability.startsWith("cur") || capability.startsWith("ecur")) {
                    final double readingValue = Double.parseDouble(message.replaceAll("[^\\d.]", "")) * 1000;//change to milliAmperes
                    return new ReadingTriple(device + "/" + capability, readingValue, System.currentTimeMillis());
                } else if (capability.startsWith("vlt")) {
                    final double readingValue = Double.parseDouble(message.replaceAll("[^\\d.]", ""));
                    return new ReadingTriple(device + "/" + capability, readingValue, System.currentTimeMillis());
                } else if (capability.startsWith("cnrg")) {
                    final double readingValue = Double.parseDouble(message.replaceAll("[^\\d.]", ""));
                    if (cnrgMap.containsKey(topic) && (readingValue > cnrgMap.get(topic))) {
                        double difReading = (readingValue - cnrgMap.get(topic)) * 1000 * 1000;
                        return new ReadingTriple(device + "/" + capability, difReading, System.currentTimeMillis());
                    }
                    cnrgMap.put(topic, readingValue);
                } else if (capability.startsWith("frq")) {
                    final double readingValue = Double.parseDouble(message.replaceAll("[^\\d.]", ""));
                    return new ReadingTriple(device + "/" + capability, readingValue, System.currentTimeMillis());
                } else if (capability.startsWith("izmov")) {
                    final double readingValue = Double.parseDouble(message.replaceAll("[^\\d.]", ""));
                    return new ReadingTriple(device + "/" + capability, readingValue, System.currentTimeMillis());
                } else if (capability.startsWith("tmp")) {
                    final double readingValue = Double.parseDouble(message.replaceAll("[^\\d.]", ""));
                    return new ReadingTriple(device + "/" + capability, readingValue, System.currentTimeMillis());
                } else if (capability.startsWith("clhmd")) {
                    final double readingValue = Double.parseDouble(message.replaceAll("[^\\d.]", ""));
                    return new ReadingTriple(device + "/" + capability, readingValue, System.currentTimeMillis());
                }
            }
        } else if (topic.startsWith("hist")) {

        }
        return null;
    }

    public static class ReadingTriple {
        private final String uri;
        private final double value;
        private final long timestamp;

        public ReadingTriple(final String uri, final double value, final long timestamp) {
            this.uri = uri;
            this.value = value;
            this.timestamp = timestamp;
        }

        public String getUri() {
            return uri;
        }

        public double getValue() {
            return value;
        }

        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return "ReadingTriple{" +
                    "uri='" + uri + '\'' +
                    ", value=" + value +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}
