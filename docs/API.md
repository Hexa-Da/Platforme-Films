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

### Reviews

| Méthode | Endpoint | Auth | Description |
|---------|----------|------|-------------|
| GET | `/movies/{id}/reviews` | Non | Liste des critiques d'un film |
| POST | `/movies/{id}/reviews` | Oui (JWT) | Ajouter une critique (409 si déjà existante) |
| PUT | `/movies/{id}/reviews/{reviewId}` | Oui (JWT) | Modifier sa critique existante |
| DELETE | `/movies/{id}/reviews/{reviewId}` | Oui (JWT) | Supprimer sa critique |

**ReviewRequest** (body JSON):
```json
{
  "content": "string"
}
```

### Ratings

| Méthode | Endpoint | Auth | Description |
|---------|----------|------|-------------|
| GET | `/movies/{id}/ratings` | Non | Liste des notes d'un film |
| GET | `/movies/{id}/ratings/average` | Non | Note moyenne d'un film |
| POST | `/movies/{id}/ratings` | Oui (JWT) | Créer sa note (409 si déjà existante) |
| PUT | `/movies/{id}/ratings/{ratingId}` | Oui (JWT) | Modifier sa note existante |

**RatingRequest** (body JSON):
```json
{
  "score": 4
}
```

## Authentification

Header pour les endpoints protégés:
```
Authorization: Bearer <token>
```
