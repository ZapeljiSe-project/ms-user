# RSO: Microservice User for ZapeljiSe app

## Prerequisites

```bash
docker run -d --name pg-image-metadata -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=image-metadata -p 5432:5432 postgres:13

docker run -d --name pg-user -e POSTGRES_USER=vwzlirwy -e POSTGRES_PASSWORD=Y-i_Gt9JiZ0-qr0fhQu-Pj0oi1EToBk3 -e POSTGRES_DB=msuser -p 5432:5432 
postgres:13
```

## Build and run commands
```bash
mvn clean package
cd api/target
java -jar ms-user-api-1.0.0-SNAPSHOT.jar
```
Available at: localhost:8081/v1/users

## Docker commands
```bash
docker build -t novaslika .   
docker images
docker run novaslika    
docker tag novaslika prporso/novaslika   
docker push prporso/novaslika  
```
```bash
docker network ls  
docker network rm rso
docker network create rso
docker run -d --name pg-image-metadata -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=image-metadata -p 5432:5432 --network rso postgres:13
docker inspect pg-image-metadata
docker run -p 8081:8081 --network rso -e KUMULUZEE_DATASOURCES0_CONNECTIONURL=jdbc:postgresql://pg-image-metadata:5432/image-metadata prporso/ms-ride:2022-11-14-12-45-13
```

## Kubernetes
```bash
kubectl version
kubectl --help
kubectl get nodes
kubectl create -f ms-user-deployment.yaml 
kubectl apply -f ms-user-deployment.yaml 
kubectl get services 
kubectl get deployments
kubectl get pods
kubectl logs ms-user-deployment-6f59c5d96c-rjz46
kubectl delete pod ms-user-deployment-6f59c5d96c-rjz46
```

Kubernetes secrets configuration: https://kubernetes.io/docs/tasks/configmap-secret/managing-secret-using-kubectl/

```bash
kubectl create secret generic pg-pass-user --from-literal=password=<\mypassword>
kubectl get secrets
kubectl describe secret pg-pass-user
```

-----
## Other notes
**Basic commands:**
```bash
docker run -d --name pg-user -e POSTGRES_USER=vwzlirwy -e POSTGRES_PASSWORD=Y-i_Gt9JiZ0-qr0fhQu-Pj0oi1EToBk3 -e POSTGRES_DB=msuser -p 5432:5432 
postgres:13

docker build -t gh6987/rso-user:msuser .  ((tale 'msuser' je iz config.yaml))
docker push gh6987/rso-user:msuser
docker run -d --name pg-user -e POSTGRES_USER=dbuser_Diagnoses7782 -e POSTGRES_PASSWORD=T3Bo32fu7yW#Gj^%r!%^ -e POSTGRES_DB=msuser -p 5432:5432 postgres:13
mvn clean package...
```

-----
**Local URL's:**
- Backend API available at: http://localhost:8081/v1/users
- Swagger OpenAPI: http://localhost:8081/api-specs/ui
- Health Check (liveness): http://localhost:8081/health/live
- Health Check (readiness): http://localhost:8081/health/ready
- POST call to demonstrate unhealthy service: http://localhost:8081/v1/demo/break
- POST call to demonstrate healthy service: http://localhost:8081/v1/demo/repair

-----
**Production URL's:**
- Backend API available at: http://52.188.141.107/ms-user/v1/users
- Swagger OpenAPI: http://52.188.141.107/ms-user/api-specs/ui/?url=http://52.188.141.107/ms-user/openapi&oauth2RedirectUrl=http://52.188.141.107/ms-user/api-specs/ui/oauth2-redirect.html
- Health Check (liveness): http://52.188.141.107/ms-user/health/live
- Health Check (readiness): http://52.188.141.107/ms-user/health/ready
- POST call to demonstrate unhealthy service: http://52.188.141.107/ms-user/v1/demo/break
- POST call to demonstrate healthy service: http://52.188.141.107/ms-user/v1/demo/repair

-----
**Logs (queries):**

That states for the **Ride** microservice.

https://kibana.logit.io/s/af96aeac-0250-4f55-bb51-627ab4111040/app/discoverLegacy#/

- How many times **GET .../v1/town** has been called: <br> contextMap.method: getTowns AND (marker.name: ENTRY OR marker.name: EXIT)
- Check all info logs for this microservice. Notice also the same **uniqueRequestId**: <br> contextMap.applicationName:ms-ride-service AND level:INFO <br> Later, add also: <br> AND contextMap.uniqueRequestId: <\id here>
- Check all warning logs for this microservice (e.g. for method **GET .../v1/rides/<\id which doesn't exist>**): <br> contextMap.applicationName:ms-ride-service AND level:WARN 

-----
**Deploy:**

Run commands from 'connect' tab on Azure:

```bash
az login
az account set --subscription 54abde38-43ac-4774-bbb8-becd29f7593d
az aks get-credentials --resource-group rso-projekt --name kubernetesClusterRso
```

Run other commands (add namespace --> **-n ingress-nginx**):

```bash
kubectl create -f deployment-template.yaml
kubectl apply -f deployment-template.yaml
```

```bash
kubectl get nodes
kubectl get services 
kubectl get deployments
kubectl get pods
```

```bash
kubectl delete node ...
kubectl delete service ...
kubectl delete deployment ...
kubectl delete pod ...
```