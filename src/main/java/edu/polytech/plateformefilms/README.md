# Structure du package `plateformefilms`

Ce package contient l'application backend de la plateforme de films — API REST, authentification JWT et logique métier.

## `config/`

Configuration Spring et intégration avec Spring Security.

| Fichier | Rôle |
|---------|------|
| `SecurityConfig` | Configuration de la chaîne de sécurité : règles d'autorisation, CORS, JWT filter, session stateless |
| `PasswordEncoderConfig` | Bean BCrypt pour le hachage des mots de passe |
| `UserDetailsServiceImpl` | Implémentation de `UserDetailsService` pour charger un utilisateur par username (authentification) |

## `controller/`

Contrôleurs REST et pages HTML.

| Fichier | Rôle |
|---------|------|
| `AuthController` | Endpoints auth : `POST /api/v1/auth/register`, `POST /api/v1/auth/login` → JWT |
| `MovieController` | API films : `GET /api/v1/movies`, `GET /api/v1/movies/{id}` |
| `HtmlController` | Pages web : `/`, `/index.html` → Thymeleaf |

## `dto/`

Data Transfer Objects pour les requêtes/réponses API.

| Fichier | Rôle |
|---------|------|
| `RegisterRequest` | DTO d'inscription (username, email, password) |
| `LoginRequest` | DTO de connexion (username, password) |
| `AuthResponse` | Réponse auth : `{ token }` |

## `model/`

Entités JPA mappées aux tables de la base de données.

| Fichier | Rôle |
|---------|------|
| `User` | Utilisateur (username, email, password) |
| `Movie` | Film (title, director, releaseYear, genre, synopsis) |
| `Review` | Critique d'un film par un utilisateur |
| `Rating` | Note d'un film par un utilisateur |

## `repository/`

Interfaces Spring Data JPA — accès aux données.

| Fichier | Rôle |
|---------|------|
| `UserRepo` | Utilisateurs : findByUsername, findByEmail, existsByUsername |
| `MovieRepo` | Films : findByTitleContainingIgnoreCase, findByGenre, findByReleaseYear |
| `ReviewRepo` | Critiques : findByMovie, findByUser |
| `RatingRepo` | Notes : findByUserAndMovie, findByMovie |

## `security/`

Composants liés à l'authentification JWT.

| Fichier | Rôle |
|---------|------|
| `JwtUtil` | Génération, validation et extraction du username depuis un token JWT |
| `JwtAuthenticationFilter` | Filtre HTTP : extrait le token Bearer, valide et enregistre l'authentification |
| `JwtAuthenticationEntryPoint` | Réponse JSON en cas d'erreur 401 (non authentifié) |

## `service/`

Logique métier et orchestration.

| Fichier | Rôle |
|---------|------|
| `UserService` | Inscription (BCrypt), recherche par username |
| `MovieService` | Liste des films, récupération par ID |

## Fichier racine

| Fichier | Rôle |
|---------|------|
| `PlateformeFilmsApplication` | Point d'entrée Spring Boot, `CommandLineRunner` pour charger des films de démo |
