# Plan - Plateforme de films (Lead + 3 juniors)

## Cahier des charges

**Figures imposées** : Git | API RESTful | Doc sur / | Swagger | Tests | Spring Security  
**Au choix** : Sujet API (films) | Front (React)  
**Options** : MySQL exposée | OAuth2 | Docker  

---

## Répartition des rôles

| Rôle | Périmètre | Résumé |
|------|-----------|--------|
| **Lead Dev / Ops** | Initialisation, infra, auth, Docker, MySQL, OAuth2, CI, review | Tout ce qui est structurant, ops et validation |
| **Junior 1** | Backend — Reviews & Ratings | Services, controllers, DTOs, Swagger, tests pour reviews et ratings |
| **Junior 2** | Backend — Movies CRUD & Users | Enrichir MovieService/Controller (CRUD, search), créer UserController, Swagger, tests |
| **Junior 3** | Frontend React | Pages détail, formulaires critique/note, profil, navbar, intégration API |

---

## Phase 1 — Lead : Initialisation du projet

### 1. Structure et conventions

- Git : guide `docs/GIT.md`, `CONTRIBUTING.md`
- Créer `docs/API.md` : liste des endpoints, format JSON, structure des DTOs

### 2. Modèles et persistance

- Modèles JPA : `User`, `Movie`, `Review`, `Rating`
- Repositories : UserRepo, MovieRepo, ReviewRepo, RatingRepo
- CommandLineRunner : 5 films de démo
- Profils : `dev` (H2), `test` (H2), `prod` (MySQL)

### 3. Auth minimale (pour débloquer le front)

- `spring-boot-starter-security` + `jjwt`
- UserService (register, findByUsername) + BCrypt
- JwtUtil, JwtAuthenticationFilter, SecurityConfig
- AuthController : `POST /api/v1/auth/register`, `POST /api/v1/auth/login` → `{ token }`
- UserDetailsService via UserRepo

### 4. Endpoint films minimal

- MovieService (findAll, getById)
- MovieController : `GET /api/v1/movies`, `GET /api/v1/movies/{id}`
- Config CORS (localhost:5173 pour React)

### 5. Frontend et infra

- Projet React (Vite) dans `frontend/`
- Page liste films, page login/register basiques
- Dockerfile + docker-compose (app + MySQL)
- README avec instructions de lancement

### Livrables Lead

- App démarrable
- Auth fonctionnelle (register, login, JWT)
- GET /movies opérationnel
- Front React basique connecté
- `docs/API.md` à jour

---

## Phase 2 — Juniors : Développement en parallèle

Une fois le Lead livré, les 3 juniors travaillent en parallèle.

---

### Junior 1 — Backend : Reviews & Ratings

> **Tu crées toute la logique métier et l'API pour que les utilisateurs puissent publier des critiques et des notes sur les films.**

#### Ce qui existe déjà (livré par le Lead)

- Les modèles `Review` et `Rating` (dans `model/`)
- Les repositories `ReviewRepo` et `RatingRepo` (dans `repository/`)
- L'authentification JWT (tu peux récupérer l'utilisateur connecté)

#### Ce que tu dois créer

**1. ReviewService** (`service/ReviewService.java`)

Logique métier pour les critiques. Méthodes à implémenter :
- `getReviewsByMovie(Long movieId)` → liste des critiques d'un film
- `getReviewsByUser(Long userId)` → liste des critiques d'un utilisateur
- `createReview(Long movieId, Long userId, String content)` → créer une critique
- `deleteReview(Long reviewId, Long userId)` → supprimer une critique (seulement si c'est l'auteur)

*Comment faire* : injecte `ReviewRepo`, `MovieRepo` et `UserRepo` dans le constructeur. Utilise les méthodes `findById()` des repos pour récupérer les entités, puis `reviewRepo.save()` pour enregistrer.

**2. RatingService** (`service/RatingService.java`)

