# ProiectSI

**Autor:** Șandru Alexandru

Acesta este un ecosistem de agenți de tip chat construit folosind framework-ul JADE (Java Agent Development Framework). Aplicația permite crearea, comunicarea și gestionarea agenților într-un sistem descentralizat. Fiecare agent poate trimite și primi mesaje de la alți agenți și poate interacționa cu aceștia printr-o interfață grafică (GUI).

## Descrierea Proiectului

Aplicația permite utilizatorului să:
- Creeze agenți dinamici în timpul execuției aplicației.
- Trimită și primească mesaje între agenți.
- Aleagă destinatarii mesajelor.
- Vizualizeze istoricul conversațiilor între agenți.
- Salveze istoricul mesajelor într-un fișier text.
- Închide întreaga aplicație (toți agenții și containerul JADE) dintr-o interfață centralizată.


## Tehnologii

- **JADE (Java Agent DEvelopment Framework)**: Framework-ul pentru crearea agenților.
- **Java 1.8**: Limbajul principal pentru implementarea agenților și a logicii aplicației.
- **Swing**: Folosit pentru implementarea interfeței grafice (GUI).

## Instalare

1. Clonează repository-ul folosind comanda:

```bash
git clone https://github.com/Xhadrines/ProiectSI.git
```

2. Deschide proiectul cu eclips.

3. Intră în `Run Configuration` în tabul `Java Application`.

4. In secțiunea `Main` la `Main class` selectează `jade.Boot`.

5. In secțiunea `Arguments` la `Program arguments` introdu `-gui -agents Agent1:backend.ChatAgent;Agent2:backend.ChatAgent;AgentM:backend.AgentManager` după apasă `Apply` și după `Run`. 

## Resurse Suplimentare
- [Demo Video](Video-ProiectSI.mkv) - Un videoclip care demonstrează funcționalitățile aplicației.
- [Descrierea Protocoalelor](DescriereaProtocoalelorUtilizate.pdf) - Un PDF ce explică în detaliu protocoalele utilizate în proiect.
