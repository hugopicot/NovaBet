# Polymarket JavaFX App

## Prérequis
- Java 21
- Maven
- MySQL 8

## Installation et lancement

1. **Cloner le projet**
   ```bash
   git clone <url-du-repo>
   cd polymarket
   ```

2. **Configuration de la base de données**
   - Créez un fichier `src/main/resources/config.properties` s'il n'existe pas.
   - Ajoutez-y vos credentials (ce fichier est ignoré par Git) :
     ```properties
     db.url=jdbc:mysql://localhost:3306/votre_db?serverTimezone=UTC
     db.user=votre_user
     db.password=votre_mot_de_passe
     ```

3. **Compiler le projet**
   ```bash
   mvn clean install
   ```

4. **Lancer l'application**
   ```bash
   mvn javafx:run
   ```
