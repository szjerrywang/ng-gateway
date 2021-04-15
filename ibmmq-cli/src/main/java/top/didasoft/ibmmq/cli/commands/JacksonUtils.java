package top.didasoft.ibmmq.cli.commands;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.text.SimpleDateFormat;


public final class JacksonUtils {

    private static final String UNUSED = "unused";

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = JacksonUtils.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                }
                catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }


    public static ObjectMapper enhancedObjectMapper() {
        return enhancedObjectMapper(getDefaultClassLoader());
    }

    /**
     * Factory for {@link ObjectMapper} instances with registered well-known modules
     * and disabled {@link MapperFeature#DEFAULT_VIEW_INCLUSION} and
     * {@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} features.
     * @param classLoader the {@link ClassLoader} for modules to register.
     * @return the {@link ObjectMapper} instance.
     */
    public static ObjectMapper enhancedObjectMapper(ClassLoader classLoader) {
        ObjectMapper objectMapper = JsonMapper.builder()
                .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .defaultDateFormat(new SimpleDateFormat("dd-MM-yyyy hh:mm"))
                .build();
        registerWellKnownModulesIfAvailable(objectMapper, classLoader);
        return objectMapper;
    }

    @SuppressWarnings("unchecked")
    private static void registerWellKnownModulesIfAvailable(ObjectMapper objectMapper, ClassLoader classLoader) {
        //objectMapper.registerModule(new JacksonMimeTypeModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());



    }

    private JacksonUtils() {
    }

}

