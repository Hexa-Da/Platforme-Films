# Ressources Spring Boot

Ce dossier contient la configuration et les assets statiques servis par le backend Spring Boot. **Ce n'est pas le frontend** — l'application React est dans `frontend/` à la racine du projet.

---

## Profils Spring (`application-*.properties`)

| Profil | Fichier | Usage | Base de données |
|--------|----------|-------|-----------------|
| **dev** | `application-dev.properties` | Développement local (profil par défaut) | H2 en mémoire |
| **test** | `application-test.properties` | Tests unitaires/intégration | H2 en mémoire (`testdb`) |
| **prod** | `application-prod.properties` | Déploiement (Docker, etc.) | MySQL |

### Détails par profil

- **dev** : `jdbc:h2:mem://localhost:3306/movies_db`, `ddl-auto=create-drop` — base recréée à chaque démarrage
- **test** : `jdbc:h2:mem:testdb`, `ddl-auto=create-drop` — base isolée pour les tests
- **prod** : MySQL via variables d'environnement (`SPRING_DATASOURCE_*`), `ddl-auto=update` — données persistées

### Activation

```bash
# Par défaut : dev
mvn spring-boot:run

# Forcer un profil
mvn spring-boot:run -Dspring-boot.run.profiles=prod
# ou
SPRING_PROFILES_ACTIVE=prod java -jar app.jar
```

---

## `templates/`

Templates **Thymeleaf** rendus côté serveur.

| Fichier | URL | Rôle |
|---------|-----|------|
| `index.html` | `/`, `/index.html` | Page d'accueil : liens vers Swagger, liste des endpoints |

Ces pages sont servies par `HtmlController` et servent de point d'entrée vers la doc API et le frontend React.

---

## `static/`

Fichiers **statiques** servis tels quels par Spring Boot (sans rendu Thymeleaf).

- **URL** : servis à la racine — ex. `static/js/app.js` → `http://localhost:8080/js/app.js`
- **Contenu actuel** : reliquats de l'ancien projet "characters" (`app.js`, `personnage.html`) — non utilisés par l'API actuelle

Pour des assets statiques (JS, CSS, images) utilisés par les templates ou par une future intégration, placer les fichiers ici.
