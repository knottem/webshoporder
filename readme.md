# Webshop Order

1. Pushing to GitHub packages.

    ```docker build -t ghcr.io/knottem/webshoporder .```

    ```docker push ghcr.io/knottem/webshoporder```
<br><br><br>

2. start with a docker compose file, swap out the <databasepassword> with your own password.

    name the file docker-compose.yml

    The healthcheck is stupid, but it works.

```yml
version: "3.9"

networks:
  webshop-network:
    driver: bridge

services:
  webshopdatabase:
    image: ghcr.io/knottem/webshopdatabase:latest
    container_name: databasecontainer
    networks:
      - webshop-network
    ports:
      - "3306:3306"
    healthcheck:
      test: ["CMD-SHELL", "exit"]
      interval: 20s
      timeout: 30s

  webshoporder:
    image: ghcr.io/knottem/webshoporder:latest
    container_name: ordercontainer
    networks:
      - webshop-network
    environment:
      - DB_PASSWORD=<databasepassword>
      - MYSQL_HOST=webshopdatabase
    ports:
      - "9090:8080"
    depends_on:
      webshopdatabase:
        condition: service_healthy
```

3. Start the containers with docker compose.

```docker-compose up -d```
