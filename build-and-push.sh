#!/bin/bash
# Script para construir imágenes Docker y publicarlas en Google Container Registry

# Variables
PROJECT_ID=$1
if [ -z "$PROJECT_ID" ]; then
  echo "Error: Debes proporcionar el ID del proyecto de GCP como primer argumento"
  echo "Uso: ./build-and-push.sh [PROJECT_ID]"
  exit 1
fi

# Compilar los proyectos con Maven
echo "Compilando todos los microservicios..."
./mvnw clean package -DskipTests

# Configurar Docker para autenticarse con GCR
echo "Configurando Docker para autenticarse con Google Container Registry..."
gcloud auth configure-docker

# Lista de microservicios
SERVICES=(
  "service-discovery"
  "cloud-config"
  "api-gateway"
  "user-service"
  "product-service"
  "order-service"
  "payment-service"
  "shipping-service"
  "favourite-service"
  "proxy-client"
)

# Construir y publicar cada imagen
for SERVICE in "${SERVICES[@]}"; do
  echo "Construyendo y publicando $SERVICE..."
  cd $SERVICE
  
  # Construir la imagen
  docker build -t $SERVICE:0.1.0 .
  
  # Etiquetar para GCR
  docker tag $SERVICE:0.1.0 gcr.io/$PROJECT_ID/$SERVICE:0.1.0
  
  # Publicar en GCR
  docker push gcr.io/$PROJECT_ID/$SERVICE:0.1.0
  
  cd ..
  echo "$SERVICE publicado con éxito en gcr.io/$PROJECT_ID/$SERVICE:0.1.0"
done

echo "Todas las imágenes han sido construidas y publicadas en Google Container Registry."
echo "Ahora puedes proceder a desplegar la infraestructura con Terraform."
