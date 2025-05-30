@echo off
REM Script para desplegar la aplicación en Minikube

echo Iniciando Minikube...
minikube start --cpus=8 --memory=3700m --disk-size=20g

echo Configurando Docker para usar el Docker daemon de Minikube...
@FOR /f "tokens=*" %%i IN ('minikube -p minikube docker-env --shell cmd') DO @%%i

echo Aplicando namespace para ecommerce...
kubectl apply -f k8s/00-namespace.yaml

echo Desplegando Zipkin para trazabilidad...
docker pull openzipkin/zipkin:latest
kubectl create deployment zipkin --image=openzipkin/zipkin:latest -n ecommerce
kubectl expose deployment zipkin --type=NodePort --port=9411 -n ecommerce

echo Compilando todos los microservicios...
call mvnw clean package -DskipTests

echo Construyendo imágenes Docker en el entorno de Minikube...
cd service-discovery
docker build -t service-discovery:0.1.0 .
cd ..

cd cloud-config
docker build -t cloud-config:0.1.0 .
cd ..

cd api-gateway
docker build -t api-gateway:0.1.0 .
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

echo Aplicando configuraciones restantes de Kubernetes...
kubectl apply -f k8s/01-configmap.yaml

echo Desplegando servicios de infraestructura...
kubectl apply -f k8s/02-service-discovery.yaml
kubectl wait --for=condition=available --timeout=130s deployment/service-discovery -n ecommerce
kubectl apply -f k8s/03-cloud-config.yaml
kubectl wait --for=condition=available --timeout=160s deployment/cloud-config -n ecommerce

echo Desplegando servicios de negocio...
kubectl apply -f k8s/05-user-service.yaml
kubectl wait --for=condition=available --timeout=150s deployment/user-service -n ecommerce
kubectl apply -f k8s/06-product-service.yaml
kubectl wait --for=condition=available --timeout=140s deployment/product-service -n ecommerce
kubectl apply -f k8s/07-order-service.yaml
kubectl wait --for=condition=available --timeout=140s deployment/order-service -n ecommerce
kubectl apply -f k8s/08-payment-service.yaml
kubectl wait --for=condition=available --timeout=140s deployment/payment-service -n ecommerce
kubectl apply -f k8s/09-shipping-service.yaml
kubectl wait --for=condition=available --timeout=140s deployment/shipping-service -n ecommerce
kubectl apply -f k8s/10-favourite-service.yaml
kubectl wait --for=condition=available --timeout=140s deployment/favourite-service -n ecommerce

echo Desplegando servicios de frontend...
kubectl apply -f k8s/04-api-gateway.yaml
kubectl apply -f k8s/11-proxy-client.yaml

echo Obteniendo URLs de acceso...
echo API Gateway: 
minikube service api-gateway -n ecommerce --url

echo Proxy Client:
minikube service proxy-client -n ecommerce --url

echo Zipkin (para trazabilidad):
minikube service zipkin -n ecommerce --url

echo Despliegue completado!
echo Para ver el estado de los pods:
echo kubectl get pods -n ecommerce

echo Para acceder al dashboard de Zipkin:
echo Abre en tu navegador: http://[URL de Zipkin arriba]/zipkin/

echo Nota: Asegúrate de que tus microservicios tengan configurada la propiedad:
echo spring.zipkin.baseUrl=http://zipkin:9411/ en su configuración para enviar trazas a Zipkin.
