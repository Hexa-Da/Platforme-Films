# Guide de contribution

## Répartition des rôles

| Rôle | Périmètre | Branche |
|------|-----------|---------|
| **Lead Dev / Ops** | Init, auth, Docker, MySQL cloud, OAuth2, review, merge | `dev`, `main` |
| **Junior 1** | Backend Reviews & Ratings (services, controllers, DTOs, Swagger, tests) | `feature/reviews-ratings` |
| **Junior 2** | Backend Movies CRUD & Users (services, controllers, DTOs, Swagger, tests) | `feature/movies-crud-users` |
| **Junior 3** | Frontend React (pages, composants, intégration API, styles) | `feature/front-pages` |

## Conventions Git

- **Branches** : `main` (production), `dev` (intégration), `feature/nom-de-la-feature`
- **PR** : toujours vers `dev`, jamais directement vers `main`

## Workflow

1. Créer votre branche depuis `dev` :
   ```bash
   git checkout dev && git pull origin dev
   git checkout -b feature/votre-feature
   ```

2. Commits réguliers sur votre branche

3. Se remettre à jour régulièrement :
   ```bash
   git fetch origin
   git merge origin/dev
   ```

4. Pousser et ouvrir une PR vers `dev` :
   ```bash
   git push origin feature/votre-feature
   ```

5. Le Lead review et merge dans `dev`

## Documentation

- `docs/PLAN.md` — Plan complet avec les tâches détaillées de chaque junior
- `docs/API.md` — Contrat API (endpoints, DTOs, exemples)
- `docs/GIT.md` — Commandes Git et workflow
- `docs/GUIDE.md` — Guide d'installation et d'utilisation
