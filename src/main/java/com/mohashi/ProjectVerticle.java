package com.mohashi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.mohashi.model.Project;
import com.mohashi.model.ProjectStatus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class ProjectVerticle extends AbstractVerticle {

    private Logger logger = LoggerFactory.getLogger(ProjectVerticle.class);
    private MongoClient client;

    @Override
    public void start(Promise<Void> promise) throws Exception {
        JsonObject config = new JsonObject().put("host", System.getenv("MONGODB_SERVICE_HOST"))
                .put("port", Integer.valueOf(System.getenv("MONGODB_SERVICE_PORT"))).put("db_name", "db")
                .put("username", "mongo").put("password", "mongo");

        client = MongoClient.createShared(vertx, config);

        createProjects();

        // Create a router object.
        Router router = Router.router(vertx);

        // Bind "/" to our hello message.
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/html").end("<h1>Hello from my first Vert.x 3 application</h1>");
        });

        router.route("/health").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/html").end("<h1>App is working!</h1>");
        });

        router.route("/assets/*").handler(StaticHandler.create("assets"));

        router.route("/api/projects*").handler(BodyHandler.create());
        router.get("/api/projects").handler(this::getAll);
        router.get("/api/projects/status/:status").handler(this::getByStatus);
        router.post("/api/projects").handler(this::add);
        router.get("/api/projects/:id").handler(this::get);
        router.put("/api/projects/:id").handler(this::update);
        router.delete("/api/projects/:id").handler(this::delete);

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx.createHttpServer().requestHandler(router).listen(
                // Retrieve the port from the configuration,
                // default to 8080.
                config().getInteger("http.port", 8080), result -> {
                    if (result.succeeded()) {
                        promise.complete();
                    } else {
                        promise.fail(result.cause());
                    }
                });
    }

    private void createProjects() {
        client.dropCollection("projects", res -> {
            if (res.succeeded()) {
                logger.info("Collection droped: {}", res.result());
            } else {
                logger.error("Error", res.cause());
            }
        });

        client.find("projects", new JsonObject(), res -> {
            if (res.succeeded() && res.result().isEmpty()) {
                List<Project> projects = new ArrayList<>();
                projects.add(newProject(1, "Joao", "Silva", "jsilva@gmail.com", "RH-SSO Implementation",
                        "RH-SSO Implementation Project", ProjectStatus.OPEN));
                projects.add(newProject(2, "Luis", "Silva", "lsilva@gmail.com", "AMQ Implementation",
                        "AMQ Implementation Project", ProjectStatus.COMPLETED));
                projects.add(newProject(3, "Armando", "Silva", "asilva@gmail.com", "JDG Implementation",
                        "JDG Implementation Project", ProjectStatus.IN_PROGRESS));
                projects.add(newProject(4, "Jose", "Silva", "jose.silva@gmail.com", "JDV Implementation",
                        "JDV Implementation Project", ProjectStatus.CANCELLED));
                projects.add(newProject(5, "Inacio", "Silva", "isilva@gmail.com", "OCP Implementation",
                        "OCP Implementation Project", ProjectStatus.OPEN));

                for (Project project : projects) {
                    client.save("projects", JsonObject.mapFrom(project), res2 -> {
                        if (res2.succeeded()) {
                            logger.info("Doc. saved: {}", res2.result());
                        } else {
                            logger.error("Error", res2.cause());
                        }
                    });
                }
            } else {
                logger.info("Data already created!");
            }
        });
    }

    public Project newProject(int projectId, String ownerFirstName, String ownerLastName, String ownerEmail,
            String projectTitle, String projectDescription, ProjectStatus projectStatus) {
        return new Project(projectId, ownerFirstName, ownerLastName, ownerEmail, projectTitle, projectDescription,
                projectStatus);
    }

    public void getAll(RoutingContext context) {
        client.find("projects", new JsonObject(), res -> {
            if (res.succeeded()) {
                List<Project> projects = new LinkedList<>();

                for (JsonObject json : res.result()) {
                    Project project = json.mapTo(Project.class);
                    projects.add(project);
                }

                context.response().putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(projects));
            } else {
                logger.error("Error", res.cause());
                context.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(500)
                        .end();
            }
        });
    }

    public void getByStatus(RoutingContext context) {
        String status = context.request().getParam("status");

        if (status == null) {
            context.response().setStatusCode(400).end();
        } else {
            JsonObject query = new JsonObject().put("projectStatus", status);

            client.find("projects", query, res -> {
                if (res.succeeded()) {
                    if (res.result().isEmpty()) {
                        context.response().putHeader("content-type", "application/json; charset=utf-8")
                                .setStatusCode(404).end();
                    } else {
                        List<Project> projects = new LinkedList<>();

                        for (JsonObject json : res.result()) {
                            Project project = json.mapTo(Project.class);
                            projects.add(project);
                        }

                        context.response().putHeader("content-type", "application/json; charset=utf-8")
                                .setStatusCode(200).end(Json.encodePrettily(projects));
                    }
                } else {
                    logger.error("Error", res.cause());
                    context.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(500)
                            .end();
                }
            });
        }
    }

    public void add(RoutingContext context) {
        final Project project = Json.decodeValue(context.getBodyAsString(), Project.class);

        if (project == null) {
            context.response().setStatusCode(400).end();
        } else {
            client.find("projects", new JsonObject().put("projectId", project.getProjectId()), res -> {
                if (res.succeeded() && res.result().isEmpty()) {
                    client.save("projects", JsonObject.mapFrom(project), res2 -> {
                        if (res2.succeeded()) {
                            context.response().setStatusCode(202).end();
                        } else {
                            res2.cause().printStackTrace();
                            context.response().putHeader("content-type", "application/json; charset=utf-8")
                                    .setStatusCode(500).end();
                        }
                    });
                } else {
                    logger.error("Project with the same ID already exists!");
                    context.response().setStatusCode(400).end();
                }
            });
        }
    }

    public void get(RoutingContext context) {
        String id = context.request().getParam("id");
        if (id == null) {
            context.response().setStatusCode(400).end();
        } else {
            Integer idAsInteger = Integer.valueOf(id);

            JsonObject query = new JsonObject().put("projectId", idAsInteger);

            client.find("projects", query, res -> {
                if (res.succeeded()) {
                    if (res.result().isEmpty()) {
                        context.response().putHeader("content-type", "application/json; charset=utf-8")
                                .setStatusCode(404).end();
                    } else {
                        for (JsonObject json : res.result()) {
                            context.response().putHeader("content-type", "application/json; charset=utf-8")
                                    .end(Json.encodePrettily(json.mapTo(Project.class)));
                            break;
                        }
                    }
                } else {
                    logger.error("Error", res.cause());
                    context.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(500)
                            .end();
                }
            });
        }
    }

    public void update(RoutingContext context) {
        final Project project = Json.decodeValue(context.getBodyAsString(), Project.class);

        if (project == null) {
            context.response().setStatusCode(400).end();
        } else {
            JsonObject query = new JsonObject().put("projectId", project.getProjectId());

            UpdateOptions options = new UpdateOptions().setMulti(true);
            JsonObject update = new JsonObject().put("$set", JsonObject.mapFrom(project));

            client.updateCollectionWithOptions("projects", query, update, options, res -> {
                if (res.succeeded()) {
                    context.response().setStatusCode(202).end();
                } else {
                    logger.error("Error", res.cause());
                    context.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(500)
                            .end();
                }
            });
        }
    }

    public void delete(RoutingContext context) {
        String id = context.request().getParam("id");
        if (id == null) {
            context.response().setStatusCode(400).end();
        } else {
            JsonObject query = new JsonObject().put("projectId", id);

            client.removeDocuments("projects", query, res -> {
                if (res.succeeded()) {
                    context.response().setStatusCode(202).end();
                } else {
                    logger.error("Error", res.cause());
                    context.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(500)
                            .end();
                }
            });
        }
    }
}
