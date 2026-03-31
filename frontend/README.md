# Frontend — Plateforme de films

Application React (v19) avec Vite (v7) pour la plateforme de films.

## Stack

- **React 19** — framework UI
- **Vite 7** — bundler / dev server
- **react-router-dom 7** — routage SPA

## Pages

| Route | Composant | Description |
|-------|-----------|-------------|
| `/movies` | `Movies.jsx` | Liste des films avec recherche, filtres, notes moyennes |
| `/movies/:id` | `MovieDetail.jsx` | Détail d'un film, critiques, notes |
| `/movies/new` | `MovieForm.jsx` | Formulaire d'ajout de film |
| `/login` | `Login.jsx` | Connexion (JWT) |
| `/register` | `Register.jsx` | Inscription |
| `/profile` | `Profile.jsx` | Profil utilisateur |
| `/oauth2/callback` | `OAuth2Callback.jsx` | Callback OAuth2 Google |

## Composants notables

- `ReviewPopup` — Popup modale accessible (Escape, click overlay) pour noter et commenter un film
- `SearchBar` — Barre de recherche avec debounce

## Lancement

```bash
cd frontend
npm install
npm run dev
```

Le serveur de dev tourne sur `http://localhost:5173` et communique avec le backend sur `http://localhost:8080/api/v1`.

## Variables d'environnement

| Variable | Description | Défaut |
|----------|-------------|--------|
| `VITE_API_BASE` | URL de base de l'API | `http://localhost:8080/api/v1` (dev) |

## Build production

```bash
npm run build
```

Les fichiers statiques sont générés dans `dist/`. Le fichier `public/_redirects` configure les SPA rewrites pour Netlify.
