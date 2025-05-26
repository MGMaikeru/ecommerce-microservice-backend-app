@echo off
REM Script para compilar los JAR y crear las imágenes Docker

echo Compilando todos los microservicios...
call mvnw clean package -DskipTests

echo Construyendo imágenes Docker...

cd service-discovery
docker build -t service-discovery:0.1.0 .
cd ..

cd api-gateway
docker build -t api-gateway:0.1.0 .
cd ..

cd cloud-config
docker build -t cloud-config:0.1.0 .
cd ..

cd user-service
docker build -t user-service:0.1.0 .
cd ..

cd product-service
docker build -t product-service:0.1.0 .
cd ..

cd order-service
docker build -t order-service:0.1.0 .
cd ..

cd payment-service
docker build -t payment-service:0.1.0 .
cd ..

cd shipping-service
docker build -t shipping-service:0.1.0 .
cd ..

cd favourite-service
docker build -t favourite-service:0.1.0 .
cd ..

cd proxy-client
docker build -t proxy-client:0.1.0 .
cd ..

echo Todas las imágenes Docker han sido construidas.
echo Para desplegar en Minikube, ejecute:
echo minikube start
echo eval $(minikube docker-env)
echo kubectl apply -f k8s/00-namespace.yaml
echo kubectl apply -f k8s/01-configmap.yaml
echo kubectl apply -f k8s/
