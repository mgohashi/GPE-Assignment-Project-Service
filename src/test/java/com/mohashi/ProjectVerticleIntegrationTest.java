package com.mohashi;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mohashi.model.Project;
import com.mohashi.model.ProjectStatus;
import com.mohashi.mongo.MongoClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.JacksonCodec;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PrepareForTest({ MongoClient.class })
public class ProjectVerticleIntegrationTest {

    @Mock
    private MongoClient mongo;
    private Vertx vertx;

    @SuppressWarnings("unchecked")
    @Before
    public void setup(TestContext context) {
        vertx = Vertx.vertx();

        vertx.exceptionHandler(context.exceptionHandler());

        System.out.println("MongoClient mock: " + mongo.toString());
        PowerMockito.mockStatic(MongoClient.class);
        PowerMockito.when(MongoClient.createShared(Mockito.any(), Mockito.any())).thenReturn(mongo);

        // Method simulation. Necessary to prevent erros in the bus
        AsyncResult<Void> asyncResultDrop = Mockito.mock(AsyncResult.class);
        Mockito.when(asyncResultDrop.succeeded()).thenReturn(true);

        Mockito.doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                ((Handler<AsyncResult<Void>>) invocation.getArgument(1)).handle(asyncResultDrop);
                return null;
            }
        }).when(mongo).dropCollection(Mockito.any(), Mockito.any());

        // Method simulation. Necessary to prevent erros in the bus
        AsyncResult<List<JsonObject>> asyncResultFind = Mockito.mock(AsyncResult.class);
        Mockito.when(asyncResultFind.result()).thenReturn(Collections.<JsonObject>emptyList());
        Mockito.when(asyncResultFind.succeeded()).thenReturn(true);

        Mockito.doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                ((Handler<AsyncResult<List<JsonObject>>>) invocation.getArgument(2)).handle(asyncResultFind);
                return null;
            }
        }).when(mongo).find(Mockito.any(), Mockito.any(), Mockito.any());

        // Method simulation. Necessary to prevent erros in the bus
        AsyncResult<String> asyncResultSave = Mockito.mock(AsyncResult.class);
        Mockito.when(asyncResultSave.result()).thenReturn("Ok");
        Mockito.when(asyncResultSave.succeeded()).thenReturn(true);

        Mockito.doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                ((Handler<AsyncResult<String>>) invocation.getArgument(2)).handle(asyncResultSave);
                return null;
            }
        }).when(mongo).save(Mockito.any(), Mockito.any(), Mockito.any());

        // Deploying verticles
        vertx.deployVerticle(ProjectVerticle.class, new DeploymentOptions(), context.asyncAssertSuccess());
    }

    @After
    public void stop(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testMongoClient() throws Exception {
        assertEquals(mongo, MongoClient.createShared(null, null));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void whenTheresProjects_thenReturnTheSameAmount(TestContext context) {
        Project original = new Project();
        original.setOwnerFirstName("Alex");
        original.setOwnerLastName("Sample");
        original.setProjectDescription("Project XPTO");
        original.setProjectStatus(ProjectStatus.COMPLETED);
        original.setProjectTitle("Project XPTO");
        original.setProjectId(1);

        List<Project> projectList = Arrays.asList(original);

        List<JsonObject> jsonProjectList = Arrays.asList(JsonObject.mapFrom(original));

        AsyncResult<List<JsonObject>> asyncResultFind = Mockito.mock(AsyncResult.class);
        Mockito.when(asyncResultFind.result()).thenReturn(jsonProjectList);
        Mockito.when(asyncResultFind.succeeded()).thenReturn(true);

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((Handler<AsyncResult<List<JsonObject>>>) invocation.getArgument(2))
                    .handle(asyncResultFind);
                return null;
            }
        }).when(mongo).find(Mockito.any(), Mockito.any(), Mockito.any());

        Async async = context.async();

        WebClient client = WebClient.create(vertx);

        client.get(8080, "localhost", "/projects").send(ar -> {
            if (ar.succeeded()) {
                HttpResponse<Buffer> response = ar.result();
                context.assertEquals(response.statusCode(), 200);
                context.assertTrue(response.headers().get("content-type").contains("application/json"));

                TypeReference<List<Project>> ref = new TypeReference<List<Project>>() {
                };
                List<Project> values = JacksonCodec.<List<Project>>decodeValue(ar.result().body(), ref);

                context.assertEquals(projectList, values, "Collections should be equal!");
            }
            async.complete();
        });

        async.await();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void whenTheresProjects_thenReturnTheProject(TestContext context) {
        Project original = new Project();
        original.setOwnerFirstName("Alex");
        original.setOwnerLastName("Sample");
        original.setProjectDescription("Project XPTO");
        original.setProjectStatus(ProjectStatus.COMPLETED);
        original.setProjectTitle("Project XPTO");
        original.setProjectId(1);

        List<JsonObject> jsonProjectList = Arrays.asList(JsonObject.mapFrom(original));

        AsyncResult<List<JsonObject>> asyncResultFind = Mockito.mock(AsyncResult.class);
        Mockito.when(asyncResultFind.result()).thenReturn(jsonProjectList);
        Mockito.when(asyncResultFind.succeeded()).thenReturn(true);

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((Handler<AsyncResult<List<JsonObject>>>) invocation.getArgument(2))
                    .handle(asyncResultFind);
                return null;
            }
        }).when(mongo).find(Mockito.any(), Mockito.any(), Mockito.any());

        Async async = context.async();

        WebClient client = WebClient.create(vertx);

        client.get(8080, "localhost", "/projects/" + original.getProjectId()).send(ar -> {
            if (ar.succeeded()) {
                HttpResponse<Buffer> response = ar.result();
                context.assertEquals(response.statusCode(), 200);
                context.assertTrue(response.headers().get("content-type").contains("application/json"));

                TypeReference<Project> ref = new TypeReference<Project>() {
                };
                Project returned = JacksonCodec.<Project>decodeValue(ar.result().body(), ref);

                context.assertNotNull(returned, "Value should not be null");
                context.assertEquals(original, returned, "Values should be equal!");
            }
            async.complete();
        });

        async.await();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void whenTheresProjectsWithStatusOpen_thenReturnTheProject(TestContext context) {
        Project original = new Project();
        original.setOwnerFirstName("Alex");
        original.setOwnerLastName("Sample");
        original.setProjectDescription("Project XPTO");
        original.setProjectStatus(ProjectStatus.OPEN);
        original.setProjectTitle("Project XPTO");
        original.setProjectId(1);

        List<Project> expected = Arrays.asList(original);

        List<JsonObject> jsonProjectList = Arrays.asList(JsonObject.mapFrom(original));

        AsyncResult<List<JsonObject>> asyncResultFind = Mockito.mock(AsyncResult.class);
        Mockito.when(asyncResultFind.result()).thenReturn(jsonProjectList);
        Mockito.when(asyncResultFind.succeeded()).thenReturn(true);

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((Handler<AsyncResult<List<JsonObject>>>) invocation.getArgument(2))
                    .handle(asyncResultFind);
                return null;
            }
        }).when(mongo).find(Mockito.any(), Mockito.any(), Mockito.any());

        Async async = context.async();

        WebClient client = WebClient.create(vertx);

        client.get(8080, "localhost", "/projects/status/" + original.getProjectStatus().name()).send(ar -> {
            if (ar.succeeded()) {
                HttpResponse<Buffer> response = ar.result();
                context.assertEquals(response.statusCode(), 200);
                context.assertTrue(response.headers().get("content-type").contains("application/json"));

                TypeReference<List<Project>> ref = new TypeReference<List<Project>>() {
                };
                List<Project> returned = JacksonCodec.<List<Project>>decodeValue(ar.result().body(), ref);

                context.assertEquals(expected, returned, "Collections should be equal!");
            }
            async.complete();
        });

        async.await();
    }
}