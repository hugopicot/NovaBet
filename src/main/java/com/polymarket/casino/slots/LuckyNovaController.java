
package com.polymarket.casino.slots;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.Random;
public class LuckyNovaController {
 @FXML private Label reel1,reel2,reel3,resultLabel;
 private final String[] symbols={"🍒","🍋","⭐","💎","7️⃣"};
 private final Random r=new Random();
 @FXML private void spin(){
  String a=symbols[r.nextInt(symbols.length)],b=symbols[r.nextInt(symbols.length)],c=symbols[r.nextInt(symbols.length)];
  reel1.setText(a); reel2.setText(b); reel3.setText(c);
  resultLabel.setText(a.equals(b)&&b.equals(c)?"JACKPOT !":"Perdu");
 }
}
