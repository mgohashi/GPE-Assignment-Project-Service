# Deploy mongodb

oc new-app mongodb-persistent -p MONGODB_USER=mongo -p MONGODB_PASSWORD=mongo -p MONGODB_DATABASE=db -p MONGODB_ADMIN_PASSWORD=mongo

# Deploy Project Service

mvn clean fabric8:deploy -Popenshift

