# 🏗️ ARCHI.md — Architecture Logicielle du Bot Discord OoT Randomizer

## 🎯 Objectif du projet

Concevoir un bot Discord (en Java) permettant de générer des **seeds Ocarina of Time Randomizer (OoTR)** selon différents modes de jeu :
- **Franco** : seed basée sur un preset fixe avec personnalisation utilisateur ;
- **RSL** : seed basée sur un preset généré aléatoirement par un script Python ;
- **PoT** : variante du RSL avec un preset différent.

Le bot doit être **extensible**, **testable**, et respecter les principes de **Clean Architecture** (Robert C. Martin).

---

## 🧩 Vue d’ensemble

L’architecture est découpée en **trois couches principales** :

```
┌──────────────────────────┐
│       Engine Layer       │  ← Domaine & logique métier
├──────────────────────────┤
│         Bot Layer        │  ← Interface applicative Discord
├──────────────────────────┤
│        API Layer         │  ← Adaptateurs techniques externes
└──────────────────────────┘
```

### Buts principaux

- **Isolation des dépendances externes** (Discord API, HTTP, Python)
- **Orchestration claire du métier**
- **Extensibilité** (nouveaux modes de seed sans refactor global)
- **Clean boundaries** entre interface, domaine et infrastructure

---

## ⚙️ Engine Layer — Domaine & logique métier

### Responsabilités
- Définir les **use cases** de génération (Franco, RSL, PoT, etc.)
- Valider les choix utilisateurs et construire les settings finaux
- Coordonner la génération de seed via l’API du site ootrandomizer.com

### Interfaces principales

```java
interface IRandomizerApi {
    SeedResult generateSeed(SettingsFile settings);
}

interface IRSLScriptRunner {
    SettingsFile generateSettings(Preset preset);
}

interface IPresetRepository {
    Preset getPreset(String name);
}
```

### Use Cases Franco

1. **GetAvailableSettingsUseCase**  
   Fournit la liste des settings configurables pour l’utilisateur.

2. **ValidateSettingsUseCase**  
   Vérifie la cohérence des choix utilisateur (ex : `keysanity` incompatible avec `no small keys`).

3. **BuildFinalSettingsUseCase**  
   Fusionne le preset de base avec les choix utilisateur validés.

4. **GenerateSeedUseCase**  
   Envoie les settings au site du randomizer via HTTP et récupère le lien de seed.

### Use Cases RSL / PoT

1. **GenerateRSLSettingsUseCase**  
   Exécute le script Python et récupère le fichier de settings généré.

2. **GenerateSeedUseCase**  
   Identique au Franco, réutilise le même flux HTTP.

### Exemple d’orchestration (pseudo-code)

```java
class FrancoSeedGenerator implements ISeedGenerator {
    execute(request) {
        preset = presetRepo.getPreset("franco");
        valid = validator.validate(request.userSettings);
        if (!valid.isOk()) throw InvalidSettingsException();
        settings = merger.merge(preset, request.userSettings);
        return randomizerApi.generateSeed(settings);
    }
}
```

---

## 💬 Bot Layer — Couche Discord (interface applicative)

### Responsabilités
- Gérer les interactions utilisateur via Discord (commandes, boutons, menus)
- Convertir les actions utilisateur en **requêtes métier**
- Présenter les résultats métiers sous forme de **messages Discord**

### Structure interne

```
Bot Layer
├── Command Handlers
│   ├── SeedCommandHandler
│   ├── ModeButtonHandler
│   └── SettingsModalHandler
├── Services
│   └── SeedService (coordonne avec Engine)
└── Presenters
    └── DiscordSeedPresenter
```

### Exemple conceptuel

```java
@DiscordCommand(name = "seed")
class SeedCommandHandler implements IDiscordCommandHandler {
    execute(DiscordInteraction interaction) {
        message = new DiscordMessage("Quel type de seed veux-tu générer ?");
        message.addButton("Franco", "seed_franco");
        message.addButton("RSL", "seed_rsl");
        message.addButton("PoT", "seed_pot");
        discordAdapter.sendMessage(interaction.channel, message);
    }
}
```

Le handler ne dépend **d’aucune librairie Discord**.  
C’est l’adaptateur JDA (dans l’API Layer) qui traduit les événements en `DiscordInteraction`.

---

## 🔌 API Layer — Intégrations techniques

