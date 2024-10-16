#!/bin/bash

# Array con los nombres de las carpetas/proyectos a construir
projects=("orchestrator-webflux" "ResolveEnigmaAp" "ResolveEnigmaAp2" "ResolveEnigmaAp3" "batch" "webhook")

# Iterar sobre cada proyecto y ejecutar mvn clean package
for project in "${projects[@]}"; do
    echo "Building $project..."
    cd $project || { echo "Failed to enter $project directory"; exit 1; }

    # Ejecutar mvn clean package
    mvn clean package -DskipTests
    if [ $? -ne 0 ]; then
        echo "Build failed for $project"
        exit 1
    fi

    # Regresar al directorio raíz
    cd ..
    echo "$project built successfully!"
done

echo "All projects built successfully. Starting Docker Compose..."

# Ejecutar docker-compose después del build
docker compose down
docker compose up --build