# Despliegue en Kubernetes con Minikube

Este documento proporciona instrucciones para desplegar la aplicación de microservicios e-commerce en Kubernetes utilizando Minikube.

## Prerrequisitos

- [Docker](https://www.docker.com/products/docker-desktop)
- [Minikube](https://minikube.sigs.k8s.io/docs/start/)
- [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
- [Java 11 JDK](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
- [Maven](https://maven.apache.org/download.cgi)

## Estructura de los archivos

Los archivos de configuración de Kubernetes se encuentran en el directorio `k8s/`:

- `00-namespace.yaml`: Define el namespace `ecommerce`
- `01-configmap.yaml`: Configuración centralizada para todos los servicios
- `02-service-discovery.yaml`: Eureka Server para registro y descubrimiento de servicios
- `03-cloud-config.yaml`: Servidor de configuración centralizada
- `04-api-gateway.yaml`: API Gateway para enrutamiento y proxy
- `05-user-service.yaml` hasta `11-proxy-client.yaml`: Microservicios individuales

## Pasos para el despliegue

### 1. Construir imágenes Docker

Para construir todas las imágenes Docker, ejecute:

```bash
build-docker-images.bat
```

Este script compila todos los proyectos y crea las imágenes Docker necesarias.

### 2. Desplegar en Minikube

Para desplegar la aplicación en Minikube, ejecute:

```bash
deploy-to-minikube.bat
```

Este script realiza los siguientes pasos:

1. Inicia Minikube
2. Configura Docker para usar el daemon de Docker de Minikube
3. Aplica las configuraciones de Kubernetes en orden
4. Proporciona las URLs para acceder a los servicios expuestos

### 3. Verificar el despliegue

Para verificar que todos los pods están funcionando correctamente:

```bash
kubectl get pods -n ecommerce
```

Para ver los logs de un pod específico:

```bash
kubectl logs -n ecommerce <nombre-del-pod>
```

### 4. Acceder a los servicios

Los servicios expuestos son:

- API Gateway: Accesible a través de la URL proporcionada por `minikube service api-gateway -n ecommerce --url`
- Proxy Client: Accesible a través de la URL proporcionada por `minikube service proxy-client -n ecommerce --url`

### 5. Limpieza

Para eliminar todos los recursos creados:

```bash
kubectl delete namespace ecommerce
```

## Arquitectura en Kubernetes

La arquitectura de despliegue sigue estos principios:

1. **Service Discovery (Eureka)** y **Cloud Config** se despliegan primero como servicios fundamentales
2. Los microservicios de negocio se despliegan después y dependen de los servicios fundamentales
3. Los servicios de entrada (API Gateway y Proxy Client) se exponen como NodePort para acceso externo
4. La comunicación entre servicios se realiza utilizando los nombres DNS internos de Kubernetes

## Configuración y variables de entorno

Las variables de entorno y configuraciones se centralizan en el ConfigMap `ecommerce-config`. Esto permite:

1. Cambiar la configuración sin reconstruir las imágenes
2. Mantener la configuración consistente entre todos los servicios
3. Establecer las URL correctas para la comunicación entre servicios

## Monitoreo y salud

Cada servicio expone un endpoint `/actuator/health` que se utiliza para:

1. Readiness probes: Determinar cuándo un pod está listo para recibir tráfico
2. Liveness probes: Verificar continuamente que el servicio esté funcionando correctamente

## Recursos y escalado

Cada despliegue tiene configurados límites de recursos para:

- CPU: 250m (request) / 500m (limit)
- Memoria: 512Mi (request) / 768Mi (limit)

Para escalar horizontalmente cualquier servicio:

```bash
kubectl scale deployment -n ecommerce <nombre-del-servicio> --replicas=<número-de-réplicas>
```
