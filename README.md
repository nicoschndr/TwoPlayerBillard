# Two-Player-Billard

Nico Schneider, 5245802

## Kurzbeschreibung

"Two-Player-Billard" ist ein Billard Spiel, das von zwei Spielern gespielt werden kann. Man hat einen Queue, der auf die weiße Kugel zeigt.
Diesen kann man mithilfe eines Mausklicks drehen, um die Richtig des Stoßes anzupassen. Durch halten der Leertaste lädt sich die Kraft des
Stoßes bis zu einer maximalen Kraft auf. Lässt man die Leertaste los, wird die weiße Kugel ind die passende Richtung mit der gewählten
Geschwindigkeit gestoßen. Treffen Kugeln aufeinander oder auf eine Bande prallen Sie physikalisch korrekt ab. Trift man mit einer Kugel ein Loch,
wird diese vom Tisch entfernt unde neben dem Tisch als gelocht angezeigt. Auch die Regeln sind implementiert. Locht ein Spieler die erste Kugel,
so darf er danach nur noch diese lochen (bspw. "vollen") und am Ende die Schwarze. Der andere Spieler spielt in diesem Beispiel dann
mit den "halben" weiter. Ein Foul sorgt für einen Spielerwechsel und dieser hat dann "Ball in Hand", d.h er darf die Kugel beliebig auf dem Tisch
platzieren.
Ein Foul ist es dann wenn: 
	- Ein Spieler als erstes eine Kugel des Gegners oder die schwarze Kugel berührt oder gar keine
	- die weiße locht
Locht ein Spieler vorzeitig die schwarze Kugel, gewinnt der Gegner und das Spiel startet neu.

##Screenshot
![Two-Player-Billard](/res/screenshot.png)

##Notwendige Schritte zum starten des Programmes

### in einer Entwicklungsumgebung

	-entweder die "Main" oder die "Main2" class starten
	-wenn erst die "Main" gestarted wurde, dann jetzt die "Main2" starten
	-wenn erst die "Main2" gestarted wurde, dann jetzt die "Main" starten
	
### in der JShell

1. die JShell im Ordner "Billard" starten
2. folgendes eingeben: jshell.exe --class-path .\out\production\Billard
3. Alle notwendigen Dateien importieren:
	import pool.poolModel.poolGame
	import pool.poolModel.Ball
	import pool.poolModel.Player
	import pool.poolModel.Vector
4. Neues poolGame starten mit Bällen und Spielern:
	poolGame game = new poolGame()  
	game.initialize(1920,1080)
	game.addPlayers() 

##JShell Beispiel

1. Alle notwendigen Schritte ausführen um das Programm zu starten und zu initialisieren.
2. den Queue rotieren:
	jshell> game.rotateQueue(1780, 550, 750, 300)
	$8 ==> 13.642919
3. stoßen(zu hart)
	jshell> game.shoot(50)
	Power muss zwischen 1 und 30 liegen!
4. stoßen
	jshell> game.shoot(1)
5. Die Geschwindigkeit der weißen Kugel abfragen:
	jshell> ArrayList<Ball> balls = game.getBalls()
	balls ==> [pool.poolModel.Ball@7591083d, pool.poolModel.Bal ... l.poolModel.Ball@3fa77460]
	
	jshell> for(Ball ball: balls){
   ...>     if(ball.getBallId() == 0){
   ...>         System.out.println(ball.getBallVel().getVectorX());
   ...>     }
   ...> }
	-0.6478564
	
	jshell> for(Ball ball: balls){
   ...>     if(ball.getBallId() == 0){
   ...>         System.out.println(ball.getBallVel().getVectorY());
   ...>     }
   ...> }
	-0.1572467
6. für eine kurze Zeit Spielphysik anwenden:
	jshell> for(int i = 0; i<100; i++){
   ...>     game.physics();
   ...> }

7. Die Geschwindigkeit der weißen Kugel erneut abfragen:
	jshell> for(Ball ball: balls){
   ...>     if(ball.getBallId() == 0){
   ...>         System.out.println(ball.getBallVel().getVectorX());
   ...>     }
   ...> }
	-0.23713644
	
	jshell> for(Ball ball: balls){
   ...>      if(ball.getBallId() == 0){
   ...>          System.out.println(ball.getBallVel().getVectorY());
   ...>     }
   ...> }
	-0.057557374

8. Spieldaten abfragen:
	jshell> game.checkShot()
	$10 ==> true
	jshell> game.getBallInHand()
	$11 ==> false
	jshell> game.checkFoul()
	$12 ==> false
	
	
##Quellen
https://pixabay.com/de/vectors/billardtisch-taschenbillard-billard-34356/
	
	