package com.himank.automation.excelreader.config;

import org.reflections.Reflections;

import java.util.Properties;
import java.util.Set;

import com.himank.automation.annotations.InnerModelHeader;
import com.himank.automation.annotations.ModelHeader;
import com.himank.automation.properties.ApplicationProperties;

public class ModelNames {

    private static Set<Class<?>> allModelClasses;
    private static Set<Class<?>> innerModelClasses;

    public static Set<Class<?>> getModelClasses() {
        if (allModelClasses != null && !allModelClasses.isEmpty())
            return allModelClasses;

        Properties properties = ApplicationProperties.getProperties();
        Reflections reflections = new Reflections(properties.getProperty("model.classes.path"));

        allModelClasses = reflections.getTypesAnnotatedWith(ModelHeader.class);
        return allModelClasses;
    }

    public static Set<Class<?>> getInnerModelClasses() {
        if (innerModelClasses != null && !innerModelClasses.isEmpty())
            return innerModelClasses;

        Properties properties = ApplicationProperties.getProperties();
        Reflections reflections = new Reflections(properties.getProperty("model.classes.path"));

        innerModelClasses = reflections.getTypesAnnotatedWith(InnerModelHeader.class);
        return innerModelClasses;
    }

}
