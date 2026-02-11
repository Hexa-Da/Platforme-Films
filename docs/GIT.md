# Guide Git - Travail Collaboratif

## Architecture des branches

```
LOCAL
├── main                      # Branche stable, production
├── dev                       # Branche d'intégration (suit origin/dev)
└── feature/nom-de-la-feature # Votre branche de feature pour développer

REMOTE origin 
├── main                      # Branche principale (release)
├── dev                       # Intégration des features
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
git checkout dev
git pull origin dev

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
git merge origin/dev

# push sur votre branche distante (optionnel)
git push origin feature/votre-feature
```

### Avant la PR

```bash
git fetch origin # télécharger les dernières modifs du remote
git checkout feature/votre-feature
git merge origin/dev # intégrer dev dans sa branche si pas a jour avec dev

# s'il y a des conflits: éditer → git add fichier.edite → git commit

# pousser normalement (pas de force nécessaire avec merge)
git push origin feature/votre-feature
```

#### En cas de problème
- **Merge raté** : `git merge --amend`
- **Branche cassée** : `git checkout dev && git checkout -b feature/nouvelle-feature`
- **Push refusé** : vérifier qu'on est sur la bonne branche

### Après merge de la PR (dans dev)

```bash
git checkout dev 
git pull origin dev # mettre à jour dev en local

git branch -d feature/votre-feature # supprimer la branche locale si feature terminée
git branch -D feature/nom-de-la-branche
# pour forcer la suppression (même si pas mergée)

# supprimer la branche distante
git push origin --delete feature/votre-feature

git fetch --prune origin # pour mettre à jour les branches distantes existantes
```

### Phase finale : merge dev → main

Le Lead valide et fusionne `dev` dans `main` pour une release :

```bash
git checkout main
git pull origin main
git merge dev
git push origin main
```

## Vérifs rapides 

```bash
# voir l'état de vos branches de travail
git log --oneline --graph

# voir les commits que vous avez faits qui ne sont pas encore dans dev
git log --oneline --graph origin/dev..HEAD

# voir les commits que vous avez faits qui ne sont pas encore poussés sur votre branche distante
git log --oneline --graph origin/feature/votre-feature..HEAD

# observer l'arbre de travail
git log --oneline --graph --decorate --all
```