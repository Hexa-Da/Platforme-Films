# Guide de contribution

## Conventions Git

- **Branches** : `main` (production), `develop` (intégration), `feature/nom-de-la-feature` pour les nouvelles fonctionnalités
- **Commits** : Utiliser le guide `docs/GIT.md` 

## Répartition des rôles

- **Lead Dev** : Initialisation, validation, merge
- **Junior 1** : Services + DTOs
- **Junior 2** : API + Swagger + OAuth2
- **Junior 3** : Front React + Tests + MySQL

## Processus

1. Créer une branche `feature/xxx` depuis `develop`
2. Commits réguliers sur votre périmètre
3. Mettre à jour `docs/API.md` en cas de nouveau endpoint
4. Pull request vers `develop` pour review (Lead merge develop → main en phase finale)
