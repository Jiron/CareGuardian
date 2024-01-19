# CareGuardian

https://github.com/Jiron/CareGuardian

## Lint Checker 

Genutzt wurde grundsätzlich das CheckStyle Plugin von IDEA genutzt und es wird getestet, ob max. 30 Zeilen Code pro Methode eingegeben wurden.

### Installation

- Im Menü "File" -> "Settings" -> "Plugins" -> "CheckStyle-IDEA suchen -> Installieren und ggf. Android Studio neustarten

### Konfiguration und Nutzung

- Im Menü -> "File" -> "Settings" -> "Tools" -> "CheckStyle" -> Unter "Configuration file" das Plus wählen und dann im "app" Ordner den "checkstyle.xml" auswählen. Zudem eine "Description" hinzufügen, darf beliebig sein.
- Um es zu starten: Den Bleistift Icon (CheckStyle-IDEA Icon) welcher sich nun entweder ganz links oder links unten befindet anklicken -> Im "Rules" Dropdown die selbst gewählte Description anwählen -> Das Ordner Symbol links anklicken (nicht das grüne play icon) um die Checks zu starten.

## Plan changes on the go

- Das Handy vibriert zusätzlich in einem speziellen Pattern (schwach zu stärker - Pause - stark - Pause) jede Sekunde die im Countdown abläuft, um den User darauf aufmerksam zu machen
- Die anderen OPTIONALEN (also nicht Bewertungsrelevante Funktionen) Punkte wurden nicht umgesetzt, alle anderen schon.
- Es wurden zusätzlich viele fehlercases getestet und diese geben entsprechende errors

## Allgemeine Punkte zum beachten

Der Sensor ist sehr unsensibel eingestellt, damit nur stärkere Bewegungen erkannt werden. Beim Testen gerne eine Fallbewegung oder starkes Schütteln anwenden!