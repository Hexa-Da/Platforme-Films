# Plateforme de films

API REST et frontend React pour une plateforme de films (style Letterboxd). Spring Boot, Spring Security JWT, MySQL, Docker.

## Prérequis

- Java 17
- Maven (ou `./mvnw`)
- Node.js 18+ (pour le frontend)
- Docker (optionnel)

## Lancer l'API

```bash
./mvnw spring-boot:run
```

L'API démarre sur http://localhost:8080

- Documentation : http://localhost:8080/docs (Swagger)
- Page d'accueil : http://localhost:8080

## Lancer le frontend React

```bash
cd frontend
npm install
npm run dev
```

Le frontend démarre sur http://localhost:5173

## Tests

```bash
./mvnw test
```

## Docker

```bash
docker-compose up --build
```

L'API sera disponible sur http://localhost:8080 avec MySQL sur le port 3306.

## Variables d'environnement

- `SPRING_PROFILES_ACTIVE` : `dev` (H2), `test` (H2), `prod` (MySQL)
- `JWT_SECRET` : secret pour signer les tokens (prod)
- `SPRING_DATASOURCE_URL` : URL JDBC MySQL (prod)

## Structure

- `src/main/java` : Backend Spring Boot
- `frontend/` : Application React (Vite)
- `docs/API.md` : Contrat API
