@echo off
echo Ejecutando análisis SonarQube para todos los microservicios...

REM Definir variables
set SONAR_HOST_URL=http://localhost:9000
REM Reemplaza esto con tu token real
set SONAR_TOKEN=squ_9b517fa03e591ebcb5cbd7dfb7c3a266f90f3176

REM Recorrer cada microservicio
for %%s in (user-service product-service order-service payment-service shipping-service favourite-service) do (
    echo.
    echo ===== Analizando %%s =====
    echo.
    
    cd %%s
    
    REM Ejecutar pruebas y generar informe de cobertura
    call mvnw clean test jacoco:report
    
    REM Ejecutar análisis SonarQube
    call mvnw sonar:sonar ^
        -Dsonar.projectKey=%%s ^
        -Dsonar.projectName=%%s ^
        -Dsonar.host.url=%SONAR_HOST_URL% ^
        -Dsonar.login=%SONAR_TOKEN% ^
        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
    
    cd ..
)

echo.
echo Análisis SonarQube completado para todos los microservicios.
echo Revisa los resultados en %SONAR_HOST_URL%
