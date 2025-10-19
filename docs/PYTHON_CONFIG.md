# 🐍 Configuration Python Script RSL

## Configuration de la commande Python

Le bot utilise un script Python pour générer les settings RSL et PoT. La commande Python est **configurable** selon l'environnement.

### Fichiers de configuration

#### `application.properties` (Production)
```properties
app.python.command=python3
app.python.script.dir=plando-random-settings
app.python.script.name=RandomSettingsGenerator.py
app.python.weights.rsl=weights/rsl_season7.json
app.python.weights.pot=weights/rsl_pot.json
```

#### `application-dev.properties` (Développement local)
```properties
app.python.command=python3.11
app.python.script.dir=plando-random-settings
app.python.script.name=RandomSettingsGenerator.py
app.python.weights.rsl=weights/rsl_season7.json
app.python.weights.pot=weights/rsl_pot.json
```

---

## 🖥️ Configuration par plateforme

### macOS / Linux
```properties
# Python 3 générique
app.python.command=python3

# Ou version spécifique
app.python.command=python3.11
app.python.command=python3.12
```

### Windows
```properties
# Windows avec Python dans PATH
app.python.command=python

# Ou chemin absolu si nécessaire
app.python.command=C:/Python311/python.exe
```

### Docker / Production
```properties
# Utiliser python3 standard
app.python.command=python3
```

---

## 📂 Structure des fichiers

```
RSLFrancoBot/
├── plando-random-settings/          # Script Python à la racine
│   ├── RandomSettingsGenerator.py   # Script principal
│   ├── weights/
│   │   ├── rsl_season7.json         # Weights RSL
│   │   └── rsl_pot.json             # Weights PoT
│   └── data/                        # Fichiers générés (temporaires)
│       └── random_settings_*.json   # Fichiers créés puis supprimés
└── src/main/resources/
    ├── application.properties
    └── application-dev.properties
```

---

## 🔧 Variables d'environnement (optionnel)

Tu peux aussi utiliser des variables d'environnement pour surcharger la config:

```bash
export PYTHON_COMMAND=python3.11
export PYTHON_SCRIPT_DIR=plando-random-settings

mvn spring-boot:run
```

Puis dans `application.properties`:
```properties
app.python.command=${PYTHON_COMMAND:python3}
app.python.script.dir=${PYTHON_SCRIPT_DIR:plando-random-settings}
```

---

## ✅ Vérification de la configuration

Pour vérifier que Python est bien configuré:

```bash
# Tester la commande Python
python3.11 --version

# Tester le script
cd plando-random-settings
python3.11 RandomSettingsGenerator.py --override weights/rsl_season7.json --no_seed

# Vérifier qu'un fichier a été créé dans data/
ls -la data/
```

---

## 🚀 Workflow complet

1. User clique "RSL" dans Discord
2. Bot exécute: `python3.11 RandomSettingsGenerator.py --override weights/rsl_season7.json --no_seed`
3. Script génère: `data/random_settings_2025-10-18_18-12-45_123456.json`
4. Bot lit le fichier JSON
5. Bot **supprime** le fichier temporaire
6. Bot génère la seed avec ces settings
7. Bot envoie le lien à l'utilisateur

---

## 🐛 Troubleshooting

### Erreur "python3 not found"
- Vérifier que Python est installé: `which python3`
- Ajuster `app.python.command` dans les properties

### Erreur "Script directory not found"
- Vérifier que `plando-random-settings/` existe à la racine
- Ajuster `app.python.script.dir` si le dossier est ailleurs

### Erreur "Weight file not found"
- Vérifier que `weights/rsl_season7.json` existe
- Vérifier que `weights/rsl_pot.json` existe

### Le fichier temporaire n'est pas supprimé
- Vérifier les permissions sur `plando-random-settings/data/`
- Les logs indiqueront si la suppression a échoué
