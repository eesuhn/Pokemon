## <img src="./readme-doc/app-icon.png" alt="logo" width="18"> Pokemon <img src="./readme-doc/app-icon.png" alt="logo" width="18">

<img src="./readme-doc/landing-bg.gif" alt="landing" width="360">

A **Scala-based** game recreating the iconic Pokemon battles from *Game Boy*

[<u>**Check out the Demo**</u>](https://www.youtube.com/watch?v=dAXEsYox5lI) 🌱

### Pokemon & Moves 🐉
- **100+ Pokemon**: Wide range of Pokemon, each with its *unique stats*.
- **100+ Moves**: From *physical* to *status* moves, offering strategic choices in battle.

### AI Opponents ⚔️
- **Weighted Move Selection**: Select moves factoring in *move effectiveness* and *type advantages*.
- **Smart Switching**: Switches to Pokémon that has *type advantage* over the player’s active Pokémon.

<img src="./readme-doc/sample-battle.gif" alt="landing" width="360">

### Requirements 🛠️
```
Scala 2.11.*
SBT 1.10.*
Java 17
JavaFX 17.*
```

### Running the Game 🎮
If you running `.jar` from [releases](https://github.com/eesuhn/Pokemon/releases/tag/v1.1), make sure to [check out the doc](./readme-doc/run-jar.md)

Run the following command if you running from source:
```
sbt compile run
```

### Background Work 🛠️
<i>*If you plan to contribute</i>
- **Assets Automation**: <br>
	Automatically scrap and manipulate assets using `FFmpeg, BeautifulSoup, Pillow`. [Check out the script](https://github.com/eesuhn/pokemon-res)
	- **Sprites**: Extracted from [Pokemon Database](https://pokemondb.net/pokedex/all)
	- **SFX**: Extracted from [SFX Gen 5](https://downloads.khinsider.com/game-soundtracks/album/pokemon-sfx-gen-5-attack-moves-blk-wht-blk2-wht2)
