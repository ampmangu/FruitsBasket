package simulador;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import javax.media.j3d.*;

public class DeteccionControlPersonaje extends javax.media.j3d.Behavior {
    Figura personaje;
    WakeupOnAWTEvent    presionada = new
    WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    WakeupOnAWTEvent    liberada = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
    WakeupCondition     keepUpCondition = null;
    WakeupCriterion[]   continueArray = new WakeupCriterion[2];

 public DeteccionControlPersonaje(Figura _personaje ) {
        personaje = _personaje;
        continueArray[0]=liberada;
        continueArray[1]=presionada;
        keepUpCondition = new WakeupOr(continueArray);
    }

 public void initialize()    {
            wakeupOn(keepUpCondition);
    }

 public void processStimulus(Enumeration criteria) {
      while (criteria.hasMoreElements()){
      WakeupCriterion ster=(WakeupCriterion) criteria.nextElement();
       if (ster instanceof WakeupOnAWTEvent)   {
           AWTEvent[] events = ( (WakeupOnAWTEvent) ster).getAWTEvent();
           for (int n=0;n<events.length;n++){
              if (events[n]  instanceof KeyEvent){
                KeyEvent ek = (KeyEvent) events[n] ;
                if (ek.getID() == KeyEvent.KEY_PRESSED) {
                    if (ek.getKeyChar() == 'w') personaje.adelante= true;
                    else if (ek.getKeyChar() == 'a') personaje.izquierda=true;
                    else if (ek.getKeyChar() == 'd') personaje.derecha=true;
                    else if (ek.getKeyChar() == 's') personaje.atras=true;
                }
                else if (ek.getID() == KeyEvent.KEY_RELEASED)   {
                    if (ek.getKeyChar()== 'w') personaje.adelante=false;
                    else if (ek.getKeyChar() == 'a') personaje.izquierda=false;
                    else if (ek.getKeyChar() == 'd') personaje.derecha=false;
                    else if (ek.getKeyChar() == 's')personaje.atras=false;
                }
          }
        }
}}
 wakeupOn(keepUpCondition);
  }
}
