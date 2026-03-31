## Déploiement du projet avec Docker

Ce document explique comment lancer la plateforme de films avec **Docker** et **docker compose**, en utilisant :
- un conteneur pour l'API Spring Boot
- un conteneur pour MySQL

Prérequis :
- Docker installé et fonctionnel (`docker ps` doit répondre sans erreur)
- `docker compose` disponible (intégré dans Docker Desktop ou paquet `docker-compose-plugin`)

---

## 1. Vue d'ensemble

- Le backend est packagé dans une image Docker à partir du `Dockerfile` à la racine du projet.
- La base de données MySQL est fournie par l'image officielle `mysql:8`.
- Le fichier `docker-compose.yml` orchestre les deux services :
  - service `app` (Spring Boot)
  - service `mysql` (base de données)
- Le profil Spring utilisé est `prod`, configuré pour pointer sur MySQL.

## 2. Variables d'environnement

Le `docker-compose.yml` lit les variables depuis un fichier `.env` à la racine du projet. Voir `.env.example` pour les variables requises.

| Variable | Description | Exemple |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Profil Spring | `prod` |
| `SPRING_DATASOURCE_URL` | URL JDBC MySQL | `jdbc:mysql://mysql:3306/movies_db` |
| `SPRING_DATASOURCE_USERNAME` | Utilisateur MySQL | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Mot de passe MySQL | `changeme` |
| `SPRING_DATABASE` | Nom de la base | `movies_db` |
| `JWT_SECRET` | Clé secrète JWT | (chaîne aléatoire longue) |
| `FRONTEND_BASE_URL` | URL du frontend (redirections OAuth2) | `https://mon-app.netlify.app` |
| `GOOGLE_CLIENT_ID` | Client ID Google OAuth2 | (depuis Google Cloud Console) |
| `GOOGLE_CLIENT_SECRET` | Client Secret Google OAuth2 | (depuis Google Cloud Console) |

## 3. Fichiers Docker importants

### 3.1. `Dockerfile` (backend)

- Étape 1 : image `maven:3.9-eclipse-temurin-17`
  - copie `pom.xml` puis `src/`
  - lance `mvn package -DskipTests` pour produire le JAR dans `target/`
- Étape 2 : image `eclipse-temurin:17-jre`
  - copie le JAR en `app.jar`
  - expose le port `8080`
  - démarre `java -jar app.jar`

### 3.2. `docker-compose.yml`

- Service `app` :
  - `build: .` → utilise le `Dockerfile` du projet
  - expose `8080:8080` (API disponible sur `http://localhost:8080`)
  - variables d'environnement lues depuis `.env`
  - `depends_on.mysql.condition: service_healthy` → démarre après que MySQL soit OK
- Service `mysql` :
  - image `mysql:8`
  - base de données et mot de passe configurés via `.env`
  - expose `3306:3306`
  - healthcheck pour attendre que MySQL soit prêt

---

## 4. Démarrer le projet avec Docker

### 4.1. Préparer le `.env`

```bash
cp .env.example .env
# Éditez .env avec vos valeurs
```

### 4.2. Construction et lancement

Depuis la racine du projet (`plateforme-films/`) :

```bash
docker compose up --build
```

Pour lancer en arrière-plan :

```bash
docker compose up --build -d
```

### 4.3. Vérifier que tout est en marche

```bash
docker ps
```

Vous devez voir :
- un conteneur `app` (backend Spring)
- un conteneur `mysql` (base de données)

---

## 5. Commandes utiles

| Action | Commande |
|--------|----------|
| Démarrer (+ rebuild) | `docker compose up --build` |
| Démarrer en arrière-plan (+ rebuild) | `docker compose up --build -d` |
| Démarrer (sans rebuild) | `docker compose up` |
| Démarrer en arrière-plan | `docker compose up -d` |
| Arrêter (sans supprimer) | `docker compose stop` |
| Conteneurs en cours | `docker ps` |
| Arrêter et supprimer | `docker compose down` |
| Logs | `docker compose logs -f app` |
