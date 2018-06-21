package hellotvxlet;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Random;
import org.dvb.event.UserEventListener;
import org.dvb.ui.DVBColor;
import javax.tv.xlet.Xlet;
import javax.tv.xlet.XletContext;
import javax.tv.xlet.XletStateChangeException;
import org.bluray.ui.event.HRcEvent;
import org.davic.resources.ResourceClient;
import org.davic.resources.ResourceProxy;
import org.dvb.event.EventManager;
import org.dvb.event.UserEvent;
import org.dvb.event.UserEventRepository;
import org.havi.ui.*;
import org.havi.ui.HStillImageBackgroundConfiguration;
import org.havi.ui.event.HBackgroundImageEvent;
import org.havi.ui.event.HBackgroundImageListener;


public class HelloTVXlet implements Xlet, ResourceClient, HBackgroundImageListener, UserEventListener
{
    private HScreen screen;
    private HBackgroundDevice bgDev;
    private HStillImageBackgroundConfiguration bgConfig;
    private HBackgroundImage bgImg2 = new HBackgroundImage("BGIMG2.png");
    private HBackgroundImage bgImg1 = new HBackgroundImage("mainBG.png");
    private HScene scene;
    private HStaticText titel,winner,winner1,winner2;
    private HTextButton uitleg,player1,player2;
    
    int isBGgreen = 0;
    int whoWon = 0;
    
    String winnername = "WINNER: ";
    
    public void destroyXlet(boolean unconditional) throws XletStateChangeException {
    }
    
    public void initXlet(XletContext ctx) throws XletStateChangeException {
        screen=HScreen.getDefaultHScreen();
        bgDev=screen.getDefaultHBackgroundDevice();
        if (bgDev.reserveDevice(this)){
            System.out.println("BackgroundImage device has been reserved");
        } else {
            System.out.println("Background image device cannot be reserved");
        }
        HBackgroundConfigTemplate bgConfigTemplate =new HBackgroundConfigTemplate();
        bgConfigTemplate.setPreference(HBackgroundConfigTemplate.STILL_IMAGE, 
                HBackgroundConfigTemplate.REQUIRED);
        try {
            bgConfig=(HStillImageBackgroundConfiguration)bgDev.getBestConfiguration(bgConfigTemplate);
            bgDev.setBackgroundConfiguration(bgConfig);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
            bgImg1.load(this);

        scene = HSceneFactory.getInstance().getDefaultHScene();
        
        titel = new HStaticText("REACTION SPEED TESTER");
        titel.setLocation(160,-130);
        titel.setSize(400,400);
        scene.add(titel);
        
        uitleg = new HTextButton("Wie is de snelste van de twee?\n Zodra de achtergrond groen kleurt, druk je op je knop (a of p).\n de snelste wint!");
        uitleg.setLocation(50, 100);
        uitleg.setSize(600, 160);
        uitleg.setBackground(new DVBColor(0,0,0,100));
        uitleg.setBackgroundMode(HVisible.BACKGROUND_FILL);
        scene.add(uitleg);
        
        player1 = new HTextButton("PLAYER1\n (a)");
        player1.setLocation(100, 290);
        player1.setSize(200, 200);
        player1.setBackground(new DVBColor(0,255,0,200));
        player1.setBackgroundMode(HVisible.BACKGROUND_FILL);
        scene.add(player1);
        
        player2 = new HTextButton("PLAYER2\n (p)");
        player2.setLocation(400, 290);
        player2.setSize(200, 200);
        player2.setBackground(new DVBColor(255,0,0,200));
        player2.setBackgroundMode(HVisible.BACKGROUND_FILL);
        scene.add(player2);
        
        winner = new HStaticText(winnername);
        winner.setLocation(380,430);
        winner.setSize(200,200);
        scene.add(winner);
        
        winner1 = new HStaticText("PLAYER1");
        winner1.setLocation(700,430);
        winner1.setSize(200,200);
        scene.add(winner1);
        
        winner2 = new HStaticText("PLAYER2");
        winner2.setLocation(700,430);
        winner2.setSize(200,200);
        scene.add(winner2);
        
        
        UserEventRepository repository = new UserEventRepository("Voorbeeld");
        repository.addKey(HRcEvent.VK_A);
        repository.addKey(HRcEvent.VK_P);

        //EventManager
        EventManager.getInstance().addUserEventListener(this,repository);
 

    }

    public void userEventReceived(UserEvent e) {
        if(e.getType() == KeyEvent.KEY_PRESSED && isBGgreen == 1){ // Keys can only get pressed if the BG is green
            System.out.println("pushed btn");
            switch(e.getCode()){
                case HRcEvent.VK_A:
                    System.out.println("A pressed");
                    if(whoWon == 0) { // if whoWon == 0, PLAYER2 hasn't reacted yet and hasn't pressed his btn
                        player1.setBackground(new DVBColor(0,170,0,200)); // Change BTN color
                        player1.repaint();
                        winner1.setLocation(470, 430);
                        winner1.repaint();
                        whoWon = 1; // makes sure PLAYER2 can't press his button anymore and shows that PLAYER1 has won
                    } else {System.out.println("A pressed, B was First");}
     
                    break;
                case HRcEvent.VK_P:
                    System.out.println("P pressed");
                    if(whoWon == 0){ // if whoWon == 0, PLAYER1 hasn't reacted yet and hasn't pressed his btn
                       player2.setBackground(new DVBColor(170,0,0,200)); // Change BTN color
                       player2.repaint();
                       winner2.setLocation(470, 430);
                       winner2.repaint();
                       whoWon = 2; // makes sure PLAYER1 can't press his button anymore and shows that PLAYER2 has won
                    } else {System.out.println("B pressed, A was First");}
                    
                    break;
            
            }
        }
        
            
    }
    
    public void startXlet() throws XletStateChangeException {
        scene.validate();
        scene.setVisible(true);
        
    }
    public void imageLoaded(HBackgroundImageEvent e) {
        Random rnd = new Random();
        int  n = rnd.nextInt(8000) + 5000;
        System.out.println(n);
        
        System.out.println("Image geladen");
       try {
            bgConfig.displayImage(bgImg1);
            try{
                Thread.sleep(n);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            
            bgConfig.displayImage(bgImg2);
            isBGgreen = 1;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void imageLoadFailed(HBackgroundImageEvent e) {
        System.out.println("Image mislukt");
    }
    
    public void pauseXlet() {
    }
    
    public boolean requestRelease(ResourceProxy proxy, Object requestData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void release(ResourceProxy proxy) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void notifyRelease(ResourceProxy proxy) {
        throw new UnsupportedOperationException("Not supported yet.");
    }  
}