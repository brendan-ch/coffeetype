package renderers;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.Component;
import java.awt.FlowLayout;

import helpers.*;
import assets.*;

class MultiplayerScreen implements CustomComponent {
  public static final String PAGE_TITLE = "Multiplayer";
  public static final int MAX_LISTENERS = 10;

  private JPanel panel;

  private EventListener[] listeners;
  private int numListeners;

  // private StatsTracker stats;

  public MultiplayerScreen(NetworkManager networkManager) {
    // this.stats = stats;
    this.numListeners = 0;
    this.listeners = new EventListener[MAX_LISTENERS];
    
    this.panel = new JPanel();
    this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
    this.panel.setBorder(new EmptyBorder(0, 0, 0, 0));
    this.panel.setBackground(Constants.BACKGROUND);

    PageHeader header = new PageHeader(PAGE_TITLE, true);
    this.panel.add(header.getPanel());

    JPanel itemWrapper = new JPanel();
    itemWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
    itemWrapper.setLayout(new BoxLayout(itemWrapper, BoxLayout.Y_AXIS));
    itemWrapper.setBackground(Constants.BACKGROUND);

    class HeaderListener implements EventListener {
      public void actionPerformed(Event e) {
        for (EventListener listener : listeners) {
          if (listener != null) {
            listener.actionPerformed(e);
          }
        }
      }
    }
    header.addEventListener(new HeaderListener());

    JPanel newRoomPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
    newRoomPanel.setBackground(Constants.BACKGROUND);
    newRoomPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
    
    
    JLabel newRoomHeader = new JLabel("Create a new room                            ");
    newRoomHeader.setFont(Assets.TYPING_FONT);
    newRoomHeader.setForeground(Constants.EMPHASIS_COLOR);
    IconButton newRoomButton = new IconButton(Assets.scaleIcon(Assets.NEXT_TEST), Event.NEW_ROOM_PRESSED);
    newRoomPanel.add(newRoomHeader);
    newRoomPanel.add(newRoomButton.getPanel());
    class ButtonListener implements EventListener {
      private NetworkManager networkManager;

      public ButtonListener(NetworkManager m) {
        this.networkManager = m;
      }

      public void actionPerformed(Event e) {
        if (e.EVENT_TYPE == Event.NEW_ROOM_PRESSED) {
          // Create a new room
          networkManager.createRoom();
        } else if (e.EVENT_TYPE == Event.JOIN_ROOM_PRESSED) {
          // Open a new window with an input
          JFrame newFrame = new JFrame();
          JPanel inputPanel = new JPanel();

          
        }
      }
    }
    newRoomButton.addEventListener(new ButtonListener(networkManager));
    
    this.panel.add(newRoomPanel);

    JPanel joinRoomPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
    joinRoomPanel.setBackground(Constants.BACKGROUND);
    joinRoomPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

    JLabel joinRoomHeader = new JLabel("Join an existing room                        ");
    joinRoomHeader.setFont(Assets.TYPING_FONT);
    joinRoomHeader.setForeground(Constants.EMPHASIS_COLOR);
    
    IconButton joinRoomButton = new IconButton(Assets.scaleIcon(Assets.NEXT_TEST), Event.NEW_ROOM_PRESSED);
    joinRoomPanel.add(joinRoomHeader);
    joinRoomPanel.add(joinRoomButton.getPanel());
    
    this.panel.add(joinRoomPanel);

    JPanel setNicknamePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
    setNicknamePanel.setBackground(Constants.BACKGROUND);
    setNicknamePanel.setBorder(new EmptyBorder(5, 0, 5, 0));

    JLabel setNicknameHeader = new JLabel("Set your nickname                            ");
    setNicknameHeader.setFont(Assets.TYPING_FONT);
    setNicknameHeader.setForeground(Constants.EMPHASIS_COLOR);

    

    IconButton setNicknameButton = new IconButton(Assets.scaleIcon(Assets.NEXT_TEST), Event.NEW_ROOM_PRESSED);
    setNicknamePanel.add(setNicknameHeader);
    setNicknamePanel.add(setNicknameButton.getPanel());
    
    this.panel.add(setNicknamePanel);
    

    this.render();
  }
  
  public JPanel getPanel() {
    return this.panel;
  }

  public void render() {
  }

  public void addEventListener(EventListener listener) {
    if (this.numListeners < MAX_LISTENERS) {
      this.listeners[this.numListeners++] = listener;
    }
  }
  
}