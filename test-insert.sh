#!/bin/sh

echo '{
    "ownerEmail": "dsilva@gmail.com",
    "ownerFirstName": "Duff",
    "ownerLastName": "Silva",
    "projectDescription": "RHEV Implementation Project",
    "projectId": 6,
    "projectStatus": "OPEN",
    "projectTitle": "RHEV Implementation"
}' |  http -v POST $(oc get route/project-service -o jsonpath='{.spec.host}')/projects