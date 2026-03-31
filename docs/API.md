# Contrat API - Plateforme de films

## Base URL

`/api/v1`

## Authentification

Header pour les endpoints protégés:
```
Authorization: Bearer <token>
```

## Endpoints

### Auth

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/auth/register` | Inscription (409 si username déjà pris) |
| POST | `/auth/login` | Connexion → `{ "token": "..." }` |
| GET | `/oauth2/authorization/google` | Démarre le flux OAuth2 Google |

**Register** (body JSON):
```json
{
  "username": "string (2-50 car, obligatoire)",
  "email": "string (format email)",
  "password": "string (min 4 car, obligatoire)"
}
```

**Login** (body JSON):
```json
{
  "username": "string (obligatoire)",
  "password": "string (obligatoire)"
}
```

### Users

| Méthode | Endpoint | Auth | Description |
|---------|----------|------|-------------|
| GET | `/users/me` | Oui (JWT) | Profil de l'utilisateur connecté |
| GET | `/users/{id}` | Oui (JWT) | Infos publiques d'un utilisateur par id (404 si absent) |

**UserResponse** (réponse) :

```json
{
  "id": "number",
  "username": "string",
  "email": "string",
  "createdAt": "string (ISO-8601)"
}
```

### Movies

| Méthode | Endpoint | Auth | Description |
|---------|----------|------|-------------|
| GET | `/movies` | Non | Liste des films (filtrable par `?title=` ou `?genre=`) |
| GET | `/movies/{id}` | Non | Détail d'un film (404 si absent) |
| POST | `/movies` | Non | Ajouter un film (409 si doublon titre+réalisateur+année) |
| PUT | `/movies/{id}` | Non | Modifier un film (404 si absent) |
| DELETE | `/movies/{id}` | Non | Supprimer un film (404 si absent, 204 sinon) |

**MovieRequest** (body JSON):
```json
{
  "title": "string (obligatoire)",
  "director": "string (obligatoire)",
  "releaseYear": "number (obligatoire)",
  "genre": "string (obligatoire)",
  "synopsis": "string"
}
```

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
| PUT | `/movies/{id}/reviews/{reviewId}` | Oui (JWT) | Modifier une critique par son ID (403 si pas l'auteur) |
| PUT | `/movies/{id}/reviews/mine` | Oui (JWT) | Modifier ma critique existante |
| DELETE | `/movies/{id}/reviews/{reviewId}` | Oui (JWT) | Supprimer sa critique (403 si pas l'auteur) |

**ReviewRequest** (body JSON):
```json
{
  "content": "string (obligatoire)"
}
```

### Ratings

| Méthode | Endpoint | Auth | Description |
|---------|----------|------|-------------|
| GET | `/movies/{id}/ratings` | Non | Liste des notes d'un film |
| GET | `/movies/{id}/ratings/average` | Non | Note moyenne d'un film |
| POST | `/movies/{id}/ratings` | Oui (JWT) | Créer sa note (409 si déjà existante) |
| PUT | `/movies/{id}/ratings/{ratingId}` | Oui (JWT) | Modifier une note par son ID (403 si pas l'auteur) |
| PUT | `/movies/{id}/ratings/mine` | Oui (JWT) | Modifier ma note existante |

**RatingRequest** (body JSON):
```json
{
  "score": "number (1-5, obligatoire)"
}
```

## Codes de retour courants

| Code | Signification |
|------|---------------|
| 200 | Succès |
| 201 | Ressource créée |
| 204 | Succès sans contenu (delete) |
| 400 | Requête invalide (validation) |
| 401 | Non authentifié |
| 403 | Interdit (pas l'auteur) |
| 404 | Ressource introuvable |
| 409 | Conflit (doublon) |

## Documentation interactive

Swagger UI disponible en dev : `http://localhost:8080/docs`
