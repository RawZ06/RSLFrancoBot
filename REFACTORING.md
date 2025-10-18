# 🏗️ Refactoring Clean Architecture - RSLFrancoBot

## ✅ Refactoring Terminé

Le projet a été entièrement refactoré selon les principes de **Clean Architecture** de Robert C. Martin.

---

## 📦 Nouvelle Structure

```
src/main/java/fr/rawz06/rslfrancobot/
├── engine/                          # 🎯 COUCHE DOMAINE (Logique Métier)
│   ├── domain/
│   │   ├── entities/               # Entités métier pures
│   │   │   ├── SeedMode.java
│   │   │   ├── SeedRequest.java
│   │   │   ├── SettingsFile.java
│   │   │   ├── SeedResult.java
│   │   │   ├── Preset.java
│   │   │   └── ValidationResult.java
│   │   └── ports/                  # Interfaces (contrats)
│   │       ├── IRandomizerApi.java
│   │       ├── IRSLScriptRunner.java
│   │       └── IPresetRepository.java
│   └── usecases/                   # Logique métier pure
│       ├── franco/
│       │   ├── ValidateSettingsUseCase.java
│       │   ├── BuildFinalSettingsUseCase.java
│       │   └── GenerateFrancoSeedUseCase.java
│       └── rsl/
│           └── GenerateRSLSeedUseCase.java
│
├── bot/                            # 💬 COUCHE APPLICATION (Interface Discord)
│   ├── models/                     # DTOs Discord abstraits
│   │   ├── DiscordInteraction.java
│   │   ├── DiscordMessage.java
│   │   ├── DiscordButton.java
│   │   └── DiscordSelectMenu.java
│   ├── handlers/                   # Handlers découplés de JDA
│   │   ├── SeedCommandHandler.java
│   │   ├── FrancoButtonHandler.java
│   │   ├── FrancoValidateHandler.java
│   │   ├── FrancoSelectMenuHandler.java
│   │   ├── RSLButtonHandler.java
│   │   └── PoTButtonHandler.java
│   ├── services/
│   │   └── SeedService.java        # Orchestration des use cases
│   └── presenters/
│       └── SeedPresenter.java      # Formatage des réponses Discord
│
└── api/                            # 🔌 COUCHE INFRASTRUCTURE (Adaptateurs)
    ├── discord/
    │   ├── JDAEventListener.java       # Routing des événements JDA
    │   ├── JDAInteractionAdapter.java  # Adaptateur Button/Select
    │   └── JDASlashCommandAdapter.java # Adaptateur Slash Commands
    ├── randomizer/
    │   └── MockRandomizerApiAdapter.java # Mock HTTP (à remplacer)
    ├── python/
    │   └── MockRSLScriptAdapter.java     # Mock Python (à remplacer)
    └── repositories/
        └── YamlPresetRepository.java     # Chargement YAML/JSON
```

---

## 🎯 Principes Appliqués

### 1. **Dependency Inversion Principle**
- Les dépendances pointent **toujours vers le domaine**
- L'Engine Layer ne connaît **rien** de Discord, HTTP ou Python
- Toutes les dépendances externes sont abstraites via des **ports** (interfaces)

### 2. **Single Responsibility Principle**
- Chaque classe a **une seule raison de changer**
- Use Cases = logique métier pure
- Handlers = gestion des interactions Discord
- Adapters = communication avec l'extérieur

