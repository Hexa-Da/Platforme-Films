# Contrat API - Plateforme de films

## Base URL

`/api/v1`

## Endpoints

### Auth

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/auth/register` | Inscription |
| POST | `/auth/login` | Connexion → `{ "token": "..." }` |
| GET  | `/auth/oauth2/google` | Démarre le flux de connexion avec Google |

**Register** (body JSON):
```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```

**Login** (body JSON):
```json
{
  "username": "string",
  "password": "string"
}
```

### Movies

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/movies` | Liste des films |
| GET | `/movies/{id}` | Détail d'un film |

**Movie** (réponse):
```json
{
  "id": "number",
  "title": "string",
  "director": "string",
  "releaseYear": "number",
  "genre": "string",
  "synopsis": "string"
}
```

### À venir (Juniors)

- GET/POST `/movies/{id}/reviews`
- GET/POST `/movies/{id}/ratings`
- GET `/users/{id}`

## Authentification

Header pour les endpoints protégés:
```
Authorization: Bearer <token>
```
