# Plan - Plateforme de films (Lead + 3 juniors)

## Cahier des charges

**Figures imposées** : Git | API RESTful | Doc sur / | Swagger | Tests | Spring Security  
**Au choix** : Sujet API (films) | Front (React)  
**Options** : MySQL exposée | OAuth2 | Docker  

---

## Phase Lead : Initialisation du projet

### 1. Structure et conventions

- Git : branche `main`, guide `GIT.md`, `CONTRIBUTING.md`
- Supprimer : FictionalCharacter, CharacterRepo, CharacterController, CharacterService, tests associés
- Créer `docs/API.md` : liste des endpoints, format JSON, structure des DTOs

### 2. Modèles et persistance

- Modèles JPA : `User`, `Movie`, `Review`, `Rating` (relations ManyToOne/OneToMany)
- Repositories : UserRepo, MovieRepo, ReviewRepo, RatingRepo
- CommandLineRunner : 5–10 films de démo
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
- Config CORS (ex. localhost:5173 pour React)

### 5. Frontend et infra

- Projet React (Vite) dans `frontend/`
- Page liste films qui appelle l’API (ou mock en attendant)
- Page login/register basique
- Dockerfile + docker-compose (app + mysql)
- README avec instructions de lancement

### Livrables Lead

- App démarrable
- Auth fonctionnelle (register, login, JWT)
- GET /movies opérationnel
- Front React basique connecté
- `docs/API.md` à jour
- Docker fonctionnel

---

## Phase Juniors : Développement en parallèle

Une fois le Lead livré, les 3 juniors travaillent en parallèle sur des périmètres séparés.


| Junior       | Périmètre              | Tâches                                                                                                       | Dépendances                           |
| ------------ | ---------------------- | ------------------------------------------------------------------------------------------------------------ | ------------------------------------- |
| **Junior 1** | Services + DTOs        | ReviewService, RatingService. MovieService : search, create, update, delete. DTOs si besoin.                 | Aucune (repos déjà présents)          |
| **Junior 2** | API + Swagger + OAuth2 | ReviewController, RatingController, UserController. Swagger (@Operation). Option : OAuth2 (Google/GitHub).   | Services (Junior 1)                   |
| **Junior 3** | Front + Tests + MySQL  | Pages React : détail film, formulaire critique/note, profil. Tests services + MockMvc. Option : MySQL cloud. | API (Junior 2 pour endpoints avancés) |


### Parallélisme

- **Junior 1** : peut démarrer immédiatement (repos livrés par le Lead).
- **Junior 2** : attend les services de Junior 1 pour les controllers. Peut commencer par Swagger sur les endpoints existants et la config OAuth2.
- **Junior 3** : peut démarrer tout de suite sur les pages React (détail, formulaires) avec des appels vers les endpoints déjà présents (movies, auth). Les tests et la connexion aux nouveaux endpoints suivent la livraison de Junior 2.

### Conventions pour les juniors

- Branches `feature/nom-de-la-feature`
- Commits réguliers sur leur périmètre
- Mise à jour de `docs/API.md` en cas de nouveau endpoint

---

## Phase finale : Tests, doc, options


| Responsable  | Tâches                                     |
| ------------ | ------------------------------------------ |
| **Tous**     | Revue de code, tests complets, corrections |
| **Junior 3** | Doc sur `/` (index.html), README final     |
| **Junior 2** | Vérifier Swagger, sécurité                 |
| **Lead**     | Validation finale, merge vers `main`       |


---

## Référence technique

**Modèles** : User, Movie, Review, Rating (JPA)  
**Endpoints** : `/api/v1/movies` (CRUD, search), `/api/v1/movies/{id}/reviews`, `/api/v1/movies/{id}/ratings`, `/api/v1/users/{id}`, `/api/v1/auth/*`  
**Security** : JWT dans `Authorization`, permitAll sur `/auth/**`, `/docs`, GET `/movies`  
**React** : Vite, fetch/axios, JWT dans localStorage  
**MySQL** : profil prod, PlanetScale/Railway/Aiven