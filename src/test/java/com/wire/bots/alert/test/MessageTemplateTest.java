package com.wire.bots.alert.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.wire.bots.alert.model.Prometheus;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class MessageTemplateTest {
    // ------------------- Tests -------------------
    @Test
    public void prometheusAlert() throws IOException {
        Mustache mustache = compileTemplate("prometheus.mustache");

        Path path = FileSystems.getDefault().getPath("examples", "prometheus.json");
        String json = new String(Files.readAllBytes(path));
        ObjectMapper mapper = new ObjectMapper();
        Prometheus prometheus = mapper.readValue(json, Prometheus.class);
        String message = execute(mustache, prometheus);

        path = FileSystems.getDefault().getPath("examples", "prometheus.message");
        String expected = new String(Files.readAllBytes(path));

        Assert.assertEquals("Wrong message", expected, message);
    }

    // ------------------- Tests -------------------

    private Mustache compileTemplate(String template) {
        MustacheFactory mf = new DefaultMustacheFactory();
        String path = String.format("templates/%s", template);
        Mustache mustache = mf.compile(path);
        Assert.assertNotNull(path, mustache);
        return mustache;
    }

    private String execute(Mustache mustache, Object model) {
        try (StringWriter sw = new StringWriter()) {
            mustache.execute(new PrintWriter(sw), model).flush();
            return sw.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(mustache.getName(), false);
            return null;
        }
    }
}