Logique métier pour les notes (1 à 5). Méthodes à implémenter :
- `getRatingsByMovie(Long movieId)` → liste des notes d'un film
- `getAverageRating(Long movieId)` → moyenne des notes d'un film (retourne un `Double`)
- `rateMovie(Long movieId, Long userId, Integer score)` → noter un film (crée ou met à jour si l'utilisateur a déjà noté)

*Comment faire* : `RatingRepo` a une méthode `findByUserAndMovie()` — utilise-la pour vérifier si l'utilisateur a déjà noté. Si oui, mets à jour le `score` existant au lieu d'en créer un nouveau.

**3. ReviewController** (`controller/ReviewController.java`)

Endpoints REST :

| Méthode | Endpoint | Auth | Description |
|---------|----------|------|-------------|
| GET | `/api/v1/movies/{id}/reviews` | Non | Liste des critiques d'un film |
| POST | `/api/v1/movies/{id}/reviews` | Oui (JWT) | Ajouter une critique |
| DELETE | `/api/v1/movies/{id}/reviews/{reviewId}` | Oui (JWT) | Supprimer sa critique |

**4. RatingController** (`controller/RatingController.java`)

| Méthode | Endpoint | Auth | Description |
|---------|----------|------|-------------|
| GET | `/api/v1/movies/{id}/ratings` | Non | Liste des notes d'un film |
| GET | `/api/v1/movies/{id}/ratings/average` | Non | Note moyenne |
| POST | `/api/v1/movies/{id}/ratings` | Oui (JWT) | Noter un film |

**5. DTOs**

Crée des records dans `dto/` :
- `ReviewRequest(String content)`
- `ReviewResponse(Long id, String username, String content, LocalDateTime createdAt)`
- `RatingRequest(Integer score)`
- `RatingResponse(Long id, String username, Integer score)`

*Pourquoi des DTOs ?* Pour ne pas exposer les entités JPA directement (éviter d'envoyer le mot de passe de l'User ou les relations circulaires dans le JSON).

**6. Swagger**

Ajoute `@Operation(summary = "...")` sur chaque méthode de tes controllers. Exemple :
```java
@Operation(summary = "Liste des critiques d'un film")
@GetMapping
public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Long id) { ... }
```

**7. Tests**

- Tests unitaires pour `ReviewService` et `RatingService` (avec `@Mock` sur les repos)
- Tests `@WebMvcTest` pour les controllers (MockMvc)

#### Récupérer l'utilisateur connecté dans un controller

```java
@PostMapping
public ResponseEntity<?> create(@PathVariable Long id, @RequestBody ReviewRequest request,
                                 Authentication authentication) {
    String username = authentication.getName();
    // puis utilise userService.findByUsername(username) pour avoir le User
}
```

#### Branche Git

```bash
git checkout dev && git pull origin dev
git checkout -b feature/reviews-ratings
```

---

### Junior 2 — Backend : Movies CRUD & Users

> **Tu enrichis l'API des films (ajout, modification, suppression, recherche) et tu crées l'endpoint utilisateur.**

#### Ce qui existe déjà (livré par le Lead)

- `MovieService` avec `findAll()` et `getById()` (dans `service/`)
- `MovieController` avec GET liste et GET détail (dans `controller/`)
- `MovieRepo` avec des méthodes de recherche déjà déclarées (`findByTitleContainingIgnoreCase`, `findByGenre`, `findByReleaseYear`)
- `UserService` avec `findByUsername()` et `getById()`

#### Ce que tu dois créer

**1. Enrichir MovieService** (`service/MovieService.java`)

Ajouter les méthodes :
- `searchByTitle(String title)` → utilise `movieRepo.findByTitleContainingIgnoreCase(title)`
- `searchByGenre(String genre)` → utilise `movieRepo.findByGenre(genre)`
- `createMovie(Movie movie)` → `movieRepo.save(movie)`
- `updateMovie(Long id, Movie updated)` → récupère le film, met à jour les champs, save
- `deleteMovie(Long id)` → `movieRepo.deleteById(id)`

*Comment faire* : les méthodes de recherche existent déjà dans `MovieRepo`, il suffit de les appeler depuis le service.

**2. Enrichir MovieController** (`controller/MovieController.java`)

Ajoute ces endpoints au controller existant :

| Méthode | Endpoint | Auth | Description |
|---------|----------|------|-------------|
| GET | `/api/v1/movies?title=xxx` | Non | Recherche par titre |
| GET | `/api/v1/movies?genre=xxx` | Non | Recherche par genre |
| POST | `/api/v1/movies` | Oui (JWT) | Créer un film |
| PUT | `/api/v1/movies/{id}` | Oui (JWT) | Modifier un film |
| DELETE | `/api/v1/movies/{id}` | Oui (JWT) | Supprimer un film |

*Comment gérer la recherche* : ajoute des `@RequestParam(required = false)` sur le `getAllMovies()` existant :
```java
@GetMapping
public ResponseEntity<List<Movie>> getAllMovies(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String genre) {
    if (title != null) return ResponseEntity.ok(movieService.searchByTitle(title));
    if (genre != null) return ResponseEntity.ok(movieService.searchByGenre(genre));
    return ResponseEntity.ok(movieService.findAll());
}
```

**3. DTOs Films**

Crée dans `dto/` :
- `MovieRequest(String title, String director, Integer releaseYear, String genre, String synopsis)` — pour POST et PUT
- Tu peux retourner l'entité `Movie` directement pour les réponses (pas de données sensibles)

**4. UserController** (`controller/UserController.java`)

| Méthode | Endpoint | Auth | Description |
|---------|----------|------|-------------|
| GET | `/api/v1/users/{id}` | Oui (JWT) | Profil d'un utilisateur |
| GET | `/api/v1/users/me` | Oui (JWT) | Mon profil (utilisateur connecté) |

Crée un DTO :
- `UserResponse(Long id, String username, String email, LocalDateTime createdAt)` — ne jamais exposer le password

**5. Swagger**

Ajoute `@Operation(summary = "...")` sur chaque endpoint. Ajoute aussi `@Tag(name = "Movies")` et `@Tag(name = "Users")` sur les controllers pour grouper dans Swagger.

**6. Tests**

- Tests unitaires pour les nouvelles méthodes de `MovieService`
- Tests `@WebMvcTest(MovieController.class)` pour les nouveaux endpoints (MockMvc)
- Test du `UserController`

#### Branche Git

```bash
git checkout dev && git pull origin dev
git checkout -b feature/movies-crud-users
```

---

### Junior 3 — Frontend React

> **Tu crées les pages React pour que l'utilisateur puisse voir les détails d'un film, poster des critiques/notes, et gérer son profil.**

#### Ce qui existe déjà (livré par le Lead)

- `App.jsx` avec le routing (/, /movies, /movies/:id, /login, /register)
- `api.js` avec les fonctions `login()`, `register()`, `getMovies()`, `getMovie()`, `logout()`
- Pages : `Movies.jsx` (liste), `MovieDetail.jsx` (détail basique), `Login.jsx`, `Register.jsx`
- Le token JWT est stocké dans `localStorage`

#### Ce que tu dois créer

**1. Enrichir la page détail film** (`pages/MovieDetail.jsx`)

La page actuelle affiche titre, réalisateur, année, genre, synopsis. Ajoute :
- La liste des critiques du film (appel `GET /api/v1/movies/{id}/reviews`)
- La note moyenne (appel `GET /api/v1/movies/{id}/ratings/average`)
- Un formulaire pour poster une critique (si connecté)
- Un formulaire pour noter le film (select 1 à 5, si connecté)

*Comment savoir si l'utilisateur est connecté* : vérifie `localStorage.getItem('token')`. S'il n'y a pas de token, affiche un lien vers `/login` au lieu des formulaires.

**2. Ajouter les appels API** (`api.js`)

Ajoute ces fonctions dans `api.js` :
```javascript
export async function getReviews(movieId) { /* GET /movies/{movieId}/reviews */ }
export async function postReview(movieId, content) { /* POST /movies/{movieId}/reviews, body: { content } */ }
export async function getAverageRating(movieId) { /* GET /movies/{movieId}/ratings/average */ }
export async function postRating(movieId, score) { /* POST /movies/{movieId}/ratings, body: { score } */ }
export async function getUser(userId) { /* GET /users/{userId} */ }
export async function getMe() { /* GET /users/me */ }
```

Utilise `authHeaders()` (déjà dans `api.js`) pour les requêtes authentifiées. Suis le même pattern que les fonctions existantes.

**3. Page profil** (`pages/Profile.jsx`)

Nouvelle page qui affiche :
- Username, email, date d'inscription (appel `GET /api/v1/users/me`)
- Liste des critiques de l'utilisateur (optionnel, si endpoint dispo)

Ajoute la route dans `App.jsx` :
```jsx
<Route path="/profile" element={<Profile />} />
```

**4. Navbar commune** (`components/Navbar.jsx`)

Actuellement la nav est dupliquée dans `Movies.jsx`. Crée un composant `Navbar` réutilisable :
- Lien "Films" → `/movies`
- Si connecté : lien "Profil" + bouton "Déconnexion"
- Si pas connecté : liens "Connexion" + "Inscription"

Utilise-le dans un layout commun (dans `App.jsx`, en dehors des `<Routes>`).

**5. Recherche de films**

Ajoute un champ de recherche sur la page `Movies.jsx` :
- Input texte + bouton "Rechercher"
- Appelle `GET /api/v1/movies?title=xxx`
- Ajoute la fonction `searchMovies(title)` dans `api.js`

**6. Styles**

Les fichiers CSS existent déjà (`Movies.css`, `MovieDetail.css`, `Auth.css`). Enrichis-les pour les nouvelles sections (liste de critiques, formulaires, profil). Garde le même style que l'existant.

#### Ce que tu peux faire tout de suite (sans attendre Junior 1 et 2)

- La Navbar commune
- La recherche de films (l'endpoint GET existe déjà, Junior 2 ajoutera les filtres)
- Le layout de la page profil (avec des données mockées en attendant le `GET /users/me`)
- Améliorer le design des pages existantes

#### Ce qui nécessite Junior 1 et 2

- Afficher les critiques et notes sur la page détail (dépend des endpoints de Junior 1)
- Les formulaires de critique et de note (dépend des endpoints de Junior 1)
- Le profil complet (dépend du `GET /users/me` de Junior 2)

#### Branche Git

```bash
git checkout dev && git pull origin dev
git checkout -b feature/front-pages
```

---

## Phase 3 — Lead Ops : pendant que les juniors développent

Pendant que les juniors codent, le Lead gère l'infra et les options :

| Tâche | Détail |
|-------|--------|
| MySQL cloud | Déployer sur PlanetScale / Railway / Aiven, mettre à jour `application-prod.properties` |
| OAuth2 (option) | Ajouter `spring-boot-starter-oauth2-client`, configurer Google (login OAuth2 → JWT interne) |
| Docker | Vérifier que `docker-compose up` fonctionne toujours avec les nouvelles features |
| Swagger | Vérifier que `/docs` affiche tous les endpoints après merge |
| SecurityConfig | Mettre à jour les règles d'autorisation si nouveaux endpoints (ex: `permitAll` sur GET reviews/ratings) |
| Code review | Relire les PRs des juniors avant merge dans `dev` |

---

## Phase 4 — Finalisation

| Responsable | Tâches |
|-------------|--------|
| **Tous** | Revue de code, tests complets, corrections |
| **Junior 3** | Doc sur `/`, vérifier que toutes les pages React fonctionnent |
| **Junior 1 & 2** | Vérifier Swagger complet, tester tous les endpoints |
| **Lead** | Validation finale, merge `dev` → `main`, déploiement |
