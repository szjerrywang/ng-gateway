package top.didasoft.ibmmq.cli.commands;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractJavaTypeMapper {

	/**
	 * Default header name for type information.
	 */
	public static final String DEFAULT_CLASSID_FIELD_NAME = "__TypeId__";

	/**
	 * Default header name for container object contents type information.
	 */
	public static final String DEFAULT_CONTENT_CLASSID_FIELD_NAME = "__ContentTypeId__";

	/**
	 * Default header name for map key type information.
	 */
	public static final String DEFAULT_KEY_CLASSID_FIELD_NAME = "__KeyTypeId__";

	/**
	 * Default header name for key type information.
	 */
	public static final String KEY_DEFAULT_CLASSID_FIELD_NAME = "__Key_TypeId__";

	/**
	 * Default header name for key container object contents type information.
	 */
	public static final String KEY_DEFAULT_CONTENT_CLASSID_FIELD_NAME = "__Key_ContentTypeId__";

	/**
	 * Default header name for key map key type information.
	 */
	public static final String KEY_DEFAULT_KEY_CLASSID_FIELD_NAME = "__Key_KeyTypeId__";

	private final Map<String, Class<?>> idClassMapping = new HashMap<String, Class<?>>();

	private final Map<Class<?>, byte[]> classIdMapping = new HashMap<Class<?>, byte[]>();

	private String classIdFieldName = DEFAULT_CLASSID_FIELD_NAME;

	private String contentClassIdFieldName = DEFAULT_CONTENT_CLASSID_FIELD_NAME;

	private String keyClassIdFieldName = DEFAULT_KEY_CLASSID_FIELD_NAME;

	private ClassLoader classLoader = ClassUtils.getDefaultClassLoader();

	public String getClassIdFieldName() {
		return this.classIdFieldName;
	}

	/**
	 * Configure header name for type information.
	 * @param classIdFieldName the header name.
	 * @since 2.1.3
	 */
	public void setClassIdFieldName(String classIdFieldName) {
		this.classIdFieldName = classIdFieldName;
	}

	public String getContentClassIdFieldName() {
		return this.contentClassIdFieldName;
	}

	/**
	 * Configure header name for container object contents type information.
	 * @param contentClassIdFieldName the header name.
	 * @since 2.1.3
	 */
	public void setContentClassIdFieldName(String contentClassIdFieldName) {
		this.contentClassIdFieldName = contentClassIdFieldName;
	}

	public String getKeyClassIdFieldName() {
		return this.keyClassIdFieldName;
	}

	/**
	 * Configure header name for map key type information.
	 * @param keyClassIdFieldName the header name.
	 * @since 2.1.3
	 */
	public void setKeyClassIdFieldName(String keyClassIdFieldName) {
		this.keyClassIdFieldName = keyClassIdFieldName;
	}

	public void setIdClassMapping(Map<String, Class<?>> idClassMapping) {
		this.idClassMapping.putAll(idClassMapping);
		createReverseMap();
	}

	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	protected ClassLoader getClassLoader() {
		return this.classLoader;
	}

	protected void addHeader(Headers headers, String headerName, Class<?> clazz) {
		if (this.classIdMapping.containsKey(clazz)) {
			headers.add(new RecordHeader(headerName, this.classIdMapping.get(clazz)));
		}
		else {
			headers.add(new RecordHeader(headerName, clazz.getName().getBytes(StandardCharsets.UTF_8)));
		}
	}

	protected String retrieveHeader(Headers headers, String headerName) {
		String classId = retrieveHeaderAsString(headers, headerName);
		if (classId == null) {
			throw new MessagingException(
					"failed to convert Message content. Could not resolve " + headerName + " in header");
		}
		return classId;
	}

	protected String retrieveHeaderAsString(Headers headers, String headerName) {
		Header header = headers.lastHeader(headerName);
		if (header != null) {
			String classId = null;
			if (header.value() != null) {
				classId = new String(header.value(), StandardCharsets.UTF_8);
			}
			return classId;
		}
		return null;
	}

	private void createReverseMap() {
		this.classIdMapping.clear();
		for (Map.Entry<String, Class<?>> entry : this.idClassMapping.entrySet()) {
			String id = entry.getKey();
			Class<?> clazz = entry.getValue();
			this.classIdMapping.put(clazz, id.getBytes(StandardCharsets.UTF_8));
		}
	}

	public Map<String, Class<?>> getIdClassMapping() {
		return Collections.unmodifiableMap(this.idClassMapping);
	}

	/**
	 * Configure the TypeMapper to use default key type class.
	 * @param isKey Use key type headers if true
	 * @since 2.1.3
	 */
	public void setUseForKey(boolean isKey) {
		if (isKey) {
			setClassIdFieldName(AbstractJavaTypeMapper.KEY_DEFAULT_CLASSID_FIELD_NAME);
			setContentClassIdFieldName(AbstractJavaTypeMapper.KEY_DEFAULT_CONTENT_CLASSID_FIELD_NAME);
			setKeyClassIdFieldName(AbstractJavaTypeMapper.KEY_DEFAULT_KEY_CLASSID_FIELD_NAME);
		}
	}

}
