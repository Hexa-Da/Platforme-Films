# Guide d'utilisation - Plateforme de films

Ce guide explique comment installer, lancer et utiliser la plateforme de films (API REST + frontend React).

---

## Prérequis

| Outil | Version |
|-------|---------|
| Java | 17+ |
| Maven | 3.6+ (ou utilisez `./mvnw` inclus) |
| Node.js | 18+ |
| Docker | optionnel (pour MySQL en prod) |

---

## Démarrage rapide

### 1. Lancer l'API en mode développement

```bash
./mvnw spring-boot:run
```

- **API** : http://localhost:8080
- **Swagger** : http://localhost:8080/docs
- **Page d'accueil** : http://localhost:8080

Le profil `dev` est utilisé par défaut (base H2 en mémoire, données recréées à chaque démarrage).

### 2. Lancer le frontend React

```bash
cd frontend
npm install
npm run dev
```

- **Frontend** : http://localhost:5173

### 3. Utiliser l'application

1. Ouvrir http://localhost:5173
2. La page **Films** s'affiche (liste des films ; GET /movies est public)
3. Créer un compte : **S'inscrire** → username, email, password
4. Se connecter : **Connexion** → username, password
5. Le token JWT est stocké dans `localStorage` et envoyé automatiquement pour les requêtes protégées

---

## Utilisation de l'API

### Authentification

**Inscription**

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","password":"secret123"}'
```

Réponse : `{ "token": "eyJhbGciOiJIUzI1NiIs..." }`

**Connexion**

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"secret123"}'
```

### Films (endpoints publics)

```bash
# Liste des films
curl http://localhost:8080/api/v1/movies

# Détail d'un film
curl http://localhost:8080/api/v1/movies/1
```

### Endpoints protégés

Pour les futurs endpoints protégés, ajouter le header :

```
Authorization: Bearer <votre_token>
```

Voir `docs/API.md` pour le contrat complet.

---

## Utilisation du frontend

| Route | Description |
|-------|-------------|
| `/` | Redirection vers `/movies` |
| `/movies` | Liste des films |
| `/movies/:id` | Détail d'un film |
| `/login` | Connexion |
| `/register` | Inscription |

Le token est conservé entre les sessions (localStorage). Pour se déconnecter : appel à `logout()` (suppression du token).

---

## Docker (production)

Lance l'API avec MySQL :

```bash
docker-compose up --build
```

- **API** : http://localhost:8080
- **MySQL** : port 3306

Le profil `prod` est activé automatiquement. Les variables de connexion sont définies dans `docker-compose.yml`.

---

## Variables d'environnement

| Variable | Description | Défaut |
|----------|-------------|--------|
| `SPRING_PROFILES_ACTIVE` | `dev`, `test` ou `prod` | `dev` |
| `JWT_SECRET` | Secret pour signer les tokens | (valeur de dev) |
| `SPRING_DATASOURCE_URL` | URL JDBC MySQL (profil prod) | `jdbc:mysql://localhost:3306/movies_db` |
| `SPRING_DATASOURCE_USERNAME` | Identifiant MySQL | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Mot de passe MySQL | — |

---

## Tests

```bash
./mvnw test
```

Utilise le profil `test` (H2 en mémoire).

---

## Documentation associée

- `docs/API.md` — Contrat API et format des DTOs
- `docs/GIT.md` — Workflow Git et conventions
- `docs/PLAN.md` — Plan du projet et répartition des tâches
