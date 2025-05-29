#!/bin/bash

# Script to run SonarQube analysis for all microservices
echo "Running SonarQube analysis for all microservices..."

# Define variables
SONAR_HOST_URL="http://localhost:9000"
SONAR_TOKEN="squ_9b517fa03e591ebcb5cbd7dfb7c3a266f90f3176"

# List of microservices
SERVICES=(
    "user-service"
    "product-service"
    "order-service"
    "payment-service"
    "shipping-service"
    "favourite-service"
    "api-gateway"
    "cloud-config"
    "service-discovery"
    "proxy-client"
)

# Loop through each service
for SERVICE in "${SERVICES[@]}"; do
    echo ""
    echo "===== Analyzing $SERVICE ====="
    echo ""
    
    # Change to service directory
    cd "$SERVICE" || { echo "Failed to change to $SERVICE directory"; continue; }
    
    # Run tests and generate coverage report
    echo "Running tests and generating coverage report..."
    ./mvnw clean test jacoco:report
    
    # Run SonarQube analysis
    echo "Running SonarQube analysis..."
    ./mvnw sonar:sonar \
        -Dsonar.projectKey="$SERVICE" \
        -Dsonar.projectName="$SERVICE" \
        -Dsonar.host.url="$SONAR_HOST_URL" \
        -Dsonar.login="$SONAR_TOKEN" \
        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
    
    # Return to parent directory
    cd ..
done

echo ""
echo "SonarQube analysis completed for all microservices."
echo "Check results at $SONAR_HOST_URL"
