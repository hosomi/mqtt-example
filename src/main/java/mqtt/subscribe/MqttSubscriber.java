package mqtt.subscribe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.ConfigManager;

public class MqttSubscriber implements MqttCallback {
	
	private final static Logger logger = LoggerFactory.getLogger(MqttSubscriber.class);

	/**
	 * MQTT ブローカーとの接続を失った時に呼び出される。
	 */
	@Override
	public void connectionLost(Throwable cause) {
		logger.warn("Connection lost!");
		
		System.exit(1);
	}

	/**
	 * メッセージの送信が完了したときに呼ばれる。
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		logger.info("deliveryComplete!");
	}

	/**
	 * メッセージを受信したときに呼ばれる。
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		logger.info("Message arrived");
		logger.info("Topic:", topic);
		logger.info("Message: " + new String(message.getPayload()));
	}

	public static void main(String[] args) throws InterruptedException {
		try {
			MqttSubscriber subscriber = new MqttSubscriber();
			subscriber.subscribe();
		} catch (MqttException me) {
			logger.error("reason: {} ", me.getReasonCode());
			logger.error("message: {} ", me.getMessage());
			logger.error("localize: {}", me.getLocalizedMessage());
			logger.error("cause: {} ", me.getCause());
			logger.error("exception: {}", me);
		}
	}

	/**
	 * メッセージを受信する。
	 * 
	 * @throws MqttException
	 * @throws InterruptedException
	 */
	public void subscribe() throws MqttException, InterruptedException {

		final String brokerTcp = String.valueOf(ConfigManager.getBrokerConfig("tcp"));
		final String brokerUsername = (String) ConfigManager.getBrokerConfig("username");
		final String brokerPassword = (String) ConfigManager.getBrokerConfig("password");
		final String topic = String.valueOf(ConfigManager.getPubSubConfig("subscribe", "topic"));
		final int qos = (int) ConfigManager.getPubSubConfig("subscribe", "qos");
		final String clientId = String.valueOf(ConfigManager.getPubSubConfig("subscribe", "clientId"));

		MqttClient client = new MqttClient(brokerTcp, clientId, new MemoryPersistence());
		client.setCallback(this);
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(false);
		if (brokerUsername != null && brokerPassword != null) {
			connOpts.setUserName(brokerUsername);
			connOpts.setPassword(brokerPassword.toCharArray());
		}

		logger.info("Connecting to broker: {}", brokerTcp);
		client.connect(connOpts);

		logger.info("Connected and subscribing message: qos -> {}, topic -> {}", qos, topic);
		client.subscribe(topic, qos);

		logger.info("Please press any key if you would disconnect to broker.");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			br.readLine();
		} catch (IOException e) {
			System.exit(1);
		}
		client.disconnect();
		logger.info("Disconnected");
	}
}
