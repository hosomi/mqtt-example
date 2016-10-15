package commons;

import java.util.Map;
import java.util.Objects;

import org.yaml.snakeyaml.Yaml;

public class ConfigManager {

	/**
	 * YAML ファイルから全ての設定値を取得する。
	 * 
	 * @return map 取得した設定値
	 */
	private static Map<String, Object> loadAll() {
		Yaml yaml = new Yaml();
		@SuppressWarnings("unchecked")
		final Map<String, Object> configMap = yaml.loadAs(ClassLoader.getSystemResourceAsStream("conf/config.yaml"),
				Map.class);
		return configMap;
	}

	/**
	 * ブローカー  の設定値を取得する。
	 * 
	 * @param target 取得したい設定値を指定する
	 * @return ブローカー の設定値の値
	 */
	@SuppressWarnings("unchecked")
	public static String getBrokerConfig(String target) {

		Objects.requireNonNull(target);

		final Map<String, Object> brokerConfig = (Map<String, Object>) loadAll().get("broker");
		return (String) brokerConfig.get(target);
	}

	/**
	 * 引数に指定された内容に従って Publish/Subscribe の設定情報を取得する。
	 * 
	 * @param pubSubType 取得したい Publish/Subscribe を指定する
	 * @param target 取得したい設定値を指定する
	 * @return Publish/Subscribe の設定値の値
	 */
	@SuppressWarnings("unchecked")
	public static Object getPubSubConfig(String pubSubType, String target) {

		Objects.requireNonNull(pubSubType);
		Objects.requireNonNull(target);

		final Map<String, Object> pubSubConfig = (Map<String, Object>) loadAll().get(pubSubType);
		Object element = pubSubConfig.get(target);
		Objects.requireNonNull(element, "Can not find element");
		return element;
	}
}
