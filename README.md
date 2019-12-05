# Basic-Torpedo-Game-on-network
Torpedo game on network. The ships positions comes from .txt files and it is only 1v1. The players only get information about their shots. Table visualization on the server, one table for every player. (Archived code)

#### About the ships file:

##### It looks like this: (for example)

shipType  X Coordinate Y Coordinate
("X" - One field size ship)
("I" - 3 field size vertical ship)
("-" - 3 field size horizontal ship)
```
X 3 3
I 5 6
- 5 5
- 5 7
- 0 0
```
##### Code section of the shipTypes

```java
Scanner lineScanner = new Scanner(line);
String shipType = lineScanner.next();
int x = lineScanner.nextInt();
int y = lineScanner.nextInt();
switch (shipType) {
case "X":
	placeShip(x, y);
	break;
case "I":
	placeShip(x, y - 1);
	placeShip(x, y);
	placeShip(x, y + 1);
	break;
case "-":
	placeShip(x - 1, y);
	placeShip(x, y);
	placeShip(x + 1, y);
	break;
}
```

