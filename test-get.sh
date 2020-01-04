#!/bin/sh

http $(oc get route/project-service -o jsonpath='{.spec.host}')/projects/$1