### Rôle
Regroupe les adaptateurs concrets vers les dépendances externes :
- Discord (via JDA)
- ootrandomizer.com (via HTTP)
- Script Python (via ProcessBuilder)

### Exemple d’adaptateur HTTP

```java
class OotrHttpClient implements IRandomizerApi {
    SeedResult generateSeed(SettingsFile settings) {
        // HTTP POST vers ootrandomizer.com avec le body JSON
        // retourne un SeedResult avec lien, hash, etc.
    }
}
```

### Exemple d’adaptateur Python

```java
class PythonRSLAdapter implements IRSLScriptRunner {
    SettingsFile generateSettings(Preset preset) {
        // Appel "python3 script.py preset.json"
        // Lit le fichier de sortie et retourne le JSON de settings
    }
}
```

### Exemple d’adaptateur Discord

```java
class JDAAdapter implements IDiscordAdapter {
    registerCommand(command) {
        // Crée la slash command dans Discord
    }

    onInteraction(event) {
        // Convertit l'événement JDA → DiscordInteraction
        // Trouve le bon handler et appelle handler.execute()
    }
}
```

---

## 🧩 Gestion dynamique des commandes Discord

### Concept
Utiliser des annotations pour déclarer des commandes indépendamment de JDA.

```java
@Retention(RUNTIME)
@Target(TYPE)
@interface DiscordCommand {
    String name();
    String description() default "";
}
```

Un `DiscordCommandRegistry` scanne le code à l’initialisation et enregistre automatiquement les commandes auprès de l’adaptateur Discord.

### Exemple conceptuel

```java
registry.registerAnnotatedCommands("fr.botrando.discord.commands");
```

Résultat :  
Tu peux ajouter une nouvelle commande simplement en écrivant une classe annotée, sans toucher à JDA.

---

## 🧱 Communication inter-couches

| Depuis | Vers | Par |
|---------|------|-----|
| Bot Layer | Engine Layer | Interfaces métiers (`ISeedGenerator`, `ISeedService`) |
| Engine Layer | API Layer | Ports abstraits (`IRandomizerApi`, `IRSLScriptRunner`, etc.) |
| API Layer | Extérieur | Implémentations concrètes (JDA, HTTP, Python) |

Les dépendances **vont toujours vers le haut** (principe de dépendance inversée).

---

## 🔮 Extensibilité prévue

- Ajouter un nouveau type de seed :  
  → Créer un nouveau `ISeedGenerator` + un nouveau bouton dans le bot.
- Ajouter un nouveau preset :  
  → Ajouter un fichier JSON dans `PresetRepository`.
- Ajouter un nouveau canal d’entrée (ex. site web, CLI) :  
  → Nouvelle implémentation du port d’entrée sans modifier le domaine.

---

## ✅ Résumé des principes respectés

| Principe | Application |
|-----------|--------------|
| **Clean Architecture** | Séparation stricte des couches / dépendances inversées |
| **Single Responsibility** | Chaque module a un rôle précis |
| **Open/Closed Principle** | Ajout de nouveaux modes sans modifier l’existant |
| **Testabilité** | Domain testé sans Discord / réseau |
| **Extensibilité** | Facile à enrichir par ajout de Use Cases ou Adapters |

---

## 📦 Entités principales (conceptuelles)

| Entité | Description |
|---------|--------------|
| `SeedRequest` | Représente la demande utilisateur (mode, options, preset, etc.) |
| `SettingsFile` | JSON ou objet représentant les settings complets |
| `SeedResult` | Contient le lien, le hash et les infos de seed |
| `Preset` | Template de configuration pour un mode donné |
| `ValidationResult` | Résultat de cohérence des settings |

---

## 🧠 Diagramme conceptuel simplifié

```
[Discord User]
   ↓
[Bot Command Handler] → [SeedService] → [ISeedGenerator]
                                      ↙           ↘
                              [FrancoSeedGen]   [RSLSeedGen]
                                      ↓                ↓
                            [IRandomizerApi]     [IRSLScriptRunner]
                                      ↓                ↓
                          [ootrandomizer.com]   [python3 script.py]
```

---

## 📚 Conclusion

L’architecture proposée permet :  
- une séparation claire des responsabilités,  
- une évolutivité maîtrisée,  
- et une isolation des dépendances techniques.

Le bot Discord devient un **système modulaire et maintenable**, fidèle aux principes de **Clean Architecture**, prêt à accueillir de nouveaux modes de génération ou de nouvelles interfaces utilisateur.