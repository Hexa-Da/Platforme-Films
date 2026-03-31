# Structure du package `plateformefilms`

Ce package contient l'application backend de la plateforme de films — API REST, authentification JWT/OAuth2 et logique métier.

## `config/`

Configuration Spring et intégration avec Spring Security.

| Fichier | Rôle |
|---------|------|
| `SecurityConfig` | Configuration de la chaîne de sécurité : règles d'autorisation, CORS, JWT filter, OAuth2, session stateless |
| `PasswordEncoderConfig` | Bean BCrypt pour le hachage des mots de passe |
| `UserDetailsServiceImpl` | Implémentation de `UserDetailsService` pour charger un utilisateur par username |
| `GlobalExceptionHandler` | `@RestControllerAdvice` : gestion centralisée des erreurs (404, 403, 409, 401) |

## `controller/`

Contrôleurs REST.

| Fichier | Rôle |
|---------|------|
| `AuthController` | Endpoints auth : `POST /register`, `POST /login` → JWT |
| `MovieController` | CRUD films : GET, POST, PUT, DELETE `/movies` |
| `ReviewController` | Critiques : GET, POST, PUT, DELETE `/movies/{id}/reviews` |
| `RatingController` | Notes : GET, POST, PUT `/movies/{id}/ratings` |
| `UserController` | Profil : `GET /users/me` |
| `HtmlController` | Pages web : `/`, `/index.html` → Thymeleaf |

## `dto/`

Data Transfer Objects pour les requêtes/réponses API.

| Fichier | Rôle |
|---------|------|
| `RegisterRequest` | DTO d'inscription (username, email, password) avec validation |
| `LoginRequest` | DTO de connexion (username, password) avec validation |
| `AuthResponse` | Réponse auth : `{ token }` |
| `MovieRequest` | DTO création/modification de film avec validation |
| `ReviewRequest` | DTO création de critique (`@NotBlank content`) |
| `ReviewResponse` | DTO réponse critique (id, username, content, createdAt) |
| `RatingRequest` | DTO création de note (`@Min(1) @Max(5) score`) |
| `RatingResponse` | DTO réponse note (id, username, score) |
| `UserResponse` | DTO réponse profil utilisateur |

## `exception/`

Exceptions métier typées, gérées par `GlobalExceptionHandler`.

| Fichier | Rôle |
|---------|------|
| `NotFoundException` | Ressource introuvable → 404 |
| `DuplicateException` | Doublon (review/rating déjà existant) → 409 |
| `ForbiddenException` | Action interdite (pas l'auteur) → 403 |

## `model/`

Entités JPA mappées aux tables de la base de données.

| Fichier | Rôle |
|---------|------|
| `User` | Utilisateur (username unique, email unique, password) |
| `Movie` | Film (title, director, releaseYear, genre, synopsis) — contrainte unicité titre+réalisateur+année |
| `Review` | Critique d'un film par un utilisateur — contrainte unicité user+movie |
| `Rating` | Note d'un film par un utilisateur — contrainte unicité user+movie |

## `repository/`

Interfaces Spring Data JPA — accès aux données.

| Fichier | Rôle |
|---------|------|
| `UserRepo` | Utilisateurs : findByUsername, findByEmail, existsByUsername |
| `MovieRepo` | Films : findByTitleContainingIgnoreCase, findByGenre, existsByTitle+Director+Year |
| `ReviewRepo` | Critiques : findByMovie, findByUser, findByUserAndMovie |
| `RatingRepo` | Notes : findByUserAndMovie, findByMovie |

## `security/`

Composants liés à l'authentification JWT et OAuth2.

| Fichier | Rôle |
|---------|------|
| `JwtUtil` | Génération, validation et extraction du username depuis un token JWT |
| `JwtAuthenticationFilter` | Filtre HTTP : extrait le token Bearer, valide et enregistre l'authentification |
| `JwtAuthenticationEntryPoint` | Réponse JSON en cas d'erreur 401 (non authentifié) |
| `OAuth2AuthenticationSuccessHandler` | Après auth OAuth2 Google : génère un JWT et redirige vers le frontend via fragment URL |

## `service/`

Logique métier et orchestration.

| Fichier | Rôle |
|---------|------|
| `UserService` | Inscription (BCrypt, vérification unicité), recherche par username, OAuth2 |
| `MovieService` | CRUD films, recherche par titre/genre, vérification doublon |
| `ReviewService` | CRUD critiques, unicité user+movie, vérification auteur |
| `RatingService` | CRUD notes, calcul moyenne, unicité user+movie, vérification auteur |

## Fichier racine

| Fichier | Rôle |
|---------|------|
| `PlateformeFilmsApplication` | Point d'entrée Spring Boot, `CommandLineRunner` pour charger des films de démo |
