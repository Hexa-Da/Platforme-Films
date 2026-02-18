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
