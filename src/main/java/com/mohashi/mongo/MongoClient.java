package com.mohashi.mongo;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import io.vertx.ext.mongo.UpdateOptions;

public class MongoClient {
    private io.vertx.ext.mongo.MongoClient mongo;

    public static MongoClient createShared(Vertx vertx, JsonObject config) {
        return new MongoClient(io.vertx.ext.mongo.MongoClient.createShared(vertx, config));
    }

    private MongoClient(io.vertx.ext.mongo.MongoClient mongo) {
        this.mongo = mongo;
    }

    public MongoClient dropCollection(String collection, Handler<AsyncResult<Void>> resultHandler) {
        this.mongo.dropCollection(collection, resultHandler);
        return this;
    }

    public MongoClient find(String collection, JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        this.mongo.find(collection, query, resultHandler);
        return this;
    }

    public MongoClient save(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) {
        this.mongo.save(collection, document, resultHandler);
        return this;
    }

    public MongoClient updateCollectionWithOptions(String collection, JsonObject query, JsonObject update, UpdateOptions options,
                                          Handler<AsyncResult<MongoClientUpdateResult>> resultHandler) {
        this.mongo.updateCollectionWithOptions(collection, query, update, options, resultHandler);
        return this;
    }

    public MongoClient removeDocuments(String collection, JsonObject query, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
        this.mongo.removeDocuments(collection, query, resultHandler);
        return this;
    }
}