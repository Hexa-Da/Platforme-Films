# Authentification

Ce document retrace, fichier par fichier, chaque etape de l'authentification dans le projet.
Il y a deux facons de se connecter (login classique et Google OAuth2) mais les deux produisent le meme resultat : un JWT interne stocké dans le navigateur.

---

## Login classique (username / password)

### Etape 1 : l'utilisateur remplit le formulaire

**Fichier** : `frontend/src/pages/Login.jsx`

L'utilisateur saisit son username et son password. Au submit du formulaire, la fonction `handleSubmit` appelle `login(username, password)` depuis `api.js`.

### Etape 2 : le frontend envoie la requete au backend

**Fichier** : `frontend/src/api.js`

Ca envoie un `POST /api/v1/auth/login` avec le body `{ "username": "...", "password": "..." }`.

### Etape 3 : le controller recoit la requete

**Fichier** : `controller/AuthController.java`

Le body JSON est deserialise dans `LoginRequest` (fichier `dto/LoginRequest.java`, un simple `record(String username, String password)`).

Le controller delegue a `authenticationManager.authenticate(...)` qui va declencher la verification du password.

### Etape 4 : Spring Security verifie le password

**Fichier** : `config/UserDetailsServiceImpl.java`

Spring Security appelle automatiquement `loadUserByUsername(username)`. Ce service charge le `User` depuis la base via `UserRepo.findByUsername()` et retourne un `UserDetails` avec le hash BCrypt du password.

**Fichier** : `config/PasswordEncoderConfig.java`

Spring compare ensuite le password envoye avec le hash en base en utilisant le `BCryptPasswordEncoder` declare ici comme bean.

Si le password ne correspond pas, une exception est levee et le controller renvoie une erreur.

### Etape 5 : generation du JWT

**Fichier** : `security/JwtUtil.java`

Le token est signe avec une cle HMAC-SHA configuree dans `application.properties` (`jwt.secret`). Il expire apres 24h (`jwt.expiration=86400000`).

Le controller retourne `{ "token": "eyJ..." }` au frontend.

### Etape 6 : le frontend stocke le token

**Fichier** : `frontend/src/pages/Login.jsx`

Le token est stocke dans `localStorage` et l'utilisateur est redirige vers la liste des films.

---

## Login avec Google (OAuth2)

### Etape 1 : l'utilisateur clique sur le bouton Google

**Fichier** : `frontend/src/pages/Login.jsx`

C'est une navigation complète vers le backend. Le navigateur quitte le frontend React.

### Etape 2 : Spring Security demarre le flux OAuth2

**Fichier** : `config/SecurityConfig.java`

L'URL `/oauth2/authorization/google` est geree automatiquement par Spring Security OAuth2 Client (grace a la dependance `spring-boot-starter-oauth2-client` dans `pom.xml`).

Spring lit la configuration du client Google.

**Fichier** : `application-dev.properties`

Spring redirige alors le navigateur vers l'ecran de consentement Google.

### Etape 3 : l'utilisateur accepte sur Google

Google demande a l'utilisateur s'il autorise l'application a acceder a son email et son profil. S'il accepte, Google redirige le navigateur vers le backend avec un code d'autorisation : GET /login/oauth2/code/google?code=xxx&state=yyy

Cette URL est geree automatiquement par Spring. Le framework echange ce code contre un access token aupres de Google, puis recupere les infos du profil (email, name, sub).

### Etape 4 : le success handler genere un JWT interne

**Fichier** : `security/OAuth2AuthenticationSuccessHandler.java`

C'est le coeur du pont entre OAuth2 et notre systeme JWT :

Ce qui se passe ligne par ligne :
1. On recupere l'email et le sub (identifiant unique Google) depuis les attributs OAuth2.
2. On choisit le username : l'email si disponible, sinon `google_<sub>`.
3. On retrouve ou cree le user dans notre base (etape suivante).
4. On genere un JWT **avec `JwtUtil`**, exactement le meme que pour un login classique.
5. On redirige le navigateur vers le frontend avec le JWT dans la query string.

### Etape 5 : creation ou rattachement du compte interne

**Fichier** : `service/UserService.java`

- Si un compte avec cet email existe deja en base (meme cree par inscription classique), on le reutilise. Pas de doublon.
- Sinon, on cree un nouveau compte avec un password placeholder (hash BCrypt de `"oauth2-google"`). L'utilisateur ne pourra pas se connecter avec ce password en classique, mais ce n'est pas grave : il passera toujours par Google.

