## Déploiement du projet avec Docker

Ce document explique comment lancer la plateforme de films avec **Docker** et **docker compose**, en utilisant :
- un conteneur pour l’API Spring Boot
- un conteneur pour MySQL

Prérequis :
- Docker installé et fonctionnel (`docker ps` doit répondre sans erreur)
- `docker compose` disponible (intégré dans Docker Desktop ou paquet `docker-compose-plugin`)

---

## 1. Vue d’ensemble

- Le backend est packagé dans une image Docker à partir du `Dockerfile` à la racine du projet.
- La base de données MySQL est fournie par l’image officielle `mysql:8`.
- Le fichier `docker-compose.yml` orchestre les deux services :
  - service `app` (Spring Boot)
  - service `mysql` (base de données)
- Le profil Spring utilisé est `prod`, configuré pour pointer sur MySQL.

## 2. Fichiers Docker importants

### 2.1. `Dockerfile` (backend)

- Étape 1 : image `maven:3.9-eclipse-temurin-17`
  - copie `pom.xml` puis `src/`
  - lance `mvn package -DskipTests` pour produire le JAR dans `target/`
- Étape 2 : image `eclipse-temurin:17-jre`
  - copie le JAR en `app.jar`
  - expose le port `8080`
  - démarre `java -jar app.jar`

### 2.2. `docker-compose.yml`

- Service `app` :
  - `build: .` → utilise le `Dockerfile` du projet
  - expose `8080:8080` (API disponible sur `http://localhost:8080`)
  - variables d’environnement :
    - `SPRING_PROFILES_ACTIVE=prod`
    - `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
    - `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET` (OAuth2 Google)
  - `depends_on.mysql.condition: service_healthy` → démarre après que MySQL soit OK
- Service `mysql` :
  - image `mysql:8`
  - variables :
    - `MYSQL_ROOT_PASSWORD=root`
    - `MYSQL_DATABASE=movies_db`
  - expose `3306:3306`
  - healthcheck pour attendre que MySQL soit prêt

---

## 3. Variables d’environnement et secrets

Certaines variables sont obligatoires pour que le déploiement Docker fonctionne correctement :

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME` 
- `SPRING_DATASOURCE_PASSWORD`)
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`

---

## 4. Démarrer le projet avec Docker

### 4.1. Construction et lancement

Depuis la racine du projet (`plateforme-films/`) :

```bash
docker compose up --build
```

Explications :
- `--build` force la reconstruction de l’image backend si le code a changé.
- Les services démarrent dans le terminal courant (logs visibles en direct).

Pour lancer en arrière-plan :

```bash
docker compose up --build -d
```

### 4.2. Vérifier que tout est en marche

Lister les conteneurs :

```bash
docker ps
```

Tu dois voir au moins :
- un conteneur `app` (backend Spring)
- un conteneur `mysql` (base de données)

---

## 5. Commandes utiles

- **Démarrer tout (et reconstruire l'image)** :

```bash
docker compose up --build
```

- **Démarrer en arrière-plan (et reconstruire l'image)** :

```bash
docker compose up --build -d
```

- **Démarrer (sans reconstruire, si les images sont déjà prêtes)** :

```bash
docker compose up
```

- **Démarrer en arrière-plan (sans reconstruire)** :

```bash
docker compose up -d
```

- **Arrêter les conteneurs (sans les supprimer, pour les relancer plus tard)** :

```bash
docker compose stop
```

- **Voir les conteneurs en cours** :

```bash
docker ps
```

- **Arrêter et supprimer les conteneurs** :

```bash
docker compose down
```


