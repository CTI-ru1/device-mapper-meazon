package net.sparkworks.mapper.configuration;

import net.sparkworks.mapper.Utils;
import net.sparkworks.mapper.service.SenderService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MqttConfiguration {
    /**
     * LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(MqttConfiguration.class);

    @Value("${mqtt.url}")
    private String mqttUrl;
    @Value("${mqtt.topics}")
    private String mqttTopics;
    @Value("${mqtt.clientId}")
    private String mqttClientId;

    @Autowired
    SenderService senderService;

    private final Map<String, Double> cnrgMap = new HashMap<>();

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(mqttUrl, mqttClientId, mqttTopics);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(0);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }


    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {

            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                final String topic = (String) message.getHeaders().get("mqtt_topic");

                if (topic.startsWith("s") || topic.equals("heartbeat") || topic.equals("stats")) return;
                parseMessage(topic, message);
            }
        };
    }

    private void parseMessage(final String topic, final Message<?> messageObj) {
        final String message = (String) messageObj.getPayload();
        final Utils.ReadingTriple reading = Utils.parseMessage(topic, message);
        if (reading != null) {
            senderService.sendMeasurement(reading);
        }
    }
}