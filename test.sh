echo '{
    "ownerEmail": "dsilva@gmail.com",
    "ownerFirstName": "Duff",
    "ownerLastName": "Silva",
    "projectDescription": "RHEV Implementation Project",
    "projectId": 6,
    "projectStatus": "OPEN",
    "projectTitle": "RHEV Implementation"
}' |  http -v POST project-service-assignment-cld-ntv.apps.na311.openshift.opentlc.com/api/projects