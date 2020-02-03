package spinnery.widget.api;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import io.github.cottonmc.jankson.JanksonOps;
import net.minecraft.util.Identifier;
import spinnery.Spinnery;
import spinnery.util.MutablePair;
import spinnery.widget.WWidget;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class WStyle {
	protected final Map<String, JsonElement> properties = new HashMap<>();

	public WStyle(Map<String, JsonElement> properties) {
		this.properties.putAll(properties);
	}

	public WStyle(WStyle other) {
		this.properties.putAll(other.properties);
	}

	public WStyle() {
	}

	protected JsonElement getElement(String key) {
		return properties.get(key);
	}

	public boolean contains(String key) {
		return properties.get(key) != null;
	}

	// GETTERS

	public boolean asBoolean(String property) {
		return JanksonOps.INSTANCE.getNumberValue(getElement(property)).orElse(0).intValue() == 1;
	}

	public String asString(String property) {
		return JanksonOps.INSTANCE.getStringValue(getElement(property)).orElse("");
	}

	protected Number asNumber(String property) {
		return JanksonOps.INSTANCE.getNumberValue(getElement(property)).orElse(0);
	}

	public int asInt(String property) {
		return asNumber(property).intValue();
	}

	public long asLong(String property) {
		return asNumber(property).longValue();
	}

	public float asFloat(String property) {
		return asNumber(property).floatValue();
	}

	public double asDouble(String property) {
		return asNumber(property).doubleValue();
	}

	public WColor asColor(String property) {
		return asColor(property, WColor.of("0xff000000"));
	}

	public WColor asColor(String property, WColor defaultColor) {
		return JanksonOps.INSTANCE.getNumberValue(getElement(property))
				.map(WColor::of).orElse(defaultColor);
	}

	public WSize asSize(String property) {
		JsonElement el = getElement(property);
		if (!(el instanceof JsonArray)) return WSize.of(0, 0);
		JsonArray array = (JsonArray) el;
		return WSize.of(array.getInt(0, 0), array.getInt(1, 0));
	}

	// Clockwise from top, returns <vertical, horizontal>
	public MutablePair<WSize, WSize> asSidedSize(String property) {
		JsonElement el = getElement(property);
		Optional<Number> singleValue = JanksonOps.INSTANCE.getNumberValue(el);
		MutablePair<WSize, WSize> pair = MutablePair.of(WSize.of(0, 0), WSize.of(0, 0));
		if (singleValue.isPresent()) {
			int intValue = singleValue.get().intValue();
			WSize size = WSize.of(intValue, intValue);
			return MutablePair.of(size, size);
		}

		if (!(el instanceof JsonArray)) return pair;
		JsonArray array = (JsonArray) el;

		if (array.size() == 1) {
			return MutablePair.of(WSize.of(array.getInt(0, 0), array.getInt(0, 0)), WSize.of(array.getInt(0, 0), array.getInt(0, 0)));
		} else if (array.size() == 2) {
			return MutablePair.of(WSize.of(array.getInt(0, 0), array.getInt(0, 0)), WSize.of(array.getInt(1, 0), array.getInt(1, 0)));
		} else if (array.size() >= 4) {
			return MutablePair.of(WSize.of(array.getInt(0, 0), array.getInt(2, 0)), WSize.of(array.getInt(1, 0), array.getInt(3, 0)));
		}
		return pair;
	}

	public WPosition asPosition(String property) {
		JsonElement el = getElement(property);
		if (!(el instanceof JsonArray)) return new WPosition().set(0, 0, 0);
		JsonArray array = (JsonArray) el;
		return new WPosition().set(array.getInt(0, 0), array.getInt(1, 0), array.getInt(2, 0));
	}

	public WPosition asAnchoredPosition(String property, WWidget anchor) {
		JsonElement el = getElement(property);
		if (!(el instanceof JsonArray)) return new WPosition().anchor(anchor).set(0, 0, 0);
		JsonArray array = (JsonArray) el;
		return new WPosition().anchor(anchor).set(array.getInt(0, 0), array.getInt(1, 0), array.getInt(2, 0));
	}

	public Identifier asIdentifier(String property) {
		return new Identifier(asString(property));
	}

	// SETTERS

	protected static Map<Class<?>, Function<?, JsonElement>> jsonSerializers = new HashMap<>();
	protected static <T> void registerSerializer(Class<T> vClass, Function<T, JsonElement> serializer) {
		jsonSerializers.put(vClass, serializer);
	}
	@SuppressWarnings("unchecked")
	protected static <T> Function<T, JsonElement> getSerializer(T value) {
		for (Class<?> serClass : jsonSerializers.keySet()) {
			if (serClass.isAssignableFrom(value.getClass())) {
				return (Function<T, JsonElement>) jsonSerializers.get(serClass);
			}
		}
		return null;
	}
	static {
		registerSerializer(Number.class, JanksonOps.INSTANCE::createNumeric);
		registerSerializer(String.class, JanksonOps.INSTANCE::createString);
		registerSerializer(Boolean.class, JanksonOps.INSTANCE::createBoolean);
		registerSerializer(WPosition.class, v -> JanksonOps.INSTANCE.createIntList(IntStream.of(v.offsetX, v.offsetY, v.offsetZ)));
		registerSerializer(WSize.class, v -> JanksonOps.INSTANCE.createIntList(IntStream.of(v.getWidth(), v.getHeight())));
		registerSerializer(WColor.class, v -> JanksonOps.INSTANCE.createLong(v.ARGB));
		// TODO: sided size serialization
	}

	public <T> void override(String property, T value) {
		Function<T, JsonElement> ser = getSerializer(value);
		if (ser != null) {
			properties.put(property, ser.apply(value));
		} else {
			Spinnery.LOGGER.warn("Failed to override {}: themes do not support values of class {}",
					property, value.getClass().getSimpleName());
		}
	}
}