### Etape 6 : le frontend recoit le JWT

**Fichier** : `frontend/src/pages/OAuth2Callback.jsx`

Le navigateur arrive sur `/oauth2/callback?token=eyJ...`. Le composant lit le token dans l'URL, le stocke dans `localStorage`, et redirige vers `/movies`.

A partir de la, le frontend fonctionne exactement comme apres un login classique.

---

## Parcours commun — Appel a un endpoint protege

Quel que soit le parcours de login utilise, tous les appels API suivent le meme chemin.

### Etape 1 : le frontend envoie le token

**Fichier** : `frontend/src/api.js`

Chaque appel (`getMovies()`, `getMovie(id)`, etc.) passe par `authHeaders()` qui injecte `Authorization: Bearer <token>` dans les headers.

### Etape 2 : le filtre JWT intercepte la requete

**Fichier** : `security/JwtAuthenticationFilter.java`

Ce filtre est enregistré dans la chaine Spring Security (dans `SecurityConfig.java` via `.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)`). Il s'execute avant chaque requete :

1. Extrait le token du header `Authorization: Bearer ...`.
2. Appelle `JwtUtil.extractUsername(token)` pour lire le `subject` du JWT.
3. Appelle `UserService.findByUsername(username)` pour charger le user depuis la base.
4. Appelle `JwtUtil.validateToken(token, username)` pour verifier la signature et l'expiration.
5. Si tout est OK, place un `UsernamePasswordAuthenticationToken` dans le `SecurityContext` avec l'autorite `ROLE_USER`.

### Etape 3 : Spring Security decide

**Fichier** : `config/SecurityConfig.java`

Les regles de `authorizeHttpRequests` determinent si l'endpoint est public ou protege :

- Si l'endpoint est `permitAll` : la requete passe meme sans token.
- Si l'endpoint est `authenticated` et que le filtre JWT n'a pas pu authentifier : `JwtAuthenticationEntryPoint` renvoie un `401 Unauthorized` en JSON.

### Etape 4 : reponse 401 si non authentifié

**Fichier** : `security/JwtAuthenticationEntryPoint.java`

Renvoie `{ "error": "Unauthorized", "message": "..." }` avec un code HTTP 401.

---

## Deconnexion

**Fichier** : `frontend/src/api.js`

Il n'y a rien cote serveur : le backend est stateless. Supprimer le token du `localStorage` suffit a deconnecter l'utilisateur.

---

## Recapitulatif des fichiers

| Fichier | Role dans l'auth |
|---------|-----------------|
| `frontend/src/pages/Login.jsx` | Formulaire login classique + bouton Google |
| `frontend/src/pages/Register.jsx` | Formulaire inscription classique |
| `frontend/src/pages/OAuth2Callback.jsx` | Recoit le JWT apres login Google |
| `frontend/src/api.js` | Fonctions `login()`, `register()`, `authHeaders()`, `logout()` |
| `frontend/src/App.jsx` | Declaration des routes (`/login`, `/register`, `/oauth2/callback`) |
| `controller/AuthController.java` | `POST /auth/register` et `POST /auth/login` |
| `service/UserService.java` | `register()`, `findByUsername()`, `findOrCreateOAuth2User()` |
| `security/JwtUtil.java` | `generateToken()`, `validateToken()`, `extractUsername()` |
| `security/JwtAuthenticationFilter.java` | Intercepte chaque requete, valide le JWT, authentifie |
| `security/JwtAuthenticationEntryPoint.java` | Renvoie 401 JSON si non authentifie |
| `security/OAuth2AuthenticationSuccessHandler.java` | Convertit un succes Google OAuth2 en JWT interne |
| `config/SecurityConfig.java` | Chaine de securite : CORS, CSRF, session stateless, oauth2Login, regles d'acces, filtre JWT |
| `config/UserDetailsServiceImpl.java` | Charge un user pour la verification du password (login classique) |
| `config/PasswordEncoderConfig.java` | Bean BCrypt |
| `dto/LoginRequest.java` | Body du login classique |
| `dto/RegisterRequest.java` | Body de l'inscription |
| `dto/AuthResponse.java` | Reponse `{ "token": "..." }` |
| `application.properties` | `jwt.secret`, `jwt.expiration`, `app.frontend.base-url` |
| `application-dev.properties` | Config OAuth2 Google (client-id, client-secret, scopes) |
| `pom.xml` | Dependances : `spring-boot-starter-security`, `spring-boot-starter-oauth2-client`, `jjwt` |
