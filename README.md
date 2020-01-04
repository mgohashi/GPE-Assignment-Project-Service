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

# Deploy Project Service

```shell
$ mvn clean fabric8:deploy -Popenshift
```

# Tests on openshift

To use the following scripts you should have installed the [httpie](https://httpie.org/).

* Get freelancers
  ```shell
  $ ./test-get.sh
  ```
* Get freelancer
  ```shell
  $ ./test-get.sh 1
  ```
* Insert freelancer
  ```shell
  $ ./test-insert.sh
  ```
* Update freelancer
  ```shell
  $ ./test-update.sh 1
  ```
* Delete freelancer
  ```shell
  $ ./test-delete.sh 1
  ```

# Execute Unit Tests

```shell
$ mvn clean test
```