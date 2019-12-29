# Deploy mongodb

```shell
oc new-app mongodb-persistent -p MONGODB_USER=mongo \
                              -p MONGODB_PASSWORD=mongo \
                              -p MONGODB_DATABASE=db \
                              -p MONGODB_ADMIN_PASSWORD=mongo \
                              -p DATABASE_SERVICE_NAME=project-mongodb
```

# Deploy Project Service

```shell
mvn clean fabric8:deploy -Popenshift
```
