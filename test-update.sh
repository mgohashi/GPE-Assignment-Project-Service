echo '{
    "ownerEmail": "dsilva@gmail.com",
    "ownerFirstName": "Duff-2",
    "ownerLastName": "Silva-2",
    "projectDescription": "RHEV-2 Implementation Project",
    "projectId": 6,
    "projectStatus": "OPEN",
    "projectTitle": "RHEV-2 Implementation"
}' |  http -v PUT $(oc get route/project-service -o jsonpath='{.spec.host}')/projects/6