### 3. **Open/Closed Principle**
- Ajout de nouveaux modes de seed = nouveau use case (pas de modification de l'existant)
- Ajout de nouvelles options Franco = modification du YAML uniquement

### 4. **Testabilité**
- Le domaine peut être testé **sans Discord**, **sans HTTP**, **sans Python**
- Les use cases sont testables avec des mocks

### 5. **Découplage de JDA**
- Les handlers utilisent `DiscordInteraction` (abstraction)
- Changement de librairie Discord = modification de l'API Layer uniquement

---

## 🔄 Flux de Données

### Exemple : Génération d'une seed Franco

```
1. User clique "/seed" sur Discord
   ↓
2. JDAEventListener reçoit SlashCommandInteractionEvent
   ↓
3. JDASlashCommandAdapter convertit en DiscordInteraction
   ↓
4. SeedCommandHandler.handle(interaction)
   ↓
5. SeedPresenter.presentModeSelection() → DiscordMessage
   ↓
6. User clique bouton "Franco"
   ↓
7. FrancoButtonHandler → SeedService.getAvailableOptions("franco")
   ↓
8. PresetRepository charge franco.yaml + franco.json
   ↓
9. SeedPresenter affiche le menu de sélection
   ↓
10. User sélectionne options et clique "Valider"
    ↓
11. FrancoValidateHandler → SeedService.generateSeed(FRANCO, userId, options)
    ↓
12. GenerateFrancoSeedUseCase:
    - ValidateSettingsUseCase (vérifie incompatibilités)
    - BuildFinalSettingsUseCase (fusionne preset + options)
    - IRandomizerApi.generateSeed() → MockRandomizerApiAdapter
    ↓
13. SeedResult retourné → SeedPresenter → DiscordMessage
    ↓
14. JDAInteractionAdapter envoie le message Discord
```

---

## 🧩 Points d'Extension Futurs

### Remplacer les Mocks

#### 1. **Implémenter l'appel HTTP réel au randomizer**
```java
@Component
public class OotrHttpClient implements IRandomizerApi {
    @Override
    public SeedResult generateSeed(SettingsFile settings) {
        // POST vers ootrandomizer.com
        // Parser la réponse
        // Retourner SeedResult
    }
}
```

#### 2. **Implémenter l'appel au script Python**
```java
@Component
public class PythonRSLScriptAdapter implements IRSLScriptRunner {
    @Override
    public SettingsFile generateSettings(Preset preset) {
        // ProcessBuilder pour lancer python3 script.py
        // Lire le fichier de sortie
        // Retourner SettingsFile
    }
}
```

### Ajouter un Nouveau Mode de Seed

1. Créer un nouveau use case dans `engine/usecases/`
2. Ajouter un handler dans `bot/handlers/`
3. Enregistrer le bouton dans `JDAEventListener`
4. Ajouter le preset dans `YamlPresetRepository`

**Pas de modification du code existant nécessaire!** ✅

---

## 🗂️ Fichiers de Configuration

- **`src/main/resources/data/franco.yaml`** : Options configurables Franco
- **`src/main/resources/data/franco.json`** : Preset de base Franco
- **`src/main/resources/application.properties`** : Configuration Spring
- **`src/main/resources/application-dev.properties`** : Token Discord dev

---

## 🚀 Lancer le Bot

```bash
# Compiler
mvn clean compile

# Lancer avec le profil dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## 🧪 Tests à Effectuer

### ✅ Tests Fonctionnels
- [ ] `/seed` affiche les 3 boutons (Franco, RSL, PoT)
- [ ] Bouton "Franco" affiche le menu d'options
- [ ] Sélection d'options incompatibles affiche une erreur
- [ ] Validation génère une seed (URL mockée)
- [ ] Bouton "RSL" génère directement une seed
- [ ] Bouton "PoT" génère directement une seed

### ✅ Tests Techniques
- [ ] Injection de dépendances Spring fonctionne
- [ ] Chargement des presets YAML/JSON OK
- [ ] Gestion des erreurs (preset introuvable, etc.)
- [ ] Logs corrects dans la console

---

## 📚 Prochaines Étapes

1. **Implémenter l'appel HTTP réel** à ootrandomizer.com
2. **Implémenter l'appel Python** pour RSL/PoT
3. **Ajouter des tests unitaires** pour les use cases
4. **Ajouter des presets RSL et PoT** (actuellement utilisent franco.json)
5. **Gérer l'envoi du fichier de settings** en JSON à l'utilisateur

---

## 🎓 Avantages de cette Architecture

✅ **Maintenabilité** : Code organisé, facile à comprendre
✅ **Testabilité** : Domaine testable sans dépendances externes
✅ **Extensibilité** : Ajout de fonctionnalités sans casse
✅ **Découplage** : Changement de Discord API = modification isolée
✅ **Réutilisabilité** : Use cases réutilisables dans d'autres contextes (CLI, Web, etc.)

---

**Refactoring réalisé selon les principes de Clean Architecture (Robert C. Martin)**
*Date : Octobre 2025*
