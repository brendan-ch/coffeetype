package renderers;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
          inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.PAGE_AXIS));
          JTextField textField = new JTextField();

          textField.setSize(50, 15);
          inputPanel.add(textField);
          
          // JLabel label = new JLabel("Press enter to join, or esc to cancel");
          // inputPanel.add(label);
          
          JButton enterButton = new JButton("Join");
          JButton cancelButton = new JButton("Cancel");

          inputPanel.add(enterButton);
          inputPanel.add(cancelButton);

          class EnterListener implements ActionListener {
            private JTextField textField;

            public EnterListener(JTextField textField) {
              this.textField = textField;
            }

            public void actionPerformed(ActionEvent e) {
              // Join the room
              String code = textField.getText();
              networkManager.joinRoom(code);
            }
          }

          class CancelListener implements ActionListener {
            private JFrame frame;

            public CancelListener(JFrame frame) {
              this.frame = frame;

            }
            public void actionPerformed(ActionEvent e) {
              // Close the frame
              this.frame.dispose();
            }
          }

          enterButton.addActionListener(new EnterListener(textField));
          cancelButton.addActionListener(new CancelListener(newFrame));
          newFrame.add(inputPanel);

          newFrame.pack();
          newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
          newFrame.setResizable(false);
          newFrame.setVisible(true);
          
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
    
    IconButton joinRoomButton = new IconButton(Assets.scaleIcon(Assets.NEXT_TEST), Event.JOIN_ROOM_PRESSED);
    joinRoomPanel.add(joinRoomHeader);
    joinRoomPanel.add(joinRoomButton.getPanel());
    joinRoomButton.addEventListener(new ButtonListener(networkManager));
    
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