package de.frozenbytes.kickermost.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import de.frozenbytes.kickermost.conf.PropertiesLoader;
import de.frozenbytes.kickermost.conf.PropertiesHolder;
import de.frozenbytes.kickermost.dto.Match;
import de.frozenbytes.kickermost.dto.StoryPart;
import de.frozenbytes.kickermost.dto.Team;
import de.frozenbytes.kickermost.dto.property.*;
import de.frozenbytes.kickermost.dto.type.Country;
import de.frozenbytes.kickermost.dto.type.StoryEvent;
import de.frozenbytes.kickermost.exception.UnableToParsePropertiesFileException;
import de.frozenbytes.kickermost.exception.UnableToSavePropertiesFileException;
import de.frozenbytes.kickermost.http.MattermostWebhookClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;

public class KickermostWindow {

   private static final Logger logger = LoggerFactory.getLogger(KickermostWindow.class);

   private JComboBox<Country> cmbTeamA;
   private JSpinner spnTeamAScore;
   private JLabel lblScoreSeparator;
   private JSpinner spnTeamBScore;
   private JComboBox<Country> cmbTeamB;
   private JLabel lblHeaderScore;
   private JLabel lblWebhookUrl;
   private JTextField txtWebhookUrl;
   private JLabel lblBotIcon;
   private JTextField txtBotIconUrl;
   private JLabel lblChannel;
   private JTextField txtChannel;
   private JLabel lblBotName;
   private JTextField txtBotName;
   private JLabel lblProperties;
   private JLabel lblMessageOptions;
   private JLabel lblType;
   private JComboBox<StoryEvent> cmbType;
   private JLabel lblTitleText;
   private JTextArea txaTitleMessage;
   private JLabel lblDescriptionText;
   private JTextArea txaMessage;
   private JButton btnSend;
   private JButton btnSave;
   private JButton btnClose;
   private JPanel pnlMain;
   private JLabel lblGameTime;
   private JSpinner spnGameTime;

   private final MattermostWebhookClient client;
   private PropertiesHolder propertiesHolder;

   public KickermostWindow() throws UnableToParsePropertiesFileException {
      this.propertiesHolder = PropertiesLoader.createPropertiesHolder();

      setupUI();
      JFrame frame = new JFrame("Kickermost");
      frame.setContentPane(this.pnlMain);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);

