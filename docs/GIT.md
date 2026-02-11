# Guide Git - Travail Collaboratif

## Architecture des branches

```
LOCAL
├── main                      # Branche stable, production
├── develop                   # Branche d'intégration (suit origin/develop)
└── feature/nom-de-la-feature # Votre branche de feature pour développer

REMOTE origin 
├── main                      # Branche principale (release)
├── develop                   # Intégration des features
├── feature/reviews-ratings   # Junior 1 - Services + DTOs
├── feature/api-swagger       # Junior 2 - API + Swagger + OAuth2
└── feature/front-tests       # Junior 3 - Front React + Tests + MySQL
```

## Commandes essentielles

- `git fetch` - Télécharger SANS modifier
- `git pull` - Télécharger ET appliquer  
- `git merge` - Fusionner deux branches
- `git branch` - Lister/créer des branches
- `git checkout` - Changer de branche
- `git status` - Voir l'état du repo

## Workflow 

### Début de session, création d'une branche

```bash
# 1. Récupérer les dernières modifs
git fetch origin
git checkout develop
git pull origin develop

# 2. Créer une nouvelle branche de feature
git checkout -b feature/nom-de-la-feature
```

### Pendant le dev (petits commits, merge régulier pour se mettre à jour)

```bash
# travailler sur votre branche
git checkout feature/votre-feature
  
# commit régulier
git add .
git commit -m "message clair"

# se remettre à jour avec les features des autres (via merge)
git fetch origin
git merge origin/develop

# push sur votre branche distante (optionnel)
git push origin feature/votre-feature
```

### Avant la PR

```bash
git fetch origin # télécharger les dernières modifs du remote
git checkout feature/votre-feature
git merge origin/develop # intégrer develop dans sa branche

# s'il y a des conflits: éditer → git add fichier.edite → git commit

# pousser normalement (pas de force nécessaire avec merge)
git push origin feature/votre-feature
```

#### En cas de problème
- **Merge raté** : `git merge --abort`
- **Branche cassée** : `git checkout develop && git checkout -b feature/nouvelle-feature`
- **Push refusé** : vérifier qu'on est sur la bonne branche

### Après merge de la PR (dans develop)

```bash
git checkout develop 
git pull origin develop # mettre à jour develop en local

git branch -d feature/votre-feature # supprimer la branche locale si feature terminée
git branch -D feature/nom-de-la-branche
# pour forcer la suppression (même si pas mergée)

# supprimer la branche distante
git push origin --delete feature/votre-feature

git fetch --prune origin # pour mettre à jour les branches distantes existantes
```

### Phase finale : merge develop → main

Le Lead valide et fusionne `develop` dans `main` pour une release :

```bash
git checkout main
git pull origin main
git merge develop
git push origin main
```

## Vérifs rapides 

```bash
# voir l'état de vos branches de travail
git log --oneline --graph

# voir les commits que vous avez faits qui ne sont pas encore dans develop
git log --oneline --graph origin/develop..HEAD

# voir les commits que vous avez faits qui ne sont pas encore poussés sur votre branche distante
git log --oneline --graph origin/feature/votre-feature..HEAD

# observer l'arbre de travail
git log --oneline --graph --decorate --all
```