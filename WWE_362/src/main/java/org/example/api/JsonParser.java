package org.example.api;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonParser {

    public static <T> String serialize(T obj) {
        return buildJson(obj, new HashSet<>());
    }

    private static <T> String buildJson(T obj, Set<Object> visited) {
        if (obj == null) {
            return "null";
        }

        if (obj instanceof List<?>) {
            return buildList((List<?>) obj, visited);
        }

        if (obj instanceof String) {
            return "\"" + obj + "\"";
        }

        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }

        if (visited.contains(obj)) {
            return "\"CYCLE\"";
        }

        visited.add(obj);

        StringBuilder builder = new StringBuilder();
        builder.append("{");

        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);

            try {
                Object value = field.get(obj);

                builder.append("\"").append(field.getName()).append("\":");

                if (value == null) {
                    builder.append("null");
                } else if (value instanceof String) {
                    builder.append("\"").append(value).append("\"");
                } else if (value instanceof Number || value instanceof Boolean) {
                    builder.append(value);
                } else if (value instanceof List<?>) {
                    builder.append(buildList((List<?>) value, visited));
                } else {
                    builder.append(buildJson(value, visited));
                }

                builder.append(",");
            } catch (Exception e) {
                return "null";
            }
        }

        if (builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append("}");
        return builder.toString();
    }

    private static String buildList(List<?> list, Set<Object> visited) {
        if (list == null) {
            return "null";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("[");

        for (Object item : list) {
            if (item == null) {
                builder.append("null");
            } else if (item instanceof String) {
                builder.append("\"").append(item).append("\"");
            } else if (item instanceof Number || item instanceof Boolean) {
                builder.append(item);
            } else {
                builder.append(buildJson(item, visited));
            }

            builder.append(",");
        }

        if (builder.length() > 1 && builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append("]");
        return builder.toString();
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        try {
            if (json == null) {
                return null;
            }

            json = json.trim();

            if (json.equals("null")) {
                return null;
            }

            if (clazz == String.class) {
                return clazz.cast(removeQuotes(json));
            }

            if (clazz == int.class || clazz == Integer.class) {
                return clazz.cast(Integer.valueOf(json));
            }

            if (clazz == boolean.class || clazz == Boolean.class) {
                return clazz.cast(Boolean.valueOf(json));
            }

            if (clazz == double.class || clazz == Double.class) {
                return clazz.cast(Double.valueOf(json));
            }

            if (clazz == float.class || clazz == Float.class) {
                return clazz.cast(Float.valueOf(json));
            }

            if (clazz == long.class || clazz == Long.class) {
                return clazz.cast(Long.valueOf(json));
            }

            if (clazz.isArray()) {
                return deserializeArrayType(json, clazz);
            }

            if (!json.startsWith("{") || !json.endsWith("}")) {
                return null;
            }

            T instance = clazz.getDeclaredConstructor().newInstance();
            String body = json.substring(1, json.length() - 1);
            Map<String, String> pairs = extractPairs(body);

            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);

                String rawValue = pairs.get(field.getName());
                if (rawValue == null) {
                    continue;
                }

                Class<?> fieldType = field.getType();

                if (fieldType == String.class) {
                    field.set(instance, removeQuotes(rawValue));
                } else if (fieldType == int.class || fieldType == Integer.class) {
                    field.set(instance, Integer.parseInt(rawValue));
                } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                    field.set(instance, Boolean.parseBoolean(rawValue));
                } else if (fieldType == double.class || fieldType == Double.class) {
                    field.set(instance, Double.parseDouble(rawValue));
                } else if (fieldType == float.class || fieldType == Float.class) {
                    field.set(instance, Float.parseFloat(rawValue));
                } else if (fieldType == long.class || fieldType == Long.class) {
                    field.set(instance, Long.parseLong(rawValue));
                } else if (List.class.isAssignableFrom(fieldType)) {
                    ParameterizedType listType = (ParameterizedType) field.getGenericType();
                    Class<?> itemType = (Class<?>) listType.getActualTypeArguments()[0];
                    field.set(instance, deserializeList(rawValue, itemType));
                } else {
                    field.set(instance, deserialize(rawValue, fieldType));
                }
            }

            return instance;
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> deserializeList(String json, Class<T> type) {
        List<T> results = new ArrayList<>();

        if (json == null) {
            return results;
        }

        json = json.trim();

        if (json.equals("null") || json.equals("[]")) {
            return results;
        }

        if (!json.startsWith("[") || !json.endsWith("]")) {
            return results;
        }

        String content = json.substring(1, json.length() - 1).trim();

        if (content.isEmpty()) {
            return results;
        }

        List<String> items = splitTopLevel(content);

        for (String item : items) {
            T value = deserialize(item, type);
            if (value != null) {
                results.add(value);
            }
        }

        return results;
    }

    private static <T> T deserializeArrayType(String json, Class<T> clazz) {
        if (!json.startsWith("[") || !json.endsWith("]")) {
            return null;
        }

        String content = json.substring(1, json.length() - 1).trim();
        List<String> items = splitTopLevel(content);

        Class<?> componentType = clazz.getComponentType();
        Object array = Array.newInstance(componentType, items.size());

        for (int i = 0; i < items.size(); i++) {
            Object value = deserialize(items.get(i), componentType);
            Array.set(array, i, value);
        }

        return clazz.cast(array);
    }

    private static Map<String, String> extractPairs(String json) {
        Map<String, String> map = new HashMap<>();
        List<String> pairs = splitTopLevel(json);

        for (String pair : pairs) {
            int colonIndex = findColonOutsideQuotes(pair);
            if (colonIndex == -1) {
                continue;
            }

            String key = pair.substring(0, colonIndex).trim();
            String value = pair.substring(colonIndex + 1).trim();

            key = removeQuotes(key);
            map.put(key, value);
        }

        return map;
    }

    private static List<String> splitTopLevel(String text) {
        List<String> parts = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            return parts;
        }

        int start = 0;
        int braceDepth = 0;
        int bracketDepth = 0;
        boolean inQuotes = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '"' && !isEscaped(text, i)) {
                inQuotes = !inQuotes;
            } else if (!inQuotes) {
                if (c == '{') {
                    braceDepth++;
                } else if (c == '}') {
                    braceDepth--;
                } else if (c == '[') {
                    bracketDepth++;
                } else if (c == ']') {
                    bracketDepth--;
                } else if (c == ',' && braceDepth == 0 && bracketDepth == 0) {
                    parts.add(text.substring(start, i).trim());
                    start = i + 1;
                }
            }
        }

        parts.add(text.substring(start).trim());
        return parts;
    }

    private static int findColonOutsideQuotes(String text) {
        boolean inQuotes = false;
        int braceDepth = 0;
        int bracketDepth = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '"' && !isEscaped(text, i)) {
                inQuotes = !inQuotes;
            } else if (!inQuotes) {
                if (c == '{') {
                    braceDepth++;
                } else if (c == '}') {
                    braceDepth--;
                } else if (c == '[') {
                    bracketDepth++;
                } else if (c == ']') {
                    bracketDepth--;
                } else if (c == ':' && braceDepth == 0 && bracketDepth == 0) {
                    return i;
                }
            }
        }

        return -1;
    }

    private static boolean isEscaped(String text, int index) {
        int slashCount = 0;
        int i = index - 1;

        while (i >= 0 && text.charAt(i) == '\\') {
            slashCount++;
            i--;
        }

        return slashCount % 2 != 0;
    }

    private static String removeQuotes(String value) {
        value = value.trim();

        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
            value = value.substring(1, value.length() - 1);
        }

        return value;
    }
}