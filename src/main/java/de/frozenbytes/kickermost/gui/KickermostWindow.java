package de.frozenbytes.kickermost.gui;

import de.frozenbytes.kickermost.PropertiesLoader;
import de.frozenbytes.kickermost.dto.Match;
import de.frozenbytes.kickermost.dto.StoryPart;
import de.frozenbytes.kickermost.dto.Team;
import de.frozenbytes.kickermost.dto.property.*;
import de.frozenbytes.kickermost.dto.type.Country;
import de.frozenbytes.kickermost.dto.type.StoryEvent;
import de.frozenbytes.kickermost.http.MattermostWebhookClient;

import javax.swing.*;
import java.time.LocalTime;
import java.util.Properties;

public class KickermostWindow {
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

    public KickermostWindow() {
        JFrame frame = new JFrame("Kickermost");
        frame.setContentPane(this.pnlMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        client = new MattermostWebhookClient();
        fillComboBoxes();
        fillPropertyFields();
        createListener();
    }

    private void fillPropertyFields(){
        Properties properties = PropertiesLoader.loadProperties();
        txtWebhookUrl.setText(properties.get(PropertiesLoader.WEBHOOK_URL).toString());
        txtBotIconUrl.setText(properties.get(PropertiesLoader.ICON_URL).toString());
        txtBotName.setText(properties.get(PropertiesLoader.USERNAME).toString());
        txtChannel.setText(properties.get(PropertiesLoader.CHANNEL).toString());
    }

    private void fillComboBoxes() {
        DefaultComboBoxModel<Country> teamAModel = new DefaultComboBoxModel<>(Country.values());
        DefaultComboBoxModel<Country> teamBModel = new DefaultComboBoxModel<>(Country.values());
        DefaultComboBoxModel<StoryEvent> typeModel = new DefaultComboBoxModel<>(StoryEvent.values());
        cmbTeamA.setModel(teamAModel);
        cmbTeamB.setModel(teamBModel);
        cmbType.setModel(typeModel);
    }

    private void createListener(){
        btnSend.addActionListener(e -> {
            Team teamA = new Team(TeamName.create(((Country)cmbTeamA.getSelectedItem()).getName()), TeamScore.create((Integer)spnTeamAScore.getValue()));
            Team teamB = new Team(TeamName.create(((Country)cmbTeamB.getSelectedItem()).getName()), TeamScore.create((Integer)spnTeamBScore.getValue()));
            Match match = new Match(teamA, teamB, null);

            StoryPart messageParameters = new StoryPart(LocalTime.now(), GameMinute.create(spnGameTime.getValue().toString()+"."),
                    (StoryEvent) cmbType.getSelectedItem(), StoryTitle.create(txaTitleMessage.getText()), StoryDescription.create(txaMessage.getText()));

            ConfirmSendMessageDialog dlg = new ConfirmSendMessageDialog();
            if(dlg.isSend()) {
                client.postMessage(match, messageParameters);
            }
        });

        btnClose.addActionListener(e -> System.exit(0));

        btnSave.addActionListener(e -> PropertiesLoader.saveProperties(txtBotName.getText(), txtChannel.getText(), txtBotIconUrl.getText(), txtWebhookUrl.getText()));
    }
}