      client = new MattermostWebhookClient(propertiesHolder);
      fillComboBoxes();
      fillPropertyFields();
      createListener();
   }

   public static void main(String[] args) {
      try {
         new KickermostWindow();
      } catch (UnableToParsePropertiesFileException e) {
         logger.error(e.getMessage(), e);
      }
   }

   private void fillPropertyFields() {
      txtWebhookUrl.setText(propertiesHolder.getMattermostWebhookUrl());
      txtBotIconUrl.setText(propertiesHolder.getMattermostIconUrl());
      txtBotName.setText(propertiesHolder.getMattermostUsername());
      txtChannel.setText(propertiesHolder.getMattermostChannelName());
   }

   private void fillComboBoxes() {
      DefaultComboBoxModel<Country> teamAModel = new DefaultComboBoxModel<>(Country.values());
      DefaultComboBoxModel<Country> teamBModel = new DefaultComboBoxModel<>(Country.values());
      DefaultComboBoxModel<StoryEvent> typeModel = new DefaultComboBoxModel<>(StoryEvent.values());
      cmbTeamA.setModel(teamAModel);
      cmbTeamB.setModel(teamBModel);
      cmbType.setModel(typeModel);
   }

   private void createListener() {
      btnSend.addActionListener(e -> {
         Team teamA = new Team(TeamName.create(((Country) cmbTeamA.getSelectedItem()).getName()), TeamScore.create((Integer) spnTeamAScore.getValue()));
         Team teamB = new Team(TeamName.create(((Country) cmbTeamB.getSelectedItem()).getName()), TeamScore.create((Integer) spnTeamBScore.getValue()));
         Match match = new Match(teamA, teamB, null);

         StoryPart messageParameters = new StoryPart(LocalTime.now(), LocalTime.now(), GameMinute.create(spnGameTime.getValue().toString() + "."), (StoryEvent) cmbType.getSelectedItem(), StoryTitle.create(txaTitleMessage.getText()),
               StoryDescription.create(txaMessage.getText()));

         ConfirmSendMessageDialog dlg = new ConfirmSendMessageDialog();
         if (dlg.isSend()) {
            client.postMessage(match, messageParameters);
         }
      });

      btnClose.addActionListener(e -> System.exit(0));

      btnSave.addActionListener(
            ev -> {
               try {
                  PropertiesLoader.saveProperties(txtBotName.getText(), txtChannel.getText(), txtBotIconUrl.getText(), txtWebhookUrl.getText());
                  this.propertiesHolder = PropertiesLoader.createPropertiesHolder();
                  this.client.setPropertiesHolder(propertiesHolder);
               } catch (UnableToParsePropertiesFileException | UnableToSavePropertiesFileException e) {
                  logger.error(e.getMessage(), e);
               }
            });
   }

   /**
    * Method generated by IntelliJ IDEA GUI Designer
    * >>> IMPORTANT!! <<<
    * DO NOT edit this method OR call it in your code!
    *
    * @noinspection ALL
    */
   private void setupUI() {
      pnlMain = new JPanel();
      pnlMain.setLayout(new GridLayoutManager(9, 5, new Insets(5, 5, 5, 5), -1, -1));
      lblWebhookUrl = new JLabel();
      lblWebhookUrl.setText("Webhook URL");
      pnlMain.add(lblWebhookUrl,
            new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                  GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
      lblChannel = new JLabel();
      lblChannel.setText("Channel");
      pnlMain.add(lblChannel,
            new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                  GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
      txtWebhookUrl = new JTextField();
      pnlMain.add(txtWebhookUrl, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
      txtChannel = new JTextField();
      pnlMain.add(txtChannel, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
      lblBotIcon = new JLabel();
      lblBotIcon.setText("Bot icon URL");
      pnlMain.add(lblBotIcon,
            new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                  GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
      txtBotIconUrl = new JTextField();
      pnlMain.add(txtBotIconUrl, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
      lblBotName = new JLabel();
      lblBotName.setText("Bot name");
      pnlMain.add(lblBotName,
            new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                  GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
      txtBotName = new JTextField();
      pnlMain.add(txtBotName, new GridConstraints(4, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
      lblProperties = new JLabel();
      lblProperties.setText("Properties");
      pnlMain.add(lblProperties,
            new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                  GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
      lblMessageOptions = new JLabel();
      lblMessageOptions.setText("Message options");
      pnlMain.add(lblMessageOptions,
            new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                  GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
      lblType = new JLabel();
      lblType.setText("Type");
      pnlMain.add(lblType,
            new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                  GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
      cmbType = new JComboBox();
      pnlMain.add(cmbType, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
      lblTitleText = new JLabel();
      lblTitleText.setText("Title text");
      pnlMain.add(lblTitleText,
            new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                  GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
      txaTitleMessage = new JTextArea();
      pnlMain.add(txaTitleMessage,
            new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW,
                  GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(250, 100), null, 0, false));
      lblDescriptionText = new JLabel();
      lblDescriptionText.setText("Description text");
      pnlMain.add(lblDescriptionText,
            new GridConstraints(7, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                  GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
      txaMessage = new JTextArea();
      pnlMain.add(txaMessage,
            new GridConstraints(7, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW,
                  GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(250, 100), null, 0, false));
      final JPanel panel1 = new JPanel();
      panel1.setLayout(new GridLayoutManager(2, 5, new Insets(0, 0, 0, 0), -1, -1));
      pnlMain.add(panel1, new GridConstraints(0, 0, 2, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
      cmbTeamA = new JComboBox();
      panel1.add(cmbTeamA, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
      spnTeamAScore = new JSpinner();
      panel1.add(spnTeamAScore, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(84, 30), null, 0, false));
      lblScoreSeparator = new JLabel();
      lblScoreSeparator.setText(":");
      panel1.add(lblScoreSeparator,
            new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                  GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
      spnTeamBScore = new JSpinner();
      panel1.add(spnTeamBScore, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(84, 30), null, 0, false));
      cmbTeamB = new JComboBox();
      panel1.add(cmbTeamB, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
      lblHeaderScore = new JLabel();
      lblHeaderScore.setText("Match score");
      panel1.add(lblHeaderScore,
            new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                  GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
      final JPanel panel2 = new JPanel();
      panel2.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
      pnlMain.add(panel2, new GridConstraints(8, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
      btnSend = new JButton();
      btnSend.setText("Send");
      panel2.add(btnSend, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null,
            0, false));
      btnSave = new JButton();
      btnSave.setText("Save properties");
      panel2.add(btnSave, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null,
            0, false));
      btnClose = new JButton();
      btnClose.setText("Close");
      panel2.add(btnClose, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null,
            0, false));
      final Spacer spacer1 = new Spacer();
      panel2.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
      lblGameTime = new JLabel();
      lblGameTime.setText("GameTime");
      pnlMain.add(lblGameTime,
            new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                  GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
      spnGameTime = new JSpinner();
      pnlMain.add(spnGameTime, new GridConstraints(6, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
   }

   /**
    * @noinspection ALL
    */
   public JComponent getRootComponent() {
      return pnlMain;
   }
}
