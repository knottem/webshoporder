1. create docker network for the project

```docker network create webshop-network```


2.  start up the database, change password to your own password

```docker run --network=webshop-network --name webshopdatabase -itd -p 3306:3306 ghcr.io/knottem/webshopdatabase```


3. start up the backend, change password to same as in step 2


```docker run --name webshoporder -itd --network=webshop-network -e DB_PASSWORD=password -e MYSQL_HOST=webshopdatabase -p 8080:8080 ghcr.io/knottem/webshoporder```

4. Sidenote

```docker build -t ghcr.io/knottem/webshopdatabase .```

```docker push ghcr.io/knottem/webshopdatabase```